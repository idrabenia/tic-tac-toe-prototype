package idrabenia.actors;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import idrabenia.bayeux.BayeuxInitializer;
import idrabenia.bayeux.GameService;

import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 06.04.13
 */
public class GameServiceActor extends UntypedActor {

    public static class GameFinishedMessage {
        private final List<String> players;
        private final String winner;
        private final ActorRef curGame;

        public GameFinishedMessage(ActorRef curGame, List<String> players, String winner) {
            this.curGame = curGame;
            this.players = players;
            this.winner = winner;
        }

        public ActorRef getCurGame() {
            return curGame;
        }

        public List<String> getPlayers() {
            return players;
        }

        public String getWinner() {
            return winner;
        }
    }

    public static class GameStartedMessage {
        private final String player1;
        private final String player2;

        public GameStartedMessage(String player1, String player2) {
            this.player1 = player1;
            this.player2 = player2;
        }

        public String getPlayer1() {
            return player1;
        }

        public String getPlayer2() {
            return player2;
        }
    }

    public static class NextStepPlayerChangedMessage {
        private final List<String> players;
        private final String nextStepPlayer;

        public NextStepPlayerChangedMessage(String nextStepPlayer, List<String> players) {
            this.nextStepPlayer = nextStepPlayer;
            this.players = players;
        }

        public String getNextStepPlayer() {
            return nextStepPlayer;
        }

        public List<String> getPlayers() {
            return players;
        }
    }

    public static class CellMarkedMessage {
        private final List<String> players;
        private final int index;
        private final String markType;

        public CellMarkedMessage(int index, String markType, List<String> players) {
            this.index = index;
            this.markType = markType;
            this.players = players;
        }

        public int getIndex() {
            return index;
        }

        public String getMarkType() {
            return markType;
        }

        public List<String> getPlayers() {
            return players;
        }
    }

    @Override
    public void onReceive(Object message) {
        if (message instanceof GameFinishedMessage) {
            GameFinishedMessage finishedMessage = (GameFinishedMessage) message;
            getGameService().onGameFinished(finishedMessage.getCurGame(), finishedMessage.getPlayers(),
                    finishedMessage.getWinner());
        } else if (message instanceof GameStartedMessage) {
            GameStartedMessage startedMessage = (GameStartedMessage) message;
            getGameService().onGameStarted(startedMessage.getPlayer1(), startedMessage.getPlayer2());
        } else if (message instanceof NextStepPlayerChangedMessage) {
            NextStepPlayerChangedMessage stepMessage = (NextStepPlayerChangedMessage) message;
            getGameService().onNextStepPlayerChanged(stepMessage.getPlayers(), stepMessage.getNextStepPlayer());
        } else if (message instanceof CellMarkedMessage) {
            CellMarkedMessage cellMarkedMessage = (CellMarkedMessage) message;
            getGameService().onCellMarked(cellMarkedMessage.getPlayers(), cellMarkedMessage.getIndex(),
                    cellMarkedMessage.getMarkType());
        } else {
            unhandled(message);
        }
    }

    private GameService getGameService() {
        return BayeuxInitializer.getGameService();
    }

}
