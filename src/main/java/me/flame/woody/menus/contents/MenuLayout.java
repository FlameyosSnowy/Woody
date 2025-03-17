package me.flame.woody.menus.contents;

import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import me.flame.woody.menus.item.MenuItem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class MenuLayout {
    @NotNull
    private final String[] patterns;

    private final int rows;

    @NotNull
    private final Map<Character, MenuItem> mappedButtons;

    public MenuLayout(final @NotNull String[] patterns, final int rows, final @NotNull Map<Character, MenuItem> mappedButtons) {
        validateData(patterns, rows);
        this.patterns = patterns;
        this.rows = rows;
        this.mappedButtons = mappedButtons;
    }

    @Contract(" -> new")
    public static @NotNull MenuLayoutBuilder builder() {
        return new MenuLayoutBuilder(new Char2ObjectOpenHashMap<>());
    }

    private static void validateData(final @NotNull String[] patterns, final int rows) {
        if (rows > 6) throw new IllegalArgumentException("Too big of a menu.");
        if (rows < 1) throw new IllegalArgumentException("Too small of a menu.");
        Objects.requireNonNull(patterns, "Pattern array must NOT be null.");
        Objects.checkFromToIndex(1, 6, patterns.length);
        for (String line : patterns) Objects.checkFromToIndex(1, 6, line.length());
    }

    public MenuItem getMappedItem(int row, int column) {
        Objects.checkFromToIndex(1, rows, row);
        Objects.checkFromToIndex(1, 9, column);
        return Objects.requireNonNull(
                this.mappedButtons.get(patterns[row].charAt(column)),
                "This shall not come out null."
        );
    }
}