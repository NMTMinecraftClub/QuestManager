package nmt.minecraft.QuestManager.Region;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class SphericalRegion extends Region {
	
	private Vector center;
	
	private double radius;
	
	public SphericalRegion(Vector center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	@Override
	public boolean isIn(Entity e) {
		return e.getLocation().toVector().distance(center) <= radius;
	}

	@Override
	public boolean isIn(Location loc) {
		return loc.toVector().distance(center) <= radius;
	}
	
	
	
}
