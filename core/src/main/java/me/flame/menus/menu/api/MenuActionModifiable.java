package me.flame.menus.menu.api;

import me.flame.menus.events.BeforeAnimatingEvent;
import me.flame.menus.events.MenuCloseEvent;
import me.flame.menus.menu.actions.ActionManager;
import me.flame.menus.menu.actions.Actions;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface MenuActionModifiable<M extends SimpleMenu> {
    /**
     * The action manager for the Menu.
     * @return the action manager.
     */
    ActionManager<M> actions();

    default void setClickAction(BiConsumer<InventoryClickEvent, M> eventAction) {
        actions().addDefaultClickAction(eventAction);
    }

    default void setTopClickAction(BiConsumer<InventoryClickEvent, M> eventAction) {
        actions().addTopClickAction(eventAction);
    }

    default void setOpenAction(BiConsumer<InventoryOpenEvent, M> eventAction) {
        actions().addOpenClickAction(eventAction);
    }

    default void setCloseAction(BiConsumer<MenuCloseEvent, M> eventAction) {
        actions().addCloseClickAction(eventAction);
    }

    default void setDragAction(BiConsumer<InventoryDragEvent, M> eventAction) {
        actions().addDragClickAction(eventAction);
    }

    default void setBottomClickAction(BiConsumer<InventoryClickEvent, M> eventAction) {
        actions().addBottomClickAction(eventAction);
    }

    default void setOnAnimate(BiConsumer<BeforeAnimatingEvent, M> eventAction) {
        actions().addAnimateAction(eventAction);
    }
}
