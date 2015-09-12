package nmt.minecraft.QuestManager;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import nmt.minecraft.QuestManager.Configuration.Utils.GUID;
import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.Party;
import nmt.minecraft.QuestManager.Player.QuestPlayer;

/**
 * Stores a database of QuestPlayers for lookup and loading
 * @author Skyler
 *
 */
public class PlayerManager {
	
	private Map<UUID, QuestPlayer> players;
	
	private Map<GUID, Party> parties;
	
	/**
	 * Creates and loads player manager information from the provided configuration file.
	 * @param config
	 */
	public PlayerManager(YamlConfiguration config) {
		
		players = new HashMap<UUID, QuestPlayer>();
		parties = new HashMap<GUID, Party>();
		
		QuestManagerPlugin.questManagerPlugin.getLogger().info("Loading player database...");
		
		ConfigurationSection pSex = config.getConfigurationSection("players");
		
		if (!pSex.getKeys(false).isEmpty())
		for (String key : pSex.getKeys(false)) {
			players.put(
					UUID.fromString(key), (QuestPlayer) pSex.get(key));
		}
		
		ConfigurationSection gSex = config.getConfigurationSection("parties");
		
		if (!gSex.getKeys(false).isEmpty())
		for (String key : gSex.getKeys(false)) {
			parties.put(
					GUID.valueOf(key), (Party) gSex.get(key));
		}
		
	}
	
	/**
	 * Returns the QuestPlayer corresponding the the passed OfflinePlayer.<br />
	 * This method creates a new QuestPlayer wrapper for the provided UUID if there does not
	 * already exist a record for it.
	 * @param id
	 * @return
	 */	
	public QuestPlayer getPlayer(OfflinePlayer player) {
		return getPlayer(player.getUniqueId());
	}
	
	/**
	 * Returns the QuestPlayer corresponding the the passed UUID.<br />
	 * This method creates a new QuestPlayer wrapper for the provided UUID if there does not
	 * already exist a record for it.
	 * @param id
	 * @return
	 */
	public QuestPlayer getPlayer(UUID id) {
		if (players.containsKey(id)) {
			return players.get(id);
		}
		
		//initialize a player!
		QuestPlayer player = new QuestPlayer(Bukkit.getOfflinePlayer(id));
		players.put(id, player);
		return player;
	}
	
	/**
	 * Returns the party paired with the given ID.
	 * <br />if the party doesn't exist, null is returned instead
	 * @param id
	 * @return
	 */
	public Party getParty(GUID id) {
		if (!parties.containsKey(id)) {
			return null;
		}
		
		return parties.get(id);
	}
	
	public void addParty(Party party) {
		parties.put(party.getID(), party);
	}
	
	public void removeParty(Party party) {
		parties.remove(party.getID());
	}
	
	public Participant getParticipant(String idString) {
		
		if (GUID.valueOf(idString) != null) {
			return parties.get(GUID.valueOf(idString));
		}
		
		//assume it's a player string
		return getPlayer(UUID.fromString(idString));
	}
	
	public Collection<Party> getParties() {
		return parties.values();
	}
	
	public Collection<QuestPlayer> getPlayers() {
		return players.values();
	}
	
	public void save(File saveFile) {
		
		QuestManagerPlugin.questManagerPlugin.getLogger().info(
				"Saving player database...");
		
		YamlConfiguration config = new YamlConfiguration();
		ConfigurationSection playSex = config.createSection("players");
		
		if (!players.isEmpty()) {
			for (UUID key : players.keySet()) {
				playSex.set(key.toString(), getPlayer(key));
			}
		}
		
		ConfigurationSection gSex = config.createSection("parties");
		if (!parties.isEmpty()) {
			for (GUID key : parties.keySet()) {
				if (key == null) {
					continue;
				}
				gSex.set(key.toString(), getParty(key));
			}
		}
		
		try {
			config.save(saveFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
