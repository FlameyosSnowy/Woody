package me.flame.menus.menu.api;

import me.flame.menus.items.ItemResponse;

import java.util.Map;

public interface SlotActionsModifiable {
    /**
     * The immutable list of slot actions.
     * @return slot actions
     */
    Map<Integer, ItemResponse> getSlotActions();

    /**
     * Checks if this menu utilizes slot actions
     * @return Returns true if this menu utilizes slot actions, otherwise false
     */
    default boolean hasSlotActions() {
        return !getSlotActions().isEmpty();
    }

    /**
     * Sets a slot to an item response of the menu, anytime the slot is clicked, no matter the item, it will execute.
     * @param slot the slot.
     * @param response the slot action.
     */
    void setSlotAction(int slot, ItemResponse response);
}
