package me.flame.menus.menu;

import com.google.common.collect.ImmutableList;
import me.flame.menus.menu.api.*;
import me.flame.menus.menu.loader.PagedMenuLoader;
import me.flame.menus.menu.pagination.IndexedPagination;
import me.flame.menus.modifiers.Modifier;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.contents.BukkitContents;
import me.flame.menus.menu.loader.MenuLoader;
import me.flame.menus.menu.opener.MenuOpener;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.time.Duration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public interface Menu extends InventoryHolder,
        BukkitContents,
        ActionModifiable,
        SlotActionsModifiable,
        MenuActionModifiable<Menu>,
        SizeModifiable,
        AnimationModifiable,
        SimpleMenu {

    /**
     * Updates the menu every X ticks (repeatTime)
     *
     * @param  repeatTime  the time interval between each execution of the task
     */
    default void updatePer(long repeatTime) {
        updatePer(0, repeatTime);
    }

    /**
     * Updates the menu every X time (repeatTime)
     *
     * @param  repeatTime  the time interval between each execution of the task
     */
    default void updatePer(@NotNull Duration repeatTime) {
        this.updatePer(repeatTime.toMillis() * 50);
    }

    /**
     * Updates the menu every X ticks (repeatTime) with the delay of X ticks
     *
     * @param  delay       the time interval before the first execution
     * @param  repeatTime  the time interval between each execution of the task
     */
    void updatePer(long delay, long repeatTime);

    /**
     * Updates the menu every X ticks (repeatTime) with the delay of X ticks
     *
     * @param  delay       the time interval before the first execution
     * @param  repeatTime  the time interval between each execution of the task
     */
    default void updatePer(@NotNull Duration delay, @NotNull Duration repeatTime) {
        this.updatePer(delay.toMillis() * 50, repeatTime.toMillis() * 50);
    }

    /**
     * Update the inventory with the title
     * @apiNote this does not use NMS; so while this is backwards compatible, it can be slow.
     * @param title the new title
     */
    default void updateTitle(String title) { updateTitle(Component.text(title)); }

    /**
     * Get the modifiers of this Menu
     * @return the modifiers, a mutable view.
     */
    EnumSet<Modifier> getModifiers();

    /**
     * Update the inventory with the title (RE-OPENS THE INVENTORY)
     * @param title the new title
     */
    void updateTitle(Component title);

    /**
     * Get a list of the menu items in the menu
     * @return an unmodifiable list.
     */
    @NotNull
    @Unmodifiable
    default List<MenuItem> getItemList() { return ImmutableList.copyOf(getMutableItems().values()); }

    /**
     * The direct link to the contents of this menu.
     * <p>
     * The menu relies on this mostly, and even might change to rely on this in the future.
     * @return contents
     */
    BukkitContents contents();

    MenuOpener opener();

    Menus manager();

    @NotNull
    @Contract("_, _ -> new")
    static MenuBuilder builder(Menus menus, int rows) {
        return new MenuBuilder(menus, rows);
    }

    @NotNull
    @Contract("_ -> new")
    static MenuBuilder builder(Menus menus) {
        return new MenuBuilder(menus);
    }

    @NotNull
    @Contract("_, _ -> new")
    static PaginatedBuilder paginated(Menus menus, int rows) {
        return new PaginatedBuilder(menus, rows);
    }

    @NotNull
    @Contract("_ -> new")
    static PaginatedBuilder paginated(Menus menus) {
        return new PaginatedBuilder(menus);
    }

    static @NotNull Menu create(Menus manager, @NotNull MenuLoader loader) {
        Menu menu = loader.structure().type().inventoryType() == InventoryType.CHEST
                ? new MenuImpl(loader.structure().rows(), loader.title(), EnumSet.noneOf(Modifier.class), manager, loader.opener(), (loadedMenu, properties) -> loader.load(loadedMenu))
                : new MenuImpl(loader.structure().type(), loader.title(), EnumSet.noneOf(Modifier.class), manager, loader.opener(), (loadedMenu, properties) -> loader.load(loadedMenu));
        loader.setup(menu);

        BukkitContents contents = loader.load(menu);
        if (contents != null) menu.replaceContents(contents);

        return menu;
    }

    static @NotNull IndexedPagination create(Menus manager, @NotNull PagedMenuLoader loader) {
        Map.Entry<Integer, MenuItem> nextItem = loader.nextItem();
        Map.Entry<Integer, MenuItem> previousItem = loader.previousItem();
        int pageCount = loader.pageCount();
        Structure structure = loader.structure();
        PaginatedMenuImpl menu = structure.type().inventoryType() == InventoryType.CHEST
                ? new PaginatedMenuImpl(structure.rows(), pageCount, loader.title(), EnumSet.noneOf(Modifier.class), nextItem.getValue(), previousItem.getValue(), nextItem.getKey(), previousItem.getKey(), loader.opener(), manager, loader.concurrencyProperties(),
                        (loadedMenu, properties) -> loader.load((IndexedPagination) loadedMenu))
                : new PaginatedMenuImpl(structure.type(), pageCount, loader.title(), EnumSet.noneOf(Modifier.class), nextItem.getValue(), previousItem.getValue(), nextItem.getKey(), previousItem.getKey(), loader.opener(), manager, loader.concurrencyProperties(),
                        (loadedMenu, properties) -> loader.load((IndexedPagination) loadedMenu));
        if (pageCount != menu.paginationSize()) {
            throw new IllegalArgumentException("The list of pages is not equal to the pageCount." + "\nPages size: " + menu.paginationSize() + "\nPage count: " + pageCount);
        }
        loader.setup(menu);
        return menu;
    }

    /**
     * Complex and Fast builder to build (paginated) menus from a list of strings or a so-called pattern.
     * <p>
     * Example usage:
     * <pre>{@code
     *     Map<Character, MenuItem> menuItems = ImmutableMap.of(
     *          'X', ItemBuilder.of(Material.STONE).buildItem();
     *          'K', ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).buildItem();
     *     );
     *     Menu menu = MenuLayoutBuilder.bind(menuItems)
     *                  .pattern(
     *                      "KKKKKKKKK"
     *                      "KXX   XXK"
     *                      "KX     XK"
     *                      "KX     XK"
     *                      "KXX   XXK"
     *                      "KKKKKKKKK"
     *                  )
     *                  .createMenu("Awesome");
     * }</pre>
     * @author FlameyosFlow
     * @param itemMap the default identifiers mapped to the items.
     * @since 1.2.0, 100% Stabilized at 1.5.7
     */
    @Contract(value = "_ -> new", pure = true)
    static @NotNull MenuLayoutBuilder layout(Map<Character, MenuItem> itemMap) {
        return new MenuLayoutBuilder(itemMap);
    }

    /**
     * Complex and Fast builder to build (paginated) menus from a list of strings or a so-called pattern.
     * <p>
     * Example usage:
     * <pre>{@code
     *     Map<Character, MenuItem> menuItems = ImmutableMap.of(
     *          'X', ItemBuilder.of(Material.STONE).buildItem();
     *          'K', ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE).buildItem();
     *     );
     *     Menu menu = MenuLayoutBuilder.bind(menuItems)
     *                  .pattern(
     *                      "KKKKKKKKK"
     *                      "KXX   XXK"
     *                      "KX     XK"
     *                      "KX     XK"
     *                      "KXX   XXK"
     *                      "KKKKKKKKK"
     *                  )
     *                  .createMenu("Awesome");
     * }</pre>
     * @author FlameyosFlow
     * @since 1.2.0, 100% Stabilized at 1.5.7
     * @see Menu#layout(Map) 
     */
    @Contract(value = "-> new", pure = true)
    static @NotNull MenuLayoutBuilder layout() {
        return new MenuLayoutBuilder(new HashMap<>(10));
    }

    Structure structure();

    default int addItem(@NotNull final MenuItem... items) {
        return this.contents().addItem(items);
    }

    default int addItem(final List<MenuItem> toAdd, final @NotNull MenuItem... items) {
        return this.contents().addItem(toAdd, items);
    }
}
