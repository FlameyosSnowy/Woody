package me.flame.woody;

import me.flame.woody.internals.MenuView;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuManager {
    private static final Map<UUID, MenuView> menus = new HashMap<>();

    public MenuManager(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new MenuListeners(this), plugin);
    }

    public MenuView getOpenedMenu(UUID uuid) {
        return menus.get(uuid);
    }

    public void insertOpenedMenu(UUID uuid, MenuView menu) {
        menus.put(uuid, menu);
    }

    public void removeOpenedMenu(UUID uuid) {
        menus.remove(uuid);
    }

    public void openMenu(Player player, MenuView menu) {
        insertOpenedMenu(player.getUniqueId(), menu);
        player.openInventory(menu.getInventory());
    }
}
