package me.flame.woody.menus.contents;

import me.flame.woody.menus.Slot;
import me.flame.woody.menus.Structure;
import me.flame.woody.menus.internals.MenuView;
import me.flame.woody.menus.item.MenuItem;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class Contents implements Iterable<MenuItem> {
    private final Map<Integer, MenuItem> contents;
    private final Structure structure;
    private final MenuView view;

    public Contents(Structure structure, MenuView view) {
        this.contents = new LinkedHashMap<>(structure.getSize());
        this.structure = structure;
        this.view = view;
    }

    public static ContentsBuilder builder(Structure structure, MenuView view) {
        return new ContentsBuilder(structure, view);
    }

    public MenuItem getItem(Slot slot) {
        return contents.get(slot.getSlot());
    }

    public void setItem(Slot slot, MenuItem item) {
        contents.put(slot.getSlot(), item);
    }

    public MenuItem removeItem(Slot slot) {
        return contents.remove(slot.getSlot());
    }

    public MenuItem getItem(int slot) {
        return contents.get(slot);
    }

    public void setItem(int slot, MenuItem item) {
        contents.put(slot, item);
    }

    public MenuItem removeItem(int slot) {
        return contents.remove(slot);
    }

    public int size() {
        return contents.size();
    }

    public boolean isEmpty() {
        return contents.isEmpty();
    }

    public boolean contains(int slot) {
        return contents.containsKey(slot);
    }

    public boolean contains(@NotNull Slot slot) {
        return contents.containsKey(slot.getSlot());
    }

    public boolean contains(MenuItem item) {
        return contents.containsValue(item);
    }

    public void addItems(MenuItem... items) {
        int index = 0;
        for (int slot = 0; slot < this.structure.getSize(); slot++) {
            if (index >= items.length) break;
            contents.put(slot, items[index++]);
        }
    }

    public void addItems(Map<Integer, MenuItem> items) {
        contents.putAll(items);
    }

    public void addItems(@NotNull Contents contents) {
        this.contents.putAll(contents.contents);
    }

    public void removeItems(MenuItem @NotNull ... items) {
        for (MenuItem item : items) {
            contents.values().remove(item);
        }
    }

    public void clear() {
        contents.clear();
    }

    @Override
    public @NotNull Iterator<MenuItem> iterator() {
        return contents.values().iterator();
    }

    public @NotNull ContentsIterator iterator(ContentsIterationDirection direction) {
        return new ContentsIterator(direction, view);
    }

    public @NotNull ContentsIterator iterator(ContentsIterationDirection direction, int maxRows, int maxColumns) {
        return new ContentsIterator(maxRows, maxColumns, direction, view);
    }

    public @NotNull ContentsIterator iterator(int maxRows, int maxColumns) {
        return new ContentsIterator(maxRows, maxColumns, ContentsIterationDirection.HORIZONTAL, view);
    }

    @Override
    public void forEach(Consumer<? super MenuItem> action) {
        contents.values().forEach(action);
    }
}
