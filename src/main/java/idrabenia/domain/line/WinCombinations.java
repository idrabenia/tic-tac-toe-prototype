package idrabenia.domain.line;

import idrabenia.domain.table.Cell;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Ilya Drabenia
 * @since 09.03.13
 */
public class WinCombinations {
    private final List<Line> lines;

    public WinCombinations(Line... linesValue) {
        lines = Collections.unmodifiableList(Arrays.asList(linesValue));
    }

    public WinCombinations(List<Cell> cells) {
        this(
                new Line(cells.get(0), cells.get(1), cells.get(2)),
                new Line(cells.get(3), cells.get(4), cells.get(5)),
                new Line(cells.get(6), cells.get(7), cells.get(8)),
                new Line(cells.get(0), cells.get(3), cells.get(6)),
                new Line(cells.get(1), cells.get(4), cells.get(7)),
                new Line(cells.get(2), cells.get(5), cells.get(8)),
                new Line(cells.get(0), cells.get(4), cells.get(8)),
                new Line(cells.get(3), cells.get(4), cells.get(5)),
                new Line(cells.get(6), cells.get(4), cells.get(2))
        );
    }

    public boolean hasWinCombination() {
        boolean hasWinLine = false;

        for (Line curLine : lines) {
            if (curLine.hasWinCombination()) {
                hasWinLine = true;
            }
        }

        return hasWinLine;
    }

    public Integer getWinnerNumber() {
        Integer winnerNumber = null;

        for (Line curLine : lines) {
            if (curLine.getWinnerNumber() != null) {
                winnerNumber = curLine.getWinnerNumber();
            }
        }

        return winnerNumber;
    }

}
