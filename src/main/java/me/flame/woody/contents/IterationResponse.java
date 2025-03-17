package me.flame.woody.contents;

import me.flame.woody.Slot;
import me.flame.woody.item.MenuItem;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface IterationResponse {
    void accept(Slot slot, Contents contents, @Nullable MenuItem item);
}
