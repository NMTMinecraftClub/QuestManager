package nmt.minecraft.QuestManager;

import java.util.HashMap;
import java.util.Map;

import nmt.minecraft.QuestManager.Quest.Requirements.Requirement;
import nmt.minecraft.QuestManager.Quest.Requirements.RequirementFactory;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Keep information about registered requirements and their factory classes.
 * @author Skyler
 *
 */
public class RequirementManager {
	
	private Map<String, RequirementFactory<?>> factories;
	
	/**
	 * Creates a new, empty RequirementManager
	 */
	public RequirementManager() {
		factories = new HashMap<String, RequirementFactory<?>>();
	}
	
	/**
	 * Attempts to look up an appropriate factory and return an instance of the requirement that factory is
	 * paired to.<br />
	 * Factories much first be registered with this manager before being able to be created from this method. 
	 * @param key The registered key used to look up the factory. This is the same as the ID key in quest
	 * configuration files used to identify the requirement.
	 * @param config The portion of the config file used to initialize the requirement
	 * @return
	 */
	public Requirement instanceRequirement(String key, YamlConfiguration config) {
		if (!factories.containsKey(key)) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to find "
					+ "constructor for requirement of type: " + key);
			return null;
		}
		
		
		Requirement r = factories.get(key).instance(config);
		
		return r;
	}
	
	/**
	 * Adds the factory to the manager's database with the given key.<br />
	 * Multiple requirements may not share the same key. If a key already exists, the factory will be
	 * refused.
	 * @param key The key used in config to denote the requirement instantiated by the passed factory
	 * @param factory The factory to be called when the user uses the above key
	 * @return On success, this method returns true. If a collision occurs, this method returns false.
	 */
	public boolean registerRequirement(String key, RequirementFactory<?> factory) {
		if (factories.containsKey(key)) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to register requirement"
					+ " factory, as the name is already registered: " + key);
			return false;
		}
		
		factories.put(key, factory);
		return true;
	}
}
