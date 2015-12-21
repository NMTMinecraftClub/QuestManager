package com.SkyIsland.QuestManager.Enemy;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.EntityType;

/*
 * Depicts a QM enemy, which can be created or destroyed as the world loads and unloads.<br />
 * Each enemy object represents a unique, spawnable enemy type. Each instance of an enemy is created from this
 * class when spawning things, but this class doesn't hold instances.
 */
public abstract class Enemy implements ConfigurationSerializable {
	
	protected EntityType type;
	
	public Enemy(EntityType type) {
		this.type = type;
	}
	
	public void spawn(Location loc) {
		loc.getWorld().spawnEntity(loc, type);
	}
	
}
