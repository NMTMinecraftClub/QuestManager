package nmt.minecraft.QuestManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.QuestManager.Configuration.PluginConfiguration;
import nmt.minecraft.QuestManager.Configuration.Utils.LocationState;
import nmt.minecraft.QuestManager.NPC.MuteNPC;
import nmt.minecraft.QuestManager.Player.Party;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Requirements.ArriveRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.PositionRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.PossessRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.VanquishRequirement;
import nmt.minecraft.QuestManager.UI.ChatGuiHandler;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Provided API and Command Line interaction between enlisted quest managers and
 * the user. <br />
 * 
 * @author Skyler
 * @todo Figure out where QuestManagers are going to be created. Through a
 * command? If so, how do you specify which quests for which manager? Do you
 * go through every single one and add it? Maybe instead through configs? 
 * What do the configs need? Maybe world name, list of quest names? How do
 * we look up quests by quest name? [next step]
 */
public class QuestManagerPlugin extends JavaPlugin {
	
	public static QuestManagerPlugin questManagerPlugin;
	
	private RequirementManager reqManager;
	
	private PlayerManager playerManager;
	
	private List<QuestManager> managers;
	
	private ChatGuiHandler guiHandler;
	
	private PluginConfiguration config;
	
	private File saveDirectory;
	
	private File questDirectory;
	
	private final static String configFileName = "QuestManagerConfig.yml";
	
	private final static String playerConfigFileName = "players.yml";
	
	public static final double version = 1.00;
	
	@Override
	public void onLoad() {
		QuestManagerPlugin.questManagerPlugin = this;
		reqManager = new RequirementManager();
		
		//load up config
		File configFile = new File(getDataFolder(), configFileName);
		
		config = new PluginConfiguration(configFile);		
				
		//perform directory checks
		saveDirectory = new File(getDataFolder(), config.getSavePath());
		if (!saveDirectory.exists()) {
			saveDirectory.mkdirs();
		}
		
		questDirectory = new File(getDataFolder(), config.getQuestPath());
		if (!questDirectory.exists()) {
			questDirectory.mkdirs();
		}
		
		
		//register our own requirements
		reqManager.registerFactory("ARRIVE", 
				new ArriveRequirement.ArriveFactory());
		reqManager.registerFactory("POSITION", 
				new PositionRequirement.PositionFactory());
		reqManager.registerFactory("POSSESS", 
				new PossessRequirement.PossessFactory());
		reqManager.registerFactory("VANQUISH", 
				new VanquishRequirement.VanquishFactory());
		
	}
	
	@Override
	public void onEnable() {
		//register our Location util!
		LocationState.registerWithAliases();
		QuestPlayer.registerWithAliases();
		Party.registerWithAliases();
		MuteNPC.registerWithAliases();
		
		managers = new LinkedList<QuestManager>();
		guiHandler = new ChatGuiHandler(this);
		
		//preload Player data
				File playerFile = new File(getDataFolder(), playerConfigFileName);
				if (!playerFile.exists()) {
					try {
						YamlConfiguration tmp = new YamlConfiguration();
						tmp.createSection("players");
						tmp.createSection("parties");
						tmp.save(playerFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				//get Player data & manager
				YamlConfiguration playerConfig = new YamlConfiguration();
				try {
					playerConfig.load(playerFile);
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
				playerManager = new PlayerManager(playerConfig);
		
		
		//parse config
		for (String managerName : config.getQuestManagerNames()) {
			File sDir = new File(saveDirectory, managerName);
			if (!sDir.exists()) {
				sDir.mkdirs();
			}
			QuestManager manager = new QuestManager(
					managerName,
					questDirectory, 
					sDir,
					config.getQuests(managerName));
			
			registerManager(manager);
					
		}
		
		
	}
	
	@Override
	public void onDisable() {
		
		//save user database
		playerManager.save(new File(getDataFolder(), playerConfigFileName));
		stopAllQuests();
	}
	
	/**
	 * Adds the requested manager to the list of active managers.<br />
	 * @param manager
	 */
	public void registerManager(QuestManager manager) {
		managers.add(manager);
	}
	
	/**
	 * Attempts to remove the passed manager.<br />
	 * @param manager
	 * @return
	 */
	public boolean unregisterManager(QuestManager manager) {
		return managers.remove(manager);
	}
	
	/**
	 * Attempts to softly stop all running quest managers and quests.<br />
	 * Quest managers (and underlying quests) may not be able to stop softly,
	 * and this method is not guaranteed to stop all quests (<i>especially</i>
	 * immediately).
	 */
	public void stopAllQuests() {
		if (managers == null || managers.isEmpty()) {
			return;
		}
	
		for (QuestManager man : managers) {
			man.stopQuests();
		}
	}
	
	/**
	 * Performs a hard stop to all quests.<br />
	 * Quests that are halted are not expected to perform any sort of save-state
	 * procedure, not halt in a particularly pretty manner. <br />
	 * Halting a quest <i>guarantees</i> that it will stop immediately upon
	 * receipt of the halt notice.
	 */
	public void haltAllQuests() {
		
		if (managers == null || managers.isEmpty()) {
			return;
		}
	
		for (QuestManager man : managers) {
			man.haltQuests();
		}
		
	}
	
	public RequirementManager getRequirementManager() {
		return this.reqManager;
	}
	
	public PluginConfiguration getPluginConfiguration() {
		return this.config;
	}
	
	public PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	public ChatGuiHandler getGuiHandler() {
		return guiHandler;
	}
	
}
