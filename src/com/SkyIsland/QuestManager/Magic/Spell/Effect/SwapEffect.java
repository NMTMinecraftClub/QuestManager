package com.SkyIsland.QuestManager.Magic.Spell.Effect;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;

/**
 * Swaps location of the caster and an entity target. Does nothing on contact with a solid. 
 * @author Skyler
 *
 */
public class SwapEffect extends SpellEffect {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(SwapEffect.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(SwapEffect.class);
	}
	

	private enum aliases {
		DEFAULT(SwapEffect.class.getName()),
		LONGI("SpellTeleport"),
		LONG("TeleportSpell"),
		SHORT("STeleport");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public static SwapEffect valueOf(Map<String, Object> map) {
		return new SwapEffect(
				Material.valueOf((String) map.get("typefrom")),
				Material.valueOf((String) map.get("typeto"))
				);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("typefrom", typeFrom.name());
		map.put("typeto", typeTo.name());
		
		return map;
	}
	
	private Material typeFrom, typeTo;
	
	public SwapEffect(Material typeFrom, Material typeTo) {
		this.typeFrom = typeFrom;
		this.typeTo = typeTo;
	}
	
	@Override
	public void apply(Entity e, Entity cause) {
		Location tmp = e.getLocation().clone();
		e.teleport(cause);
		cause.teleport(tmp);
	}
	
	@Override
	public void apply(Location loc) {
		; //do nothing
	}
	
	
	
}
