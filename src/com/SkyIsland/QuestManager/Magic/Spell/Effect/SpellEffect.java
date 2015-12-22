package com.SkyIsland.QuestManager.Magic.Spell.Effect;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

/**
 * An effect a spell might have.
 * @author Skyler
 *
 */
public abstract class SpellEffect {
	
	public abstract void apply(Entity e, Entity cause);
	
	public abstract void apply(Location loc);
	
}
