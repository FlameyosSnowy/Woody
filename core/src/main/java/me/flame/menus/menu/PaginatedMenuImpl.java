package me.flame.menus.menu;

import com.google.common.collect.ImmutableList;

import me.flame.menus.menu.contents.*;
import me.flame.menus.events.PageChangeEvent;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.opener.MenuOpener;
import me.flame.menus.menu.pagination.*;
import me.flame.menus.modifiers.Modifier;
import me.flame.menus.util.PaginatedContentsFactory;
import net.kyori.adventure.text.Component;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.*;

import java.util.*;

import static me.flame.menus.menu.MenuImpl.updatePlayerInventories;

/**
 * Menu that allows you to have multiple pages
 * @since 2.0.0
 * @author FlameyosFlow
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PaginatedMenuImpl extends AbstractPagination<Integer> implements
        IndexedPagination, Menu, RandomAccess, java.io.Serializable, InventoryHolder, BukkitContents {
    private int nextItemSlot = -1, previousItemSlot = -1, pagesSize = 0;

    @NotNull
    private Integer pageIdentifier = 0;

    private MenuItem nextItem, previousItem;

    @Override
    public @NotNull Integer getPageIdentifier() {
        return pageIdentifier;
    }

    /**
     * Adds a blank page to the menu.
     * @return the index the page was added at
     */
    public int addPage(Page data) {
        this.setPage(pagesSize, data);
        return pagesSize++;
    }

    /**
     * Main constructor to provide a way to create PaginatedMenu
     *
     * @param rows The page size.
     */
    PaginatedMenuImpl(final int rows, final int pages, Component title, EnumSet<Modifier> modifiers, MenuItem nextItem, MenuItem previousItem, int nextItemSlot, int previousItemSlot, MenuOpener opener, Menus menus, ConcurrencyProperties properties, PaginatedContentsFactory<Integer> defaultPages) {
        super(rows * 9, rows, pages, title, modifiers, opener, menus, properties, defaultPages);
        Page defaultPage = this.pages.get(0);
        if (defaultPage != null) this.contents = defaultPage.contents();
        this.paginationInitialization(MenuType.CHEST, pages, nextItem, previousItem, nextItemSlot, previousItemSlot);
    }

    int paginationSize() {
        return pages.size();
    }

    /**
     * Main constructor to provide a way to create PaginatedMenu
     */
    PaginatedMenuImpl(OpenedType type, final int pages, Component title, EnumSet<Modifier> modifiers, MenuItem nextItem, MenuItem previousItem, int nextItemSlot, int previousItemSlot, MenuOpener opener, Menus menus, ConcurrencyProperties properties, PaginatedContentsFactory<Integer> defaultPages) {
        super(type, pages, title, modifiers, opener, menus, properties, defaultPages);
        Page defaultPage = this.pages.get(0);
        if (defaultPage != null) this.contents = defaultPage.contents();
        this.paginationInitialization(type, pages, nextItem, previousItem, nextItemSlot, previousItemSlot);
    }

    PaginatedMenuImpl(int rows, final int pages, Component title, EnumSet<Modifier> modifiers, MenuItem nextItem, MenuItem previousItem, int nextItemSlot, int previousItemSlot, MenuOpener opener, Menus menus, ConcurrencyProperties properties) {
        this(rows, pages, title, modifiers, nextItem, previousItem, nextItemSlot, previousItemSlot, opener, menus, properties,
                null);
    }

    PaginatedMenuImpl(OpenedType type, final int pages, Component title, EnumSet<Modifier> modifiers, MenuItem nextItem, MenuItem previousItem, int nextItemSlot, int previousItemSlot, MenuOpener opener, Menus menus, ConcurrencyProperties properties) {
        this(type, pages, title, modifiers, nextItem, previousItem, nextItemSlot, previousItemSlot, opener, menus, properties,
                null);
    }

    private void paginationInitialization(OpenedType type, int pageCount, MenuItem nextItem, MenuItem previousItem, int nextItemSlot, int previousItemSlot) {
        if (contents == null) {
            Contents items = new Contents(this, properties.concurrentContents());
            this.addPage(new Page(this, items));
            this.contents = items;
        }
        this.setNextPageItem(nextItemSlot, nextItem);
        this.setPreviousPageItem(previousItemSlot, previousItem);

        if (!pages.isEmpty()) return;
        for (int pageIndex = 1; pageIndex < pageCount; pageIndex++)
            this.setPage(pageIndex, new Page(this, new Contents(this, properties.concurrentContents())));
    }

    public List<Page> pages() { return List.copyOf(pages.values()); }

    @Override
    public void setPage(final Integer index, final Page page) {
        this.pages.put(Objects.requireNonNull(index), Objects.requireNonNull(page));
        setPageItems(page.contents());
    }

    public void setNextPageItem(int nextItemSlot, MenuItem nextItem) {
        this.nextItemSlot = nextItemSlot;
        this.nextItem = Objects.requireNonNull(nextItem);
        this.pageItems.put(nextItemSlot, nextItem);
        this.setItem(nextItemSlot, nextItem);
        nextItem.setClickAction((player, event) -> this.next());
    }

    public void setPreviousPageItem(int previousItemSlot, MenuItem previousItem) {
        this.previousItemSlot = previousItemSlot;
        this.previousItem = Objects.requireNonNull(previousItem);
        this.pageItems.put(previousItemSlot, previousItem);
        this.setItem(previousItemSlot, previousItem);
        previousItem.setClickAction((player, event) -> this.previous());
    }

    @Override
    public void update() {
        updating = true;
        updatePlayerInventories(this.inventory, this.getViewers(), this.contents);
        updating = false;
    }

    @Override
    public synchronized void updateTitle(final Component title) {
        Inventory oldInventory = inventory;
        this.inventory = opener.open(manager, this, this.structure());
        updating = true;
        contents.recreateItems(inventory);
        for (HumanEntity player : ImmutableList.copyOf(oldInventory.getViewers())) player.openInventory(inventory);
        updating = false;
    }

    public void open(@NotNull final HumanEntity entity, final @Range(from = 0, to = Integer.MAX_VALUE) Integer openPage) {
        if (entity.isSleeping()) return;
        if (openPage < 0 || openPage >= pages.size()) throw new IllegalArgumentException("\"openPage\" out of bounds; must be 0-" + (pages.size() - 1) + "\nopenPage: " + openPage + "\nFix: Make sure \"openPage\" is 0-" + (pages.size() - 1));
        Objects.requireNonNull(inventory, "Inventory was provided null by the menu opener: " + this.opener.getClass().getSimpleName() + " at: " + this.opener.getClass().getPackageName());
        this.updatePage(openPage);
        entity.openInventory(inventory);
    }

    public void open(@NotNull final HumanEntity player) { this.open(player, 0); }

    public int getCurrentPageNumber() { return pageIdentifier + 1; }

    @Override
    public boolean isLastPage() { return pageIdentifier == (pages.size() - 1); }

    @Override
    public boolean isFirstPage() { return pageIdentifier == 0; }

    @Override
    public int getPagesSize() { return pagesSize; }

    @Override
    public boolean pageChangingAction(final int clickedSlot) {
        return (clickedSlot == this.nextItemSlot) || (clickedSlot == this.previousItemSlot);
    }

    @Override
    public @Nullable PageChangeEvent createPageEvent(final @NotNull InventoryClickEvent event) {
        int newNumber = pageIdentifier + (nextItemSlot == event.getSlot() ? 1 : -1);
        if (newNumber < 0 || newNumber >= pages.size()) return null;
        Page currentPage = this.getPage(newNumber);
        return new PageChangeEvent(this, this.getPage(pageIdentifier), currentPage, (Player) event.getWhoClicked(), pageIdentifier, newNumber);
    }

    @Override
    public boolean next() { return page(pageIdentifier + 1); }

    @Override
    public boolean previous() { return page(pageIdentifier - 1); }

    @Override
    public boolean page(Integer pageNum) {
        if (pageNum < 0 || pageNum >= pages.size()) return false;
        updatePage(pageNum);
        return true;
    }

    private void updatePage(final Integer pageNum) {
        this.pageIdentifier = pageNum;
        this.contents = this.pages.get(pageNum).contents();
        this.update();
    }

    public @Override @Nullable Page getPage(Integer index) { return this.pages.get(index); }

    @Override
    public Class<Integer> indexClass() { return Integer.class; }

    public @Override Map<Integer, MenuItem> pageItems() { return pageItems; }

    public void addItems(@NotNull MenuItem @NotNull ... items) {
        int currentIndex = 0, length = items.length;
        for (int pageIndex = this.pageIdentifier; pageIndex < pagesSize && currentIndex < length; pageIndex++) {
            final int toIndex = Math.min(length - currentIndex, size - 1), fromIndex = currentIndex;
            int itemsAdded = Optional.ofNullable(this.getPage(pageIndex))
                    .map(page -> page.contents().addItem(fromIndex, toIndex, items))
                    .orElse(0);
            currentIndex += itemsAdded;
        }

        if (!dynamicSizing) return;
        while (currentIndex < length) {
            BukkitContents newPage = new Contents(this, properties.concurrentContents());
            addPage(new Page(this, newPage));

            final int toIndex = Math.min(length - currentIndex, size - 1), fromIndex = currentIndex;
            int itemsAdded = Optional.of(newPage)
                    .map(page -> page.addItem(fromIndex, toIndex, items))
                    .orElse(0);
            currentIndex += itemsAdded;
        }
    }

    /*private static @NotNull MenuItem @NotNull [] addItemsToPage(final @NotNull BukkitContents page, final List<MenuItem> leftovers, @NotNull MenuItem @NotNull [] items) {
        page.addItem(leftovers, items);
        MenuItem[] newItems = leftovers.toArray(new MenuItem[0]);
        leftovers.clear();
        return newItems;
    }*/

    public @NotNull IndexedPagination copy() {
        PaginatedMenuImpl menu = type.inventoryType() == InventoryType.CHEST
                ? new PaginatedMenuImpl(rows, pagesSize, title, modifiers, nextItem, previousItem, nextItemSlot, previousItemSlot, opener, manager, properties, (loadedMenu, properties) -> pages)
                : new PaginatedMenuImpl(this.type, pagesSize, title, modifiers, nextItem, previousItem, nextItemSlot, previousItemSlot, opener, manager, properties, (loadedMenu, properties) -> pages);
        menu.setDynamicSizing(dynamicSizing);
        menu.actions = actions;
        return menu;
    }
}