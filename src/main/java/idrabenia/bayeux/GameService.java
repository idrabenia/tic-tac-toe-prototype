package idrabenia.bayeux;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.Future;
import akka.dispatch.OnSuccess;
import akka.pattern.Patterns;
import idrabenia.ApplicationContext;
import idrabenia.actors.GameActor;
import idrabenia.domain.Game;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ServerSession;
import org.cometd.server.AbstractService;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
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
            List<ActorRef> upcomingGames = ApplicationContext.get().getUpcomingGames();

            if (upcomingGames.size() > 0) {
                ActorRef curGame = upcomingGames.remove(0);

                curGame.tell(new GameActor.SetSecondPlayerMessage(curPlayer));
                ApplicationContext.get().getGames().put(curPlayer, curGame);
            } else {
                ActorRef newGame = ActorSystem.create("TicTacToe").actorOf(new Props(GameActor.class));
                newGame.tell(new GameActor.NewGameMessage(curPlayer));

                upcomingGames.add(newGame);

                Future future = Patterns.ask(newGame, "getFirstPlayer", 5000);
                future.onSuccess(new OnSuccess<String>() {

                    public void onSuccess(String firstPlayer) {
                        onGameUpcoming(firstPlayer);
                    }

                });

                ApplicationContext.get().getGames().put(curPlayer, newGame);
            }
        }
    }

    public void markCell(ServerSession remote, Message message) {
        Map<String, Object> input = message.getDataAsMap();

        String curPlayer = (String) input.get("player");
        int cellNumber = Integer.parseInt((String) input.get("cellNumber"));
        ApplicationContext.get().getSessions().put(curPlayer, remote);

        ActorRef curGame = ApplicationContext.get().getGames().get(curPlayer);

        curGame.tell(new GameActor.MarkCellMessage(cellNumber, curPlayer));
    }

    public void onGameUpcoming(String player1) {
        clientGateway.deliver(player1, "/game/onUpcoming", "gameState", "Upcoming");
    }

    public void onGameStarted(String player1, String player2) {
        clientGateway.deliver(Arrays.asList(player1, player2), "/game/onStarted",
                "gameState", "Running",
                "player1", player1,
                "player2", player2,
                "nextStepPlayer", player1);
    }

    public void onGameFinished(ActorRef curGame, List<String> players, String winnerPlayer) {
        clientGateway.deliver(players, "/game/onFinished",
                "gameState", "Finished",
                "winner", winnerPlayer);

        ApplicationContext.get().getGames().remove(curGame);
    }

    public void onCellMarked(List<String> players, int index, String markType) {
        clientGateway.deliver(players, "/game/onCellMarked",
                "markedCell", index,
                "markType", markType);
    }

    public void onNextStepPlayerChanged(List<String> players, String nextStepPlayer) {
        clientGateway.deliver(players, "/game/onNextStepPlayerChanged", "nextStepPlayer", nextStepPlayer);
    }

}
