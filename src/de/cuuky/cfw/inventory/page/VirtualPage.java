package de.cuuky.cfw.inventory.page;

import de.cuuky.cfw.inventory.Info;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

class VirtualPage implements Page<AdvancedPageInventory> {

    private final Runnable runnable;
    private final Supplier<Integer> size;
    private final Supplier<String> title;
    private final List<Info<?>> infos = new ArrayList<>();

    public VirtualPage(Runnable runnable, Supplier<Integer> size, Supplier<String> title) {
        this.runnable = runnable;
        this.size = size;
        this.title = title;
        this.generateInfoList();
    }

    private void generateInfoList() {
        if (this.size != null)
            infos.add(Info.SIZE);
        if (this.title != null)
            infos.add(Info.TITLE);
    }

    @Override
    public AdvancedPageInventory getInventory() {
        return null;
    }

    @Override
    public void refreshContent() {
        runnable.run();
    }

    @Override
    public int getSize() {
        return this.size.get();
    }

    @Override
    public String getTitle() {
        return this.title.get();
    }

    @Override
    public int getPriority() {
        return 1;
    }

    @Override
    public List<Info<?>> getProvidedInfos() {
        return this.infos;
    }
}