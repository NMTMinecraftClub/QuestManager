package com.SkyIsland.QuestManager.Enemy;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

/*
 * Depicts a QM enemy, which can be created or destroyed as the world loads and unloads.<br />
 * Each enemy object represents a unique, spawnable enemy type. Each instance of an enemy is created from this
 * class when spawning things, but this class doesn't hold instances.
 */
public abstract class Enemy {
	
	protected EntityType type;
	
	public void spawn(Location loc) {
		loc.getWorld().spawnEntity(loc, type);
	}
	
}
