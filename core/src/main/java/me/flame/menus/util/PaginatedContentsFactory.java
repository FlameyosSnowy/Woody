package me.flame.menus.util;

import me.flame.menus.menu.ConcurrencyProperties;
import me.flame.menus.menu.pagination.Page;
import me.flame.menus.menu.pagination.Pagination;

import java.util.Map;

@FunctionalInterface
public interface PaginatedContentsFactory<T> {
    Map<T, Page> apply(Pagination<T> var1, ConcurrencyProperties var2);
}
