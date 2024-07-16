package me.flame.menus.menu;

import me.flame.menus.items.ClickSound;
import me.flame.menus.patterns.IterationPattern;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: add per-player views
@SuppressWarnings("unused")
public final class Menus {
    public static final EnumSet<InventoryType> TYPES = Stream.of(InventoryType.values())
            .filter(Predicate.not(InventoryType::isCreatable))
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(InventoryType.class)));

    public Plugin getPlugin() {
        return plugin;
    }

    private final Plugin plugin;

    private final Map<InventoryType, Map.Entry<String, OpenedType>> types = new EnumMap<>(InventoryType.class);
    private final Map<Class<?>, IterationPattern> patterns = new HashMap<>(5);
    // private final Map<UUID, Menu> inventories = new HashMap<>(5);

    private ClickSound globalItemClickSound;

    public Menus(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(new MenuListeners(this), plugin);
    }

    public void putType(final @NotNull OpenedType openedType) {
        InventoryType type = openedType.inventoryType();
        if (TYPES.contains(type)) throw new IllegalArgumentException("Inventory type that was provided is not creatable: " + type.name());
        this.types.put(type, Map.entry(type.name(), openedType));
    }

    public OpenedType getType(InventoryType type) {
        return this.types.get(type).getValue();
    }

    public OpenedType getType(@NotNull String type) {
        return this.types.get(InventoryType.valueOf(type.toUpperCase())).getValue();
    }

    public void putPattern(IterationPattern pattern) {
        this.patterns.put(pattern.getClass(), pattern);
    }

    public IterationPattern getPattern(Class<IterationPattern> pattern) {
        return this.patterns.get(pattern);
    }

    public ClickSound getGlobalItemClickSound() {
        return globalItemClickSound;
    }

    public void setGlobalItemClickSound(final ClickSound globalItemClickSound) {
        this.globalItemClickSound = globalItemClickSound;
    }

    /*public Menu getPlayerMenu(UUID uniqueId) {
        return this.inventories.get(uniqueId);
    }

    public void removePlayerMenu(UUID uniqueId) {
        this.inventories.remove(uniqueId);
    }

    public void addPlayerMenu(UUID uniqueId, Menu inventory) {
        this.inventories.put(uniqueId, inventory);
    }*/
}
