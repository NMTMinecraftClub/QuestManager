package nmt.minecraft.QuestManager.Quest.Requirements.Factory;

import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Requirements.Requirement;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class RequirementFactory<T extends Requirement> {
	
	public abstract T fromConfig(Goal goal, ConfigurationSection conf);
	
}
