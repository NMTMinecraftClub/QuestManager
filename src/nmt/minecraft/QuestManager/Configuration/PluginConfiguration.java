package nmt.minecraft.QuestManager.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nmt.minecraft.QuestManager.QuestManagerPlugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

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
		return config.getStringList("questworlds");
	}
	
	/**
	 * Gets the stored quest path information -- where the quest configuration files are stored
	 * @return
	 */
	public String getQuestPath() {
		return config.getString("questdir");
	}
	
	/**
	 * Indicates whether or not the config indicates invalid configuration files, states, or
	 * active logs should be kept or removed.
	 * @return
	 */
	public boolean getKeepOnError() {
		return config.getBoolean("conservativemode", true);
	}
	
	/**
	 * Indicates whether or not menus should print out extra messages about expired menus.<br />
	 * This can be used as a security feature to avoid players from spamming old menus!
	 * @return
	 */
	public boolean getMenuVerbose() {
		return config.getBoolean("verbosemenus");
	}
	
	/**
	 * Gets the stored save data path information
	 * @return
	 */
	public String getSavePath() {
		return config.getString("savedir");
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
		config.set("conservativemode", true);
		config.set("verbosemenus", false);
		
		List<String> worlds = new ArrayList<String>();
		worlds.add("QuestWorld");
		worlds.add("TutorialWorld");
		config.set("questworlds", worlds);
		//ConfigurationSection managers = config.createSection("managers");
		
		List<String> questNames = new ArrayList<String>(1);
		config.set("quests", questNames);
		config.set("questdir", "quests/");
		config.set("savedir", "savedata/");
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return config;
	}
	
}
