package com.SkyIsland.QuestManager.Magic.Spell.Effect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Tameable;

import com.SkyIsland.QuestManager.QuestManagerPlugin;

import net.md_5.bungee.api.ChatColor;

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
	
	public static final String summonDenial = ChatColor.YELLOW + "You cannot summon this, as you already have too many summons!";
	
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
		if (!(cause instanceof AnimalTamer)) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to summon tamed "
					+ "entity to caster, because they aren't an AnimalTamer: " + cause.getCustomName());
			return;
		}
		
		Location tmp = e.getLocation().clone();
		tmp.add(e.getLocation().getDirection().normalize().multiply(2));
		Entity ent = tmp.getWorld().spawnEntity(tmp, type);
		if (ent instanceof Tameable) {
			((Tameable) ent).setTamed(true);
			((Tameable) ent).setOwner((AnimalTamer) cause);
		} else {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to summon tamed"
					+ " entity, as entity type is not tameable: [" + type + "]");
		}
	}
	
	@Override
	public void apply(Location loc, Entity cause) {
		; //do nothing
	}
	
	
	
}
