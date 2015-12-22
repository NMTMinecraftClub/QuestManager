package com.SkyIsland.QuestManager.Effects;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.SkyIsland.QuestManager.QuestManagerPlugin;

public class MagicEffect extends QuestEffect implements Runnable {

	private Location cur;
	
	private Location target;
	
	private Vector dir;
	
	private double speed;
	
	private static final int ticksPerSecond = 20;
	
	private int delay;
	
	private int perTick;

	public MagicEffect(double blocksPerSecond) {
		this.speed = blocksPerSecond;
	}
	
	@Override
	public void play(Player player, Location location) {
		
		// TODO Auto-generated method stub
		cur = player.getEyeLocation();
		dir = location.toVector().subtract(
				player.getLocation().toVector());
		dir.normalize();
		target = location;
		
		double rate = ticksPerSecond / speed;
		
		if (rate >= 1) {
			perTick = 1;
			delay = (int) Math.round(rate);
		} else {
			delay = 1;
			perTick = (int) Math.round(1 / rate);
		}
		
		Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, this, delay);
	}

	@Override
	public void run() {
		
		for (int i = 0; i < perTick; i++) {	
			cur.add(dir);
			spark();
			if (cur.distance(target) < 2) {
				break;
			}
		}
		if (cur.distance(target) > 2) {
			Bukkit.getScheduler().runTaskLater(QuestManagerPlugin.questManagerPlugin, this, delay);
		}
	}
	
	private void spark() {
		cur.getWorld().playEffect(cur, Effect.WITCH_MAGIC, 0);
	}
}
