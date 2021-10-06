package de.cuuky.cfw.inventory.confirm;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.ItemInfo;
import de.cuuky.cfw.utils.item.BuildItem;
import de.cuuky.cfw.version.types.Materials;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ConfirmInventory extends AdvancedInventory {

    private final String title;
    private final Consumer<Boolean> resultReceiver;

    public ConfirmInventory(AdvancedInventoryManager manager, Player player, String title, Consumer<Boolean> result) {
        super(manager, player);

        this.title = title;
        this.resultReceiver = result;
    }

    public ConfirmInventory(AdvancedInventory from, String title, Consumer<Boolean> result) {
        this(from.getManager(), from.getPlayer(), title, result);
    }

    protected ItemInfo getYesItem() {
        return new ItemInfo(this.getCenter() + 2, new BuildItem().displayName("§2Yes").material(Materials.GREEN_DYE).build());
    }

    protected ItemInfo getNoItem() {
        return new ItemInfo(this.getCenter() - 2, new BuildItem().displayName("§4No").material(Materials.RED_DYE).build());
    }

    @Override
    public int getSize() {
        return 36;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    private void addDecisionItem(ItemInfo decision, boolean accept) {
        this.addItem(decision.getIndex(), decision.getStack(), (event) -> {
            this.resultReceiver.accept(accept);
            this.close(this.getPrevious() == null);
        });
    }

    @Override
    public void refreshContent() {
        this.addDecisionItem(this.getYesItem(), true);
        this.addDecisionItem(this.getNoItem(), false);
    }
}