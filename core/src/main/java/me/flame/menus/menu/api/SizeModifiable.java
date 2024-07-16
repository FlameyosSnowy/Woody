package me.flame.menus.menu.api;

public interface SizeModifiable {
    /**
     * Check if the menu has the ability to size itself when it's full
     * @return true if it can.
     */
    boolean isDynamicSizing();
    /**
     * Check if the menu has the ability to size itself when it's full
     * @return true if it can.
     */
    void setDynamicSizing(boolean dynamicSizing);

    /**
     * Checks if this menu can be resized, usually returns false when the menu is at maximum rows, or does not use a resizable inventory type.
     * @return true if it can be resized
     */
    boolean canResize();

    void grow();

    void grow(int growRows);
}
