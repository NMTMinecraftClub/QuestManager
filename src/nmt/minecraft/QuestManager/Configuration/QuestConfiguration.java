package nmt.minecraft.QuestManager.Configuration;

import java.util.List;
import java.util.Set;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Quest.Goal;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Wrapper for quest configuration
 * @author Skyler
 *
 */
public class QuestConfiguration {
	
	public static final double configVersion = 1.00;
	
	private YamlConfiguration config;
		
	public QuestConfiguration(YamlConfiguration config) throws InvalidConfigurationException {
		
		this.config = config;
		
		if (!config.contains("configversion")) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Invalid quest "
					+ "configuration!");
			
			//just null out the config
			config = null;
			
			throw new InvalidConfigurationException();
		}
		
		//check config has all the fields we need, for safety
		checkConfig();
		
	}
	
	
	/**
	 * Checks the held configuration file for missing/corrupted/outdated fields, and corrects
	 * them when possible. <br />
	 * This is an internal method with straight implementation details.
	 * @see QuestConfigurationField
	 */
	private void checkConfig() {
		
		//Check each field and put in defaults if they aren't there (niave approach)
		for (QuestConfigurationField field : QuestConfigurationField.values()) {
			if (!config.contains(field.getKey())) {
				QuestManagerPlugin.questManagerPlugin.getLogger().warning("Failed to "
						+ "find field information: " + field.name());
				QuestManagerPlugin.questManagerPlugin.getLogger().info("Adding default value...");
				config.set(field.getKey(), field.getDefault());
			}
		}
	}
	
	/**
	 * Returns the stored quest name
	 * @return The name of the quest, or it's registered {@link QuestConfigurationField default}
	 */
	public String getName() {
		return config.getString(QuestConfigurationField.NAME.getKey(), (String) QuestConfigurationField.NAME.getDefault());
	}
	
	/**
	 * Gets the quest description
	 * @return
	 */
	public String getDescription() {
		return config.getString(QuestConfigurationField.DESCRIPTION.getKey(), (String) QuestConfigurationField.DESCRIPTION.getDefault());
	}
	
//	/**
//	 * Returns a set containing all the names of the included goals.
//	 * @return A set of the names, or <i>null</i> on error
//	 */
//	public Set<String> getGoalNames() {
//		
//		if (!config.contains(QuestConfigurationField.GOALS.getKey())) {
//			return null;
//		}
//		
//		Set<String> names;
//		ConfigurationSection goals = config.getConfigurationSection(
//				QuestConfigurationField.GOALS.getKey());
//		
//		names = goals.getKeys(false);
//		
//		return names;
//	}
	
	
	public List<Goal> getGoals() {
		
		if (!config.contains(QuestConfigurationField.GOALS.getKey())) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		List<ConfigurationSection> goalList = (List<ConfigurationSection>) config.getList(
				QuestConfigurationField.GOALS.getKey());
		
		List<Goal> goals;
		
		for (ConfigurationSection section : goalList) {
			Goal goal = Goal.fromConfig(section);
			goals.add(goal);
		}
		
		return goals;
	}
	
}
