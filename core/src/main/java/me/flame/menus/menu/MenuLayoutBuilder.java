package me.flame.menus.menu;

import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;

import me.flame.menus.menu.opener.MenuOpener;
import me.flame.menus.modifiers.Modifier;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.contents.*;

import net.kyori.adventure.text.Component;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Map;

@SuppressWarnings("unused")
public final class MenuLayoutBuilder {
    @NotNull
    private final Map<Character, MenuItem> mappedButtons;

    @NotNull
    private String[] patterns;

    private int rows;

    public MenuLayoutBuilder(@NotNull Map<Character, MenuItem> mappedButtons) {
        this.mappedButtons = new Char2ObjectOpenHashMap<>(mappedButtons);
        this.patterns = null;
        this.rows = 0;
    }

    public MenuLayoutBuilder set(char id, MenuItem mapped) {
        this.mappedButtons.put(id, mapped);
        return this;
    }

    @Contract("_ -> this")
    @SafeVarargs
    public final MenuLayoutBuilder setAll(Map.Entry<Character, MenuItem> @NotNull ... entries) {
        for (Map.Entry<Character, MenuItem> entry : entries) this.mappedButtons.put(entry.getKey(), entry.getValue());
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

    /**
     * Creates a menu with the given title and populates it with items.
     *
     * @param  title  the title of the menu
     * @return        the created menu
     */
    public @NotNull Menu createMenu(String title, Menus menus) {
        return createMenu(Component.text(title), menus);
    }

    /**
     * Creates a menu with the given title and populates it with items.
     *
     * @param  title  the title of the menu
     * @return        the created menu
     */
    @Contract("_, _, _ -> new")
    public @NotNull Menu createMenu(String title, EnumSet<Modifier> modifiers, Menus menus) {
        return createMenu(Component.text(title), modifiers, menus);
    }

    /**
     * Creates a menu with the given title and populates it with items.
     *
     * @param  title  the title of the menu
     * @return        the created menu
     */
    public @NotNull Menu createMenu(Component title, Menus menus) {
        this.validateData();
        return new MenuImpl(rows, title, EnumSet.noneOf(Modifier.class), menus, MenuOpener.DEFAULT, (loadedMenu, properties)  -> this.createContents(loadedMenu, properties.concurrentContents()));
    }

    /**
     * Creates a menu with the given title and populates it with items.
     *
     * @param  title  the title of the menu
     * @return        the created menu
     */
    @Contract("_, _, _ -> new")
    public @NotNull Menu createMenu(Component title, EnumSet<Modifier> modifiers, Menus menus) {
        this.validateData();
        return new MenuImpl(rows, title, modifiers, menus, MenuOpener.DEFAULT, (loadedMenu, properties)  -> this.createContents(loadedMenu, properties.concurrentContents()));
    }

    public BukkitContents createContents(Menu menu, boolean concurrent) {
        this.validateData();
        return Contents.builder(menu)
                .iterate((contents, buttonIndex) -> {
                    MenuItem item = this.mappedButtons.get(patterns[buttonIndex.row()].charAt(buttonIndex.column()));
                    if (item != null) contents.setItem(buttonIndex, item);
                })
                .create();
    }

    public BukkitContents createContents(Menu menu) {
        return this.createContents(menu, false);
    }

    private void validateData() {
        if (patterns == null) throw new IllegalStateException("No patterns specified. \nFix: use the pattern() method before creating the menu.");
        else if (rows > 6 || rows < 1) throw new IllegalStateException("Patterns array has too many or too low rows (" + rows + "). \nFix: Reduce/increase the amount of strings in the array of pattern()");
    }
}
