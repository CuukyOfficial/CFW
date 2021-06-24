package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.inventory.AdvancedInventory;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.ItemClick;
import de.cuuky.cfw.inventory.ItemInfo;
import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.version.types.Materials;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class AdvancedListInventory<T> extends AdvancedInventory {

    private final List<T> list;
    private int emptyClicked = 0;

    public AdvancedListInventory(AdvancedInventoryManager manager, Player player, List<T> list) {
        super(manager, player);

        this.list = list;
    }

    @Deprecated
    public AdvancedListInventory(AdvancedInventoryManager manager, Player player, Supplier<List<T>> list) {
        this(manager, player, list.get());
    }

    public AdvancedListInventory(AdvancedInventoryManager manager, Player player) {
        this(manager, player, (List<T>) null);
    }

    protected String getEmptyName() {
        if (emptyClicked < 10) return "§cNothing here";
        else if (emptyClicked < 20) return "§csrsly, nothing here";
        else if (emptyClicked < 30) return "§4stop?";
        else if (emptyClicked < 35) return "§4I SAID STOP";
        else if (emptyClicked < 40) return  "§4WTF MEN";
        else if (emptyClicked < 45) return "§4REEEEEEEEEEEEEE";
        else if (emptyClicked < 60) return "§fone last warning...";
        else if (emptyClicked < 100) return "§c§k§4Injecting virus...§c§k";
        else if (emptyClicked < 120) return "§2Not impressed?";
        else if (emptyClicked < 130) return "§2Yeah that's it now... I give up";
        else if (emptyClicked < 1000) return "§24 real now, bye";
        else if (emptyClicked < 21474) return "§fok";
        else return "get a life, nerd";
    }

    protected void addListItem(int index, T item) {
        this.addItem(index, this.getItemStack(item), this.getClick(item));
    }

    protected int getEmptyClicked() {
        return this.emptyClicked;
    }

    protected boolean copyList() {
        return true;
    }

    protected ItemInfo getEmptyInfoStack() {
        return new ItemInfo(this.getCenter(), new ItemBuilder()
                .displayname(this.getEmptyName())
                .itemstack(Materials.POPPY.parseItem()).lore(emptyClicked < 30 ? "§f:(" : "§f>:(").build());
    }

    protected ItemClick getEmptyInfoClick() {
        return (event) -> emptyClicked++;
    }

    protected abstract ItemStack getItemStack(T item);

    protected abstract ItemClick getClick(T item);

    protected int getRecommendedSize(int min, int max) {
        int size = this.calculateInvSize(this.getList().size());
        return (size < min ? min : Math.min(size, max)) + this.getHotbarSize();
    }

    protected int getRecommendedSize(int min) {
        return this.getRecommendedSize(min, 54);
    }

    protected int getRecommendedSize() {
        return this.getRecommendedSize(27);
    }

    @Override
    protected final int getStartPage() {
        return 1;
    }

    @Override
    protected final int getMinPage() {
        return 1;
    }

    @Override
    public int getMaxPage() {
        List<T> original = this.getList();
        if (original == null || original.size() == 0)
            return 1;

        return (int) Math.ceil((float) original.size() / (float) this.getUsableSize());
    }

    @Override
    public void refreshContent() {
        List<T> original = this.getList();
        if (original == null || original.isEmpty()) {
            ItemInfo info = this.getEmptyInfoStack();
            if (info != null)
                this.addItem(info.getIndex(), info.getStack(), this.getEmptyInfoClick());
            return;
        }

        int start = this.getUsableSize() * (this.getPage() - 1);
        List<T> list = this.copyList() ? new ArrayList<>(original) : original;
        for (int i = 0; (start + i) < list.size() && i < this.getUsableSize(); i++) {
            T item = list.get(i + start);
            this.addListItem(i, item);
        }
    }

    public List<T> getList() {
        return list;
    }
}