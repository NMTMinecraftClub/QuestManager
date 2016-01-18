package com.SkyIsland.QuestManager.Magic.Spell.Effect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * Catches entities on fire
 * @author Skyler
 *
 */
public class FireEffect extends SpellEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(FireEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(FireEffect.class);
	}
	

	private enum aliases {
		DEFAULT(FireEffect.class.getName()),
		LONGI("SpellFire"),
		LONG("FireSpell"),
		SHORT("SFire");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static FireEffect valueOf(Map<String, Object> map) {
		return new FireEffect((int) map.get("duration"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("duration", duration);
		
		return map;
	}
	
	private int duration;
	
	public FireEffect(int fireDuration) {
		this.duration = fireDuration;
	}
	
	@Override
	public void apply(Entity e, Entity cause) {
		if (e instanceof LivingEntity) {
			LivingEntity targ = (LivingEntity) e;
			targ.setFireTicks(duration);
			targ.damage(0.0, cause);
		}
	}
	
	@Override
	public void apply(Location loc, Entity cause) {
		//can't damage a location
		//do nothing 
		;
	}
	
	
	
}
