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

package de.cuuky.cfw.inventory.list;

import de.cuuky.cfw.hooking.HookManager;
import de.cuuky.cfw.hooking.hooks.chat.ChatHook;
import de.cuuky.cfw.hooking.hooks.chat.ChatHookHandler;
import de.cuuky.cfw.inventory.AdvancedInventoryManager;
import de.cuuky.cfw.inventory.Info;
import de.cuuky.cfw.inventory.ItemClick;
import de.cuuky.cfw.inventory.ItemInfo;
import de.cuuky.cfw.utils.item.BuildItem;
import de.cuuky.cfw.version.types.Materials;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AdvancedListInventory<T> extends AdvancedItemShowInventory {

    private List<T> list;
    private int emptyClicked = 0;

    public AdvancedListInventory(AdvancedInventoryManager manager, Player player, List<T> list) {
        super(manager, player);

        this.updateList(list);
    }

    @Deprecated
    public AdvancedListInventory(AdvancedInventoryManager manager, Player player, Supplier<List<T>> list) {
        this(manager, player, list.get());
    }

    private boolean checkEmpty() {
        if (this.list.isEmpty()) {
            ItemInfo info = this.getEmptyInfoStack();
            if (info != null) this.addItem(info.getIndex(), info.getStack(), this.getEmptyInfoClick());
            return true;
        }
        return false;
    }

    protected void updateList(List<T> list) {
        assert list != null;
        this.list = this.copyList() ? new ArrayList<>(list) : list;
    }

    protected String getEmptyName() {
        if (emptyClicked < 10) return "§cNothing here";
        else if (emptyClicked < 20) return "§csrsly, nothing here";
        else if (emptyClicked < 30) return "§4stop?";
        else if (emptyClicked < 35) return "§4I SAID STOP";
        else if (emptyClicked < 40) return "§4WTF MEN";
        else if (emptyClicked < 45) return "§4REEEEEEEEEEEEEE";
        else if (emptyClicked < 60) return "§fone last warning...";
        else if (emptyClicked < 100) return "§c§k§4Injecting virus...§c§k";
        else if (emptyClicked < 120) return "§2Not impressed?";
        else if (emptyClicked < 130) return "§2Yeah that's it now... I give up";
        else if (emptyClicked < 200) return "§24 real now, bye";
        else if (emptyClicked < 21474) return "§fok";
        else return "get a life, nerd";
    }

    protected int getEmptyClicked() {
        return this.emptyClicked;
    }

    protected ItemInfo getEmptyInfoStack() {
        return new ItemInfo(this.getCenter(), new BuildItem().displayName(this.getEmptyName())
                .material(Materials.POPPY).lore(emptyClicked < 30 ? "§f:(" : "§f>:(").build());
    }

    protected ItemClick getEmptyInfoClick() {
        return (event) -> emptyClicked++;
    }

    protected boolean copyList() {
        return false;
    }

    protected void addJumpMap() {
        ItemInfo info = this.getJumpToItemInfo();
        if (info != null && this.getHookManager() != null && this.getMinPage() != this.getMaxPage())
            this.addItem(info.getIndex(), info.getStack(), this.getJumpToClick());
    }

    protected ItemInfo getJumpToItemInfo() {
        if (this.getHookManager() == null || this.getInfo(Info.HOTBAR_SIZE) == 0) return null;
        return new ItemInfo(this.getUsableSize() + 5,
                new BuildItem().displayName("§2Jump to...").material(Materials.MAP).build());
    }

    protected ItemClick getJumpToClick() {
        return (event) -> {
            this.getHookManager().registerHook(new ChatHook(getPlayer(), "§7Enter page:", new ChatHookHandler() {
                @Override
                public boolean onChat(AsyncPlayerChatEvent event) {
                    int page;
                    try {
                        page = Integer.parseInt(event.getMessage());
                    } catch (NumberFormatException e) {
                        getPlayer().sendMessage("§cPlease enter a valid number!");
                        return false;
                    }

                    setPage(page);
                    open();
                    return true;
                }
            }));
            this.close();
        };
    }

    @Override
    protected Map.Entry<ItemStack, ItemClick> getInfo(int index) {
        if (this.list.size() <= index) return null;
        T item = this.list.get(index);
        return new AbstractMap.SimpleEntry<>(this.getItemStack(item), this.getClick(item));
    }

    @Override
    protected int getMinSize() {
        return Math.max(27, this.list.size());
    }

    protected abstract ItemStack getItemStack(T item);

    protected abstract ItemClick getClick(T item);

    protected HookManager getHookManager() {
        return null;
    }

    @Override
    public int getMaxPage() {
        List<T> original = this.list;
        if (original.size() == 0) return 1;
        return (int) Math.ceil((float) original.size() / (float) this.getUsableSize());
    }

    @Override
    public void refreshContent() {
        if (this.checkEmpty()) return;
        super.refreshContent();
        this.addJumpMap();
    }

    public final List<T> getList() {
        return this.list;
    }
}