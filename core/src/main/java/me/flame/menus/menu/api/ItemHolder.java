package me.flame.menus.menu.api;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Slot;

import java.util.Optional;

public interface ItemHolder {
    Optional<MenuItem> getItem(int slot);

    Optional<MenuItem> getItem(Slot slot);
}
