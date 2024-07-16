package me.flame.menus.menu.pagination;

import me.flame.menus.menu.contents.BukkitContents;
import org.jetbrains.annotations.NotNull;

public class Page {
    @NotNull
    private BukkitContents contents;

    @NotNull
    private final Pagination<?> menu;

    public Page(final @NotNull Pagination<?> menu, final @NotNull BukkitContents contents) {
        this.menu = menu;
        this.contents = contents;
    }

    public @NotNull BukkitContents contents() {
        return contents;
    }

    public void replaceContents(final BukkitContents contents) {
        this.contents = contents;
    }
}
