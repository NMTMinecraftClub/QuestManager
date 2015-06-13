package nmt.minecraft.QuestManager.Quest.Requirements;


import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Creates requirements from class information. <br />
 * Factories of course provide an easy way to instantiate requirements in a standard way. RequirementFactories also
 * apply the requirement that the requirement provides a means for loading up from config and provides a uniform
 * interface to interact with that requirement.
 * @author Skyler
 *
 */
public abstract class RequirementFactory<T extends Requirement> {
	
	
	public abstract T instance();
	
	public abstract T instance(YamlConfiguration config);
	
}
