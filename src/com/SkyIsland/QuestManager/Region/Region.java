package com.SkyIsland.QuestManager.Region;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * Specifies some region of land.
 * @author Skyler
 *
 */
public abstract class Region {
	
	/**
	 * Checks whether the provided entity is in the region
	 * @param e
	 * @return
	 */
	public abstract boolean isIn(Entity e);
	
	/**
	 * Checks whether a specified location falls within this region
	 * @param loc
	 * @return
	 */
	public abstract boolean isIn(Location loc);
	
	/**
	 * Returns a random location from within this region.
	 * @param safe Whether or not the location should be safe to spawn a normal mob (2x1)
	 * @return
	 */
	public abstract Location randomLocation(boolean safe);
	
	@Override
	public abstract int hashCode();
	
}
