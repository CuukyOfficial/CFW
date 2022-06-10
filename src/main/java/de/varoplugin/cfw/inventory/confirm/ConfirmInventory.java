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

package de.varoplugin.cfw.inventory.confirm;

import java.util.function.Consumer;

import org.bukkit.entity.Player;

import com.cryptomorin.xseries.XMaterial;

import de.varoplugin.cfw.inventory.AdvancedInventory;
import de.varoplugin.cfw.inventory.AdvancedInventoryManager;
import de.varoplugin.cfw.inventory.ItemInfo;
import de.varoplugin.cfw.utils.item.BuildItem;

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
        return new ItemInfo(this.getCenter() - 2, new BuildItem().displayName("§2Yes").material(XMaterial.GREEN_DYE).build());
    }

    protected ItemInfo getNoItem() {
        return new ItemInfo(this.getCenter() + 2, new BuildItem().displayName("§4No").material(XMaterial.RED_DYE).build());
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
