package me.flame.menus.menu.actions;

import me.flame.menus.events.BeforeAnimatingEvent;
import me.flame.menus.events.MenuCloseEvent;
import me.flame.menus.events.PageChangeEvent;
import me.flame.menus.menu.Menu;
import me.flame.menus.menu.api.MenuActionModifiable;
import me.flame.menus.menu.api.SimpleMenu;
import me.flame.menus.menu.pagination.IndexedPagination;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.BiConsumer;

public interface ActionManager<M extends SimpleMenu> {
    <T extends Event, E extends Event> void addInventoryEvent(@NotNull Action<E, T, M> event);

    <T extends Event> void removeInventoryEvents(Class<T> eventClass);

    M menu();

    default void addDefaultClickAction(BiConsumer<InventoryClickEvent, M> eventAction) {
        addInventoryEvent(
                Action.builder(InventoryClickEvent.class, InventoryClickEvent.class, this.menu())
                        .customEvent((event, menu) -> event)
                        .action(eventAction)
                        .build()
        );
    }

    default void addTopClickAction(BiConsumer<InventoryClickEvent, M> eventAction) {
        addInventoryEvent(
                Action.builder(InventoryClickEvent.class, InventoryClickEvent.class, this.menu())
                        .customEvent((event, menu) -> event)
                        .action(eventAction)
                        .executeIf((e, func) -> {
                            Inventory clickedInventory = e.getClickedInventory();
                            return (clickedInventory != null) && clickedInventory.equals(e.getView().getTopInventory());
                        })
                        .build()
        );
    }

    default void addOpenClickAction(BiConsumer<InventoryOpenEvent, M> eventAction) {
        addInventoryEvent(
                Action.builder(InventoryOpenEvent.class, InventoryOpenEvent.class, this.menu())
                        .customEvent((event, menu) -> event)
                        .action(eventAction)
                        .build()
        );
    }

    default void addCloseClickAction(BiConsumer<MenuCloseEvent, M> eventAction) {
        addInventoryEvent(
                Action.builder(MenuCloseEvent.class, InventoryCloseEvent.class, this.menu())
                        .customEvent((event, menu) -> new MenuCloseEvent(event.getView()))
                        .action(eventAction)
                        .build()
        );
    }

    default void addDragClickAction(BiConsumer<InventoryDragEvent, M> eventAction) {
        addInventoryEvent(
                Action.builder(InventoryDragEvent.class, InventoryDragEvent.class, this.menu())
                        .customEvent((event, menu) -> event)
                        .action(eventAction)
                        .build()
        );
    }

    default void addBottomClickAction(BiConsumer<InventoryClickEvent, M> eventAction) {
        addInventoryEvent(
                Action.builder(InventoryClickEvent.class, InventoryClickEvent.class, this.menu())
                        .customEvent((event, menu) -> event)
                        .action(eventAction)
                        .executeIf((e, func) -> {
                            Inventory clickedInventory = e.getClickedInventory();
                            return (clickedInventory != null) &&  clickedInventory.equals(e.getView().getBottomInventory());
                        })
                        .build()
        );
    }

    default void addAnimateAction(BiConsumer<BeforeAnimatingEvent, M> eventAction) {
        addInventoryEvent(
                Action.builder(BeforeAnimatingEvent.class, InventoryOpenEvent.class, this.menu())
                        .customEvent((event, menu) ->
                                new BeforeAnimatingEvent((Player) event.getPlayer(), (Menu) event.getInventory().getHolder()))
                        .action(eventAction)
                        .executeIf((e, func) -> e.getViewers().isEmpty())
                        .build()
        );
    }

    default void addOnPageAction(BiConsumer<PageChangeEvent, M> eventAction) {
        addInventoryEvent(
                Action.builder(PageChangeEvent.class, InventoryClickEvent.class, menu())
                        .customEvent((event, menu) -> ((IndexedPagination) menu).createPageEvent(event))
                        .action(eventAction)
                        .executeIf((e, menu) -> menu instanceof IndexedPagination pagination && pagination.pageChangingAction(e.getSlot()))
                        .build()
        );
    }

    /**
     *
     * @param event the event to supply the custom event
     * @param menu the menu required to execute the action
     * @return the cancelled events (if cancelled then it doesn't return the ignore cancel events)
     * @param <E> the custom event
     * @param <T> the event that has been provided
     */
    <E extends Event, T extends Event> Set<Class<?>> executeInventoryEventBy(@NotNull T event);

    <T> boolean hasActionList(Class<T> actionType);
}
