package nmt.minecraft.QuestManager.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import nmt.minecraft.QuestManager.QuestManagerPlugin;

/**
 * Wrapper class for configuration files needed by the plugin.<br />
 * This does not include configuration files for individual quests.
 * @author Skyler
 *
 */
public class PluginConfiguration {
	
	private YamlConfiguration config;
	
	
	
	public PluginConfiguration(File configFile) {
		config = new YamlConfiguration();
		if (!configFile.exists() || configFile.isDirectory()) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning(ChatColor.YELLOW + "Unable to find Quest Manager config file!" + ChatColor.RESET);
			config = createDefaultConfig(configFile);
		} else 	try {
			config.load(configFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (config.getBoolean("conservativeMode", true)) {
			QuestManagerPlugin.questManagerPlugin.getLogger().info("Conservative mode is on,"
					+ " so invalid configs will simply be ignored instead of destroyed.");
		}
	}
	
	/**
	 * Returns the version number of the current configuration file.<br />
	 * This is simply the reported version number in the configuration file.
	 * @return
	 */
	public double getVersion() {
		return config.getDouble("version", 0.0);
	}
	
	
	/**
	 * Returns a list of quest names that are listed under the provided manager.
	 * @param managerName The name of the manager to look up
	 * @return A set of all quest names under the specified manager, or <i>null</i> if the
	 * manager is not in the configuration file.
	 */
	public List<String> getQuests() {
		
		return config.getStringList("quests");
	}
	
	public List<String> getWorlds() {
		return config.getStringList("questWorlds");
	}
	
	/**
	 * Gets the stored quest path information -- where the quest configuration files are stored
	 * @return
	 */
	public String getQuestPath() {
		return config.getString("questDir");
	}
	
	/**
	 * Indicates whether or not the config indicates invalid configuration files, states, or
	 * active logs should be kept or removed.
	 * @return
	 */
	public boolean getKeepOnError() {
		return config.getBoolean("conservativeMode", true);
	}
	
	/**
	 * Should the plugin remove ALL villager in quest worlds before populating it with quest related NPCs?<br />
	 * This is useful to avoid stray villagers escape on error of the plugin, but removed the possibility to use villagers
	 * that aren't managed by QuestManager in registered QuestWorlds!
	 * @return
	 */
	public boolean getVillagerCleanup() {
		return config.getBoolean("villagerCleanup");
	}
	
	/**
	 * Should xp gained in the quest world count as 'money'?<br />
	 * When this is enabled, all XP received is instead converted to 'money'. This is represented to the player's client
	 * as the level of the player.
	 * @return
	 */
	public boolean getXPMoney() {
		return config.getBoolean("useXPMoney");
	}
	
	/**
	 * Returns the largest size a party can get
	 * @return
	 */
	public int getMaxPartySize() {
		return config.getInt("maxPartySize");
	}
	
	/**
	 * Can players tame animals in the QuestWorlds?
	 * @return
	 */
	public boolean getAllowTaming() {
		return config.getBoolean("allowTaming");
	}
	
	/**
	 * Whether or not multiverse portals should be used and tracked.<br />
	 * When this is on, players will be returned to the last portal they used when leaving registered QuestWorlds.<br />
	 * @return
	 */
	public boolean getUsePortals() {
		return config.getBoolean("usePortals");
	}
	
	/**
	 * Returns whether or not the number of XP mobs drop should depend on their level
	 * @note Currently, this requires that the name of the mob have "Lvl ###" in it! TODO
	 * @return
	 */
	public boolean getAdjustXP() {
		return config.getBoolean("adjustXP");
	}
	
	/**
	 * Indicates whether or not menus should print out extra messages about expired menus.<br />
	 * This can be used as a security feature to avoid players from spamming old menus!
	 * @return
	 */
	public boolean getMenuVerbose() {
		return config.getBoolean("verboseMenus");
	}
	
	public boolean getAllowCrafting() {
		return config.getBoolean("allowCrafting");
	}
	
	/**
	 * Returns whether or not titles should be put into chat in all worlds
	 * @return
	 */
	public boolean getChatTitle() {
		return config.getBoolean("titleInChat");
	}
	
	/**
	 * Gets the stored save data path information
	 * @return
	 */
	public String getSavePath() {
		return config.getString("saveDir");
	}
	
	/**
	 * Sets up a default configuration file with blank values
	 * @param configFile
	 */
	private YamlConfiguration createDefaultConfig(File configFile) {
		if (configFile.isDirectory()) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning(ChatColor.RED + 
					"Unable to create default config!" + ChatColor.RESET);
			return null;
		}
		
		YamlConfiguration config = new YamlConfiguration();
		
		config.set("version", QuestManagerPlugin.version);
		config.set("conservativeMode", true);
		config.set("verboseMenus", false);
		config.set("allowCrafting", false);
		config.set("villagerCleanup", false);
		config.set("useXPMoney", true);
		config.set("maxPartySize", 4);
		config.set("allowTaming", false);
		config.set("usePortals", true);
		config.set("adjustXP", true);
		config.set("titleInChat", true);
		
		List<String> worlds = new ArrayList<String>();
		worlds.add("QuestWorld");
		worlds.add("TutorialWorld");
		config.set("questWorlds", worlds);
		//ConfigurationSection managers = config.createSection("managers");
		
		List<String> questNames = new ArrayList<String>(1);
		config.set("quests", questNames);
		config.set("questDir", "quests/");
		config.set("saveDir", "savedata/");
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return config;
	}
	
}
