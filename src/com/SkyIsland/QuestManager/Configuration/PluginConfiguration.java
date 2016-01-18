package com.SkyIsland.QuestManager.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.SkyIsland.QuestManager.QuestManagerPlugin;
import com.SkyIsland.QuestManager.Player.Utils.Compass;
import com.SkyIsland.QuestManager.Player.Utils.SpellHolder;

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
		ALLOWMAGIC("magic.enabled"),
		MANADEFAULT("magic.startingMana"),
		DAYREGEN("magic.dayRegen"),
		NIGHTREGEN("magic.nightRegen"),
		OUTSIDEREGEN("magic.outsideRegen"),
		KILLREGEN("magic.regenOnKill"),
		XPREGEN("magic.regenOnXP"),
		FOODREGEN("magic.regenOnFood"),
		HOLDERNAME("interface.magic.holderName"),
		ALTERTYPE("interface.magic.alterBlockType"),
		WORLDS("questWorlds"),
		//QUESTS("quests"),
		QUESTDIR("questDir"),
		SAVEDIR("saveDir"),
		ENEMYDIR("enemyDir"),
		SPELLDIR("spellDir"),
		SUMMONLIMIT("summonLimit");
		
		
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
		
		SpellHolder.SpellHolderDefinition.setDisplayName(getSpellHolderName());
		SpellHolder.SpellAlterTableDefinition.setBlockType(getAlterType());
	}
	
	/**
	 * Returns the version number of the current configuration file.<br />
	 * This is simply the reported version number in the configuration file.
	 * @return
	 */
	public double getVersion() {
		return config.getDouble(PluginConfigurationKey.VERSION.key, 0.0);
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
	 * Returns how many summons a player is allowed to have
	 * @return
	 */
	public int getSummonLimit() {
		return config.getInt(PluginConfigurationKey.SUMMONLIMIT.key, 2);
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
	 * Returns whether or not magic is set to be enabled
	 * @return
	 */
	public boolean getMagicEnabled() {
		return config.getBoolean(PluginConfigurationKey.ALLOWMAGIC.key);
	}
	
	/**
	 * Gets the specified default mana allotment, for new players
	 * @return
	 */
	public int getStartingMana() {
		return config.getInt(PluginConfigurationKey.MANADEFAULT.key);
	}
	
	/**
	 * Returns the amount of mp to regen in the day.<br />
	 * Negative amounts indicate a percentage to regen rather than a constant (-(return)%)
	 * @return The amount to regen; positive values indicate a constant, negative a rate (out of 100)
	 */
	public int getMagicRegenDay() {
		return config.getInt(PluginConfigurationKey.DAYREGEN.key);
	}
	
	/**
	 * Amount to regen at night
	 * @return The amount to regen; positive values indicate a constant, negative a rate (out of 100)
	 */
	public int getMagicRegenNight() {
		return config.getInt(PluginConfigurationKey.NIGHTREGEN.key);
	}
	
	/**
	 * @return whether or not mp should regen only when outside
	 */
	public boolean getMagicRegenOutside() {
		return config.getBoolean(PluginConfigurationKey.OUTSIDEREGEN.key);
	}
	
	/**
	 * The amount of mp to regen when a player gets a kill
	 * @return The amount to regen; positive values indicate a contant, negative a rate (out of 100)
	 */
	public int getMagicRegenKill() {
		return config.getInt(PluginConfigurationKey.KILLREGEN.key);
	}
	
	/**
	 * The amount to regen per xp picked up by a player
	 * @return The amount to regen; positive values indicate a constant, negative a rate (out of 100)
	 */
	public int getMagicRegenXP() {
		return config.getInt(PluginConfigurationKey.XPREGEN.key);
	}
	
	/**
	 * Amount to regen when a player consumes a food item
	 * @return The amount to regen; positive values indicate a constant, negative a rate (out of 100)
	 */
	public int getMagicRegenFood() {
		return config.getInt(PluginConfigurationKey.FOODREGEN.key);
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
	 * Returns the path to where enemy spawning location is kept
	 * @return
	 */
	public String getEnemyPath() {
		return config.getString(PluginConfigurationKey.ENEMYDIR.key);
	}
	
	/**
	 * Returns the path to where spell configuration
	 * @return
	 */
	public String getSpellPath() {
		return config.getString(PluginConfigurationKey.SPELLDIR.key);
	}
	
	/**
	 * Gets the name of the spell holders
	 * @return
	 */
	public String getSpellHolderName() {
		return config.getString(PluginConfigurationKey.HOLDERNAME.key);
	}
	
	/**
	 * Gets the material block type used for spell holder alteration
	 * @return
	 */
	public Material getAlterType() {
		return Material.valueOf(config.getString(PluginConfigurationKey.ALTERTYPE.key));
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
		
		config.set(PluginConfigurationKey.COMPASS.key, true);
		config.set(PluginConfigurationKey.COMPASSNAME.key, "Magic Compass");
		config.set(PluginConfigurationKey.COMPASSTYPE.key, "COMPASS");
		
		config.set(PluginConfigurationKey.HOLDERNAME.key, "Magic Scroll");
		config.set(PluginConfigurationKey.ALTERTYPE.key, "ENCHANTING_TABLE");
		
		//magic options
		config.set(PluginConfigurationKey.ALLOWMAGIC.key, true);
		config.set(PluginConfigurationKey.MANADEFAULT.key, 20);
		config.set(PluginConfigurationKey.DAYREGEN.key, 1);
		config.set(PluginConfigurationKey.NIGHTREGEN.key, 1);
		config.set(PluginConfigurationKey.OUTSIDEREGEN.key, false);
		config.set(PluginConfigurationKey.KILLREGEN.key, 0);
		config.set(PluginConfigurationKey.XPREGEN.key, 0);
		config.set(PluginConfigurationKey.FOODREGEN.key, 0);
		
		List<String> worlds = new ArrayList<String>();
		worlds.add("QuestWorld");
		worlds.add("TutorialWorld");
		config.set(PluginConfigurationKey.WORLDS.key, worlds);
		//ConfigurationSection managers = config.createSection("managers");
		
		config.set(PluginConfigurationKey.QUESTDIR.key, "quests/");
		config.set(PluginConfigurationKey.SAVEDIR.key, "savedata/");
		config.set(PluginConfigurationKey.ENEMYDIR.key, "enemies/");
		config.set(PluginConfigurationKey.SPELLDIR.key, "spells/");
				
		try {
			config.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return config;
	}
	
}
