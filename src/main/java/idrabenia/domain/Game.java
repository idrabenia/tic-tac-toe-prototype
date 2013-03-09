package idrabenia.domain;

import idrabenia.domain.table.CellState;
import idrabenia.domain.table.Table;

import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 09.03.13
 */
public class Game {
    private final String player1;
    private volatile String player2;
    private volatile GameState state = GameState.UPCOMING;
    private final Table table = new Table();
    private volatile String nextStepPlayer;

    private final PropertyChangeSupport publisher = new PropertyChangeSupport(this);

    public Game(String player1Value) {
        player1 = player1Value;
        nextStepPlayer = player1;
    }

    public String getPlayer1() {
        return player1;
    }

    public String getPlayer2() {
        return player2;
    }

    public synchronized void setPlayer2(String player2Value) {
        if (this.player2 == null) {
            this.player2 = player2Value;

            state = GameState.RUNNING;
            publisher.firePropertyChange("gameStarted", false, true);
        }
    }

    public Table getTable() {
        return table;
    }

    public PropertyChangeSupport getPublisher() {
        return publisher;
    }

    public synchronized void markCell(String player, int number) {
        if (!nextStepPlayer.equals(player) || state != GameState.RUNNING) {
            return;
        }

        table.markCell(number, getNewValue(player));

        checkWinCombinations();

        computeNextStepPlayer();
    }

    public CellState getNewValue(String player) {
        CellState newState = null;
        if (player1.equals(player)) {
            newState = CellState.X;
        } else if (player2.equals(player)) {
            newState = CellState.ZERO;
        } else {
            throw new IllegalStateException();
        }

        return newState;
    }

    public synchronized void checkWinCombinations() {
        if (table.isGameFinished()) {
            state = GameState.FINISHED;

            publisher.firePropertyChange("gameFinished", false, true);
        }
    }

    public String getWinnerName() {
        if (table.getWinnerNumber() == null) {
            return null;
        }

        switch (table.getWinnerNumber()) {
            case 1:
                return player1;
            case 2:
                return player2;
            default:
                return null;
        }
    }

    public synchronized void computeNextStepPlayer() {
        if (nextStepPlayer.equals(player1)) {
            nextStepPlayer = player2;
            publisher.firePropertyChange("nextStepPlayer", player1, player2);
        } else {
            nextStepPlayer = player1;
            publisher.firePropertyChange("nextStepPlayer", player2, player1);
        }
    }

    public GameState getState() {
        return state;
    }

    public String getNextStepPlayer() {
        return nextStepPlayer;
    }

    public List<String> getPlayers() {
        return Arrays.asList(player1, player2);
    }
}
