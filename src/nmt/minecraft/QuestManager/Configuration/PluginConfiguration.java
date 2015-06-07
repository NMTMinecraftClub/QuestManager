package nmt.minecraft.QuestManager.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import nmt.minecraft.QuestManager.QuestManagerPlugin;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Wrapper class for configuration files needed by the plugin.<br />
 * This does not include configuration files for individual quests.
 * @author Skyler
 *
 */
public class PluginConfiguration {
	
	public static final double generatorVersion = 1.00;
	
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
	 * Gets and returns a set of all manager names in the configuration file
	 * @return
	 */
	public Set<String> getQuestManagerNames() {
		Set<String> names = config.getConfigurationSection("managers").getKeys(false);
		
		return names;
	}
	
	/**
	 * Returns a list of quest names that are listed under the provided manager.
	 * @param managerName The name of the manager to look up
	 * @return A set of all quest names under the specified manager, or <i>null</i> if the
	 * manager is not in the configuration file.
	 */
	public List<String> getQuests(String managerName) {
		ConfigurationSection managers = config.getConfigurationSection("managers");
		
		return managers.getStringList(managerName);
	}
	
	/**
	 * Gets the stored quest path information -- where the quest configuration files are stored
	 * @return
	 */
	public String getQuestPath() {
		return config.getString("quests");
	}
	
	/**
	 * Gets the stored save data path information
	 * @return
	 */
	public String getSavePath() {
		return config.getString("saves");
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
		ConfigurationSection managers = config.createSection("managers");
		
		List<String> questNames = new ArrayList<String>(4);
		questNames.add("Treasure Hunt");
		questNames.add("Regicide");
		questNames.add("An Unlikely Guest");
		questNames.add("Trial By Fire");
		managers.set("manager_1", questNames);
		
		ConfigurationSection locations = config.createSection("locations");
		locations.set("quests", "quests/");
		locations.set("saves", "savedata/");
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return config;
	}
	
}
