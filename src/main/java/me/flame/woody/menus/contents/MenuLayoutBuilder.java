package me.flame.woody.menus.contents;

import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

import me.flame.woody.menus.item.MenuItem;
import net.kyori.adventure.text.Component;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Map;

@SuppressWarnings("unused")
public final class MenuLayoutBuilder {
    @NotNull
    private final Char2ObjectOpenHashMap<MenuItem> mappedButtons;

    @NotNull
    private String[] patterns;

    private int rows;

    MenuLayoutBuilder(@NotNull Map<Character, MenuItem> mappedButtons) {
        this.mappedButtons = new Char2ObjectOpenHashMap<>(mappedButtons);
        this.patterns = null;
        this.rows = 0;
    }

    public MenuLayoutBuilder set(char id, MenuItem mapped) {
        this.mappedButtons.put(id, mapped);
        return this;
    }

    public MenuLayoutBuilder pattern(String @NotNull... patterns) {
        if (this.patterns != null) return this;
        this.patterns = patterns;
        this.rows = patterns.length;
        return this;
    }

    /**
     * Creates a MenuLayout managed by BukkitContents and populates it with items.
     * @apiNote This is the recommended way.
     * @return The new MenuLayout
     */
    @NotNull
    @Contract(" -> new")
    public MenuLayout create() {
        return new MenuLayout(patterns, rows, mappedButtons);
    }

    private void validateData() {
        if (patterns == null) throw new IllegalStateException("No patterns specified. \nFix: use the pattern() method before creating the menu.");
        else if (rows > 6 || rows < 1) throw new IllegalStateException("Patterns array has too many or too low rows (" + rows + "). \nFix: Reduce/increase the amount of strings in the array of pattern()");
    }
}