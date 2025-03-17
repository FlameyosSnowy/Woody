package me.flame.woody.menus;

public class Structure {
    private final int rows;
    private final int columns;
    private final int size;

    public Structure(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.size = rows * columns;
    }

    public Structure(int rows) {
        this(rows, 9);
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public int getSize() {
        return size;
    }
}
