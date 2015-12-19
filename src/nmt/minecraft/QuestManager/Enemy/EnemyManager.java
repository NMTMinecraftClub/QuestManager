package nmt.minecraft.QuestManager.Enemy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nmt.minecraft.QuestManager.Region.Region;

public final class EnemyManager {
	
	private Map<Region, List<Enemy>> enemyMap;
	
	public EnemyManager() {
		enemyMap = new HashMap<Region, List<Enemy>>();
	}
	
	/**
	 * Adds the enemy to the list of enemies for a region.
	 * @param key
	 * @param enemy
	 * @return false if the region is not in the map, true otherwise
	 */
	public boolean addEnemy(Region key, Enemy enemy) {
		if (!enemyMap.containsKey(key)) {
			return false;
		}
		
		List<Enemy> list = enemyMap.get(key);
		list.add(enemy);
		
		return true;
	}
}
