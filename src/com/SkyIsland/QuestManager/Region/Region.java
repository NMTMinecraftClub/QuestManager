package com.SkyIsland.QuestManager.Region;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Specifies some region of land.
 * @author Skyler
 *
 */
public abstract class Region {
	
	public abstract boolean isIn(Entity e);
	
	public abstract boolean isIn(Location loc);
	
	@Override
	public abstract int hashCode();
	
}
