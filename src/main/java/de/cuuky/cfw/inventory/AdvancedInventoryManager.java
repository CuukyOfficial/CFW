/*
 * MIT License
 * 
 * Copyright (c) 2020-2022 CuukyOfficial
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.cuuky.cfw.inventory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import de.cuuky.cfw.manager.FrameworkManager;
import de.cuuky.cfw.manager.FrameworkManagerType;

public class AdvancedInventoryManager extends FrameworkManager {

    private final List<AdvancedInventory> inventories = new CopyOnWriteArrayList<>();

    public AdvancedInventoryManager(JavaPlugin ownerInstance) {
        super(FrameworkManagerType.ADVANCED_INVENTORY, ownerInstance);
        this.ownerInstance.getServer().getPluginManager().registerEvents(new AdvancedInventoryListener(this), ownerInstance);
    }

    protected AdvancedInventory registerInventory(AdvancedInventory inventory) {
        this.inventories.add(inventory);
        return inventory;
    }

    protected boolean unregisterInventory(AdvancedInventory inventory) {
        return this.inventories.remove(inventory);
    }

    @Override
    public void disable() {
        this.closeInventories();
    }

    public void updateInventories(AdvancedInventory self, Function<AdvancedInventory, Boolean> filter) {
        for (AdvancedInventory inventory : this.inventories)
            if ((self != null && !inventory.equals(self)) && filter.apply(inventory))
                inventory.update();
    }

    public void updateInventories(Function<AdvancedInventory, Boolean> filter) {
        this.updateInventories(null, filter);
    }

    @SafeVarargs
    public final void updateInventories(Class<? extends AdvancedInventory>... clazzes) {
        List<Class<? extends AdvancedInventory>> clazzList = Arrays.asList(clazzes);
        this.updateInventories(inv -> clazzList.contains(inv.getClass()));
    }

    public void closeInventories() {
        this.inventories.forEach(AdvancedInventory::close);
    }

    public List<AdvancedInventory> getInventories() {
        return inventories;
    }

    public AdvancedInventory getInventory(Player player) {
        return inventories.stream().filter(inv -> inv.getPlayer() != null && inv.getPlayer().equals(player)).findFirst().orElse(null);
    }

    public AdvancedInventory getInventory(Inventory inventory) {
        return inventories.stream().filter(inv -> inv.getInventory() != null && inv.getInventory().equals(inventory)).findFirst().orElse(null);
    }

    // This is a method that belongs to a temporary hotfix and should be removed once the issue is fixed properly
    @Deprecated
    public AdvancedInventory getPrevInventory(Inventory inventory) {
        return inventories.stream().filter(inv -> inv.getPrevious() != null && inv.getPrevious().getInventory() != null && inv.getPrevious().getInventory().equals(inventory)).findFirst().orElse(null);
    }
}
