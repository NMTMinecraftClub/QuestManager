package com.SkyIsland.QuestManager.Magic.Spell;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import com.SkyIsland.QuestManager.Magic.MagicUser;

public abstract class TargetSpell extends Spell {
	
	protected TargetSpell(int cost, String name, String description) {
		super(cost, name, description);
	}
	
	public abstract void cast(MagicUser caster, Vector direction);
	
	protected abstract void onBlockHit(Location loc);
	
	protected abstract void onEntityHit(Entity target);
	
}
