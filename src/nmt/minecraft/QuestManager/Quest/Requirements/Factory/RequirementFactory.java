package nmt.minecraft.QuestManager.Quest.Requirements.Factory;

import org.bukkit.configuration.ConfigurationSection;

import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Requirements.Requirement;

public abstract class RequirementFactory<T extends Requirement> {
	
	public abstract T fromConfig(Goal goal, ConfigurationSection conf);
	
}
