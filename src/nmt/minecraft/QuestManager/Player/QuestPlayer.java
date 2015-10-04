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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
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

import de.inventivegames.util.tellraw.TellrawConverterLite;
import de.inventivegames.util.title.TitleManager;
import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.Utils.LocationState;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Player.Utils.Compass;
import nmt.minecraft.QuestManager.Player.Utils.CompassTrackable;
import nmt.minecraft.QuestManager.Player.Utils.QuestJournal;
import nmt.minecraft.QuestManager.Player.Utils.QuestLog;
import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Quest;
import nmt.minecraft.QuestManager.Quest.History.History;
import nmt.minecraft.QuestManager.Quest.History.HistoryEvent;
import nmt.minecraft.QuestManager.Quest.Requirements.Requirement;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.ChatMenuOption;
import nmt.minecraft.QuestManager.UI.Menu.MultioptionChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.Action.BootFromPartyAction;
import nmt.minecraft.QuestManager.UI.Menu.Action.ChangeTitleAction;
import nmt.minecraft.QuestManager.UI.Menu.Action.ForgeAction;
import nmt.minecraft.QuestManager.UI.Menu.Action.PartyInviteAction;
import nmt.minecraft.QuestManager.UI.Menu.Action.ShowChatMenuAction;
import nmt.minecraft.QuestManager.UI.Menu.Message.PlainMessage;

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
	
	private String focusQuest;
	
	private List<String> journalNotes;
	
	private int fame;
	
	private int money;
	
	private String title;
	
	private List<String> unlockedTitles;
	
	private Location questPortal;
	
	private Party party;
	
	private CompassTrackable compassTarget;
	
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
		this.unlockedTitles = new LinkedList<String>();
		this.journalNotes = new LinkedList<String>();
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
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
			if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
					.contains(p.getWorld().getName())) {
				questPortal = p.getWorld().getSpawnLocation();
			}
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
		QuestLog.addQuestlog(this);
	}
	
	/**
	 * Adds a journal to the player's inventory if there's space. Also updates immediately.
	 */
	public void addJournal() {
		QuestJournal.addQuestJournal(this);
	}
	
	/**
	 * Updates the players quest book, if they have it in their inventory.<br />
	 * If the user does not have abook already or has discarded it, this method will do nothing.
	 */
	public void updateQuestBook(boolean silent) {
		QuestLog.updateQuestlog(this, silent);
		updateCompass(true);
	}
	
	public void updateQuestLog(boolean silent) {
		QuestJournal.updateQuestJournal(this, silent);
	}
	
	public void updateCompass(boolean silent) {
		this.getNextTarget();
		Compass.updateCompass(this, silent);
	}
	
	public void setCompassTarget(CompassTrackable target, boolean silent) {
		this.compassTarget = target;
		updateCompass(silent);
	}
	
	public Location getCompassTarget() {
		if (compassTarget == null) {
			return null;
		}
		
		return compassTarget.getLocation();
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
		history.addHistoryEvent(new HistoryEvent("Accepted the quest " + ChatColor.DARK_PURPLE + quest.getName()));
		if (focusQuest == null) {
			setFocusQuest(quest.getName());
		}
		//addQuestBook();
		//updateQuestBook();
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
				if (focusQuest.equals(quest.getName())) {
					if (currentQuests.isEmpty()) {
						focusQuest = null;
						QuestJournal.addQuestJournal(this);
					} else {
						setFocusQuest(currentQuests.get(0).getName());
					}
				}
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
				new HistoryEvent("Completed the quest " + ChatColor.DARK_PURPLE + quest.getName()));
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
	
	public void leaveParty(String message) {
		if (getPlayer().isOnline()) {
			getPlayer().getPlayer().sendMessage(message);
			getPlayer().getPlayer().setScoreboard(
					Bukkit.getScoreboardManager().getNewScoreboard());
		}
		
		if (!currentQuests.isEmpty()) {
			for (Quest q : currentQuests) {
				if (q.getRequireParty()) {
					removeQuest(q);
					if (getPlayer().isOnline()) {
						getPlayer().getPlayer().sendMessage(ChatColor.YELLOW + "The quest " 
								+ ChatColor.DARK_PURPLE + q.getName() + ChatColor.YELLOW
								+ " has been failed because you left the party!");
					}
				}
			}
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
		if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
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
	
	public void addTitle(String title) {
		if (this.unlockedTitles.contains(title)) {
			return;
		}
		this.unlockedTitles.add(title);
		
		if (!getPlayer().isOnline()) {
			return;
		}
		
		ChatMenu menu = new SimpleChatMenu(
				new FancyMessage("You've unlocked the ")
					.color(ChatColor.DARK_GRAY)
				.then(title)
					.color(ChatColor.GOLD)
					.style(ChatColor.BOLD)
				.then(" title!"));
		
		menu.show(getPlayer().getPlayer());
		
		TitleManager.sendTimings(getPlayer().getPlayer(), 30, 80, 30);

//        TitleManager.sendSubTitle(getPlayer().getPlayer(), TellrawConverterLite.convertToJSON(
//        		new FancyMessage(title).toOldMessageFormat()));

        TitleManager.sendTitle(getPlayer().getPlayer(), TellrawConverterLite.convertToJSON(
        		ChatColor.GREEN + "Title Unlocked!"));
        
        getPlayer().getPlayer().playSound(getPlayer().getPlayer().getLocation(), Sound.FIREWORK_TWINKLE, 10, 1);
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
		map.put("unlockedtitles", unlockedTitles);
		map.put("fame", fame);
		map.put("money", money);
		map.put("id", getPlayer().getUniqueId().toString());
		map.put("portalloc", this.questPortal);
		map.put("completedquests", completedQuests);
		map.put("focusquest", focusQuest);
		map.put("notes", journalNotes);
		
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
				 || !map.containsKey("portalloc") || !map.containsKey("money")
				 || !map.containsKey("unlockedtitles")) {
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
		qp.unlockedTitles = (List<String>) map.get("unlockedtitles");
		qp.completedQuests = (List<String>) map.get("completedquests");
		qp.focusQuest = (String) map.get("focusquest");
		qp.journalNotes = (List<String>) map.get("notes");
		
		if (qp.completedQuests == null) {
			qp.completedQuests = new LinkedList<String>();
		}
		
		if (qp.unlockedTitles == null) {
			qp.unlockedTitles = new LinkedList<String>();
		}
		
		if (qp.journalNotes == null) {
			qp.journalNotes = new LinkedList<String>();
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
		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getUsePortals()) {
			return;
		}
		
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
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getXPMoney()) {
			return;
		}
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
		
		if (e.getItem() == null) {
			return;
		}
		
		if (e.getItem() != null && e.getItem().getType().equals(Material.WRITTEN_BOOK)) {
			BookMeta meta = (BookMeta) e.getItem().getItemMeta();
			
			if (meta.getTitle().equals("Quest Log") && 
					e.getItem().getEnchantmentLevel(Enchantment.LUCK) == 5) {
				//it's a quest log. Update it
				
				updateQuestBook(true);
			}
			
			return;
		}
		
		if (e.getItem().hasItemMeta() && e.getItem().getType() == Material.BOOK_AND_QUILL) {
			BookMeta meta = (BookMeta) e.getItem().getItemMeta();
			if (meta.hasTitle() && meta.getTitle().equals("Journal")
					&& meta.hasAuthor() && meta.getAuthor().equals(p.getName())
					&& e.getItem().getEnchantmentLevel(Enchantment.LUCK) == 5) {
				updateQuestLog(true);
			}
			
			return;
		}
		
		if (Compass.CompassDefinition.isCompass(e.getItem())) {
			updateCompass(false);
			return;
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
		e.setKeepInventory(true);
		
		boolean trip = false;
		
		//step through inventory, reduce durability of equipment
		for (ItemStack item : p.getInventory()) {
			if (item == null || item.getType() == Material.AIR) {
				continue;
			}
			
			if (ForgeAction.Repairable.isRepairable(item.getType())) {
				trip = true;
				item.setDurability((short) Math.min(item.getType().getMaxDurability() - 1, 
						item.getDurability() + item.getType().getMaxDurability() / 2));
			}
		}
		
		for (ItemStack item : p.getEquipment().getArmorContents()) {
			if (item == null || item.getType() == Material.AIR) {
				continue;
			}
			
			if (ForgeAction.Repairable.isRepairable(item.getType())) {
				trip = true;
				item.setDurability((short) Math.min(item.getType().getMaxDurability() - 1, 
						item.getDurability() + item.getType().getMaxDurability() / 2));
			}
		}
		
		if (trip) {
			p.sendMessage(ChatColor.DARK_RED + "Your equipment has been damaged!" + ChatColor.RESET);
		}
		
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
			party.removePlayer(this, "You've been disconnected!");
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
		
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds()
				.contains(p.getWorld().getName())) {
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
	
	@EventHandler
	public void onPlayerRuinJournal(PlayerEditBookEvent e) {
		if (!getPlayer().isOnline()) {
			return;
		}
		
		if (!e.getPlayer().equals(getPlayer().getPlayer())) {
			return;
		}
		
		BookMeta oldMeta = e.getPreviousBookMeta(),
				newMeta = e.getNewBookMeta();
		
		if (oldMeta.hasTitle() && oldMeta.getTitle().equals("Journal")
			&& oldMeta.hasAuthor() && oldMeta.getAuthor().equals(e.getPlayer().getName())
			&& oldMeta.getEnchantLevel(Enchantment.LUCK) == 5) {
			//grab the player notes
			int pageNum;
			String page;
			for (pageNum = 1; pageNum <= newMeta.getPageCount(); pageNum++) {
				page = newMeta.getPage(pageNum);
				if (page.contains("  Player Notes")) {
					break;
				}
			}
			pageNum++;
			this.journalNotes.clear();
			if (pageNum > newMeta.getPageCount()) {
				//we went beyond what we have
			} else {
				//save their notes
				for (; pageNum <= newMeta.getPageCount(); pageNum++) {
					journalNotes.add(newMeta.getPage(pageNum));
				}
			}
			
			e.setCancelled(true);
			QuestJournal.updateQuestJournal(this, true);
			
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
		
		ChatMenuOption opt1;
		
		if (party != null && player.party != null && player.getParty().getIDString().equals(party.getIDString())) {
			//already in party, so give option to kick
			if (party.getLeader().getIDString().equals(getIDString())) {
				opt1 = new ChatMenuOption(new PlainMessage("Kick from Party"),
						new BootFromPartyAction(party, player));
			} else {
				opt1 = new ChatMenuOption(new PlainMessage(new FancyMessage("Kick from Party").color(ChatColor.DARK_GRAY)),
						new ShowChatMenuAction(
								new SimpleChatMenu(new FancyMessage("Only the party leader can kick players!").color(ChatColor.DARK_RED))
								, getPlayer().getPlayer()));
			}
		} else {
			opt1 = new ChatMenuOption(new PlainMessage("Invite to Party"), 
					new PartyInviteAction(this, player));
		}
		
		
		ChatMenuOption opt2 = new ChatMenuOption(new PlainMessage("View Info"), 
				new ShowChatMenuAction(new SimpleChatMenu(
						new FancyMessage(player.getPlayer().getName())
							.color(ChatColor.DARK_PURPLE)
						.then(" - ")
							.color(ChatColor.WHITE)
						.then(player.getTitle())
						.then("\n\n")
						.then("This player has ")
						.then(player.money + "")
							.color(ChatColor.GOLD)
						.then(" gold.\nThis player has completed ")
							.color(ChatColor.WHITE)
						.then("" + player.completedQuests.size())
							.color(ChatColor.GREEN)
							.tooltip(player.completedQuests)
						.then(" quests.")
							.color(ChatColor.WHITE)
					), 
				this.getPlayer().getPlayer()));
		
		ChatMenu menu = new MultioptionChatMenu(new PlainMessage(msg), opt1, opt2);
		
		menu.show(this.getPlayer().getPlayer().getPlayer());
		
	}
	
	/**
	 * Shows to this player their personal title menu, used to switch titles
	 */
	public void showTitleMenu() {		
		if (!getPlayer().isOnline()) {
			return;
		}
		
		if (this.unlockedTitles.isEmpty()) {
			ChatMenu menu = new SimpleChatMenu(new FancyMessage("You have not unlocked any titles!").color(ChatColor.DARK_RED));
			menu.show(getPlayer().getPlayer());
			return;
		}
		
		LinkedList<ChatMenuOption> opts = new LinkedList<ChatMenuOption>();
		
		for (String t : unlockedTitles) {
			opts.add(new ChatMenuOption(
					new PlainMessage(t),
					new ChangeTitleAction(this, t)));
		}
		

		MultioptionChatMenu menu = new MultioptionChatMenu(new PlainMessage("Choose your title:"), opts);
		
		menu.show(getPlayer().getPlayer());
		
		
	}
	
	public OfflinePlayer getPlayer() {
		return Bukkit.getOfflinePlayer(playerID);
	}
	
	public Quest getFocusQuest() {
		if (focusQuest == null) {
			return null;
		}
		
		for (Quest q : currentQuests) {
			if (q.getName().equals(focusQuest)) {
				return q;
			}
		}
		
		return null;
	}
	
	public List<String> getPlayerNotes() {
		return this.journalNotes;
	}
	
	public void setFocusQuest(String questName) {
		for (Quest q : currentQuests) {
			if (q.getName().equals(questName)) {
				focusQuest = questName;
				break;
			}
		}
		QuestJournal.updateQuestJournal(this, false);
		if (getPlayer().isOnline()) {
			getPlayer().getPlayer().sendMessage("Your now focusing on the quest " + ChatColor.DARK_PURPLE + questName);
		}
		
		updateCompass(true);
	}
	
	/**
	 * Helper method to select the next compass target from the current focus quest's goal
	 */
	private void getNextTarget() {
		Quest quest = this.getFocusQuest();
		
		if (quest == null) {
			this.compassTarget = null;
			return;
		}
		
		Goal goal = quest.getCurrentGoal();
		if (goal.getRequirements().isEmpty()) {
			this.compassTarget = null;
			return;
		}
		
		for (Requirement req : goal.getRequirements()) {
			if (req instanceof CompassTrackable && !req.isCompleted()) {
				compassTarget = (CompassTrackable) req;
				return;
			}
		}
		
		//got all the way through. Are all requirements complete? Then either the quest is done or there are
		//reqs left we can't track, so we point to null.
		
		//if (goal.isComplete()) {
			//HOPE that the quest is actually complete.
		this.compassTarget = null;
		return;
		//}
	}
	
	/**
	 * Checks whether this player has enough of the provided item.<br />
	 * This method checks the name of the item when calculating how much they have
	 * @param searchItem
	 * @return
	 */
	public boolean hasItem(ItemStack searchItem) {
		if (!getPlayer().isOnline()) {
			return false;
		}
		
		Inventory inv = getPlayer().getPlayer().getInventory();
		int count = 0;
		String itemName = null;
		
		if (searchItem.hasItemMeta() && searchItem.getItemMeta().hasDisplayName()) {
			itemName = searchItem.getItemMeta().getDisplayName();
		}
		
		for (ItemStack item : inv.all(searchItem.getType()).values()) {
			if ((itemName == null && (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())) || 
					(item.hasItemMeta() && item.getItemMeta().getDisplayName() != null 
					  && item.getItemMeta().getDisplayName().equals(itemName))) {
				count += item.getAmount();
			}
		}
		
		if (count >= searchItem.getAmount()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes the passed item from the player's inventory.<br />
	 * This method also uses item lore to make sure the correct items are removed
	 * @param inv
	 * @param item
	 */
	public void removeItem(ItemStack searchItem) {
		
		if (!getPlayer().isOnline()) {
			return;
		}
		
		Inventory inv = getPlayer().getPlayer().getInventory();
		//gotta go through and find ones that match the name
		int left = searchItem.getAmount();
		String itemName = null;
		ItemStack item;
		
		if (searchItem.hasItemMeta() && searchItem.getItemMeta().hasDisplayName()) {
			itemName = searchItem.getItemMeta().getDisplayName();
		}
		
		for (int i = 0; i <= 35; i++) {
			item = inv.getItem(i);
			if (item != null && item.getType() == searchItem.getType())
			if (  (itemName == null && (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()))
				|| (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(itemName))	
					) {
				//deduct from this item stack as much as we can, up to 'left'
				//but if there's more than 'left' left, just remove it
				int amt = item.getAmount();
				if (amt <= left) {
					//gonna remove entire stack
					item.setType(Material.AIR);
					item.setAmount(0);
					item.setItemMeta(null);
				} else {
					item.setAmount(amt - left);
				}
				
				inv.setItem(i, item);
				left-=amt;
				
				if (left <= 0) {
					break;
				}
			}
		}
	}
}
