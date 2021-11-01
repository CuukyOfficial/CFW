package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AdvancedEditListInventory extends AdvancedEditInventory {

    private final List<ItemStack> stacks;

    public AdvancedEditListInventory(AdvancedInventoryManager manager, Player player, List<ItemStack> stacks) {
        super(manager, player);

        this.stacks = new ArrayList<>(stacks);
    }

    @Override
    protected Collection<ItemStack> getInitialItems() {
        return this.stacks;
    }

    @Override
    protected int getMinSize() {
        return this.stacks.size();
    }

    protected Stream<ItemStack> collectNullFilteredItems() {
        return this.getItems().stream().filter(Objects::nonNull);
    }

    protected List<ItemStack> collectItemsListed() {
        return this.collectNullFilteredItems().collect(Collectors.toList());
    }
}