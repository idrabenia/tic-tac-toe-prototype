package idrabenia.domain.table;

import idrabenia.domain.line.WinCombinations;

import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 09.03.13
 */
public class Table {
    private final List<Cell> cells = Collections.unmodifiableList(Arrays.asList(
            new Cell(1), new Cell(2), new Cell(3),
            new Cell(4), new Cell(5), new Cell(6),
            new Cell(7), new Cell(8), new Cell(9)
    ));

    private final WinCombinations winCombinations;

    private final PropertyChangeSupport publisher = new PropertyChangeSupport(this);

    public Table() {
        winCombinations = new WinCombinations(cells);
    }

    public boolean hasWinCombination() {
        return winCombinations.hasWinCombination();
    }

    public boolean isGameFinished() {
        boolean hasEmptyCells = false;

        for (Cell curCell : cells) {
            if (curCell.getState() == CellState.EMPTY) {
                hasEmptyCells = true;
            }
        }

        return !hasEmptyCells || hasWinCombination();
    }

    public Integer getWinnerNumber() {
        return winCombinations.getWinnerNumber();
    }

    public void markCell(int number, CellState newState) {
        Cell curCell = cells.get(number - 1);
        if (curCell.getState() != CellState.EMPTY) {
            return;
        }

        curCell.setState(newState);
        publisher.fireIndexedPropertyChange("cells", number, CellState.EMPTY, newState);
    }

    public PropertyChangeSupport getPublisher() {
        return publisher;
    }
}
