package nmt.minecraft.QuestManager.Configuration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		
		
		
		try {
			config.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return config;
	}
	
}
