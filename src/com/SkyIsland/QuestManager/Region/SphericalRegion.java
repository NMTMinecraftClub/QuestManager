package com.SkyIsland.QuestManager.Region;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class SphericalRegion extends Region {
	
	private World world;
	
	private Vector center;
	
	private double radius;
	
	public SphericalRegion(World world, Vector center, double radius) {
		this.world = world;
		this.center = center;
		this.radius = radius;
	}

	@Override
	public boolean isIn(Entity e) {
		return e.getLocation().getWorld().equals(world) && 
				e.getLocation().toVector().distance(center) <= radius;
	}

	@Override
	public boolean isIn(Location loc) {
		return loc.getWorld().equals(world) &&
				loc.toVector().distance(center) <= radius;
	}

	@Override
	public int hashCode() {
		return (int) (112000 
				+ (800 * world.getName().hashCode())
				+ (7 * center.getY())
				+ (17 * center.getZ())
				+ (23 * center.getX())
				+ (13 * radius));
	}
	
	@Override
	public Location randomLocation(boolean safe) {
		Location loc = world.getSpawnLocation().clone();
		loc.setX(center.getX());
		loc.setY(center.getY());
		loc.setZ(center.getZ());
		
		Random rand = new Random();
		double rad = rand.nextDouble() * radius;
		double dir = rand.nextDouble() * 360;
		
		loc.add(rad * Math.cos(dir), 0, rad * Math.sin(dir));
		
		if (!safe) {
			return loc;
		}
		
		//check y, climb up till we are out of our y
		if (loc.add(0,1,0).getBlock().getType().isSolid() ||
				loc.clone().add(0,1,0).getBlock().getType().isSolid()) {
			return randomLocation(safe);
		}
		
		loc.setY(Math.floor(loc.getY()));
		return loc;
	}
	
	
}
