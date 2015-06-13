package nmt.minecraft.QuestManager.Configuration.Active;

import nmt.minecraft.QuestManager.Player.Participant;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Wraps basic informaton contained in active quest configuration, like players involved.
 * @author Skyler
 *
 */
public class ActiveQuestConfiguration {
	
	private ConfigurationSection config;
	
	public ActiveQuestConfiguration(ConfigurationSection config) {
		this.config = config;
		
	}
	
	public Participant getParticipant() {
		String uselessKey = config.getKeys(false).iterator().next();
		return (Participant) config.get(uselessKey);
	}
}
