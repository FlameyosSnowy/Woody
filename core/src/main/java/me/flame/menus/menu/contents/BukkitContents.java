package me.flame.menus.menu.contents;

import me.flame.menus.menu.Menu;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Slot;
import me.flame.menus.menu.api.ModifiableItemHolder;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface BukkitContents extends ModifiableItemHolder {
    default void setItems(List<Integer> slots, MenuItem item) {
        for (int slot : slots) this.setItem(slot, item);
    }

    default void setItems(int[] slots, MenuItem item) {
        for (int slot : slots) this.setItem(slot, item);
    }

    Map<Integer, MenuItem> getItems();

    Map<Integer, MenuItem> getMutableItems();

    void refreshItem(int index);

    default void refreshItem(Slot index) {
        validateSlot(index.slot(), size());
        refreshItem(index.slot());
    }

    void refreshItem(int index, Supplier<MenuItem> item);

    default void refreshItem(Slot index, Supplier<MenuItem> item) {
        validateSlot(index.slot(), size());
        refreshItem(index.slot());
    }

    default void addRefreshableItem(Slot index, Supplier<MenuItem> item) {
        validateSlot(index.slot(), size());
        this.addRefreshableItem(index.slot(), item);
    }

    default void removeRefreshableItem(Slot index) {
        validateSlot(index.slot(), size());
        this.removeRefreshableItem(index.slot());
    }

    /**
     * Get a stream loop of the items in the menu
     * <p>This is streaming on an array copy</p>
     * @return the stream
     */
    Stream<MenuItem> stream();

    /**
     * Get a parallel stream loop of the items in the menu
     * <p>This is streaming on an array copy</p>
     * @apiNote use this if you want to do things in parallel, and you're sure of how to use it, else it might even get slower than the normal .stream()
     * @return the stream
     */
    default Stream<MenuItem> parallelStream() { return stream().parallel(); }

    default int firstEmptySlot() {
        return this.firstEmptySlot(0);
    }

    int itemCount();

    int firstEmptySlot(int startingPoint);

    void addRefreshableItem(int index, Supplier<MenuItem> item);

    void removeRefreshableItem(int index);

    default int addItem(final @NotNull MenuItem... items) {
        return addItem(new ArrayList<>(items.length), items);
    }

    int addItem(int fromIndex, @NotNull final MenuItem... items);

    int addItem(int fromIndex, int toIndex, @NotNull final MenuItem... items);

    int addItem(final List<MenuItem> toAdd, @NotNull final MenuItem... items);

    int addItem(final List<MenuItem> toAdd, int fromIndex, int toIndex, final MenuItem... items);

    /**
     * Set the contents of the menu
     * @param items the contents to set
     * @apiNote this will replace all items in the menu, and this is an O(1) operation
     */
    void replaceContents(MenuItem... items);

    void forEach(Consumer<? super MenuItem> action);

    void indexed(ObjIntConsumer<? super MenuItem> action);

    Optional<MenuItem> findFirst(Predicate<MenuItem> action);

    MenuItem removeItem(int index);

    boolean removeItem(MenuItem... its);

    boolean isConcurrent();

    /**
     * Add the itemStack to the list of items in the menu.
     * @param item the itemStack to add
     */
    default void setItem(Slot position, MenuItem item) {
        validateSlot(position.slot(), size());
        this.setItem(position.slot(), item);
    }
    /**
     * get the itemStack from the list of items in the menu.
     * @return the optional itemStack or an empty optional
     */
    default Optional<MenuItem> getItem(@NotNull Slot position) {
        validateSlot(position.slot(), size());
        return this.getItem(position.slot());
    }

    /**
     * Checks if the given position has an item.
     *
     * @param  position  the position to check
     * @return       true if the position has an item, false otherwise
     */
    default boolean hasItem(@NotNull Slot position) {
        validateSlot(position.slot(), size());
        return hasItem(position.slot());
    }

    /**
     * Clear every item in the menu.
     */
    void clear();

    int size();

    default boolean hasItem(int slot) {
        return getItem(slot).isPresent();
    }

    void recreateItems(Inventory inventory);

    default int addItem(@NotNull final List<MenuItem> items) {
        return addItem(items.toArray(new MenuItem[0]));
    }

    void replaceContents(BukkitContents items);

    default boolean removeItem(@NotNull List<MenuItem> itemStacks) {
        return removeItem(itemStacks.toArray(new MenuItem[0]));
    }

    int rows();

    int columns();

    Menu getMenu();

    Set<Map.Entry<Integer, MenuItem>> getEntries();

    static void validateSlot(int slot, int size) {
        if (slot < 0 || slot >= size) throw new IllegalArgumentException("This slot is out of bounds for menu size: " + size + "\nSize: " + size + "\nSlot: " + slot);
    }
}
