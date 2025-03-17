package me.flame.woody;

import me.flame.woody.api.Menu;
import me.flame.woody.events.CloseResult;
import me.flame.woody.internals.MenuView;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MenuListeners implements Listener {
    private final MenuManager manager;

    public MenuListeners(MenuManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        MenuView view = this.manager.getOpenedMenu(event.getWhoClicked().getUniqueId());
        if (view != null) {
            Menu menu = view.getMenu();
            menu.onPreClick(view, event);
            if (!event.isCancelled()) view.getMenu().onPostClick(view, event);
        }
    }

    @EventHandler
    public void onInventoryClose(@NotNull InventoryCloseEvent event) {
        UUID id = event.getPlayer().getUniqueId();
        MenuView view = this.manager.getOpenedMenu(id);

        if (view == null) {
            return;
        }

        CloseResult result = new CloseResult();
        view.getMenu().onClose(view, event, result);

        if (!result.isCancelled()) {
            this.manager.removeOpenedMenu(id);
            return;
        }

        manager.openMenu((Player) event.getPlayer(), view);
    }

    @EventHandler
    public void onInventoryOpen(@NotNull InventoryOpenEvent event) {
        MenuView view = this.manager.getOpenedMenu(event.getPlayer().getUniqueId()); // added before inventory is opened
        if (view != null) view.getMenu().onOpen(view, event);
    }

    @EventHandler
    public void onInventoryDrag(@NotNull InventoryDragEvent event) {
        MenuView view = this.manager.getOpenedMenu(event.getWhoClicked().getUniqueId());
        if (view != null) view.getMenu().onDrag(view, event);
    }
}
