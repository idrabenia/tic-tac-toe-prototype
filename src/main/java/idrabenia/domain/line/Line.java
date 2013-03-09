package idrabenia.domain.line;

import idrabenia.domain.table.Cell;
import idrabenia.domain.table.CellState;

/**
 * @author Ilya Drabenia
 * @since 09.03.13
 */
public class Line {
    private Cell cell1;
    private Cell cell2;
    private Cell cell3;

    public Line(Cell cell1, Cell cell2, Cell cell3) {
        this.cell1 = cell1;
        this.cell2 = cell2;
        this.cell3 = cell3;
    }

    public boolean hasWinCombination() {
        return cell1.getState() != CellState.EMPTY
                && (cell1.getState() == cell2.getState() && cell2.getState() == cell3.getState());
    }

    public Integer getWinnerNumber() {
        if (!hasWinCombination()) {
            return null;
        }

        if (cell1.getState() == CellState.X) {
            return 1;
        } else {
            return 2;
        }
    }

}
