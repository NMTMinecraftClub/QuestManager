package com.SkyIsland.QuestManager.Magic.Spell.Effect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.SkyIsland.QuestManager.QuestManagerPlugin;

/**
 * Summons a tamed creature for the caster 
 * @author Skyler
 *
 */
public class SummonTamedEffect extends SpellEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(SummonTamedEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(SummonTamedEffect.class);
	}
	

	private enum aliases {
		DEFAULT(SummonTamedEffect.class.getName()),
		LONGI("SpellSummonTamed"),
		LONG("SummonTamedSpell"),
		SHORT("SSummonTamed");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static SummonWolfEffect valueOf(Map<String, Object> map) {
		return new SummonWolfEffect();
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		return map;
	}
	
	private static boolean isTameable(EntityType type) {
		switch (type) {
		case HORSE:
		case OCELOT:
		case WOLF:
			return true;
		default:
			return false;
		}
	}
	
	private int duration;
	
	private EntityType type;
	
	private int count;
	
	private Map<String, Entity> summonMap;
	
	public SummonTamedEffect(int duration, EntityType type, int count) {
		this.duration = duration;
		this.type = type;
		this.count = count;
		if (!isTameable(type)) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning(
					"WARNING! Summon'ed type [" + type + "] may not be tameable, and could "
					+ "result in exceptions further on. Please review tameable mobs and "
					+ "use one of those instead!");
		}
		
		this.summonMap = new HashMap<String, Entity>();
		
	}
	
	@Override
	public void apply(Entity e, Entity cause) {
		Location tmp = e.getLocation().clone();
		e.teleport(cause);
		cause.teleport(tmp);
	}
	
	@Override
	public void apply(Location loc, Entity cause) {
		; //do nothing
	}
	
	
	
}
