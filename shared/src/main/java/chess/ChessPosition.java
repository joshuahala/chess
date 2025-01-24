package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    int thisRow;
    int thisCol;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        return thisRow == that.thisRow && thisCol == that.thisCol;
    }

    @Override
    public int hashCode() {
        return Objects.hash(thisRow, thisCol);
    }

    public ChessPosition(int row, int col) {
        thisRow = row;
        thisCol = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return thisRow;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return thisCol;
    }
}
