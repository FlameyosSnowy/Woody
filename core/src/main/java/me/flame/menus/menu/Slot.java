package me.flame.menus.menu;

import com.google.errorprone.annotations.CompileTimeConstant;

import org.jetbrains.annotations.NotNull;

/**
 * Slot wrapper of a (row, column) pair to represent a slot in a menu.
 * <p>
 * Fail-fast implementation.
 * @author FlameyosFlow
 */
@SuppressWarnings("unused")
public class Slot {
    public int row() {
        return row;
    }

    public int column() {
        return column;
    }

    private final int row, column, slot;
    /**
     * First as in row 1, col 1.
     * Slot = 0 (always)
     */
    @CompileTimeConstant
    public static final Slot FIRST = getFirst();

    /**
     * Not A Slot; a purposeful invalid slot to save performance and memory.
     */
    @CompileTimeConstant
    public static final Slot NaS = getNaS();

    public Slot(int slot) {
        this(slot > 54 || slot < 0 ? -1 :
                slot / 9,
            slot > 54 || slot < 0 ? -1 :
                slot % 9,
                slot);
    }

    public Slot(int row, int column) {
        this(row, column, (column + (row - 1) * 9) - 1);
    }

    private Slot(int row, int column, int slot) {
        this.row = row;
        this.column = column;
        this.slot = slot;
    }

    public int slot() {
        return slot;
    }

    // faster NaS (Not A Slot) alternative to creating a Slot like new Slot(8, 1);
    @NotNull
    private static Slot getNaS() {
        return new Slot(-1, -1, 1);
    }

    //FIRST slot compared to doing "new Slot(1, 1)"
    @NotNull
    public static Slot getFirst() {
        return new Slot(1, 1, 0);
    }

    public boolean isValid() {
        return slot != -1;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Slot index)) return false;
        return (index == this) || (index.row == row && index.column == column);
    }

    public boolean equals(int slot) {
        return slot % 9 == column && slot / 9 == row;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + row;
        result = 31 * result + column;
        return result;
    }
}