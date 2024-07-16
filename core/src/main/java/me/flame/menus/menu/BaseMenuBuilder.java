package me.flame.menus.menu;

import me.flame.menus.menu.contents.BukkitContents;
import me.flame.menus.menu.opener.MenuOpener;
import me.flame.menus.modifiers.Modifier;

import net.kyori.adventure.text.Component;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.function.Function;

@SuppressWarnings({ "unchecked", "unused" })
public class BaseMenuBuilder<T extends BaseMenuBuilder<T>> {
    protected Component title;

    @NotNull
    protected OpenedType type = MenuType.CHEST;

    @NotNull
    protected final EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);

    @NotNull
    protected final Menus menus;

    @NotNull
    protected MenuOpener opener = MenuOpener.DEFAULT;

    @NotNull
    protected ConcurrencyProperties properties = ConcurrencyProperties.EMPTY;

    protected int rows;

    public BaseMenuBuilder(final @NotNull Menus menus, int rows) {
        checkRows(rows);
        this.menus = menus;
        this.rows = rows;
    }

    public BaseMenuBuilder(final @NotNull Menus menus) {
        this.menus = menus;
        this.rows = 1;
    }

    /**
     * Sets the title of the menu.
     *
     * @param  title  the title to be set
     * @return        the builder for chaining
     */
    @ApiStatus.Obsolete
    public T title(String title) {
        this.title = Component.text(title);
        return (T) this;
    }

    /**
     * Sets the title of the menu.
     *
     * @param  title  the title to be set
     * @return        the builder for chaining
     */
    public T title(Component title) {
        this.title = title;
        return (T) this;
    }

    /**
     * Sets the amount of rows of the menu.
     *
     * @param  rows  the amount of rows to be set
     * @return        the builder for chaining
     */
    public T rows(int rows) {
        checkRows(rows);
        this.rows = rows;
        return (T) this;
    }

    /**
     * Sets the type of the menu.
     * @return the builder for chaining
     */
    public T type(OpenedType type) {
        this.type = type;
        return (T) this;
    }

    /**
     * Sets the concurrency properties of the menu.
     * @return the builder for chaining
     */
    public T properties(final @NotNull ConcurrencyProperties properties) {
        this.properties = properties;
        return (T) this;
    }


    /**
     * Sets the opener of the menu.
     * @return the builder for chaining
     */
    public T opener(MenuOpener opener) {
        this.opener = opener;
        return (T) this;
    }

    /**
     * Adds a modifier to the list of modifiers.
     *
     * @param  modifier  the modifier to be added
     * @return           the builder for chaining
     */
    public T addModifier(Modifier modifier) {
        modifiers.add(modifier);
        return (T) this;
    }

    /**
     * Remove a modifier from the list of modifiers.
     *
     * @param  modifier  the modifier to be removed
     * @return           the builder for chaining
     */
    public T removeModifier(Modifier modifier) {
        modifiers.remove(modifier);
        return (T) this;
    }

    /**
     * Add all the modifiers of {@link Modifier} to the list of modifiers.
     * @return the builder for chaining
     */
    public T addAllModifiers() {
        modifiers.addAll(Modifier.ALL);
        return (T) this;
    }

    static void checkRows(int rows) {
        if (rows >= 1 && rows <= 6) return;
        throw new IllegalArgumentException("Rows must be more than 1 or 6 and less" + "\nRows: " + rows + "\nFix: Rows must be 1-6");
    }

    static void checkRequirements(int rows, Component title) {
        checkRows(rows);
        if (title != null) return;
        throw new IllegalArgumentException("""
                Title must not be null or empty
                Title equals null
                Fix: Title must not be null or empty
        """);
    }
}
