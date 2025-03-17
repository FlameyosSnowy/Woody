package me.flame.woody.api;

import me.flame.woody.Structure;
import me.flame.woody.contents.Contents;
import me.flame.woody.events.CloseResult;
import me.flame.woody.internals.MenuView;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

public interface Menu {
    Structure getStructure(Player player);

    Contents getContents(Player player, MenuView view, Structure structure);

    Component getTitle(Player player);

    default void onPreClick(MenuView menuView, InventoryClickEvent event) {
    }

    default void onPostClick(MenuView menuView, InventoryClickEvent event) {
    }

    default void onClose(MenuView menuView, InventoryCloseEvent event, CloseResult result) {
    }

    default void onOpen(MenuView menuView, InventoryOpenEvent event) {
    }

    default void onDrag(MenuView menuView, InventoryDragEvent event) {
    }
}
