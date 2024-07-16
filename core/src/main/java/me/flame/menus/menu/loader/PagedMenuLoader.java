package me.flame.menus.menu.loader;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.ConcurrencyProperties;
import me.flame.menus.menu.Structure;
import me.flame.menus.menu.contents.BukkitContents;
import me.flame.menus.menu.opener.MenuOpener;
import me.flame.menus.menu.pagination.IndexedPagination;
import me.flame.menus.menu.pagination.Page;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public interface PagedMenuLoader {
    Component title();

    Structure structure();

    default MenuOpener opener() {
        return MenuOpener.DEFAULT;
    }

    default ConcurrencyProperties concurrencyProperties() {
        return ConcurrencyProperties.EMPTY;
    }

    void setup(IndexedPagination menu);

    Map<Integer, Page> load(IndexedPagination menu);

    int pageCount();

    Map.Entry<Integer, MenuItem> nextItem();

    Map.Entry<Integer, MenuItem> previousItem();
}
