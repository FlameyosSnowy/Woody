package me.flame.menus.menu;

import me.flame.lotte.LinkedConcurrentCache;
import me.flame.menus.items.ItemResponse;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.actions.Actions;
import me.flame.menus.menu.animation.Animation;
import me.flame.menus.menu.contents.BukkitContents;
import me.flame.menus.menu.opener.MenuOpener;
import me.flame.menus.modifiers.Modifier;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public abstract class AbstractMenu implements Menu {
    @NotNull
    protected final EnumSet<Modifier> modifiers;

    protected Component title;

    protected boolean dynamicSizing = false, updating = false, animating = false;
    protected int rows, size;

    @Override
    public Structure structure() { return type.inventoryType() == InventoryType.CHEST ? Structure.of(size) : Structure.of(type); }

    protected MenuOpener opener;

    protected final Menus manager;

    protected OpenedType type;
    protected Inventory inventory;
    protected BukkitContents contents;

    protected ConcurrencyProperties properties;

    protected final List<Animation> activeAnimations = new ArrayList<>(2);

    protected Map<Integer, ItemResponse> slotActions;

    protected Actions actions = new Actions(this);

    protected AbstractMenu(int size, int rows, Component title, @NotNull EnumSet<Modifier> modifiers, @NotNull MenuOpener opener, Menus manager, @NotNull ConcurrencyProperties properties) {
        this.manager = manager;
        this.modifiers = modifiers;
        this.size = size;
        this.rows = rows;
        this.type = MenuType.CHEST;
        this.title = title;
        this.opener = opener;
        this.properties = properties;
        this.slotActions = properties.concurrentContents() ? new LinkedConcurrentCache<>(9) : new LinkedHashMap<>(9);
        this.inventory = opener.open(manager, this, Structure.of(size));
    }

    protected AbstractMenu(@NotNull OpenedType type, Component title, @NotNull EnumSet<Modifier> modifiers, MenuOpener opener, Menus manager, ConcurrencyProperties properties) {
        if (type.inventoryType() == InventoryType.CHEST) throw new IllegalArgumentException("Not allowed, use the size/rows constructor. CHEST InventoryType detected.");
        this.manager = manager;
        this.modifiers = modifiers;
        this.size = type.maxSize();
        this.rows = type.maxRows();
        this.type = type;
        this.title = title;
        this.opener = opener;
        this.properties = properties;
        this.inventory = opener.open(manager, this, Structure.of(type));
    }

    @Override
    public Stream<MenuItem> stream() {
        return contents.stream();
    }

    @Override
    public void updatePer(long delay, long repeatTime) {
        if (repeatTime > 0) Bukkit.getScheduler().runTaskTimer(manager.getPlugin(), this::update, delay, repeatTime);
        throw new IllegalThreadStateException("Synchronously calling an update method that has no delay or repeat delay. \nGoal: This prevents blocking because the delay is too low.");
    }

    @Override
    public boolean addModifier(Modifier modifier) {
        return modifiers.add(modifier);
    }

    @Override
    public boolean removeModifier(Modifier modifier) {
        return modifiers.remove(modifier);
    }

    @Override
    public boolean addAllModifiers() {
        return modifiers.addAll(Modifier.ALL);
    }

    @Override
    public void removeAllModifiers() {
        Modifier.ALL.forEach(modifiers::remove);
    }

    @Override
    public boolean areItemsPlaceable() {
        return modifiers.contains(Modifier.DISABLE_ITEM_ADD);
    }

    @Override
    public boolean areItemsRemovable() {
        return modifiers.contains(Modifier.DISABLE_ITEM_REMOVAL);
    }

    @Override
    public boolean areItemsSwappable() {
        return modifiers.contains(Modifier.DISABLE_ITEM_SWAP);
    }

    @Override
    public boolean areItemsCloneable() {
        return modifiers.contains(Modifier.DISABLE_ITEM_CLONE);
    }

    @Override
    public int rows() {
        return rows;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean allModifiersAdded() {
        return modifiers.size() == 4;
    }

    @Override
    public int itemCount() {
        return contents.itemCount();
    }

    @Override
    public Component title() {
        return title;
    }

    @Override
    public MenuOpener opener() {
        return opener;
    }

    @Override
    public Menus manager() {
        return manager;
    }

    @Override
    public Actions actions() {
        return actions;
    }

    @Override
    public BukkitContents contents() {
        return contents;
    }

    @Override
    public @NotNull EnumSet<Modifier> getModifiers() {
        return modifiers;
    }

    @Override
    public boolean isDynamicSizing() {
        return dynamicSizing;
    }

    @Override
    public boolean isUpdating() {
        return updating;
    }

    @Override
    public boolean isAnimating() {
        return animating;
    }

    @Override
    public OpenedType getType() {
        return type;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public List<Animation> getActiveAnimations() {
        return activeAnimations;
    }

    @Override
    public void setDynamicSizing(final boolean dynamicSizing) {
        this.dynamicSizing = dynamicSizing;
    }

    @Override
    public void setAnimating(final boolean animating) {
        this.animating = animating;
    }

    public void setUpdating(final boolean updating) {
        this.updating = updating;
    }

    public void setOpener(final MenuOpener opener) {
        this.opener = opener;
    }

    public void setInventory(final Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void grow(int growRows) {
        int maxRows = type.maxRows();
        if (rows >= maxRows) return;

        int expectedResult = growRows + rows;
        if (expectedResult > maxRows || expectedResult < 1) {
            throw new IllegalArgumentException("The given rows is guaranteed to break the inventory functionality, attempted row growth input: " + growRows + " menu rows: " + rows);
        }

        rows += growRows;
        this.size = rows * type.maxColumns();
        this.inventory = opener.open(manager, this, this.structure());
        contents.recreateItems(inventory);
    }

    public void grow() { grow(1); }

    @Override
    public int addItem(final int fromIndex, final @NotNull MenuItem... items) {
        return this.addItem(new ArrayList<>(items.length), fromIndex, items.length, items);
    }

    @Override
    public int addItem(final int fromIndex, final int toIndex, final @NotNull MenuItem... items) {
        return this.addItem(new ArrayList<>(items.length), fromIndex, toIndex, items);
    }

    @Override
    public int addItem(List<MenuItem> toAdd, final @NotNull MenuItem... items) {
        return this.addItem(new ArrayList<>(items.length), 0, items.length, items);
    }

    @Override
    public int addItem(final List<MenuItem> toAdd, int fromIndex, int toIndex, final MenuItem... items) {
        return contents.addItem(toAdd, fromIndex, toIndex, items);
    }

    @Override
    public void refreshItem(final int index) {
        this.contents.refreshItem(index);
    }

    @Override
    public void refreshItem(final int index, final Supplier<MenuItem> item) {
        this.contents.refreshItem(index, item);
    }

    @Override
    public int firstEmptySlot(final int startingPoint) {
        return this.contents.firstEmptySlot(startingPoint);
    }

    @Override
    public void addRefreshableItem(final int index, final Supplier<MenuItem> item) {
        this.contents.addRefreshableItem(index, item);
    }

    @Override
    public void removeRefreshableItem(final int index) {
        this.contents.removeRefreshableItem(index);
    }

    @Override
    public boolean hasItem(int slot) {
        return this.contents.hasItem(slot);
    }

    @Override
    public void clear() {
        contents.clear();
    }

    @Override
    public void recreateItems(final Inventory inventory) {
        contents.recreateItems(inventory);
    }

    @Override
    public void replaceContents(final BukkitContents items) {
        this.contents = items;
    }

    @Override
    public Menu getMenu() {
        return this;
    }

    @Override
    public Set<Map.Entry<Integer, MenuItem>> getEntries() {
        return contents.getEntries();
    }

    @Override
    public int columns() {
        return type.maxColumns();
    }

    @Override
    public void replaceContents(final MenuItem... items) {
        contents.replaceContents(items);
    }

    @Override
    public void setItem(final int slot, final MenuItem item) {
        contents.setItem(slot, item);
    }

    @Override
    public Optional<MenuItem> getItem(final int index) {
        return contents.getItem(index);
    }

    @Override
    public void forEach(final Consumer<? super MenuItem> action) {
        contents.forEach(action);
    }

    @Override
    public void indexed(final ObjIntConsumer<? super MenuItem> action) {
        contents.indexed(action);
    }

    @Override
    public Optional<MenuItem> findFirst(final Predicate<MenuItem> action) {
        return contents.findFirst(action);
    }

    @Override
    public MenuItem removeItem(final int index) {
        return contents.removeItem(index);
    }

    @Override
    public boolean removeItem(final MenuItem... its) {
        return contents.removeItem(its);
    }

    @Override
    public boolean isConcurrent() {
        return contents.isConcurrent();
    }

    @Override
    public Map<Integer, MenuItem> getItems() {
        return contents.getItems();
    }

    @Override
    public Map<Integer, MenuItem> getMutableItems() {
        return contents.getMutableItems();
    }

    @Override
    public boolean canResize() {
        return dynamicSizing && rows < 6 && type.inventoryType() == InventoryType.CHEST;
    }

    @Override
    public Map<Integer, ItemResponse> getSlotActions() {
        return slotActions;
    }

    @Override
    public void setSlotAction(final int slot, final ItemResponse response) {
        this.slotActions.put(slot, response);
    }

    @Override
    public void close(@NotNull final HumanEntity player) {
        Bukkit.getScheduler().runTaskLater(manager.getPlugin(), () -> player.closeInventory(), 1L);
    }

    @Override
    public List<HumanEntity> getViewers() {
        return inventory.getViewers();
    }
}
