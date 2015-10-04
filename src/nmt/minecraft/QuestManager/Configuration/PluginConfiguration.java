package nmt.minecraft.QuestManager.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Player.Utils.Compass;

/**
 * Wrapper class for configuration files needed by the plugin.<br />
 * This does not include configuration files for individual quests.
 * @author Skyler
 *
 */
public class PluginConfiguration {
	
	private YamlConfiguration config;
	
	public enum PluginConfigurationKey {
		
		VERSION("version"),
		CONSERVATIVE("config.conservativeMode"),
		VERBOSEMENUS("menus.verboseMenus"),
		ALLOWCRAFTING("player.allowCrafting"),
		ALLOWNAMING("player.allowNaming"),
		ALLOWTAMING("player.allowTaming"),
		PARTYSIZE("player.maxPartySize"),
		CLEANUPVILLAGERS("world.villagerCleanup"),
		XPMONEY("interface.useXPMoney"),
		PORTALS("interface.usePortals"),
		ADJUSTXP("interface.adjustXP"),
		TITLECHAT("interface.titleInChat"),
		COMPASS("interface.compass.enabled"),
		COMPASSTYPE("interface.compass.type"),
		COMPASSNAME("interface.compass.name"),
		WORLDS("questWorlds"),
		QUESTS("quests"),
		QUESTDIR("questDir"),
		SAVEDIR("saveDir");
		
		
		private String key;
		
		private PluginConfigurationKey(String key) {
			this.key = key;
		}
		
		public String getKey() {
			return key;
		}
	}
	
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
		
		if (config.getBoolean(PluginConfigurationKey.CONSERVATIVE.key, true)) {
			QuestManagerPlugin.questManagerPlugin.getLogger().info("Conservative mode is on,"
					+ " so invalid configs will simply be ignored instead of destroyed.");
		}
		
		if (getCompassEnabled()) {
			Compass.CompassDefinition.setCompassType(getCompassType());
			Compass.CompassDefinition.setDisplayName(getCompassName());
		}
	}
	
	/**
	 * Returns the version number of the current configuration file.<br />
	 * This is simply the reported version number in the configuration file.
	 * @return
	 */
	public double getVersion() {
		return config.getDouble(PluginConfigurationKey.VERSION.key, 0.0);
	}
	
	
	/**
	 * Returns a list of quest names that are listed under the provided manager.
	 * @param managerName The name of the manager to look up
	 * @return A set of all quest names under the specified manager, or <i>null</i> if the
	 * manager is not in the configuration file.
	 */
	public List<String> getQuests() {
		
		return config.getStringList(PluginConfigurationKey.QUESTS.key);
	}
	
	public List<String> getWorlds() {
		return config.getStringList(PluginConfigurationKey.WORLDS.key);
	}
	
	/**
	 * Gets the stored quest path information -- where the quest configuration files are stored
	 * @return
	 */
	public String getQuestPath() {
		return config.getString(PluginConfigurationKey.QUESTDIR.key);
	}
	
	/**
	 * Indicates whether or not the config indicates invalid configuration files, states, or
	 * active logs should be kept or removed.
	 * @return
	 */
	public boolean getKeepOnError() {
		return config.getBoolean(PluginConfigurationKey.CONSERVATIVE.key, true);
	}
	
	/**
	 * Should the plugin remove ALL villager in quest worlds before populating it with quest related NPCs?<br />
	 * This is useful to avoid stray villagers escape on error of the plugin, but removed the possibility to use villagers
	 * that aren't managed by QuestManager in registered QuestWorlds!
	 * @return
	 */
	public boolean getVillagerCleanup() {
		return config.getBoolean(PluginConfigurationKey.CLEANUPVILLAGERS.key);
	}
	
	/**
	 * Should xp gained in the quest world count as 'money'?<br />
	 * When this is enabled, all XP received is instead converted to 'money'. This is represented to the player's client
	 * as the level of the player.
	 * @return
	 */
	public boolean getXPMoney() {
		return config.getBoolean(PluginConfigurationKey.XPMONEY.key);
	}
	
	/**
	 * Returns the largest size a party can get
	 * @return
	 */
	public int getMaxPartySize() {
		return config.getInt(PluginConfigurationKey.PARTYSIZE.key);
	}
	
	/**
	 * Can players tame animals in the QuestWorlds?
	 * @return
	 */
	public boolean getAllowTaming() {
		return config.getBoolean(PluginConfigurationKey.ALLOWTAMING.key);
	}
	
	/**
	 * Whether or not multiverse portals should be used and tracked.<br />
	 * When this is on, players will be returned to the last portal they used when leaving registered QuestWorlds.<br />
	 * @return
	 */
	public boolean getUsePortals() {
		return config.getBoolean(PluginConfigurationKey.PORTALS.key);
	}
	
	/**
	 * Returns whether or not the number of XP mobs drop should depend on their level
	 * @note Currently, this requires that the name of the mob have "Lvl ###" in it! TODO
	 * @return
	 */
	public boolean getAdjustXP() {
		return config.getBoolean(PluginConfigurationKey.ADJUSTXP.key);
	}
	
	/**
	 * Indicates whether or not menus should print out extra messages about expired menus.<br />
	 * This can be used as a security feature to avoid players from spamming old menus!
	 * @return
	 */
	public boolean getMenuVerbose() {
		return config.getBoolean(PluginConfigurationKey.VERBOSEMENUS.key);
	}
	
	public boolean getAllowCrafting() {
		return config.getBoolean(PluginConfigurationKey.ALLOWCRAFTING.key);
	}
	
	/**
	 * Whether or not renaming of items, entities is allowed through anvils
	 * @return
	 */
	public boolean getAllowNaming() {
		return config.getBoolean(PluginConfigurationKey.ALLOWNAMING.key);
	}
	
	/**
	 * Returns whether or not titles should be put into chat in all worlds
	 * @return
	 */
	public boolean getChatTitle() {
		return config.getBoolean(PluginConfigurationKey.TITLECHAT.key);
	}
	
	/**
	 * Returns whether or not compasses are enabled
	 * @return
	 */
	public boolean getCompassEnabled() {
		return config.getBoolean(PluginConfigurationKey.COMPASS.key, true);
	}
	
	/**
	 * Gets the configuration's defined material for the compass object
	 * @return
	 */
	public Material getCompassType() {
		try {
			return Material.valueOf(config.getString(PluginConfigurationKey.COMPASSTYPE.key, "COMPASS"));
		} catch (IllegalArgumentException e) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to find the compass material: " 
		+ config.getString(PluginConfigurationKey.COMPASSTYPE.key, "COMPASS"));
			return Material.COMPASS;
		}
		
	}
	
	/**
	 * Returns the name of the compass object
	 * @return
	 */
	public String getCompassName() {
		return config.getString(PluginConfigurationKey.COMPASSNAME.key, "Magic Compass");
	}
	
	/**
	 * Gets the stored save data path information
	 * @return
	 */
	public String getSavePath() {
		return config.getString(PluginConfigurationKey.SAVEDIR.key);
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
		
		config.set(PluginConfigurationKey.VERSION.key, QuestManagerPlugin.version);
		
		//config options
		config.set(PluginConfigurationKey.CONSERVATIVE.key, true);
		
		//menu options
		config.set(PluginConfigurationKey.VERBOSEMENUS.key, false);
		
		//player options
		config.set(PluginConfigurationKey.ALLOWCRAFTING.key, false);
		config.set(PluginConfigurationKey.ALLOWNAMING.key, false);
		config.set(PluginConfigurationKey.PARTYSIZE.key, 4);
		config.set(PluginConfigurationKey.ALLOWTAMING.key, false);
		
		//world options
		config.set(PluginConfigurationKey.CLEANUPVILLAGERS.key, false);
		
		//interface options
		config.set(PluginConfigurationKey.XPMONEY.key, true);
		config.set(PluginConfigurationKey.PORTALS.key, true);
		config.set(PluginConfigurationKey.ADJUSTXP.key, true);
		config.set(PluginConfigurationKey.TITLECHAT.key, true);
		
		List<String> worlds = new ArrayList<String>();
		worlds.add("QuestWorld");
		worlds.add("TutorialWorld");
		config.set(PluginConfigurationKey.WORLDS.key, worlds);
		//ConfigurationSection managers = config.createSection("managers");
		
		List<String> questNames = new ArrayList<String>(1);
		config.set(PluginConfigurationKey.QUESTS.key, questNames);
		config.set(PluginConfigurationKey.QUESTDIR.key, "quests/");
		config.set(PluginConfigurationKey.SAVEDIR.key, "savedata/");
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return config;
	}
	
}
