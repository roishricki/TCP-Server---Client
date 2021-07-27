package Matrix;

import java.util.Objects;

public class Index{
    int row, column;

    public Index(final int row, final int column) {
        this.row=row;
        this.column=column;
    }

    @Override
    /**
     *  it checks if the values of the indices are equal.
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Index index = (Index) o;
        return row == index.row &&
                column == index.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "("+row +
                "," + column +
                ')';
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}