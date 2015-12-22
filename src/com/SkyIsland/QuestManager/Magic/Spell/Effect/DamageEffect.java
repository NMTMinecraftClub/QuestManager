package com.SkyIsland.QuestManager.Magic.Spell.Effect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class DamageEffect extends SpellEffect {
	
	public static DamageEffect valueOf(Map<String, Object> map) {
		return new DamageEffect((double) map.get("damage"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("damage", damage);
		
		return map;
	}
	
	private double damage;
	
	public DamageEffect(double damage) {
		this.damage = damage;
	}
	
	@Override
	public void apply(Entity e, Entity cause) {
		if (e instanceof LivingEntity) {
			LivingEntity targ = (LivingEntity) e;
			targ.damage(damage, cause);
		}
	}
	
	@Override
	public void apply(Location loc) {
		//can't damage a location
		//do nothing 
		;
	}
	
	
	
}
