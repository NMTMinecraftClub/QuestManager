package nmt.minecraft.QuestManager.Configuration;

import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.NPC.NPC;
import nmt.minecraft.QuestManager.NPC.SimpleQuestStartNPC;
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
	
	
	/**
	 * Gets whether or not the embedded quest has {@link nmt.minecraft.QuestManager.Quest.Quest#keepState save-state} enabled
	 * @return
	 */
	public boolean getSaveState() {
		return config.getBoolean(QuestConfigurationField.SAVESTATE.getKey(), 
				(boolean) QuestConfigurationField.SAVESTATE.getDefault());
	}
	
	/**
	 * Reads and instantiates a new starting npc for this quest.<br />
	 * It's common practice to only call this method a single time, as you only need one copy
	 * of 'earl' who people talk to to give the quest.
	 * @return The new NPC instance
	 */
	public NPC GetStartingNPCInstance() {
		//load up starting NPC information
		SimpleQuestStartNPC startingNPC = null;
		if (!config.contains(QuestConfigurationField.START.getKey())) {
			QuestManagerPlugin.questManagerPlugin.getLogger().info(
					  "Quest has no starting npc specified: " + getName());
		} else {
			startingNPC = (SimpleQuestStartNPC) config.get(QuestConfigurationField.START.getKey());
			startingNPC.setQuestTemplate(this);
		}
		
		return startingNPC;
	}
	
	/**
	 * Returns the complete {@link nmt.minecraft.QuestManager.Quest.Quest Quest} this configuration represents.<br />
	 * Subsequent calls to this method return new instances of the represented quest. It is
	 * up to the caller to keep track of returned quests and optimize performance when simply
	 * needing a reference to previously-instantiated Quests
	 * @return
	 * @throws InvalidConfigurationException 
	 */
	public Quest instanceQuest() throws InvalidConfigurationException {
				
		if (!config.contains(QuestConfigurationField.GOALS.getKey())) {
			return null;
		}
		
		ConfigurationSection questSection = config.getConfigurationSection(
				QuestConfigurationField.GOALS.getKey());
		
		List<ConfigurationSection> goalList = new LinkedList<ConfigurationSection>();
		for (String key : questSection.getKeys(false)) {
			goalList.add(questSection.getConfigurationSection(key));
		}
			
		Quest quest = new Quest(getName(), getDescription(), getSaveState());
		
		for (ConfigurationSection section : goalList) {
			Goal goal = Goal.fromConfig(quest, section);
			quest.addGoal(goal);
		}
		
		
		
		//get list of NPCs and get them created
		if (config.contains(QuestConfigurationField.NPCS.getKey())) {
			ConfigurationSection npcSection = config.getConfigurationSection(
					QuestConfigurationField.NPCS.getKey());
			
			if (!npcSection.getKeys(false).isEmpty()) {
				for (String key : npcSection.getKeys(false)) {
					quest.addNPC((NPC) npcSection.get(key));
				}
			}
		}
		
		return quest;
	}
	
}
