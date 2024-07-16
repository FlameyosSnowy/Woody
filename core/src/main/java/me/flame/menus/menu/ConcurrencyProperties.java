package me.flame.menus.menu;

import me.flame.menus.menu.pagination.Pagination;

public record ConcurrencyProperties(boolean concurrentContents, boolean concurrentPageItems, boolean concurrentPages) {
    public static final ConcurrencyProperties EMPTY = new ConcurrencyProperties(false, false, false);
    public static final ConcurrencyProperties CONCURRENT_MENU = new ConcurrencyProperties(true, false, false);
    public static final ConcurrencyProperties FULL = new ConcurrencyProperties(true, true, true);

    public boolean isFullyConcurrent(Menu menu) {
        return concurrentContents;
    }

    public boolean isFullyConcurrent(Pagination<?> menu) {
        return concurrentContents && concurrentPageItems && concurrentPages;
    }
}
