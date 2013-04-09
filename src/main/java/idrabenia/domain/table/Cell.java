package idrabenia.domain.table;

/**
 * @author Ilya Drabenia
 * @since 09.03.13
 */
public class Cell {
    private int number;
    private CellState state;

    public Cell(int number) {
        this.number = number;
        state = CellState.EMPTY;
    }

    public int getNumber() {
        return number;
    }

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }
}
