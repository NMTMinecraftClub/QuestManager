package com.SkyIsland.QuestManager.Enemy;

import java.util.HashMap;
import java.util.Map;

import com.SkyIsland.QuestManager.Region.Region;
import com.SkyIsland.QuestManager.util.WeightedList;

public final class EnemyManager {
	
	private Map<Region, WeightedList<Enemy>> enemyMap;
	
	public EnemyManager() {
		enemyMap = new HashMap<Region, WeightedList<Enemy>>();
	}
	
	/**
	 * Registers the region with the manager.<br />
	 * Regions must be registered before they can start being associated with enemy types.
	 * @param region
	 * @return
	 */
	public boolean registerRegion(Region region) {
		if (region == null) {
			return false;
		}
		
		enemyMap.put(region, new WeightedList<Enemy>());
		
		return true;
	}
	
	/**
	 * Adds the enemy to the list of enemies for a region.<br />
	 * The underlying list does not make any checks against duplicates. Duplicate adds/inserts
	 * will result in duplicate entries.
	 * @param key
	 * @param enemy
	 * @param weight
	 * @return false if the region is not in the map, true otherwise
	 */
	public boolean addEnemy(Region key, Enemy enemy, double weight) {
		if (!enemyMap.containsKey(key)) {
			return false;
		}
		
		WeightedList<Enemy> list = enemyMap.get(key);
		list.add(enemy, weight);
		
		return true;
	}
	
	/**
	 * Adds the enemy to the region with a default weight 1
	 * @param key
	 * @param enemy
	 * @return
	 * @see #addEnemy(Region, Enemy, double)
	 */
	public boolean addEnemy(Region key, Enemy enemy) {
		return addEnemy(key, enemy, 1.0);
	}
	
	public void clear() {
		if (enemyMap.isEmpty()) {
			return;
		}
		
		for (WeightedList<Enemy> l : enemyMap.values()) {
			l.clear();
		}
		
		enemyMap.clear();
	}
}
