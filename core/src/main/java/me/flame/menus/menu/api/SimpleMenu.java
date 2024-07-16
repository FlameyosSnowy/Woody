package me.flame.menus.menu.api;

import me.flame.menus.menu.OpenedType;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SimpleMenu extends ItemHolder {
    /**
     * Get how many rows there are in the menu
     * @return the number of rows
     */
    int rows();

    /**
     * Get how many columns there are in the menu
     * @return the number of rows
     */
    int columns();

    /**
     * Get the size of the menu
     * @return the size of the menu
     */
    int size();

    /**
     * Open the inventory for the provided player.
     * <p>
     * Do NOT call this in {@link InventoryOpenEvent} under any circumstances.
     * @apiNote Will not work if the player is sleeping.
     * @param entity the provided entity to open the inventory for.
     */
    void open(@NotNull HumanEntity entity);

    /**
     * Closes the menu for the player.
     * @param player to close the inventory for.
     */
    void close(@NotNull final HumanEntity player);

    /**
     * Get the current players viewing this inventory.
     * @return the viewers of this inventory.
     */
    List<HumanEntity> getViewers();

    /**
     * Update the inventory which recreates the items on default
     */
    void update();


    /**
     * Get the title of the menu as a legacy String.
     * @return the title
     * @apiNote  Use {@link #title()}, it has the same control, but adventure.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval(inVersion = "3.1.0")
    default String getTitle() {
        return title().toString();
    }

    /**
     * Get the title of the menu as a {@link Component}.
     * <p>
     * Component+ exists to allow support for legacy and adventure titles.
     * @return the title
     */
    Component title();

    /**
     * get the type of this menu
     * @return the type of this menu.
     */
    OpenedType getType();

    /**
     * Checks if the menu is updating and doing a heavy task
     * @return if the menu is updating
     */
    boolean isUpdating();
}
