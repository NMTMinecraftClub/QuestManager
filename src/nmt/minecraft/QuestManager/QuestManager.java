package nmt.minecraft.QuestManager;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.QuestManager.Configuration.QuestConfiguration;
import nmt.minecraft.QuestManager.Quest.Quest;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class QuestManager {
	
	private List<Quest> runningQuests;
	
	private List<QuestConfiguration> questTemplates;
	
	private File saveDirectory;
	
	private String name;
	
	/**
	 * Constructs a manager with the given directory information and a config file with
	 * the manager configuration section ready. The config passed is expected to have
	 * one key (the name of the manager) and the value be a list of strings (name of quests)
	 */
	public QuestManager(String name, File questDirectory, File saveDirectory, 
			List<String> questNames) {
		
		runningQuests = new LinkedList<Quest>();
		questTemplates = new LinkedList<QuestConfiguration>();
		
		this.saveDirectory = saveDirectory;
		
		//lookup and load templates for each quest name given
		for (String questName : questNames) {
			File questConfigFile = new File(questDirectory, questName);
			if (!questConfigFile.exists() || questConfigFile.isDirectory()) {
				QuestManagerPlugin.questManagerPlugin.getLogger().warning(
						"Unable to locate quest config file: "
						+ questConfigFile.getAbsolutePath());
				continue;
			}
			
			//found the file, let's load it up!
			YamlConfiguration questConfig = new YamlConfiguration();
			try {
				questConfig.load(questConfigFile);
			} catch (IOException | InvalidConfigurationException e) {
				e.printStackTrace();
				QuestManagerPlugin.questManagerPlugin.getLogger().warning(
						"Unable to load quest from file: " + questConfigFile.getAbsolutePath());
				continue;
			}
			
			QuestConfiguration questTemplate;
			try {
				questTemplate = new QuestConfiguration(questConfig);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
				QuestManagerPlugin.questManagerPlugin.getLogger().warning(
						"Error when parsing quest configuration file: " 
						+ questConfigFile.getAbsolutePath());
				continue;
			}
			
			
			questTemplates.add(questTemplate);
			
		}
	}
	
	/**
	 * Registers the quest, providing no frequency information.<br />
	 * Quests without frequency information are given equal chances of being
	 * produced when requesting a random quest.
	 */
	public void registerQuest(Quest quest) {
		runningQuests.add(quest);
	}
	
	
	/**
	 * Passes a stop signal to all quest managers, requesting a soft stop.<br />
	 * Soft stops typically save state and perform a padded stopping procedure,
	 * and are not guaranteed to stop all runningQuests.
	 */
	public void stopQuests() {
		if (runningQuests != null && !runningQuests.isEmpty()) {
			for (Quest quest : runningQuests) {
				quest.stop();
			}
		}
	}
	
	/**
	 * Immediately halts all running runningQuests.
	 */
	public void haltQuests() {
		if (runningQuests != null && !runningQuests.isEmpty()) {
			for (Quest quest : runningQuests) {
				quest.halt();
			}
		}
	}
	
	
	public File getSaveLocation() {
		return saveDirectory;
	}
	
	public String getName() {
		return this.name;
	}
	
}
