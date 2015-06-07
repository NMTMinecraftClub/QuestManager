package nmt.minecraft.QuestManager.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.QuestManager.Quest.Quest;
import nmt.minecraft.QuestManager.Quest.History.History;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 * Player wrapper to store questing information and make saving player quest status
 * easier
 * @author Skyler
 *
 */
public class QuestPlayer implements Participant {

	private Player player;
	
	private History history;
	
	private List<Quest> quests;
	
	/**
	 * Constructs a QuestPlayer from the given configuration.<br />
	 * The passed configuration is expected to be the output of the {@link #toConfig()} method.
	 * @param config
	 * @return
	 * @throws InvalidConfigurationException
	 */
	public static QuestPlayer fromConfig(YamlConfiguration config) throws InvalidConfigurationException {
		
		QuestPlayer qp = null;
		
		
		
		
		//TODO
		
		
		return qp;
		
	}
	
	/**
	 * Creates a new QuestPlayer wrapper for the given player.<br />
	 * This wrapper holds no information, and is best used when the player has never been
	 * wrapped before
	 * @param player
	 */
	public QuestPlayer(Player player) {
		
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
	
	/**
	 * Returns the currently-stored information in a YamlConfiguration. <br />
	 * Output from this method should be expected to be used with {@link QuestPlayer#fromConfig(YamlConfiguration)}
	 * to produce an exact duplicate.
	 * @return
	 */
	public YamlConfiguration toConfig() {
		
		if (player == null) {
			return null;
		}
		
		YamlConfiguration config = new YamlConfiguration();
		
		config.set("Player", player.getUniqueId().toString());
		config.set("History", history.toConfig());
		config.set("Quests", quests);
		
		return config;
	}

	@Override
	public Collection<Participant> getParticipants() {
		Collection<Participant> col = new ArrayList<Participant>();
		col.add(this);
		return col;
	}
	
	
}
