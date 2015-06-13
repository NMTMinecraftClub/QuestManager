package nmt.minecraft.QuestManager;

import java.util.HashMap;
import java.util.Map;

import nmt.minecraft.QuestManager.Quest.Requirements.Requirement;
import nmt.minecraft.QuestManager.Quest.Requirements.Factory.RequirementFactory;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Keeps track of requirement keys and registered factories
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
	 * Registers the provided factory with the provided key.
	 * @param uniqueKey The key to register the factory to. This is the key used in the configuration file to
	 * invoke this factory
	 * @param factory The factory
	 * @return Whether the registration was successful. Failed registration usually is from non-unique keys.
	 */
	public boolean registerFactory(String uniqueKey, RequirementFactory<?> factory) {
		if (factories.containsKey(uniqueKey)) {
			QuestManagerPlugin.questManagerPlugin.getLogger()
				.warning("Unable to register requirement factory: key already exists [" + uniqueKey + "]");
			return false;
		}
		
		factories.put(uniqueKey, factory);
		
		return true;
	}
	
	/**
	 * Uses registered factories to instantiate a requirement from the given key and configuration file.<br />
	 * Keys must first be registered using {@link #registerFactory(String, RequirementFactory)}
	 * @param uniqueKey The key to look up, usually from the configuration file being loaded
	 * @param config The configuration section used to instantiate the requirement. 
	 * @return A newly created requirement, or <b>null</b> on error.
	 */
	public Requirement instanceRequirement(String uniqueKey, YamlConfiguration config) {
		if (!factories.containsKey(uniqueKey)) {
			QuestManagerPlugin.questManagerPlugin.getLogger()
			.warning("Unable to find registered requirement factory for key: [" + uniqueKey + "]");
			return null;
		}
		
		RequirementFactory<?> factory = factories.get(uniqueKey);
		
		return factory.fromConfig(config);
		
		
	}
	
}