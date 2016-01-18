package com.SkyIsland.QuestManager.Magic.Spell.Effect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.SkyIsland.QuestManager.QuestManagerPlugin;
import com.SkyIsland.QuestManager.Magic.MagicUser;

public class DamageMPEffect extends SpellEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(DamageMPEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(DamageMPEffect.class);
	}
	

	private enum aliases {
		DEFAULT(DamageMPEffect.class.getName()),
		LONGI("SpellDamageMP"),
		LONG("DamageMPSpell"),
		SHORT("SDamageMP");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static DamageMPEffect valueOf(Map<String, Object> map) {
		return new DamageMPEffect((double) map.get("damage"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("damage", damage);
		
		return map;
	}
	
	private double damage;
	
	public DamageMPEffect(double damage) {
		this.damage = damage;
	}
	
	@Override
	public void apply(Entity e, Entity cause) {
		if (e instanceof Player) {
			MagicUser qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager()
					.getPlayer((Player) e);
			qp.addMP((int) -damage);
			return;
		}
		
		if (e instanceof MagicUser) {
			((MagicUser) e).addMP((int) -damage);
		}
	}
	
	@Override
	public void apply(Location loc, Entity cause) {
		//can't damage a location
		//do nothing 
		;
	}
	
	
	
}
