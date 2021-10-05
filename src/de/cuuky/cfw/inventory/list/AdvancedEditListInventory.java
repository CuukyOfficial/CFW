package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
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
    protected ItemStack getStack(int index) {
        return this.stacks.size() > index ? this.stacks.get(index) : null;
    }

    @Override
    protected int getMinPageSize() {
        return this.stacks.size();
    }

    protected Stream<ItemStack> collectNullFilteredItems() {
        return this.getItems().stream().filter(Objects::nonNull);
    }

    protected List<ItemStack> collectItemsListed() {
        return this.collectNullFilteredItems().collect(Collectors.toList());
    }

    @Override
    public abstract int getMaxPage();
}