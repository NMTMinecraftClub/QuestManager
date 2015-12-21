package com.SkyIsland.QuestManager.Enemy;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.SkyIsland.QuestManager.QuestManagerPlugin;
import com.SkyIsland.QuestManager.Region.Region;
import com.SkyIsland.QuestManager.Scheduling.Alarmable;
import com.SkyIsland.QuestManager.util.WeightedList;

public final class EnemyManager implements Alarmable<EnemyAlarms> {
	
	private Map<Region, WeightedList<Enemy>> enemyMap;
	
	private double spawnrate;
	
	/**
	 * Creates an empty enemy manager with a default spawnrate of 3 seconds
	 */
	public EnemyManager() {
		this(3.0);
	}
	
	/**
	 * Creates an enemy manager with the provided spawn rate
	 * @param spawnrate
	 */
	public EnemyManager(double spawnrate) {
		enemyMap = new HashMap<Region, WeightedList<Enemy>>();
		this.spawnrate = spawnrate;
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

	public double getSpawnrate() {
		return spawnrate;
	}

	public void setSpawnrate(double spawnrate) {
		this.spawnrate = spawnrate;
	}

	@Override
	public void alarm(EnemyAlarms reference) {
		switch (reference) {
		case SPAWN:
			spawnEnemies();
			break;
		}
	}
	
	/**
	 * Goes through all players in a quest world and spawns enemies if they are in a region.
	 */
	private void spawnEnemies() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (QuestManagerPlugin.questManagerPlugin.getPluginConfiguration().getWorlds().contains(
					player.getWorld().getName())) {
				//is in a quest world
				for (Region r : enemyMap.keySet()) {
					if (r.isIn(player)) {
						spawnInRegion(r);
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Spawns an enemy in the provided region from that region's list of enemies
	 * @param region
	 */
	private void spawnInRegion(Region region) {
		Enemy e;
		WeightedList<Enemy> l = enemyMap.get(region);
		
		e = l.getRandom();
		
		e.spawn(region.randomLocation(true));
	}
	
}
