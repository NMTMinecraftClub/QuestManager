package nmt.minecraft.QuestManager;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.QuestManager.Configuration.QuestConfiguration;
import nmt.minecraft.QuestManager.Configuration.State.QuestState;
import nmt.minecraft.QuestManager.Quest.Quest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scoreboard.Scoreboard;

public class QuestManager {
	
	private List<Quest> runningQuests;
	
	private List<QuestConfiguration> questTemplates;
	
	private File saveDirectory;
	
	private String name;
	
	private Scoreboard scoreboard;
	
	/**
	 * Constructs a manager with the given directory information and a config file with
	 * the manager configuration section ready. The config passed is expected to have
	 * one key (the name of the manager) and the value be a list of strings (name of quests)
	 */
	public QuestManager(String name, File questDirectory, File saveDirectory, 
			List<String> questNames) {
		
		runningQuests = new LinkedList<Quest>();
		questTemplates = new LinkedList<QuestConfiguration>();
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		
		this.saveDirectory = saveDirectory;
		
		//lookup and load templates for each quest name given
		for (String questName : questNames) {
			File questConfigFile = new File(questDirectory, questName + ".yml");
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
			
			
//			try {
//				registerQuest(questTemplate.instanceQuest(this));
//
//			} catch (InvalidConfigurationException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
		}
		
		//check if there is any state information for this manager
		if (saveDirectory.listFiles().length != 0) {
			QuestManagerPlugin.questManagerPlugin.getLogger().info(name + " fetching state "
					+ "information...");	
			
			
			//files are [name]_[id]
			for (File stateFile : saveDirectory.listFiles()) {
				String questName = stateFile.getName().substring(0, 
						stateFile.getName().indexOf("_"));
				
				QuestConfiguration template = getQuestTemplate(questName);
				Quest quest;
				try {
					quest = template.instanceQuest(this);
					
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
					
					//remove it?
					if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getKeepOnError()) {
						stateFile.delete();
					} else {
						QuestManagerPlugin.questManagerPlugin.getLogger().info("Ignoring invalid config.");
					}
					
					continue;
				}
				
				QuestState state = new QuestState();
				YamlConfiguration config = new YamlConfiguration();
				
				
				try {
					config.load(stateFile);
					state.load(config);
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				} 
				
				try {
					quest.loadState(state);
					//if successfull, remove state info so we don't duplicate
					
					QuestManagerPlugin.questManagerPlugin.getLogger().info(
							"Successfully loaded state information for quest!");
					registerQuest(quest);
					
					stateFile.delete();
					
				} catch (InvalidConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				
				
				
						
			}

			QuestManagerPlugin.questManagerPlugin.getLogger().info(name + " finished!");	
			
		}
	}
	
	/**
	 * Registers the quest, providing no frequency information.<br />
	 * Quests without frequency information are given equal chances of being
	 * produced when requesting a random quest.
	 */
	public void registerQuest(Quest quest) {
		if (quest == null) {
			System.out.println("error!");
		}
		runningQuests.add(quest);
	}
	
	
	/**
	 * Passes a stop signal to all quest managers, requesting a soft stop.<br />
	 * Soft stops typically save state and perform a padded stopping procedure,
	 * and are not guaranteed to stop all runningQuests.
	 */
	public void stopQuests() {
		
		if (runningQuests != null && !runningQuests.isEmpty()) {
			
			QuestManagerPlugin.questManagerPlugin.getLogger().info(
					"Stopping quests and saving state information for " + runningQuests.size() +
					" quests...");
			
			for (Quest quest : runningQuests) {
				quest.stop();
			}
			
			QuestManagerPlugin.questManagerPlugin.getLogger().info("done!");
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
	
	public Scoreboard getScoreboard() {
		return this.scoreboard;
	}
	
	/**
	 * Looks up the matching quest config based on name.
	 * This manager does not look beyond the quests allowed to it
	 * @param questName 
	 * @return the Quest Configuration used as a template, or null if it wasn't found
	 */
	public QuestConfiguration getQuestTemplate(String questName) {
		if (questTemplates.isEmpty()) {
			return null;
		}
		
		for (QuestConfiguration qc : questTemplates) {
			if (qc.getName().equals(questName)) {
				return qc;
			}
		}
		
		return null;
	}

	
}
