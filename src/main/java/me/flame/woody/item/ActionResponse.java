package me.flame.woody.item;

import me.flame.woody.internals.MenuView;

import org.bukkit.event.inventory.InventoryClickEvent;

@FunctionalInterface
public interface ActionResponse {
    void accept(MenuView view, InventoryClickEvent event);
}
