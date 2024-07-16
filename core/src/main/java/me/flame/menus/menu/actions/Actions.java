package me.flame.menus.menu.actions;

import me.flame.menus.menu.Menu;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("unused")
public class Actions implements ActionManager<Menu> {
    private final Map<Class<?>, List<Action<? extends Event, ? extends Event, Menu>>> actions = new HashMap<>(5);
    private final Menu menu;

    public Actions(Menu menu) {
        this.menu = menu;

        // add the default inventory event classes
        actions.put(InventoryOpenEvent.class, new ArrayList<>(5));
        actions.put(InventoryCloseEvent.class, new ArrayList<>(5));
        actions.put(InventoryClickEvent.class, new ArrayList<>(5));
        actions.put(InventoryDragEvent.class, new ArrayList<>(5));
    }

    public <T extends Event, E extends Event> void addInventoryEvent(@NotNull Action<E, T, Menu> event) {
        this.actions.computeIfAbsent(event.executionArea(), (key) -> new ArrayList<>(5)).add(event);
    }

    public <T extends Event> void removeInventoryEvents(Class<T> eventClass) {
        actions.remove(eventClass);
    }

    @Override
    public Menu menu() {
        return menu;
    }

    /**
     *
     * @param event the event to supply the custom event
     * @return the cancelled events (if cancelled then it doesn't return the ignore cancel events)
     * @param <E> the custom event
     * @param <T> the event that has been provided
     */
    @SuppressWarnings("unchecked")
    public <E extends Event, T extends Event> Set<Class<?>> executeInventoryEventBy(@NotNull T event) {
        @SuppressWarnings("unchecked") // java is kinda stupid, don't mind java
        Class<T> executionArea = (Class<T>) event.getClass();
        boolean cancelled = false;
        Set<Class<?>> cancelledEvents = new HashSet<>(3);
        for (Action<? extends Event, ? extends Event, Menu> unchecked : this.actions.get(executionArea)) {
            if (cancelled && !unchecked.ignoreCancelled()) {
                cancelledEvents.add(unchecked.getClass());
                continue;
            }

            Action<E, T, Menu> action = ((Action<E, T, Menu>) unchecked).copy();

            E executedEvent = null;
            if (action.canExecute(event, menu)) {
                executedEvent = action.createEvent(event, menu);
                action.executeEvent(executedEvent, menu);
            }
            cancelled = executedEvent instanceof Cancellable cancellable && cancellable.isCancelled();

            if (event instanceof Cancellable cancellable
                    && executedEvent instanceof Cancellable cancellableEvent
                    && cancellableEvent.isCancelled()) {
                cancellable.setCancelled(true);
                cancelledEvents.add(cancellableEvent.getClass());
                action.executeCancellationEvent(executedEvent, menu);
            }
        }
        return cancelledEvents;
    }

    public <T> boolean hasActionList(Class<T> actionType) {
        return actions.containsKey(actionType);
    }
}