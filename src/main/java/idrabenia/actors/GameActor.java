package idrabenia.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import idrabenia.domain.Game;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * @author Ilya Drabenia
 * @since 06.04.13
 */
public class GameActor extends UntypedActor {
    private Game game;

    public static class NewGameMessage {
        private final String player;

        public NewGameMessage(String firstPlayer) {
            player = firstPlayer;
        }

        public String getPlayer() {
            return player;
        }
    }

    public static class SetSecondPlayerMessage {
        private final String secondPlayer;

        public SetSecondPlayerMessage(String secondPlayer) {
            this.secondPlayer = secondPlayer;
        }

        public String getSecondPlayer() {
            return secondPlayer;
        }
    }

    public static class MarkCellMessage {
        private final String curPlayer;
        private final int curNumber;

        public MarkCellMessage(int curNumber, String curPlayer) {
            this.curNumber = curNumber;
            this.curPlayer = curPlayer;
        }

        public int getCurNumber() {
            return curNumber;
        }

        public String getCurPlayer() {
            return curPlayer;
        }
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof NewGameMessage) {
            createGame(message);
        } else if ("getFirstPlayer".equals(message)) {
            getSender().tell(game.getPlayer1());
        } else if (message instanceof SetSecondPlayerMessage) {
            game.setPlayer2(((SetSecondPlayerMessage) message).getSecondPlayer());
        } else if (message instanceof MarkCellMessage) {
            MarkCellMessage markMessage = (MarkCellMessage) message;
            game.markCell(markMessage.getCurPlayer(), markMessage.getCurNumber());
        } else {
            unhandled(message);
        }
    }

    private void createGame(Object message) {
        final ActorRef gameServiceActor = getContext().system().actorOf(new Props(GameServiceActor.class));
        game = new Game(((NewGameMessage) message).getPlayer());
                PropertyChangeSupport gamePublisher = game.getPublisher();

        gamePublisher.addPropertyChangeListener("gameFinished", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                gameServiceActor.tell(new GameServiceActor.GameFinishedMessage(getSelf(), game.getPlayers(),
                        game.getWinnerName()));
            }
        });

        gamePublisher.addPropertyChangeListener("gameStarted", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                gameServiceActor.tell(new GameServiceActor.GameStartedMessage(game.getPlayer1(), game.getPlayer2()));
            }
        });

        gamePublisher.addPropertyChangeListener("nextStepPlayer", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                gameServiceActor.tell(new GameServiceActor.NextStepPlayerChangedMessage(game.getNextStepPlayer(),
                        game.getPlayers()));
            }
        });

        game.getTable().getPublisher().addPropertyChangeListener("cells", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                IndexedPropertyChangeEvent event = (IndexedPropertyChangeEvent) evt;
                gameServiceActor.tell(new GameServiceActor.CellMarkedMessage(event.getIndex(),
                        evt.getNewValue().toString(), game.getPlayers()));
            }
        });
    }
}
