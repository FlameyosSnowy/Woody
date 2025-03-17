package me.flame.woody.contents;

import me.flame.woody.Slot;
import me.flame.woody.Structure;
import me.flame.woody.internals.MenuView;
import me.flame.woody.item.MenuItem;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class ContentsBuilder {
    private final Contents contents;

    ContentsBuilder(Structure structure, MenuView view) {
        this.contents = new Contents(structure, view);
    }

    public ContentsBuilder addItems(MenuItem... items) {
        contents.addItems(items);
        return this;
    }

    public ContentsBuilder addItems(Map<Integer, MenuItem> items) {
        contents.addItems(items);
        return this;
    }

    public ContentsBuilder addItems(Contents contents) {
        this.contents.addItems(contents);
        return this;
    }

    public ContentsBuilder removeItems(MenuItem... items) {
        contents.removeItems(items);
        return this;
    }

    public ContentsBuilder iterate(ContentsIterationDirection direction, IterationResponse response) {
        ContentsIterator iterator = contents.iterator(direction);
        while (iterator.hasNext()) {
            Slot slot = iterator.nextSlot();
            MenuItem item = contents.getItem(slot);
            response.accept(slot, contents, item);
        }
        return this;
    }

    public ContentsBuilder iterate(IterationResponse response) {
        return iterate(ContentsIterationDirection.HORIZONTAL, response);
    }

    public ContentsBuilder draw(@NotNull Consumer<Contents> consumer) {
        consumer.accept(contents);
        return this;
    }

    public ContentsBuilder draw(MenuItem item) {
        for (int i = 0; i < contents.size(); i++) {
            contents.setItem(i, item);
        }
        return this;
    }

    public ContentsBuilder layout(MenuLayout layout) {
        for (int i = 0; i < contents.size(); i++) {
            int row = i / 9 + 1;
            int column = i % 9 + 1;
            contents.setItem(i, layout.getMappedItem(row, column));
        }
        return this;
    }

    public ContentsBuilder drawIfEmpty(MenuItem item) {
        for (int i = 0; i < contents.size(); i++) {
            if (contents.getItem(i) == null) contents.setItem(i, item);
        }
        return this;
    }

    public Contents build() {
        return contents;
    }
}
