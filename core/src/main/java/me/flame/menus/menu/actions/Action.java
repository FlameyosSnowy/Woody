package me.flame.menus.menu.actions;

import me.flame.menus.menu.Menu;
import me.flame.menus.menu.api.MenuActionModifiable;
import me.flame.menus.menu.api.SimpleMenu;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.*;

@SuppressWarnings("unused")
public record Action<E extends Event, T extends Event, M extends SimpleMenu>(
     M menu,
     BiConsumer<E, M> action,
     BiFunction<T, M, E> customEventSupplier,
     Class<T> executionArea,
     BiPredicate<T, M> executeIf,
     BiConsumer<E, M> onCancel,
     boolean ignoreCancelled,
     boolean async
) {
    public static <E extends Event, T extends Event, M extends SimpleMenu> ActionBuilder<E, T, M> builder(Class<E> customEvent, Class<T> providedEvent, M menu) {
        return new ActionBuilder<>(customEvent, providedEvent, menu);
    }

    /**
     * Creates the event.
     * @param event the event that gets mapped to the new event.
     * @return the new event
     */
    public E createEvent(T event, M menu) {
        return customEventSupplier.apply(event, menu);
    }

    /**
     * Executes the action from the mapped event.
     * <p>
     * If "async" is set to "true", it will run in a CompletableFuture
     * @param event the event.
     */
    public void executeEvent(E event, M menu) {
        if (event == null) return;
        if (async) CompletableFuture.runAsync(() -> action.accept(event, menu));
        else action.accept(event, menu);
    }

    public boolean canExecute(T event, M menu) {
        return executeIf.test(event, menu);
    }

    public void executeCancellationEvent(E event, M menu) {
        onCancel.accept(event, menu);
    }

    @Contract(" -> new")
    public @NotNull Action<E, T, M> copy() {
        return new Action<>(menu, action, customEventSupplier, executionArea, executeIf, onCancel, ignoreCancelled, async);
    }

    public static class ActionBuilder<E extends Event, T extends Event, M extends SimpleMenu> {
        BiConsumer<E, M> eventConsumer;
        BiFunction<T, M, E> customEventSupplier;
        Class<T> executionArea;
        BiPredicate<T, M> executeIf = (unmappedEvent, menu) -> true;
        BiConsumer<E, M> onCancelAction = (event, menu) -> {};
        boolean ignoreCancelled = false;
        boolean async = false;

        M menu;

        ActionBuilder(Class<E> customEvent, Class<T> providedEvent, M menu) {
            Objects.requireNonNull(customEvent);
            this.executionArea = Objects.requireNonNull(providedEvent);
        }

        public ActionBuilder<E, T, M> action(BiConsumer<E, M> eventConsumer) {
            this.eventConsumer = eventConsumer;
            return this;
        }

        public ActionBuilder<E, T, M> customEvent(BiFunction<T, M, E> customEventSupplier) {
            this.customEventSupplier = customEventSupplier;
            return this;
        }

        public ActionBuilder<E, T, M> executeIf(BiPredicate<T, M> executeWhen) {
            this.executeIf = executeWhen;
            return this;
        }

        public ActionBuilder<E, T, M> onCancelAction(BiConsumer<E, M> onCancelAction) {
            this.onCancelAction = onCancelAction;
            return this;
        }

        public ActionBuilder<E, T, M> async(boolean async) {
            this.async = async;
            return this;
        }

        public ActionBuilder<E, T, M> ignoreCancelled(boolean ignoreCancelled) {
            this.ignoreCancelled = ignoreCancelled;
            return this;
        }

        public Action<E, T, M> build() {
            return new Action<>(menu, eventConsumer, customEventSupplier, executionArea, executeIf, onCancelAction, ignoreCancelled, async);
        }
    }
}
