package me.flame.menus.menu.pagination;

import com.google.common.collect.ImmutableSet;

import me.flame.menus.menu.contents.BukkitContents;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.Menu;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings({ "unused" })
public interface Pagination<T> extends Menu {
    default void setPageItems(BukkitContents items) {
        Objects.requireNonNull(items);
        for (Map.Entry<Integer, MenuItem> entry : this.pageItems().entrySet()) items.setItem(entry.getKey(), entry.getValue());
    }

    default void addPageItems(MenuItem... items) {
        for (Page page : pages()) page.contents().addItem(items);
    }

    List<Page> pages();

    void setPage(T index, Page page);

    @Nullable Page getPage(T key);

    Class<T> indexClass();

    default void removePageItem(int slot) {
        for (Page page : pages()) page.contents().removeItem(slot);
    }

    default void removePageItem(MenuItem slot) {
        for (Page page : pages()) page.contents().removeItem(slot);
    }

    default void removePageItem(MenuItem... slot) {
        Set<MenuItem> set = ImmutableSet.copyOf(slot);
        for (Page page : pages()) page.contents().removeItem(slot);
    }

    default void setPageItem(int[] slots, MenuItem @NotNull [] items) {
        int length = slots.length;

        if (length != items.length) throw new IllegalArgumentException("Number of slots and number of items must be equal.");
        for (Page page : pages()) {
            for (int i = 0; i < length; i++) {
                page.contents().setItem(slots[i], items[i]);
                this.pageItems().put(slots[i], items[i]);
            }
        }
    }

    @ApiStatus.Internal
    Map<Integer, MenuItem> pageItems();

    default void setPageItem(int slot, MenuItem item) {
        this.pageItems().put(slot, item);
        for (Page page : pages()) page.contents().setItem(slot, item);
    }

    default void setPageItem(int[] slots, MenuItem item) {
        for (Page page : pages()) {
            for (int slot : slots) {
                page.contents().setItem(slot, item);
                this.pageItems().put(slot, item);
            }
        }
    }

    /**
     * Goes to the specified page
     *
     * @return False if there is no next page.
     */
    boolean page(T key);

    T getPageIdentifier();

    /**
     * Gets the number of pages the GUI has
     *
     * @return The number of pages
     */
    int getPagesSize();

    /**
     * Opens the GUI to a specific page for the given player
     *
     * @param player   The player to open the GUI to
     * @param openPage The specific page to open at
     */
    void open(@NotNull final HumanEntity player, final T openPage);
}
