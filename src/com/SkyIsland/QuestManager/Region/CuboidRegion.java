package com.SkyIsland.QuestManager.Region;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class CuboidRegion extends Region {

	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(CuboidRegion.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(CuboidRegion.class);
	}
	

	private enum aliases {
		DEFAULT(CuboidRegion.class.getName()),
		SIMPLE("RCube"),
		LONG("RCuboid");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private World world;
	
	private Vector least, most;
	
	public CuboidRegion(World world, Vector pos1, Vector pos2) {
		double lx, ly, lz, mx, my, mz;
		
		if (pos1.getX() < pos2.getX()) {
			lx = pos1.getX();
			mx = pos2.getX();
		} else {
			lx = pos2.getX();
			mx = pos1.getX();
		}
		
		if (pos1.getY() < pos2.getY()) {
			ly = pos1.getY();
			my = pos2.getY();
		} else {
			ly = pos2.getY();
			my = pos1.getY();
		}
		
		if (pos1.getZ() < pos2.getZ()) {
			lz = pos1.getZ();
			mz = pos2.getZ();
		} else {
			lz = pos2.getZ();
			mz = pos1.getZ();
		}
		
		this.least = new Vector(lx, ly, lz);
		this.most = new Vector(mx, my, mz);
		
		this.world = world;
	}
	
	@Override
	public boolean isIn(Entity e) {
		Location l = e.getLocation();
		double x, y, z;
		x = l.getX();
		y = l.getY();
		z = l.getZ();
		
		if (x <= most.getX() && x >= least.getX())
		if (y <= most.getY() && y >= least.getY())
		if (z <= most.getZ() && z >= least.getZ()) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean isIn(Location loc) {
		double x, y, z;
		x = loc.getX();
		y = loc.getY();
		z = loc.getZ();
		
		if (x <= most.getX() && x >= least.getX())
		if (y <= most.getY() && y >= least.getY())
		if (z <= most.getZ() && z >= least.getZ()) {
			return true;
		}
		
		return false;
	}

	@Override
	public int hashCode() {
		return (int) (84000 + (7 * least.getX())
				//+ (379 * world.getName().hashCode())
				+ (13 * least.getY())
				+ (17 * least.getZ())
				+ (23 * most.getX())
				+ (29 * most.getY())
				+ (47 * most.getZ()));
	}

	@Override
	public Location randomLocation(boolean safe) {
		Random rand = new Random();
		
		Location loc = world.getSpawnLocation().clone();
		loc.setX(least.getX());
		loc.setY(least.getY());
		loc.setZ(least.getZ());
		
		double dx = (most.getX() - least.getX()),
				dy = (most.getY() - least.getY()),
				dz = (most.getZ() - least.getZ());
		
		dx = rand.nextDouble() * dx;
		dy = rand.nextDouble() * dy;
		dz = rand.nextDouble() * dz;
		
		loc.add(dx, dy, dz);
		
		if (!safe) {
			return loc;
		}
		
		while (loc.add(0, 1, 0).getBlock().getType().isSolid() ||
				loc.clone().add(0,1,0).getBlock().getType().isSolid()) {
			if (loc.getY() > most.getY()) {
				//exhausted y search, so get a new random and return it instead
				return randomLocation(safe);
			}
		}
		
		//now move down to the ground
		while (!loc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
			//there's not ground below
			loc.add(0, -1, 0);
			if (loc.getY() < least.getY()) {
				if (!loc.clone().add(0, -1, 0).getBlock().getType().isSolid()) {
					return randomLocation(safe);
				}
				break;
			}
		}

		loc.setX(Math.floor(loc.getX()) + .5);
		loc.setZ(Math.floor(loc.getZ()) + .5);
		loc.setY(Math.floor(loc.getY()));
		return loc;
	}
	
	public static CuboidRegion valueOf(Map<String, Object> map) {
		World world = Bukkit.getWorld((String) map.get("world"));
		
		return new CuboidRegion(world, 
				(Vector) map.get("v1"),
				(Vector) map.get("v2"));
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("world", world.getName());
		map.put("v1", least);
		map.put("v2", most);
		
		return map;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof CuboidRegion) {
			CuboidRegion r = (CuboidRegion) o;
			if (r.least.equals(least) 
					&& r.most.equals(most)) {
				return true;
			}
		}
		
		return false;
	}
}
