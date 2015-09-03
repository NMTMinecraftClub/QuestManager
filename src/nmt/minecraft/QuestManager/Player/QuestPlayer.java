package nmt.minecraft.QuestManager.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.Utils.LocationState;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Quest;
import nmt.minecraft.QuestManager.Quest.History.History;
import nmt.minecraft.QuestManager.Quest.History.HistoryEvent;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.ChatMenuOption;
import nmt.minecraft.QuestManager.UI.Menu.MultioptionChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.Action.PartyInviteAction;
import nmt.minecraft.QuestManager.UI.Menu.Action.ShowChatMenuAction;
import nmt.minecraft.QuestManager.UI.Menu.Message.PlainMessage;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.onarandombox.MultiversePortals.MultiversePortals;
import com.onarandombox.MultiversePortals.PortalPlayerSession;
import com.onarandombox.MultiversePortals.event.MVPortalEvent;

/**
 * Player wrapper to store questing information and make saving player quest status
 * easier
 * @author Skyler
 *
 */
public class QuestPlayer implements Participant, Listener {
	
	private UUID playerID;
	
	private History history;
	
	private List<Quest> currentQuests;
	
	private List<String> completedQuests;
	
	private int fame;
	
	private int money;
	
	private String title;
	
	private Location questPortal;
	
	private Party party;
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(QuestPlayer.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(QuestPlayer.class);
	}
	

	private enum aliases {
		FULL("nmt.minecraft.QuestManager.Player.QuestPlayer"),
		DEFAULT(QuestPlayer.class.getName()),
		SHORT("QuestPlayer"),
		INFORMAL("QP"),
		QUALIFIED_INFORMAL("QMQP");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	/**
	 * Constructs a QuestPlayer from the given configuration.<br />
	 * The passed configuration is expected to be the output of the {@link #toConfig()} method.
	 * @param config
	 * @return
	 * @throws InvalidConfigurationException
	 */
//	public static QuestPlayer fromConfig(YamlConfiguration config) throws InvalidConfigurationException {
//		
//		QuestPlayer qp = null;
//		
//		if (!config.contains("Player") || !config.contains("History") || !config.contains("Quests")) {
//			throw new InvalidConfigurationException();
//		}
//		
//		
//		
//		/*
//		 * config.set("Player", player.getUniqueId().toString());
//		config.set("History", history.toConfig());
//		config.set("Quests", currentQuests);
//		 */
//		
//		qp = new QuestPlayer();
//		
//		UUID id = UUID.fromString(config.getString("Player"));
//		OfflinePlayer player = Bukkit.getOfflinePlayer(id);
//		History history = History.fromConfig((YamlConfiguration) config.getConfigurationSection("History"));
//		@SuppressWarnings("unchecked")
//		List<Quest> currentQuests = (List<Quest>) config.getList("Quests");
//		
//		qp.player = player;
//		qp.history = history;
//		qp.quests = currentQuests;
//		
//		return qp;
//		
//	}
	
	private QuestPlayer() {
		this.fame = 0;
		this.money = 0;
		this.title = "The Unknown";
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
		; //do nothing. This is for non-redundant defining of QuestPlayers from config
	}
	
	/**
	 * Creates a new QuestPlayer wrapper for the given player.<br />
	 * This wrapper holds no information, and is best used when the player has never been
	 * wrapped before
	 * @param player
	 */
	public QuestPlayer(OfflinePlayer player) {
		this();
		this.playerID = player.getUniqueId();
		this.currentQuests = new LinkedList<Quest>();
		this.completedQuests = new LinkedList<String>();
		this.history = new History();
		
		if (player.isOnline()) {
			Player p = player.getPlayer();
			questPortal = p.getWorld().getSpawnLocation();
		}
	}
	
	
	public History getHistory() {
		return history;
	}
	
	/**
	 * Adds a quest book to the players inventory, if there is space.<br />
	 * This method will produce a fully updated quest book in the players inventory.
	 */
	public void addQuestBook() {
		if (!getPlayer().isOnline()) {
			return;
		}
		
		Player play = getPlayer().getPlayer();
		Inventory inv = play.getInventory();
		
		if (inv.firstEmpty() == -1) {
			//no room!
			return;
		}
		
		ItemStack book = null;
		
		for (ItemStack item : inv.all(Material.WRITTEN_BOOK).values()) {
			if (item.hasItemMeta()) {
				BookMeta meta = (BookMeta) item.getItemMeta();
				if (meta.getTitle().equals("Quest Log")
						&& meta.getAuthor().equals(play.getName())) {
					book = item;
					break;
				}
			}
		}
		
		if (book == null) {
		
			book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bookMeta = (BookMeta) book.getItemMeta();
			
			bookMeta.setTitle("Quest Log");
			bookMeta.setAuthor(play.getName());
			
			book.setItemMeta(bookMeta);
			
			book.addUnsafeEnchantment(Enchantment.LUCK, 5);
			
			inv.addItem(book);
			
			play.sendMessage(ChatColor.GRAY + "A " + ChatColor.DARK_GREEN 
					+ "Quest Log" + ChatColor.GRAY + " has been added to your inventory."
					 + ChatColor.RESET);
		}
		
		updateQuestBook();
	}
	
	/**
	 * Updates the players quest book, if they have it in their inventory.<br />
	 * If the user does not have abook already or has discarded it, this method will do nothing.
	 */
	public void updateQuestBook() {
		if (!getPlayer().isOnline()) {
			return;
		}
		
		Player play = getPlayer().getPlayer();
		Inventory inv = play.getInventory();
		ItemStack book = null;
		
		for (ItemStack item : inv.all(Material.WRITTEN_BOOK).values()) {
			if (item.hasItemMeta()) {
				BookMeta meta = (BookMeta) item.getItemMeta();
				if (meta.getTitle().equals("Quest Log")
						&& meta.getAuthor().equals(play.getName())) {
					book = item;
					break;
				}
			}
		}
		
		if (book == null) {
			//they don't have a quest log
			return;
		}
		
		
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setPages(new LinkedList<String>());
		
		//generate the first page
		bookMeta.addPage("      Quest Log\n  " 
				+ ChatColor.RESET + "\n\n"
						+ "  This book details your current quest progress & history.");
		
		//generate the stats page
		bookMeta.addPage(ChatColor.DARK_PURPLE + " " + getPlayer().getName() + " - "
				+ ChatColor.DARK_RED + title
				+ "\n-----\n  " + ChatColor.GOLD + "Fame: " + fame
				+ "\n  "			+ ChatColor.GOLD + "Gold: " + money
				+ ChatColor.DARK_GREEN + "\n\n  Current Quests: " + currentQuests.size()
				+ ChatColor.DARK_BLUE + "\n\n  Completed Quests: " + completedQuests.size()
				+ ChatColor.RESET);	
		
		
		//now do quest info
		//Quest Name
		//Quest Description
			//Goal Description? :S
		
		if (currentQuests.isEmpty()) {
			bookMeta.addPage("\nYou do not have any active quests!");
		} else {
			for (Quest quest : currentQuests) {
				
				String page = "";
				
				page += ChatColor.GOLD + quest.getName() + "\n";
				
				page += ChatColor.DARK_BLUE + quest.getDescription() + "\n";
				
				page += ChatColor.RESET + "Objectives:\n";
				
				page += ChatColor.DARK_GRAY;
				
				for (Goal goal : quest.getGoals()) {
					if (goal.isComplete()) {
						page += ChatColor.GREEN + " =" + goal.getDescription() + "\n"; 
					} else {
						page += ChatColor.DARK_RED + " -" + goal.getDescription() + "\n";
					}
				}
				
				bookMeta.addPage(page);
				
			}
		}
		
		
		
		book.setItemMeta(bookMeta);
		play.sendMessage(ChatColor.GRAY + "Your "
				+ ChatColor.DARK_GREEN + "Quest Log" + ChatColor.GRAY + " has been"
				+ " updated!" + ChatColor.RESET);
		play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(1, Tone.C));
		play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(1, Tone.A));
		
		play.setLevel(money);
	}
	
	public List<Quest> getCurrentQuests() {
		return currentQuests;
	}
	
	public List<String> getCompletedQuests() {
		return completedQuests;
	}
	
	public boolean hasCompleted(Quest quest) {
		return this.hasCompleted(quest.getName());
	}
	
	public boolean hasCompleted(String name) {
		return completedQuests.contains(name);
	}
	
	/**
	 * Checks and returns whether or not the player is in this TYPE of quest.<br />
	 * To see whether this player is in this particular instance of the quest, use
	 * the quest's {@link nmt.minecraft.QuestManager.Quest.Quest#getPlayers() getPlayers()}
	 * method and traditional lookup techniques instead.
	 * @param quest
	 * @return
	 */
	public boolean isInQuest(Quest quest) {
		return isInQuest(quest.getName());
	}
	
	public boolean isInQuest(String questName) {
		for (Quest quest : currentQuests) {
			if (quest.getName().equals(questName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void addQuest(Quest quest) {
		currentQuests.add(quest);
		history.addHistoryEvent(new HistoryEvent("Accepted the quest \"" + quest.getName() +"\""));
		addQuestBook();
		updateQuestBook();
	}
	
	public boolean removeQuest(Quest quest) {
		
		if (currentQuests.isEmpty()) {
			return false;
		}
		
		Iterator<Quest> it = currentQuests.iterator();
		
		while (it.hasNext()) {
			Quest q = it.next();
			if (q.equals(quest)) {
				it.remove();
				return true;
			}
		}
		
		return false;
	}
	
	public void completeQuest(Quest quest) {
		if (!completedQuests.contains(quest.getName())) {
			completedQuests.add(quest.getName());			
		}
		removeQuest(quest);
		
		history.addHistoryEvent(
				new HistoryEvent("Completed the quest \"" + quest.getName() + "\""));
	}
	
	public int getFame() {
		return fame;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void addFame(int fame) {
		this.fame += fame;
	}
	
	public void setFame(int fame) {
		this.fame = fame;
	}
	
	public Party getParty() {
		return party;
	}
	
	public Party createParty() {
		this.party = new Party(this);
		return party;
	}
	
	public void joinParty(Party party) {
		if (party.addMember(this))	{
			this.party = party;
		}
	}
	
	protected void leaveParty(String message) {
		if (getPlayer().isOnline()) {
			getPlayer().getPlayer().sendMessage(message);
			getPlayer().getPlayer().setScoreboard(
					Bukkit.getScoreboardManager().getNewScoreboard());
		}
		
		this.party = null;
	}
	
	/**
	 * @return the money
	 */
	public int getMoney() {
		return money;
	}

	/**
	 * @param money the money to set
	 */
	public void setMoney(int money) {
		this.money = money;
		if (getPlayer().isOnline())
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
					.getWorlds().contains(getPlayer().getPlayer().getWorld().getName())) {
			getPlayer().getPlayer().setLevel(this.money);
		}
	}
	
	/**
	 * Add some money to the player's wallet
	 * @param money
	 */
	public void addMoney(int money) {
		this.money += money;
		if (getPlayer().isOnline())
			if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
						.getWorlds().contains(getPlayer().getPlayer().getWorld().getName())) {
				getPlayer().getPlayer().setLevel(this.money);
			}
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
//	/**
//	 * Returns the currently-stored information in a YamlConfiguration. <br />
//	 * Output from this method should be expected to be used with {@link QuestPlayer#fromConfig(YamlConfiguration)}
//	 * to produce an exact duplicate.
//	 * @return
//	 */
//	public YamlConfiguration toConfig() {
//		
//		if (player == null) {
//			return null;
//		}
//		
//		YamlConfiguration config = new YamlConfiguration();
//		
//		config.set("Player", player.getUniqueId().toString());
//		config.set("History", history.toConfig());
//		config.set("Quests", currentQuests);
//		
//		return config;
//	}

	@Override
	public Collection<QuestPlayer> getParticipants() {
		Collection<QuestPlayer> col = new ArrayList<QuestPlayer>();
		col.add(this);
		return col;
	}

	/**
	 * Converts the quest player to serialized configuration output
	 */
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>(3);
		map.put("title", title);
		map.put("fame", fame);
		map.put("money", money);
		map.put("id", getPlayer().getUniqueId().toString());
		map.put("portalloc", this.questPortal);
		map.put("completedquests", completedQuests);
		
		return map;
	}
	
	/**
	 * Constucts and returns a QuestPlayer to match the data given in the passed map.
	 * @param map The configuration map to initialize the player on.
	 * @return A new quest player or null on error
	 */
	@SuppressWarnings("unchecked")
	public static QuestPlayer valueOf(Map<String, Object> map) {
		if (map == null || !map.containsKey("id") || !map.containsKey("fame") 
				 || !map.containsKey("title") || !map.containsKey("completedquests")
				 || !map.containsKey("portalloc") || !map.containsKey("money")) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Invalid Quest Player! "
					+ (map.containsKey("id") ? ": " + map.get("id") : ""));
			return null;
		}
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(
				(String) map.get("id")));
		QuestPlayer qp = new QuestPlayer(player);
		
		if (map.get("portalloc") == null) {
			qp.questPortal = null;
		} else {
			qp.questPortal = ((LocationState) map.get("portalloc")).getLocation();
		}
		
		qp.fame = (int) map.get("fame");
		qp.money = (int) map.get("money");
		qp.title = (String) map.get("title");
		qp.completedQuests = (List<String>) map.get("completedquests");
		
		if (qp.completedQuests == null) {
			qp.completedQuests = new LinkedList<String>();
		}
		
		
		return qp;
	}

	@Override
	public String getIDString() {
		return getPlayer().getUniqueId().toString();
	}

	/**
	 * @return the questPortal
	 */
	public Location getQuestPortal() {
		return questPortal;
	}

	/**
	 * @param questPortal the questPortal to set
	 */
	public void setQuestPortal(Location questPortal) {
		this.questPortal = questPortal;
	}
	
	@EventHandler
	public void onPortal(MVPortalEvent e) {
		if (!getPlayer().isOnline() || e.isCancelled()) {
			return;
		}
			
		if (e.getTeleportee().equals(getPlayer())) {			
			
			List<String> qworlds = QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
					.getWorlds();
			if (qworlds.contains(e.getFrom().getWorld().getName())) {
				
				//check that we aren't going TO antoher quest world
				if (qworlds.contains(e.getDestination().getLocation(getPlayer().getPlayer()).getWorld().getName())) {
					//we are! Don't interfere here
					return;
				}
				
				//we're leaving a quest world, so save the portal!
				this.questPortal = e.getFrom();
				
				//player quit
				onPlayerQuit();
				return;
			}
			if (qworlds.contains(e.getDestination().getLocation(getPlayer().getPlayer()).getWorld().getName())) {
				//Before we warp to our old location, we need to make sure we HAVE one
				if (this.questPortal == null) {
					//this is our first time coming in, so just let the portal take us
					//and save where it plops us out at
					this.questPortal = e.getDestination().getLocation(getPlayer().getPlayer());
					return;
				}
				
				//we're moving TO a quest world, so actually go to our saved location
				e.setCancelled(true);
				getPlayer().getPlayer().teleport(questPortal);
			}
		}
	}

	@EventHandler
	public void onExp(PlayerExpChangeEvent e) {
		if (!getPlayer().isOnline()) {
			return;
		}
		
		Player p = getPlayer().getPlayer();
		
		if (!p.getUniqueId().equals(e.getPlayer().getUniqueId())) {
			return;
		}
		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
				.getWorlds().contains(p.getWorld().getName())) {
			return;
		}
		
		money += e.getAmount();
		p.setLevel(money);
		
		e.setAmount(0);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		
		if (!getPlayer().isOnline()) {
			return;
		}
		
		Player p = getPlayer().getPlayer();
		
		if (!p.getUniqueId().equals(e.getPlayer().getUniqueId())) {
			return;
		}
		
		if (e.getItem() != null && e.getItem().getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta meta = (BookMeta) e.getItem().getItemMeta();
			
			if (meta.getTitle().equals("Quest Log") && 
					e.getItem().getEnchantmentLevel(Enchantment.LUCK) == 5) {
				//it's a quest log. Update it
				
				updateQuestBook();
			}
		}
		
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!getPlayer().isOnline()) {
			return;
		}
		
		Player p = getPlayer().getPlayer();
		
		if (!p.getUniqueId().equals(e.getEntity().getUniqueId())) {
			return;
		}
		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
				.getWorlds().contains(p.getWorld().getName())) {
			return;
		}
		
		e.setDroppedExp(0);
		e.setNewLevel(money);
		
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {

		if (!getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {
			return;
		}

		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
				.getWorlds().contains(e.getRespawnLocation().getWorld().getName())) {
			return;
		}
		
		//in a quest world, so put them back to their last checkpoint
		e.setRespawnLocation(
				this.questPortal);
		MultiversePortals mvp = (MultiversePortals) Bukkit.getPluginManager().getPlugin("Multiverse-Portals");
		
		if (mvp == null) {
			System.out.println("null");
			return;
		}
		
		PortalPlayerSession ps = mvp.getPortalSession(e.getPlayer());
		ps.playerDidTeleport(questPortal);
		ps.setTeleportTime(new Date());
		
		
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (e.getPlayer().getUniqueId().equals(getPlayer().getUniqueId())) {
			onPlayerQuit();
		}
	}
	
	/**
	 * Internal helper method to house what happens when a player quits (by leaving, logging out, etc)
	 */
	private void onPlayerQuit() {
		if (party != null) {
			party.removePlayer(this);
			party = null;
		}
	}
	
	@EventHandler
	public void onPlayerInteractWithPlayer(PlayerInteractEntityEvent e) {
		if (!getPlayer().isOnline()) {
			return;
		}
		
		Player p = getPlayer().getPlayer();
		
		if (!p.getUniqueId().equals(e.getPlayer().getUniqueId())) {
			return;
		}
		
		//did interact with another player?
		if (e.getRightClicked() instanceof Player) {
			QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(
					(Player) e.getRightClicked());
			
			showPlayerMenu(qp);
			return;
		}
	
	}
	
	/**
	 * Displays for this quest player a player menu for the given player.
	 * @param player
	 */
	private void showPlayerMenu(QuestPlayer player) {
		/*
		 * ++++++++++++++++++++++++++++++
		 *     Name - Title
		 *     
		 *  Send Message    View Info      Trade
		 *  Invite To Party
		 * ++++++++++++++++++++++++++++++
		 */
		FancyMessage msg = new FancyMessage(player.getPlayer().getName() + "  -  " + player.getTitle());
		
		ChatMenuOption opt1 = new ChatMenuOption(new PlainMessage("Invite to Party"), 
				new PartyInviteAction(this, player));
		
		
		ChatMenuOption opt2 = new ChatMenuOption(new PlainMessage("Option 2"), 
				new ShowChatMenuAction(new SimpleChatMenu(new FancyMessage("lol2")), 
				this.getPlayer().getPlayer()));
		
		ChatMenu menu = new MultioptionChatMenu(new PlainMessage(msg), opt1, opt2);
		
		menu.show(this.getPlayer().getPlayer().getPlayer());
		
	}
	
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(playerID);
	}
	
}
