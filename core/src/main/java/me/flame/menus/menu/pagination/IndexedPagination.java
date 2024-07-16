package me.flame.menus.menu.pagination;

import me.flame.menus.events.PageChangeEvent;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Menu;
import me.flame.menus.menu.actions.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface IndexedPagination extends Pagination<Integer> {
    /**
     * Gets the current page number (Inflated by 1)
     * @return The current page number
     */
    int getCurrentPageNumber();

    boolean isLastPage();

    boolean isFirstPage();

    void addItems(@NotNull MenuItem... items);

    /**
     * Goes to the next page
     * @return False if there is no next page.
     */
    boolean next();

    /**
     * Goes to the previous page if possible
     * @return False if there is no previous page.
     */
    boolean previous();

    void setNextPageItem(int nextItemSlot, MenuItem nextItem);

    void setPreviousPageItem(int previousItemSlot, MenuItem previousItem);

    default void setOnPageChange(BiConsumer<PageChangeEvent, Menu> eventAction) {
        actions().addOnPageAction(eventAction);
    }

    @ApiStatus.Internal
    boolean pageChangingAction(int clickedSlot);

    @ApiStatus.Internal
    PageChangeEvent createPageEvent(final InventoryClickEvent event);
}
