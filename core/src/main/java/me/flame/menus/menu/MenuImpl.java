package me.flame.menus.menu;

import com.google.common.collect.ImmutableList;

import me.flame.menus.menu.contents.BukkitContents;
import me.flame.menus.menu.contents.Contents;
import me.flame.menus.items.MenuItem;
import me.flame.menus.modifiers.Modifier;
import me.flame.menus.menu.opener.MenuOpener;
import me.flame.menus.menu.pagination.IndexedPagination;

import me.flame.menus.util.ContentsFactory;
import net.kyori.adventure.text.Component;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Most commonly used normal Menu
 * @since 1.0.0
 */
@SuppressWarnings({ "UnusedReturnValue" })
public final class MenuImpl extends AbstractMenu implements
        Menu, RandomAccess, java.io.Serializable, InventoryHolder, BukkitContents {
    MenuImpl(int rows, @NotNull Component title, @NotNull EnumSet<Modifier> modifiers, Menus manager, MenuOpener opener, ContentsFactory contents) {
        super(rows * 9, rows, title, modifiers, opener, manager, ConcurrencyProperties.EMPTY);
        BukkitContents gottenContents;
        this.contents = contents == null ? new Contents(this) : ((gottenContents = contents.create(this, properties)) == null ? new Contents(this) : gottenContents);
    }

    MenuImpl(@NotNull OpenedType type, @NotNull Component title, @NotNull EnumSet<Modifier> modifiers, Menus manager, MenuOpener opener, ContentsFactory contents) {
        super(type, title, modifiers, opener, manager, ConcurrencyProperties.EMPTY);
        BukkitContents gottenContents;
        this.contents = contents == null ? new Contents(this) : ((gottenContents = contents.create(this, properties)) == null ? new Contents(this) : gottenContents);
    }

    public @NotNull IndexedPagination pagination(int pages, MenuItem nextItem, MenuItem previousItem, int nextItemSlot, int previousItemSlot) {
        var pagination = new PaginatedMenuImpl(rows, pages, title, modifiers, nextItem, previousItem, nextItemSlot, previousItemSlot, opener, manager, properties);
        pagination.replaceContents(contents);
        return pagination;
    }

    @Override
    public void update() {
        updating = true;
        updatePlayerInventories(this.inventory, this.getViewers(), contents);
        updating = false;
    }

    @Override
    public void updateTitle(Component title) {
        Inventory oldInventory = inventory;
        this.inventory = opener.open(manager, this, this.structure());
        updating = true;
        contents.recreateItems(inventory);
        for (HumanEntity player : ImmutableList.copyOf(oldInventory.getViewers())) player.openInventory(inventory);
        updating = false;
    }

    static void updatePlayerInventories(Inventory inventory, @NotNull List<HumanEntity> viewers, @NotNull BukkitContents contents) {
        contents.recreateItems(inventory);
        for (HumanEntity player : new ArrayList<>(viewers)) ((Player) player).updateInventory();
    }

    @Override
    public void open(@NotNull HumanEntity entity) {
        if (!entity.isSleeping()) entity.openInventory(inventory);
    }

    public @NotNull Menu copy() {
        MenuImpl menu = type.inventoryType() == InventoryType.CHEST
                ? new MenuImpl(rows, title, modifiers, manager, opener, (loadedMenu, properties) -> contents)
                : new MenuImpl(type, title, modifiers, manager, opener, (loadedMenu, properties) -> contents);
        menu.setDynamicSizing(dynamicSizing);
        menu.actions = actions;
        menu.slotActions = slotActions;
        return menu;
    }
}