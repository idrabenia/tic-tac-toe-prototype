package idrabenia.bayeux;

import idrabenia.ApplicationContext;
import idrabenia.domain.Game;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.AbstractService;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Map;

/**
 * @author Ilya Drabenia
 * @since 09.03.13
 */
public class GameService extends AbstractService {
    private final ClientGateway clientGateway;

    public GameService(BayeuxServer bayeux) {
        super(bayeux, "hello");

        addService("/service/game/markCell", "markCell");
        addService("/service/game/join", "joinGame");

        clientGateway = new ClientGateway(this);
    }

    public void joinGame(final ServerSession remote, Message message) {
        Map<String, Object> input = message.getDataAsMap();

        String curPlayer = (String) input.get("player");
        ApplicationContext.get().getSessions().put(curPlayer, remote);

        synchronized (ApplicationContext.get().getUpcomingGames()) {
            List<Game> upcomingGames = ApplicationContext.get().getUpcomingGames();

            if (upcomingGames.size() > 0) {
                Game curGame = upcomingGames.remove(0);

                curGame.setPlayer2(curPlayer);
                ApplicationContext.get().getGames().put(curPlayer, curGame);
            } else {
                Game newGame = createNewGame(curPlayer);

                upcomingGames.add(newGame);
                onGameUpcoming(newGame);

                ApplicationContext.get().getGames().put(curPlayer, newGame);
            }
        }
    }

    public Game createNewGame(String player) {
        final Game newGame = new Game(player);

        PropertyChangeSupport gamePublisher = newGame.getPublisher();
        gamePublisher.addPropertyChangeListener("gameFinished", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                onGameFinished(getServerSession(), evt);
            }
        });

        gamePublisher.addPropertyChangeListener("gameStarted", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                onGameStarted(getServerSession(), (Game) evt.getSource());
            }
        });

        gamePublisher.addPropertyChangeListener("nextStepPlayer", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                onNextStepPlayerChanged(newGame, evt);
            }
        });

        newGame.getTable().getPublisher().addPropertyChangeListener("cells", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                onCellMarked(newGame, (IndexedPropertyChangeEvent) evt);
            }
        });

        return newGame;
    }

    public void markCell(ServerSession remote, Message message) {
        Map<String, Object> input = message.getDataAsMap();

        String curPlayer = (String) input.get("player");
        int cellNumber = Integer.parseInt((String) input.get("cellNumber"));
        ApplicationContext.get().getSessions().put(curPlayer, remote);

        Game curGame = ApplicationContext.get().getGames().get(curPlayer);

        curGame.markCell(curPlayer, cellNumber);
    }

    public void onGameUpcoming(Game game) {
        clientGateway.deliver(game.getPlayer1(), "/game/onUpcoming", "gameState", "Upcoming");
    }

    public void onGameStarted(ServerSession remote, Game newGame) {
        clientGateway.deliver(newGame.getPlayers(), "/game/onStarted",
                "gameState", "Running",
                "player1", newGame.getPlayer1(),
                "player2", newGame.getPlayer2(),
                "nextStepPlayer", newGame.getPlayer1());
    }

    public void onGameFinished(ServerSession remote, PropertyChangeEvent event) {
        Game curGame = (Game) event.getSource();

        clientGateway.deliver(curGame.getPlayers(), "/game/onFinished",
                "gameState", "Finished",
                "winner", curGame.getWinnerName());

        ApplicationContext.get().getGames().remove(curGame);
    }

    public void onCellMarked(Game game, IndexedPropertyChangeEvent event) {
        clientGateway.deliver(game.getPlayers(), "/game/onCellMarked",
                "markedCell", event.getIndex(),
                "markType", event.getNewValue().toString());
    }

    public void onNextStepPlayerChanged(Game game, PropertyChangeEvent event) {
        clientGateway.deliver(game.getPlayers(), "/game/onNextStepPlayerChanged",
                "nextStepPlayer", game.getNextStepPlayer());
    }

}
