package nmt.minecraft.QuestManager.NPC;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

public abstract class NPC implements ConfigurationSerializable {
	
	protected Entity entity;
	
	protected String name;
	
	
	
	public Entity getEntity() {
		return entity;
	}
	
	public String getName() {
		return name;
	}
}
