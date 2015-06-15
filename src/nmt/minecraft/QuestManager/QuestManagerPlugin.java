package nmt.minecraft.QuestManager;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import nmt.minecraft.QuestManager.Configuration.PluginConfiguration;
import nmt.minecraft.QuestManager.Configuration.Utils.LocationState;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Fanciful.MessagePart;
import nmt.minecraft.QuestManager.Fanciful.TextualComponent;
import nmt.minecraft.QuestManager.NPC.MuteNPC;
import nmt.minecraft.QuestManager.NPC.SimpleBioptionNPC;
import nmt.minecraft.QuestManager.NPC.SimpleChatNPC;
import nmt.minecraft.QuestManager.Player.Party;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Requirements.ArriveRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.PositionRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.PossessRequirement;
import nmt.minecraft.QuestManager.Quest.Requirements.VanquishRequirement;
import nmt.minecraft.QuestManager.UI.ChatGuiHandler;
import nmt.minecraft.QuestManager.UI.Menu.Message.BioptionMessage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
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
	
	private QuestManager manager;
	
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
		SimpleChatNPC.registerWithAliases();
		SimpleBioptionNPC.registerWithAliases();
		BioptionMessage.registerWithAliases();
		ConfigurationSerialization.registerClass(MessagePart.class);
		ConfigurationSerialization.registerClass(TextualComponent.ArbitraryTextTypeComponent.class);
		ConfigurationSerialization.registerClass(TextualComponent.ComplexTextTypeComponent.class);
		ConfigurationSerialization.registerClass(FancyMessage.class);

		guiHandler = new ChatGuiHandler(this, config.getMenuVerbose());
		
		
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
		
		
		//parse config & instantiate manager
		manager = new QuestManager(
				questDirectory, 
				saveDirectory,
				config.getQuests());
					
		
		
	}
	
	@Override
	public void onDisable() {
		
		//unregister our scheduler
		Bukkit.getScheduler().cancelTasks(this);
		
		//save user database
		playerManager.save(new File(getDataFolder(), playerConfigFileName));
		stopAllQuests();
	}
	
	
	/**
	 * Attempts to softly stop all running quest managers and quests.<br />
	 * Quest managers (and underlying quests) may not be able to stop softly,
	 * and this method is not guaranteed to stop all quests (<i>especially</i>
	 * immediately).
	 */
	public void stopAllQuests() {
		if (manager == null) {
			return;
		}
	
		manager.stopQuests();
	}
	
	/**
	 * Performs a hard stop to all quests.<br />
	 * Quests that are halted are not expected to perform any sort of save-state
	 * procedure, not halt in a particularly pretty manner. <br />
	 * Halting a quest <i>guarantees</i> that it will stop immediately upon
	 * receipt of the halt notice.
	 */
	public void haltAllQuests() {
		
		if (manager == null) {
			return;
		}
		
		manager.haltQuests();
		
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
