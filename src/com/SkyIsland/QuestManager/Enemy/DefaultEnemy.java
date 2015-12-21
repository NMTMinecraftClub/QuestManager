package com.SkyIsland.QuestManager.Enemy;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.EntityType;

/**
 * Default enemy type for default mobs in minecraft.<br />
 * Wrapper for QM enemies
 * @author Skyler
 *
 */
public class DefaultEnemy extends Enemy {
	
	public DefaultEnemy(EntityType type) {
		super(type);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();
		
		map.put("type", type.name());
		
		return map;
	}
	
	public static DefaultEnemy valueOf(Map<String, Object> map) {
		EntityType type = (EntityType) map.get("type");
		
		return new DefaultEnemy(type);
	}
	
}
