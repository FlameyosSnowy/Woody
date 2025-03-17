package me.flame.woody.menus.item;

import me.flame.woody.menus.internals.MenuView;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface ActionResponse {
    void accept(MenuView view, InventoryClickEvent event);
}
