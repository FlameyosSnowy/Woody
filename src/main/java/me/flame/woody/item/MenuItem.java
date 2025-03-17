package me.flame.woody.item;

import me.flame.woody.internals.MenuView;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MenuItem {
    private final ItemStack itemStack;
    private final ActionResponse response;
    private final Sound sound;

    public MenuItem(ItemStack itemStack, ActionResponse response, Sound sound) {
        this.itemStack = itemStack;
        this.response = response;
        this.sound = sound;
    }

    public MenuItem(ItemStack itemStack, ActionResponse response) {
        this(itemStack, response, null);
    }

    public MenuItem(ItemStack itemStack) {
        this(itemStack, null, null);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ActionResponse getResponse() {
        return response;
    }

    public Sound getSound() {
        return sound;
    }

    public Component getDisplayName() {
        if (!itemStack.hasItemMeta()) {
            return null; // we should not be here, ever.
        }
        return itemStack.getItemMeta().displayName();
    }

    public List<Component> getLore() {
        if (!itemStack.hasItemMeta()) {
            return null; // we should not be here, ever.
        }
        return itemStack.getItemMeta().lore();
    }

    public int getAmount() {
        return itemStack.getAmount();
    }

    public int getMaxStackSize() {
        return itemStack.getMaxStackSize();
    }

    public void setAmount(int amount) {
        itemStack.setAmount(amount);
    }

    public void addAmount(int amount) {
        itemStack.add(amount);
    }

    public void removeAmount(int amount) {
        itemStack.subtract(amount);
    }

    public void click(MenuView view, InventoryClickEvent event) {
        if (sound != null) event.getWhoClicked().playSound(sound);
        if (response != null) response.accept(view, event);
    }
}
