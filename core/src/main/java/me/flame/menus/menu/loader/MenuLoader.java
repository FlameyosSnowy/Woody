package me.flame.menus.menu.loader;

import me.flame.menus.menu.Menu;
import me.flame.menus.menu.*;

import me.flame.menus.menu.contents.BukkitContents;
import me.flame.menus.menu.opener.MenuOpener;
import net.kyori.adventure.text.Component;

public interface MenuLoader {
    Component title();

    Structure structure();

    default MenuOpener opener() {
        return MenuOpener.DEFAULT;
    }

    default ConcurrencyProperties concurrencyProperties() {
        return ConcurrencyProperties.EMPTY;
    }

    void setup(Menu menu);

    BukkitContents load(Menu menu);
}
