package me.flame.woody.menus.contents;

import me.flame.woody.menus.internals.MenuView;
import me.flame.woody.menus.item.MenuItem;
import me.flame.woody.menus.Slot;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ContentsIterator implements Iterator<MenuItem> {
    @NotNull
    private Slot position;

    @NotNull
    private final ContentsIterationDirection direction;

    @NotNull
    private final MenuView menuView;

    private Slot next;

    private final int rows;

    private static final String NOTHING_MORE_NEXT =
            "Used MenuIterator#next() but nothing more" +
                    "\nFix: Use hasNext() beforehand to avoid this error.";

    private static final String GREATER_THAN_ONE_ONLY =
            "Starting row and column must be 1 or greater only." +
                    "\nFix: If you're using an algorithm for rows/cols, you might wanna check it";

    public ContentsIterator(int startingRow, int startingCol,
                        @NotNull ContentsIterationDirection direction,
                        @NotNull MenuView menu) {
        Slot prePosition = new Slot(startingRow, startingCol);
        if (!prePosition.isValid()) throw new IllegalArgumentException(GREATER_THAN_ONE_ONLY);
        this.menuView = menu;
        this.next = prePosition;
        this.position = prePosition;
        this.direction = direction;

        this.rows = menu.getStructure().getRows();
    }

    public ContentsIterator(@NotNull ContentsIterationDirection direction, @NotNull MenuView menuView) {
        this.menuView = menuView;

        Slot first = Slot.getFirst();
        this.next = first;
        this.position = first;
        this.direction = direction;

        this.rows = menuView.getStructure().getRows();
    }

    /**
     * Retrieves the next slot in the menu.
     *
     * @param emptyOnly a boolean indicating whether to retrieve only empty slots
     * @return the next empty slot in the menu, or null if no empty slot is found
     */
    public @Nullable Slot nextSlot(boolean emptyOnly) {
        if (!emptyOnly) return nextSlot();

        while (menuView.getContents().contains(position)) {
            position = direction.shift(position, this.rows);
            if (!position.isValid()) return null;
        }

        // when it becomes empty
        return position;
    }

    /**
     * Retrieves the next slot in the menu.
     *
     * @return the next empty slot in the menu, or null if no empty slot is found
     */
    public Slot nextSlot() {
        position = next;
        next = null;
        return position;
    }

    @Override
    public boolean hasNext() {
        if (next != null) return true;
        next = direction.shift(position, this.rows);
        return next.isValid();
    }

    @Override
    public MenuItem next() {
        Slot slot = nextSlot();
        if (slot != null) return menuView.getContents().getItem(slot);
        throw new NoSuchElementException(NOTHING_MORE_NEXT);
    }


    /**
     * Retrieves the next optional menu item.
     *
     * @return an Optional object containing the next MenuItem, if available.
     * Returns an empty Optional if the slot in the menu is empty.
     * Returns an empty Optional if there are no more items.
     * @throws NoSuchElementException if there are no more items in the menu.
     */
    public Optional<MenuItem> nextOptional() {
        return Optional.ofNullable(this.next());
    }
}