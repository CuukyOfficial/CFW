package de.cuuky.cfw.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.cuuky.cfw.item.ItemBuilder;
import de.cuuky.cfw.menu.utils.PageAction;
import de.cuuky.cfw.version.BukkitVersion;
import de.cuuky.cfw.version.VersionUtils;
import de.cuuky.cfw.version.types.Materials;

public abstract class SuperInventory {

	// AUTHOR: "Cuuky",
	// VERSION: "0.3.3";

	private static boolean fill_inventory, animations;

	private static ItemStack forward, backwards;

	static {
		forward = new ItemBuilder().displayname("§aForwards").itemstack(new ItemStack(Material.ARROW)).build();
		backwards = new ItemBuilder().displayname("§cBackwards").itemstack(new ItemStack(Material.ARROW)).build();

		fill_inventory = true;
		animations = false;
	}

	protected SuperInventoryManager manager;
	protected String firstTitle, title;
	protected ArrayList<Integer> modifier;
	protected Inventory inv;
	protected Player opener;
	protected boolean hasMorePages, isLastPage, homePage, ignoreNextClose, setModifier;
	protected int page, size;
	
	private HashMap<ItemMeta, Runnable> itemlinks;

	public SuperInventory(String title, Player opener, int size, boolean homePage) {
		this.firstTitle = title;
		this.opener = opener;
		this.page = 1;
		this.homePage = homePage;
		this.size = size;
		this.title = getPageUpdate();
		this.inv = Bukkit.createInventory(null, size != 54 ? size + 9 : size, getPageUpdate());
		this.itemlinks = new HashMap<ItemMeta, Runnable>();

		this.modifier = new ArrayList<Integer>(Arrays.asList(inv.getSize() - 1, inv.getSize() - 9, inv.getSize() - 5));
	}

	@SuppressWarnings("deprecation")
	private void doAnimation() {
		if(!animations)
			return;

		HashMap<Integer, ItemStack> itemlist = new HashMap<Integer, ItemStack>();
		for(int i = 0; i < (inv.getSize() - 9); i++)
			itemlist.put(i, inv.getItem(i));

		for(int i = 0; i < (inv.getSize() - 9); i++)
			inv.setItem(i, null);
		opener.updateInventory();

		int delay = (int) (600) / (getSize());

		Bukkit.getScheduler().scheduleAsyncDelayedTask(this.manager.getInstance().getPluginInstance(), new Runnable() {

			@Override
			public void run() {
				int middle = (int) Math.ceil(itemlist.size() / 2);
				for(int radius = 0; middle + radius != itemlist.size(); radius++) {
					if(!isOpen())
						break;

					try {
						Thread.sleep(delay);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}

					inv.setItem(middle + radius, itemlist.get(middle + radius));
					opener.updateInventory();

					try {
						Thread.sleep(delay);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}

					inv.setItem(middle - radius, itemlist.get(middle - radius));
					opener.updateInventory();
				}

				if((inv.getSize() - 9) % 2 == 0 && isOpen()) {
					try {
						Thread.sleep(delay);
					} catch(InterruptedException e) {
						e.printStackTrace();
					}
					inv.setItem(0, itemlist.get(0));
					opener.updateInventory();
				}
			}
		}, 0);
	}

	private void fillSpace() {
		if(!fill_inventory)
			return;

		for(int i = 0; i < inv.getSize(); i++)
			if(inv.getItem(i) == null)
				inv.setItem(i, new ItemBuilder().displayname("§c").itemstack(new ItemStack(Materials.BLACK_STAINED_GLASS_PANE.parseMaterial(), 1, (short) 15)).build());
	}

	/*
	 * Getter for the back button
	 */
	private String getBack() {
		if(!homePage)
			return "§4Back";
		else
			return "§4Close";
	}

	/*
	 * String for page title
	 */
	private String getPageUpdate() {
		String suff = (hasMorePages ? " Â§7" + page : "");
		return firstTitle + (firstTitle.length() + suff.length() > 32 ? "" : suff);
	}

	/*
	 * Set Back and Forwards
	 */
	private void setSwitcher() {
		if(!setModifier)
			return;
		
		inv.setItem(modifier.get(2), new ItemBuilder().displayname(getBack()).itemstack(getBack().equals("Â§4Bacl") ? new ItemStack(Materials.STONE_BUTTON.parseMaterial()) : Materials.REDSTONE.parseItem()).build());
		if(!hasMorePages)
			return;

		if(!isLastPage)
			inv.setItem(modifier.get(0), forward);

		if(page != 1)
			inv.setItem(modifier.get(1), backwards);
	}

	protected void close(boolean unregister) {
		if(!unregister)
			ignoreNextClose = true;
		else
			this.manager.unregisterInventory(this);

		this.opener.closeInventory();
	}

	/*
	 * Enter a runnable where which is being executed when this item was clicked
	 */
	protected void linkItemTo(int location, ItemStack stack, Runnable runnable) {
		inv.setItem(location, stack);
		itemlinks.put(stack.getItemMeta(), runnable);
	}

	public void back() {
		close(true);

//		if(!onBackClick())
//			new MainMenu(opener);
	}

	public void clear(boolean all) {
		for(int i = 0; i < inv.getContents().length; i++) {
			if(modifier.contains(i) && !all)
				continue;

			inv.setItem(i, new ItemStack(Material.AIR));
		}
	}

	public void closeInventory() {
		if(ignoreNextClose) {
			ignoreNextClose = false;
			return;
		}

		this.manager.unregisterInventory(this);
		this.opener.closeInventory();
	}

	/*
	 * Executes itemlinks
	 */
	public void executeLink(ItemStack item) {
		for(ItemMeta stack : itemlinks.keySet())
			if(stack.getDisplayName().equals(item.getItemMeta().getDisplayName())) {
				if(itemlinks.get(stack) != null)
					itemlinks.get(stack).run();
				break;
			}
	}

	public int getFixedSize(int size) {
		if(VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_8))
			return(size < 1 ? 1 : (size > 64 ? 64 : size));
		else
			return size;
	}

	/*
	 * String for page title
	 */
	public void open() {
		if(manager == null)
			throw new IllegalStateException("Cannot open inventory without manager defined");
		
		isLastPage = this.onOpen();
		if(!isLastPage)
			hasMorePages = true;

		setSwitcher();
		fillSpace();
		
		if(this.opener.getOpenInventory() == null || !this.opener.getOpenInventory().getTopInventory().equals(this.inv))
			this.opener.openInventory(inv);
		
		doAnimation();
	}

	public void pageActionChanged(PageAction action) {
		onInventoryAction(action);
	}

	public void pageBackwards() {
		page--;
		updateInventory();
	}

	public void pageForwards() {
		page++;
		updateInventory();
	}

	/*
	 * A little method to reopen the gui, for example if you used a AnvilGUI
	 */
	public void reopenSoon() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(this.manager.getInstance().getPluginInstance(), new Runnable() {

			@Override
			public void run() {
				updateInventory();
			}
		}, 1);
	}

	/*
	 * Updating inventory items
	 */
	public void updateInventory() {
		String title = getPageUpdate();
		if(opener.getOpenInventory() != null && !this.title.equals(title)) {
			ignoreNextClose = true;
			opener.closeInventory();
			Inventory newInv = Bukkit.createInventory(null, size != 54 ? size + 9 : size, title);
			this.inv = newInv;
			this.title = title;
		}

		this.itemlinks = new HashMap<ItemMeta, Runnable>();
		clear(true);
		open();
	}

	public abstract boolean onBackClick();

	@Deprecated
	/*
	 * @deprecated: Please use linkItemTo() instead
	 */
	public abstract void onClick(InventoryClickEvent event);

	public abstract void onClose(InventoryCloseEvent event);

	public abstract void onInventoryAction(PageAction action);

	/*
	 * @return Return if this is the last page
	 */
	public abstract boolean onOpen();
	
	public void setManager(SuperInventoryManager manager) {
		SuperInventory inv = manager.getInventory(opener);
		if(inv != null)
			inv.close(true);
		
		this.manager = manager;
	}
	
	public SuperInventoryManager getManager() {
		return manager;
	}
	
	public Inventory getInventory() {
		return this.inv;
	}

	public Player getOpener() {
		return this.opener;
	}

	public int getPage() {
		return page;
	}

	public int getSize() {
		return size;
	}

	public String getTitle() {
		return this.title;
	}

	public boolean isHomePage() {
		return homePage;
	}

	public boolean isOpen() {
		return this.manager.getInventories().contains(this);
	}

	/*
	 * Calculates based on the list size you enter, how many pages you need
	 */
	protected static int calculatePages(int amount, int pageSize) {
		int res = (int) Math.ceil((double) amount / pageSize);
		if(res == 0)
			res = 1;
		return res;
	}
}