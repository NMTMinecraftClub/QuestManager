package com.SkyIsland.QuestManager.Magic.Spell.Effect;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;

/**
 * An effect a spell might have.
 * @author Skyler
 *
 */
public abstract class SpellEffect implements ConfigurationSerializable {
	
	public abstract void apply(Entity e, Entity cause);
	
	public abstract void apply(Location loc, Entity cause);
	
}
