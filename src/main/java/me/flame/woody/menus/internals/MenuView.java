package me.flame.woody.menus.internals;

import me.flame.woody.menus.api.Menu;
import me.flame.woody.menus.Structure;
import me.flame.woody.menus.contents.Contents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class MenuView implements InventoryHolder {
    private final Contents contents;
    private final Structure structure;
    private final Player player;
    private final Inventory inventory;
    private final Menu menu;

    public MenuView(@NotNull Menu menu, Player player) {
        this.structure = menu.getStructure(player);
        this.contents = menu.getContents(player, this, structure);
        this.inventory = Bukkit.createInventory(this, structure.getSize(), menu.getTitle(player));
        this.player = player;
        this.menu = menu;
    }

    public Contents getContents() {
        return contents;
    }

    public Structure getStructure() {
        return structure;
    }

    public Menu getMenu() {
        return menu;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
