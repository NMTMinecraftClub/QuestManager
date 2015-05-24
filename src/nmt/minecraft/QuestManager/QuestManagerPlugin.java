package nmt.minecraft.QuestManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
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
	
	private List<QuestManager> managers;
	
	private YamlConfiguration config;
	
	private static String configFileName = "QuestManagerConfig.yml";
	
	public static final double version = 1.00;
	
	@Override
	public void onLoad() {
		QuestManagerPlugin.questManagerPlugin = this;
		
		//load up config
		
		
	}
	
	@Override
	public void onEnable() {
		managers = new LinkedList<QuestManager>();
		
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
	
	
}
