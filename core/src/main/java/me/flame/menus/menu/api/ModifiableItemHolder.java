package me.flame.menus.menu.api;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Slot;

public interface ModifiableItemHolder extends ItemHolder {
    void setItem(int slot, MenuItem item);

    void setItem(Slot slot, MenuItem item);
}
