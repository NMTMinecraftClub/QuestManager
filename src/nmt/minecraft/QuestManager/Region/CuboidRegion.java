package nmt.minecraft.QuestManager.Region;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class CuboidRegion extends Region {

	private Vector least, most;
	
	public CuboidRegion(Vector pos1, Vector pos2) {
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

}
