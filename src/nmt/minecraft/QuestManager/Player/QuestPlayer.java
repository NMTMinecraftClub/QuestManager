package nmt.minecraft.QuestManager.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Quest;
import nmt.minecraft.QuestManager.Quest.History.History;
import nmt.minecraft.QuestManager.Quest.History.HistoryEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * Player wrapper to store questing information and make saving player quest status
 * easier
 * @author Skyler
 *
 */
public class QuestPlayer implements Participant {

	private OfflinePlayer player;
	
	private History history;
	
	private List<Quest> currentQuests;
	
	private List<String> completedQuests;
	
	private int fame;
	
	private String title;
	
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
		this.title = "";
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
		this.player = player;
		this.currentQuests = new LinkedList<Quest>();
		this.history = new History();
		
		
	}
	
	
	public History getHistory() {
		return history;
	}
	
	/**
	 * Adds a quest book to the players inventory, if there is space.<br />
	 * This method will produce a fully updated quest book in the players inventory.
	 */
	public void addQuestBook() {
		if (!player.isOnline()) {
			return;
		}
		
		Player play = player.getPlayer();
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
		if (!player.isOnline()) {
			return;
		}
		
		Player play = player.getPlayer();
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
		bookMeta.addPage(ChatColor.DARK_PURPLE + " " + player.getName() + " - "
				+ ChatColor.DARK_RED + title
				+ "\n-----\n  " + ChatColor.GOLD + "Fame: " + fame
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
		updateQuestBook();
	}
	
	public boolean removeQuest(Quest quest) {
		return currentQuests.remove(quest);
	}
	
	public void completeQuest(Quest quest) {
		completedQuests.add(quest.getName());
		currentQuests.remove(quest);
		
		history.addHistoryEvent(
				new HistoryEvent("Completed the quest \"" + quest.getName() + "\""));
	}
	
	public OfflinePlayer getPlayer() {
		return player;
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
		map.put("id", player.getUniqueId().toString());
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
				 || !map.containsKey("title") || !map.containsKey("completedquests")) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Invalid Quest Player! "
					+ (map.containsKey("id") ? ": " + map.get("id") : ""));
			return null;
		}
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(
				(String) map.get("id")));
		QuestPlayer qp = new QuestPlayer(player);
		
		qp.fame = (int) map.get("fame");
		qp.title = (String) map.get("title");
		qp.completedQuests = (List<String>) map.get("completedquests");
		
		if (qp.completedQuests == null) {
			qp.completedQuests = new LinkedList<String>();
		}
		
		
		return qp;
	}

	@Override
	public String getIDString() {
		return player.getUniqueId().toString();
	}
	
	
}
