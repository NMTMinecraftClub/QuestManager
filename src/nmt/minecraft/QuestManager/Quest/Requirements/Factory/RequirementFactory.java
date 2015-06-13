package nmt.minecraft.QuestManager.Quest.Requirements.Factory;

import nmt.minecraft.QuestManager.Quest.Requirements.Requirement;

import org.bukkit.configuration.file.YamlConfiguration;

public abstract class RequirementFactory<T extends Requirement> {
	
	public abstract T fromConfig(YamlConfiguration config);
	
}
