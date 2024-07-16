package me.flame.menus.menu.contents;

import com.google.common.collect.ImmutableSet;
import me.flame.lotte.LinkedConcurrentCache;
import me.flame.menus.menu.ContentsBuilder;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Menu;

import org.bukkit.inventory.Inventory;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class Contents implements BukkitContents {
    Map<Integer, MenuItem> items;
    private final Menu menu;

    private final Map<Integer, Supplier<MenuItem>> refreshableItems = new HashMap<>(9);

    private final boolean concurrent;

    public Contents(Menu menu) {
        this(menu, false);
    }

    public Contents(Menu menu, boolean concurrent) {
        Objects.requireNonNull(menu);
        this.menu = menu;
        this.concurrent = concurrent;
        this.items = concurrent ? new LinkedConcurrentCache<>(menu.size()) : new LinkedHashMap<>(menu.size());
    }

    @Contract("_ -> new")
    public static @NotNull ContentsBuilder builder(Menu menu) {
        return new ContentsBuilder(menu);
    }

    @Contract("_, _ -> new")
    public static @NotNull ContentsBuilder builder(Menu menu, boolean concurrent) {
        return new ContentsBuilder(menu, concurrent);
    }

    @Contract(pure = true)
    public @Unmodifiable Map<Integer, MenuItem> getItems() {
        return Map.copyOf(items);
    }

    public Map<Integer, MenuItem> getMutableItems() {
        return items;
    }

    @Override
    public void refreshItem(final int index) {
        BukkitContents.validateSlot(index, menu.size());
        MenuItem item = refreshableItems.get(index).get();
        this.items.put(index, item);
        this.menu.getInventory().setItem(index, item.getItemStack());
    }

    @Override
    public void refreshItem(final int index, final @NotNull Supplier<MenuItem> item) {
        BukkitContents.validateSlot(index, menu.size());
        items.put(index, item.get());
    }

    @Override
    public Stream<MenuItem> stream() {
        return items.values().stream();
    }

    @Override
    public int itemCount() {
        return items.size();
    }

    @Override
    public int firstEmptySlot(final int startingPoint) {
        final int size = menu.size();
        for (int index = startingPoint; index < size; index++)
            if (!this.items.containsKey(index)) return index;
        return -1;
    }

    @Override
    public void addRefreshableItem(final int index, final Supplier<MenuItem> item) {
        BukkitContents.validateSlot(index, size());
        refreshableItems.put(index, item);
        this.items.put(index, item.get());
    }

    @Override
    public void removeRefreshableItem(final int index) {
        BukkitContents.validateSlot(index, size());
        refreshableItems.remove(index);
        this.items.remove(index);
    }

    public int addItem(final @NotNull MenuItem... items) {
        return addItem(new ArrayList<>(items.length), items);
    }

    public int addItem(final List<MenuItem> toAdd, @NotNull final MenuItem... items) {
        return this.addItem(toAdd, 0, menu.size(), items);
    }

    public int addItem(final List<MenuItem> toAdd, int fromIndex, @NotNull final MenuItem... items) {
        return this.addItem(toAdd, fromIndex, menu.size(), items);
    }

    public int addItem(final List<MenuItem> toAdd, int fromIndex, int toIndex, final MenuItem @NotNull ... items) {
        int itemsAdded = 0, slot = fromIndex, size = menu.size(), rows = menu.rows();
        for (int itemIndex = 0; itemIndex < items.length; itemIndex++) {
            if (slot == toIndex) return itemIndex;
            MenuItem item = items[itemIndex];
            if (item == null) continue;
            while (slot < size && !this.items.containsKey(slot)) slot++;
            if (slot < 0 || slot >= size) {
                if (rows == 6) return itemsAdded;
                toAdd.addAll(Arrays.asList(items).subList(itemIndex, toIndex));
                break;
            }
            this.setItem(slot, item);
            itemsAdded++;
            slot++;
        }
        if (!toAdd.isEmpty() && menu.canResize()) {
            menu.grow(1);
            return addItem(toAdd.toArray(new MenuItem[0]));
        }
        return itemsAdded;
    }

    public int addItem(int fromIndex, int toIndex, @NotNull final MenuItem... items) {
        return this.addItem(new ArrayList<>(items.length), fromIndex, toIndex, items);
    }

    public int addItem(int fromIndex, @NotNull final MenuItem... items) {
        return this.addItem(new ArrayList<>(items.length), fromIndex, menu.size(), items);
    }

    public int addItem(@NotNull final List<MenuItem> items) {
        return addItem(items.toArray(new MenuItem[0]));
    }

    public void replaceContents(MenuItem @NotNull ... items) {
        int length = items.length;
        if (length % 9 != 0) throw new IllegalArgumentException("Length of items is not a multiple of 9");
        this.items.clear();
        for (int index = 0; index < length; index++) this.items.put(index, items[index]);
    }

    public void replaceContents(@NotNull BukkitContents contents) {
        if (contents.size() % 9 != 0) throw new IllegalArgumentException("Length of items is not a multiple of 9");
        this.items = contents.getItems();
    }

    @Override
    public boolean removeItem(@NotNull final List<MenuItem> itemStacks) {
        return this.removeItem(itemStacks.toArray(new MenuItem[0]));
    }

    @Override
    public int rows() {
        return menu.rows();
    }

    @Override
    public int columns() {
        return menu.columns();
    }

    @Override
    public Menu getMenu() {
        return menu;
    }

    @Override
    public Set<Map.Entry<Integer, MenuItem>> getEntries() {
        return this.items.entrySet();
    }

    public void setItem(int slot, MenuItem item) {
        BukkitContents.validateSlot(slot, menu.size());
        Objects.requireNonNull(item, "Item shall not be null.");
        this.items.put(slot, item);
    }

    public Optional<MenuItem> getItem(int index) {
        BukkitContents.validateSlot(index, menu.size());
        return Optional.ofNullable(this.items.get(index));
    }

    public void forEach(Consumer<? super MenuItem> action) {
        for (var item : this.items.values()) action.accept(item);
    }

    public void indexed(ObjIntConsumer<? super MenuItem> action) {
        for (var item : this.items.entrySet()) action.accept(item.getValue(), item.getKey());
    }

    public Optional<MenuItem> findFirst(Predicate<MenuItem> action) {
        for (var item : this.items.entrySet()) if (action.test(item.getValue())) return Optional.of(item.getValue());
        return Optional.empty();
    }

    public MenuItem removeItem(int index) {
        BukkitContents.validateSlot(index, menu.size());
        return this.items.remove(index);
    }

    public boolean hasItem(int slot) {
        BukkitContents.validateSlot(slot, menu.size());
        return this.items.containsKey(slot);
    }

    public boolean removeItem(MenuItem... abandonedItems) {
        Set<MenuItem> items = ImmutableSet.copyOf(abandonedItems);

        boolean changed = false;
        Iterator<Map.Entry<Integer, MenuItem>> iterator = this.items.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, MenuItem> entry = iterator.next();
            int index = entry.getKey();
            if (!items.contains(entry.getValue())) continue;
            iterator.remove();
            this.menu.getInventory().setItem(index, null);
            changed = true;
        }
        return changed;
    }

    @Override
    public boolean isConcurrent() {
        return concurrent;
    }

    public void recreateItems(Inventory inventory) {
        for (Map.Entry<Integer, MenuItem> entry : this.items.entrySet()) {
            int itemIndex = entry.getKey();
            if (itemIndex >= size() || itemIndex < 0) continue;

            MenuItem button = entry.getValue();
            Predicate<Menu> visibilityCondition;
            if (button == null || ((visibilityCondition = button.getVisiblity()) != null && !visibilityCondition.test(menu))) {
                inventory.setItem(itemIndex, null);
                continue;
            }

            Supplier<MenuItem> refreshableItem = this.refreshableItems.get(itemIndex);
            if (refreshableItem != null) inventory.setItem(itemIndex, refreshableItem.get().getItemStack());
            else inventory.setItem(itemIndex, button.getItemStack());
        }
    }

    @Override
    public void clear() {
        this.items.clear();
    }

    public int size() {
        return this.menu.size();
    }
}