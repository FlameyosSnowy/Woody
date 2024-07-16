package me.flame.menus.menu;

import me.flame.menus.events.MenuCloseEvent;
import me.flame.menus.items.ClickSound;
import me.flame.menus.items.ItemResponse;
import me.flame.menus.components.nbt.ItemNbt;
import me.flame.menus.items.MenuItem;
import me.flame.menus.menu.animation.Animation;
import me.flame.menus.menu.api.*;
import me.flame.menus.menu.contents.BukkitContents;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("MethodMayBeStatic")
public final class MenuListeners implements Listener {
    private static final EnumSet<InventoryAction> TAKE = EnumSet.of(
            InventoryAction.PICKUP_ONE,
            InventoryAction.PICKUP_SOME,
            InventoryAction.PICKUP_HALF,
            InventoryAction.PICKUP_ALL,
            InventoryAction.COLLECT_TO_CURSOR,
            InventoryAction.HOTBAR_SWAP,
            InventoryAction.MOVE_TO_OTHER_INVENTORY
    );

    private static final EnumSet<InventoryAction> PLACE = EnumSet.of(
            InventoryAction.PLACE_ONE,
            InventoryAction.PLACE_SOME,
            InventoryAction.PLACE_ALL
    );

    private static final EnumSet<InventoryAction> SWAP = EnumSet.of(
            InventoryAction.HOTBAR_SWAP,
            InventoryAction.SWAP_WITH_CURSOR,
            InventoryAction.HOTBAR_MOVE_AND_READD
    );

    private static final EnumSet<InventoryAction> DROP = EnumSet.of(
            InventoryAction.DROP_ONE_SLOT,
            InventoryAction.DROP_ALL_SLOT,
            InventoryAction.DROP_ONE_CURSOR,
            InventoryAction.DROP_ALL_CURSOR
    );

    private final Menus manager;

    public MenuListeners(final Menus manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(@NotNull InventoryClickEvent event) {
        Inventory inventory = event.getInventory(), clickedInventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        if (!(event.getInventory().getHolder() instanceof SimpleMenu menu)) return;
        int slot = event.getSlot();

        if (menu instanceof MenuActionModifiable<?> menuActionModifiable) menuActionModifiable.actions().executeInventoryEventBy(event);
        if (clickedInventory == null) return;

        if (menu instanceof SlotActionsModifiable slotActionsModifiable)
            checkSlotAction(event, slotActionsModifiable, slot);
        if (menu instanceof ActionModifiable actionsModifiable)
            cancelIfModifierDetected(event, actionsModifiable, clickedInventory, inventory);
        executeItem(event, menu, event.getCurrentItem(), player, slot, manager);
    }

    private static void cancelIfModifierDetected(final @NotNull InventoryClickEvent event, final ActionModifiable menu, final @NotNull Inventory ci, final Inventory inv) {
        if (ci.getType() != InventoryType.PLAYER && modifierDetected(menu, event.getAction(), ci.getType(), inv.getType())) event.setResult(Event.Result.DENY);
    }

    private static void checkSlotAction(final @NotNull InventoryClickEvent event, final @NotNull SlotActionsModifiable menu, final int slot) {
        Map<Integer, ItemResponse> slotActions = menu.getSlotActions();
        if (slotActions.isEmpty()) return;
        ItemResponse response = slotActions.get(slot);
        if (response != null) response.execute((Player) event.getWhoClicked(), event);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true) // monitor since it doesn't actually get cancelled
    public void onGuiClose(@NotNull InventoryCloseEvent event) {
        HumanEntity player = event.getPlayer();
        if (!(event.getInventory().getHolder() instanceof SimpleMenu menu)) return;

        Plugin plugin = manager.getPlugin();
        if (!menu.isUpdating()) {
            closeActions(event, menu, player, plugin);
            return;
        }

        new BlockingCancellationCheckerRunnable(menu, event, player, plugin)
                .runTaskTimer(plugin, 1, 1);
    }

    private static class Counter {
        int number;

        Counter(int number) {
            this.number = number;
        }
    }
 
    private static void closeActions(final @NotNull InventoryCloseEvent event, final @NotNull SimpleMenu menu, final HumanEntity player, final Plugin plugin) {
        if (reopenMenuOnCancel(event, menu, player, plugin)) return;

        if (!(menu instanceof AnimationModifiable animationModifiable) || !animationModifiable.isAnimating() || !menu.getViewers().isEmpty()) return;

        List<Animation> animations = animationModifiable.getActiveAnimations();
        animations.forEach(Animation::stop);
        animations.clear();
        animationModifiable.setAnimating(false);
    }

    private static boolean reopenMenuOnCancel(final @NotNull InventoryCloseEvent event, final @NotNull SimpleMenu menu, final HumanEntity player, final Plugin plugin) {
        if (!(menu instanceof MenuActionModifiable<?> menuActionModifiable) ||
                !menuActionModifiable.actions().executeInventoryEventBy(event).contains(MenuCloseEvent.class)) return false;
        Bukkit.getScheduler().runTaskLater(plugin, () -> menu.open(player), 1L);
        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGuiDrag(@NotNull InventoryDragEvent event) {
        if (!(event.getInventory().getHolder() instanceof SimpleMenu menu)) return;
        if (menu instanceof ActionModifiable actionModifiable) checkDragClickCancellation(event, menu.size(), actionModifiable);
        if (menu instanceof MenuActionModifiable<?> actionModifiable) actionModifiable.actions().executeInventoryEventBy(event);
    }

    private static void checkDragClickCancellation(final @NotNull InventoryDragEvent event, int size, final @NotNull ActionModifiable menu) {
        if (menu.allModifiersAdded() || (!menu.areItemsPlaceable() && isDraggingOnGui(size, event.getRawSlots())))
            event.setResult(Event.Result.DENY);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onGuiOpen(@NotNull InventoryOpenEvent event) {
        if (!(event.getInventory().getHolder() instanceof SimpleMenu menu)) return;
        menu.update();
        if (menu instanceof MenuActionModifiable<?> menuActionModifiable && !menu.isUpdating()) {
            menuActionModifiable.actions().executeInventoryEventBy(event);
        }
    }

    private static boolean isDraggingOnGui(int size, @NotNull Iterable<Integer> rawSlots) {
        for (int slot : rawSlots) if (slot < size) return true;
        return false;
    }

    private static void executeItem(InventoryClickEvent event, SimpleMenu menu, ItemStack it, Player player, int slot, Menus manager) {
        Optional<MenuItem> menuItem;
        if (it == null || (menuItem = menu.getItem(slot)).isEmpty()) return;
        final MenuItem item = menuItem.orElseThrow(() -> new IllegalArgumentException("""
            You caught an ultra rare error, this usually should not be thrown by design,
            please report this to Woody,
            Option<MenuItem> was checked successfully and found that it had a value.. but this throws?
        """));

        final String nbt = ItemNbt.getString(it, "woody-menu");
        if (nbt != null && !nbt.equals(item.getUniqueId().toString())) return;

        ItemResponse clickAction = item.getClickAction();
        if (clickAction == null) return;

        ClickSound sound = item.getSound() == null ? (item.isDisablingGlobalSound() ? null : manager.getGlobalItemClickSound()) : item.getSound();
        if (sound != null) player.playSound(player.getLocation(), sound.sound(), sound.volume(), sound.pitch());

        if (item.isAsync()) CompletableFuture.runAsync(() -> clickAction.execute(player, event));
        else clickAction.execute(player, event);
    }

    private static boolean modifierDetected(@NotNull ActionModifiable menu, InventoryAction action, InventoryType ciType, InventoryType invType) {
        return menu.allModifiersAdded() || ((!menu.areItemsPlaceable() && (action == InventoryAction.MOVE_TO_OTHER_INVENTORY && invType != ciType) && PLACE.contains(action)) ||
                    (menu.areItemsRemovable() && (action == InventoryAction.MOVE_TO_OTHER_INVENTORY || TAKE.contains(action) || DROP.contains(action))) ||
                    (!menu.areItemsSwappable() && SWAP.contains(action)) ||
                    (!menu.areItemsCloneable() && (action == InventoryAction.CLONE_STACK || action == InventoryAction.UNKNOWN)));
    }

    private static class BlockingCancellationCheckerRunnable extends BukkitRunnable {
        private final SimpleMenu menu;
        private final @NotNull InventoryCloseEvent event;
        private final HumanEntity player;
        private final Plugin plugin;
        private final Counter counter = new Counter(0);

        public BlockingCancellationCheckerRunnable(final SimpleMenu menu, final @NotNull InventoryCloseEvent event, final HumanEntity player, final Plugin plugin) {
            this.menu = menu;
            this.event = event;
            this.player = player;
            this.plugin = plugin;
        }

        @Override
        public void run() {
            if (!menu.isUpdating()) {
                closeActions(event, menu, player, plugin);
                this.cancel();
                return;
            }
            if (counter.number == 20) {
                this.cancel();
                return;
            }
            counter.number++;
        }
    }
}