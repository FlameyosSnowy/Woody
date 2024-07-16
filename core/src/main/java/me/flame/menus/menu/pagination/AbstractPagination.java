package me.flame.menus.menu.pagination;

import me.flame.lotte.LinkedConcurrentCache;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.AbstractMenu;
import me.flame.menus.menu.ConcurrencyProperties;
import me.flame.menus.menu.Menus;
import me.flame.menus.menu.OpenedType;
import me.flame.menus.menu.opener.MenuOpener;
import me.flame.menus.modifiers.Modifier;
import me.flame.menus.util.PaginatedContentsFactory;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractPagination<T> extends AbstractMenu implements Pagination<T> {
    @NotNull
    protected final Map<T, Page> pages;

    @NotNull
    protected final Map<Integer, MenuItem> pageItems;

    protected AbstractPagination(final int size, final int rows, final int pages, final Component title, @NotNull final EnumSet<Modifier> modifiers, @NotNull final MenuOpener opener, final Menus manager, @NotNull final ConcurrencyProperties properties, PaginatedContentsFactory<T> defaultPages) {
        super(size, rows, title, modifiers, opener, manager, properties);
        this.pageItems = properties.concurrentPageItems() ? new LinkedConcurrentCache<>(this.rows * 9) : new LinkedHashMap<>(this.rows * 9);
        Map<T, Page> loadedDefaultPages = defaultPages == null ? null : defaultPages.apply(this, properties);
        this.pages = loadedDefaultPages != null
                ? (properties.concurrentPages() ? new LinkedConcurrentCache<>(loadedDefaultPages) : new LinkedHashMap<>(loadedDefaultPages))
                : (properties.concurrentPages() ? new LinkedConcurrentCache<>(pages) : new LinkedHashMap<>(pages));
    }

    protected AbstractPagination(@NotNull final OpenedType type, final int pages, final Component title, @NotNull final EnumSet<Modifier> modifiers, final MenuOpener opener, final Menus manager, final ConcurrencyProperties properties, PaginatedContentsFactory<T> defaultPages) {
        super(type, title, modifiers, opener, manager, properties);
        this.pageItems = properties.concurrentPageItems() ? new LinkedConcurrentCache<>(this.rows * 9) : new LinkedHashMap<>(this.rows * 9);
        Map<T, Page> loadedDefaultPages = defaultPages == null ? null : defaultPages.apply(this, properties);
        this.pages = loadedDefaultPages != null
                ? (properties.concurrentPages() ? new LinkedConcurrentCache<>(loadedDefaultPages) : new LinkedHashMap<>(loadedDefaultPages))
                : (properties.concurrentPages() ? new LinkedConcurrentCache<>(pages) : new LinkedHashMap<>(pages));
    }
}
