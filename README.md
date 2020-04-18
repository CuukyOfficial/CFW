# CFW - Cuuky FrameWork
### A powerful bukkit framework that runs on 1.7+ and makes coding with bukkit less painful

## Dear developers
In the following I will show you the advantages of using my FrameWork and how much work it can save 

## Why should I use it for my plugin?
#### **CFW** makes it much easier to make your plugin 1.7+ compatible by having following included:

- A NetworkManager which contains following methods:
  - sendPacket(Packet packet) - Sends a packet
  - getPing() - Gets the ping of a player
  - respawnPlayer() - Respawns a player instantly without waiting for them to press the respawn button
  - getLocale() - Returns locale (language) 
  - sendLinkedMessage(String message, String link) - Sends a clickable chat message
  - sendTitle(String header, String footer) - Sends title header and footer
  - setnTablist(String header, String footer) (1.8+) - Sends tablist header and footer
  - sendActionbar(String message) (1.8+) - Sends a string to the actionbar of the player
  - setAttributeSpeed(double value) (1.9+) - Sets (removes) the hit delay implemented in 1.9
  
- VersionUtils which contain following:
  - getNmsClass() - returns Nms Class of you server version
  - getVersion() - returns your current BukkitVersion (containing .isHigherThan and more)
  - getServerSoftware() - returns Server Software type (containing hasForgeSupport etc.)

#### There are more utilities which will help you a lot while working with Bukkit included:
- SuperInventory - Easily create clickable GUI's without much effort
- ItemBuilder - Create ItemStacks with Enchantments, Lore, Displayname, Amount and Material in one line
- Hooks:
  - ItemHooks - Hook items to the inventory of players to any interaction 
  - ChatHooks - Hook to the chat the get the next chat output the player writes
- Configuration:
  - A simple configuration handler (Will be improved)
  - PlaceHolderAPI to easily and efficient replace placeholders in messages
  - Language-System to let the Server-Admins create own language files for every language for your plugin
- Clientadapter
  - Easily handle NameTags, Scoreboards and Tablist -Headers and -Footers
  - Following applies to CustomScoreboards, CustomNametags and CustomTablists:
    - Efficient replaced - if the nametag string didnt change for example, it won't send any new information to the client
    - Can be completely disabled

More coming soon!

## How to use it?
### Here's and example of how you intialize the CFW
```java
this.cuukyFrameWork = new CuukyFrameWork(this);
this.cuukyFrameWork.getClientAdapterManager().setUpdateHandler(new CustomBoardUpdateHandler());
this.cuukyFrameWork.getClientAdapterManager().setBoardTypeEnabled(CustomBoardType.TABLIST, false);
```

### A BoardUpdateHandler contains following methods
```java
	public ArrayList<String> getTablistHeader(Player player);
	
	public ArrayList<String> getTablistFooter(Player player);

	public String getScoreboardTitle(Player player);
	
	public ArrayList<String> getScoreboardEntries(Player player);
	
	public boolean showHeartsBelowName(Player player);

	public String getNametagName(Player player);
	
	public String getNametagPrefix(Player player);
	
	public String getNametagSuffix(Player player);
	
	public boolean isNametagVisible(Player player); 
```

### How to hook an item to the hotbar
```java
Main.getInstance().getCuukyFrameWork().getHookManager().registerHook(new ItemHook(player, new ItemBuilder().displayname("§bWähle dein Kit").material(Material.CHEST).build(), 0, new ItemHookHandler() {

			@Override
			public void onInteractEntity(PlayerInteractEntityEvent event) {}

			@Override
			public void onInteract(PlayerInteractEvent event) {
				if(event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR)
					return;

				new KitMenu(player);

				event.setCancelled(true);
			}

			@Override
			public void onEntityHit(EntityDamageByEntityEvent event) {}
		}));
```

### How to create a GUI
```java
public class VoteMainMenu extends SuperInventory {

	public VoteMainMenu(Player opener) {
		super("§5Vote", opener, 18, true);

		this.setModifier = false;
		
		Main.getInstance().getCuukyFrameWork().getInventoryManager().registerInventory(this);
		open();
	}

	@Override
	public boolean onBackClick() {
		return false;
	}

	@Override
	public void onClick(InventoryClickEvent event) {}

	@Override
	public void onClose(InventoryCloseEvent event) {}

	@Override
	public void onInventoryAction(PageAction action) {}

	@Override
	public boolean onOpen() {
		linkItemTo(12, new ItemBuilder().displayname("§eChests").itemstack(new ItemStack(Material.CHEST)).build(), new Runnable() {

			@Override
			public void run() {
				new ChestVoteMenu(opener);
			}
		});

		linkItemTo(14, new ItemBuilder().displayname("§6Loot").itemstack(new ItemStack(Material.GOLDEN_APPLE)).build(), new Runnable() {

			@Override
			public void run() {
				new LootVoteMenu(opener);
			}
		});
		return true;
	}
}
```

### How to check the server version
```java
if(!VersionUtils.getVersion().isHigherThan(BukkitVersion.ONE_7))
			return;
```

There is much more to use, but this is the most important stuff

## Have fun using it and thanks for reading
