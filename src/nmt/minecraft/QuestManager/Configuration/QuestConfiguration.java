package nmt.minecraft.QuestManager.Configuration;

import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.QuestManager.QuestManager;
import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Quest;

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
	
	/**
	 * Gets whether or not the embedded quest has {@link nmt.minecraft.QuestManager.Quest.Quest#keepState save-state} enabled
	 * @return
	 */
	public boolean getSaveState() {
		return config.getBoolean(QuestConfigurationField.SAVESTATE.getKey(), 
				(boolean) QuestConfigurationField.SAVESTATE.getDefault());
	}
	
	/**
	 * Returns the complete {@link nmt.minecraft.QuestManager.Quest.Quest Quest} this configuration represents.<br />
	 * Subsequent calls to this method return new instances of the represented quest. It is
	 * up to the caller to keep track of returned quests and optimize performance when simply
	 * needing a reference to previously-instantiated Quests
	 * @return
	 * @throws InvalidConfigurationException 
	 */
	public Quest instanceQuest(QuestManager manager) throws InvalidConfigurationException {
		
		System.out.println("instancing...");
		
		if (!config.contains(QuestConfigurationField.GOALS.getKey())) {
			return null;
		}
		
		ConfigurationSection questSection = config.getConfigurationSection(
				QuestConfigurationField.GOALS.getKey());
		
		List<ConfigurationSection> goalList = new LinkedList<ConfigurationSection>();
		for (String key : questSection.getKeys(false)) {
			goalList.add(questSection.getConfigurationSection(key));
		}
		
//		@SuppressWarnings("unchecked")
//		List<Map<String, Object>> goalList = (List<Map<String, Object>>) config.getList(
//				QuestConfigurationField.GOALS.getKey());
//		
		Quest quest = new Quest(manager, getName(), getDescription(), getSaveState());
		
		for (ConfigurationSection section : goalList) {
			Goal goal = Goal.fromConfig(quest, section);
			quest.addGoal(goal);
		}
		
		return quest;
	}
	
}
