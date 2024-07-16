package me.flame.menus.menu;

import com.google.common.collect.ImmutableList;

import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.contents.BukkitContents;
import me.flame.menus.menu.opener.MenuOpener;
import me.flame.menus.menu.pagination.*;
import me.flame.menus.modifiers.Modifier;
import me.flame.menus.util.PaginatedContentsFactory;

import net.kyori.adventure.text.Component;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.flame.menus.menu.MenuImpl.updatePlayerInventories;

@SuppressWarnings("unused")
public class KeyedMenuImpl extends AbstractPagination<String> implements
        Pagination<String>, Menu, RandomAccess, java.io.Serializable, InventoryHolder, BukkitContents {
    private String pageIdentifier;

    KeyedMenuImpl(final int rows, final int pages, Component title, EnumSet<Modifier> modifiers, MenuOpener opener, Menus menus, ConcurrencyProperties properties, PaginatedContentsFactory<String> defaultPages) {
        super(rows * 9, rows, pages, title, modifiers, opener, menus, properties, defaultPages);
    }

    int paginationSize() {
        return pages.size();
    }

    KeyedMenuImpl(OpenedType type, final int pages, Component title, EnumSet<Modifier> modifiers, MenuOpener opener, Menus menus, ConcurrencyProperties properties, PaginatedContentsFactory<String> defaultPages) {
        super(type, pages, title, modifiers, opener, menus, properties, defaultPages);
    }

    KeyedMenuImpl(int rows, final int pages, Component title, EnumSet<Modifier> modifiers, MenuOpener opener, Menus menus, ConcurrencyProperties properties) {
        this(rows, pages, title, modifiers, opener, menus, properties, null);
    }

    KeyedMenuImpl(OpenedType type, final int pages, Component title, EnumSet<Modifier> modifiers, MenuOpener opener, Menus menus, ConcurrencyProperties properties) {
        this(type, pages, title, modifiers, opener, menus, properties, null);
    }

    public List<Page> pages() { return List.copyOf(pages.values()); }

    @Override
    public void setPage(final String index, final Page page) {
        Objects.requireNonNull(index);
        Objects.requireNonNull(page);
        this.pages.put(index, page);
        setPageItems(page.contents());
    }

    @Override
    public void update() {
        updating = true;
        updatePlayerInventories(inventory, getViewers(), contents);
        updating = false;
    }

    @Override
    public void updateTitle(final Component title) {
        Inventory oldInventory = inventory;
        this.inventory = opener.open(manager, this, this.structure());
        updating = true;
        contents.recreateItems(inventory);
        for (HumanEntity player : ImmutableList.copyOf(oldInventory.getViewers())) player.openInventory(inventory);
        updating = false;
    }

    @Override
    public void open(@NotNull final HumanEntity entity) {
        throw new UnsupportedOperationException();
    }

    public void open(@NotNull final HumanEntity entity, final String openPage) {
        if (entity.isSleeping()) return;
        Objects.requireNonNull(inventory, "Inventory was provided null by the menu opener: " + this.opener.getClass().getSimpleName() + " at: " + this.opener.getClass().getPackageName());

        updatePage(openPage);
        entity.openInventory(inventory);
    }

    private void updatePage(final String openPage) {
        BukkitContents bukkitContents = pages.get(openPage).contents();
        this.contents = Objects.requireNonNull(bukkitContents);
        this.pageIdentifier = openPage;
        this.update();
    }

    public int getPagesSize() {
        return pages.size();
    }

    public @Nullable Page getPage(String index) {
        return pages.get(index);
    }

    @Override
    public Map<Integer, MenuItem> pageItems() { return pageItems; }

    /**
     * Goes to the specified page
     *
     * @return False if there is no next page.
     */
    public boolean page(String page) {
        updatePage(page);
        return true;
    }

    @Override
    public Class<String> indexClass() {
        return String.class;
    }

    public Pagination<String> copy() {
        KeyedMenuImpl menu = type.inventoryType() == InventoryType.CHEST
                ? new KeyedMenuImpl(rows, pages.size(), title, modifiers, opener, manager, properties, (loadedMenu, properties) -> pages)
                : new KeyedMenuImpl(type, pages.size(), title, modifiers, opener, manager, properties, (loadedMenu, properties) -> pages);
        menu.setDynamicSizing(dynamicSizing);
        menu.actions = actions;
        menu.slotActions = slotActions;
        return menu;
    }

    @Override
    public String getPageIdentifier() {
        return pageIdentifier;
    }
}