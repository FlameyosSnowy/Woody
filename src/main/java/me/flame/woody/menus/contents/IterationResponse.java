package me.flame.woody.menus.contents;

import me.flame.woody.menus.Slot;
import me.flame.woody.menus.item.MenuItem;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IterationResponse {
    void accept(Slot slot, Contents contents, @Nullable MenuItem item);
}
