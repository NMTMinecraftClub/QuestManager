package nmt.minecraft.QuestManager.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import nmt.minecraft.QuestManager.Quest.Quest;
import nmt.minecraft.QuestManager.Quest.History.History;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Player wrapper to store questing information and make saving player quest status
 * easier
 * @author Skyler
 *
 */
public class QuestPlayer implements Participant {

	private OfflinePlayer player;
	
	private History history;
	
	private List<Quest> quests;
	
	private int fame;
	
	private String title;
	
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
//		config.set("Quests", quests);
//		 */
//		
//		qp = new QuestPlayer();
//		
//		UUID id = UUID.fromString(config.getString("Player"));
//		OfflinePlayer player = Bukkit.getOfflinePlayer(id);
//		History history = History.fromConfig((YamlConfiguration) config.getConfigurationSection("History"));
//		@SuppressWarnings("unchecked")
//		List<Quest> quests = (List<Quest>) config.getList("Quests");
//		
//		qp.player = player;
//		qp.history = history;
//		qp.quests = quests;
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
		this.quests = new LinkedList<Quest>();
		this.history = new History();
		
		
	}
	
	
	public History getHistory() {
		return history;
	}
	
	public List<Quest> getQuests() {
		return quests;
	}
	
	public void addQuest(Quest quest) {
		quests.add(quest);
	}
	
	public boolean removeQuest(Quest quest) {
		return quests.remove(quest);
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
//		config.set("Quests", quests);
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
		
		return map;
	}
	
	/**
	 * Constucts and returns a QuestPlayer to match the data given in the passed map.
	 * @param map The configuration map to initialize the player on.
	 * @return A new quest player or null on error
	 */
	public static QuestPlayer valueOf(Map<String, Object> map) {
		if (map == null || !map.containsKey("id")) {
			return null;
		}
		
		OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(
				(String) map.get("id")));
		QuestPlayer qp = new QuestPlayer(player);
		
		qp.fame = (int) map.get("fame");
		qp.title = (String) map.get("title");
		
		
		return qp;
	}
	
	
}
