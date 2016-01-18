package com.SkyIsland.QuestManager.Magic.Spell.Effect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

public class HealEffect extends SpellEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(HealEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(HealEffect.class);
	}
	

	private enum aliases {
		DEFAULT(HealEffect.class.getName()),
		LONGI("SpellHeal"),
		LONG("HealSpell"),
		SHORT("SHeal");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static HealEffect valueOf(Map<String, Object> map) {
		return new HealEffect((double) map.get("amount"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("amount", amount);
		
		return map;
	}
	
	private double amount;
	
	public HealEffect(double amount) {
		this.amount = amount;
	}
	
	@Override
	public void apply(Entity caster, Entity cause) {
		if (caster instanceof LivingEntity) {
			LivingEntity e = (LivingEntity) caster;
			EntityRegainHealthEvent event = new EntityRegainHealthEvent(e, amount, RegainReason.MAGIC);
			Bukkit.getPluginManager().callEvent(event);
			
			if (event.isCancelled()) {
				return;
			}
			
			e.setHealth(Math.min(e.getMaxHealth(), 
			e.getHealth() + amount));
		}
	}
	
	@Override
	public void apply(Location loc, Entity cause) {
		//can't damage a location
		//do nothing 
		;
	}
	
	
	
}
