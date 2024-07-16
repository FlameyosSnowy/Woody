package me.flame.menus.menu;

import me.flame.menus.menu.contents.Contents;
import me.flame.menus.menu.pagination.Pagination;
import me.flame.menus.util.ContentsFactory;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Universal menu builder for menus (Menu, PaginatedMenu).
 * @since 2.0.0
 * @author FlameyosFlow
 */
@SuppressWarnings("unused")
public class MenuBuilder extends BaseMenuBuilder<MenuBuilder> {
    @NotNull
    protected ContentsFactory contentsFunction = (menu, concurrencyProperties) -> new Contents(menu, concurrencyProperties.concurrentContents());

    public MenuBuilder(final @NotNull Menus menus, int rows) {
        super(menus, rows);
    }

    public MenuBuilder(final @NotNull Menus menus) {
        super(menus);
    }

    public MenuBuilder contentsFunction(final @NotNull ContentsFactory contentsFunction) {
        this.contentsFunction = contentsFunction;
        return this;
    }

    @NotNull
    @Contract(" -> new")
    public Menu normal() {
        checkRequirements(rows, title);
        return type.inventoryType() == InventoryType.CHEST
                ? new MenuImpl(rows, title, modifiers, menus, opener, contentsFunction)
                : new MenuImpl(type, title, modifiers, menus, opener, contentsFunction);
    }

    @NotNull
    @Contract("_ -> new")
    public Pagination<String> keyed(int pageCount) {
        checkRequirements(rows, title);
        return type.inventoryType() == InventoryType.CHEST
                ? new KeyedMenuImpl(rows, pageCount, title, modifiers, opener, menus, properties)
                : new KeyedMenuImpl(type, pageCount, title, modifiers, opener, menus, properties);
    }
}
