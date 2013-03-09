package idrabenia.domain.table;

/**
 * @author Ilya Drabenia
 * @since 09.03.13
 */
public enum CellState {
    EMPTY(" "), X("X"), ZERO("0");

    private String value;

    CellState(String value) {
        this.value = value;
    }

    public String getStringValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
