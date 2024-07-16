package me.flame.menus.util;

import me.flame.menus.menu.ConcurrencyProperties;
import me.flame.menus.menu.Menu;
import me.flame.menus.menu.contents.BukkitContents;

@FunctionalInterface
public interface ContentsFactory {
    BukkitContents create(Menu menu, ConcurrencyProperties concurrencyProperties);
}
