package me.flame.menus.menu.api;

import me.flame.menus.modifiers.Modifier;

public interface ActionModifiable {
    /**
     * Checks if all modifiers have been added
     * @return if the size of modifiers are equal to 4
     */
    boolean allModifiersAdded();

    /**
     * Add a modifier to prevent a player from doing an action.
     * @param modifier the action to prevent the player from doing
     * @return the result of the operation
     */
    boolean addModifier(Modifier modifier);

    /**
     * Remove a modifier to allow a player to do an action once again.
     * @param modifier the action to allow a player to do
     * @return the result of the operation
     */
    boolean removeModifier(Modifier modifier);

    /**
     * Add every modifier to prevent a player from doing all actions
     * @return the result of the operation
     */
    boolean addAllModifiers();

    /**
     * Remove every modifier to allow a player to do all actions
     */
    void removeAllModifiers();

    /**
     * Check if items are placeable in the menu
     * @return if the items are placeable
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean areItemsPlaceable();

    /**
     * Check if items are removable in the menu
     * @return if the items are removable
     */
    boolean areItemsRemovable();

    /**
     * Check if items are swappable in the menu
     * @return if the items are swappable
     */
    boolean areItemsSwappable();

    /**
     * Check if items are cloneable in the menu
     * @return if the items are cloneable
     */
    boolean areItemsCloneable();
}
