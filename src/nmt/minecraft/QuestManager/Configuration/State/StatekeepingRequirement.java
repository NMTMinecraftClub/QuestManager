package nmt.minecraft.QuestManager.Configuration.State;

import org.bukkit.configuration.InvalidConfigurationException;

/**
 * Keeps state information
 * @author Skyler
 *
 */
public interface StatekeepingRequirement {
	
	
	public RequirementState getState();
	
	public void loadState(RequirementState state) throws InvalidConfigurationException;
	
}