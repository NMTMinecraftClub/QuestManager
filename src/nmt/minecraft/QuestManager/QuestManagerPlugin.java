package nmt.minecraft.QuestManager;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.QuestManager.Configuration.PluginConfiguration;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
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
	
	private List<QuestManager> managers;
	
	private PluginConfiguration config;
	
	private File saveDirectory;
	
	private File questDirectory;
	
	private final static String configFileName = "QuestManagerConfig.yml";
	
	public static final double version = 1.00;
	
	@Override
	public void onLoad() {
		QuestManagerPlugin.questManagerPlugin = this;
		
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
		
		
	}
	
	@Override
	public void onEnable() {
		managers = new LinkedList<QuestManager>();
		reqManager = new RequirementManager();
		
		YamlConfiguration c = new YamlConfiguration(),
				x = new YamlConfiguration(),
				y = new YamlConfiguration(),
				z = new YamlConfiguration();
		c.set("key", "value");
		x.set("1", "a");
		x.set("2", "b");
		x.set("3", "b");
		y.set("1", "a");
		y.set("2", "b");
		y.set("3", "b");
		z.set("1", "a");
		z.set("2", new ItemStack(Material.APPLE, 10));
		z.set("3", Bukkit.getWorlds().iterator().next().getSpawnLocation());
		
		
		
		List<YamlConfiguration> l = new LinkedList<YamlConfiguration>();
		l.add(x);
		l.add(y);
		l.add(z);
		
		c.set("list", l);
		
		try {
			c.save(new File(QuestManagerPlugin.questManagerPlugin.getDataFolder(),
					"temporary.yml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			
			/////////////////////
			//ADD PLAYER INFO to state!
			
			
			
			
//			Player dove = Bukkit.getPlayer("dove_bren");
//			if (!dove.isOnline()) {
//				return;
//			}
//			QuestPlayer qD = new QuestPlayer(dove);
//			Quest quest = new Quest(manager, "Quest for the Apples", "Journey to find a kingdom's worth of apples!", false);
//			Goal appleGoal = new Goal(quest, "Collect Apples", "Collect 10 apples");
//			Requirement appleReq = new PossessRequirement(qD, appleGoal, "Collect 10 Apples", Material.APPLE, 10);
//			LivingEntity foe = (LivingEntity) dove.getLocation().getWorld().spawnEntity(dove.getLocation(), EntityType.CHICKEN);
//			Requirement killReq = new VanquishRequirement(appleGoal, foe);
//			
//			appleGoal.addRequirement(appleReq);
//			appleGoal.addRequirement(killReq);
//			quest.addGoal(appleGoal);
//			qD.addQuest(quest);
//			manager.registerQuest(quest);
			
			
			
			
			
			/////////////////////
			
			
			
		}
		
		
	}
	
	@Override
	public void onDisable() {
		
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
	
}
