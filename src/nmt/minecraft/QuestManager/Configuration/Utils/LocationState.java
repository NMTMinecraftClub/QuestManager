package nmt.minecraft.QuestManager.Configuration.Utils;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Convenience class for saving and loading location data from config 
 * @author Skyler
 *
 */
public class LocationState implements ConfigurationSerializable {
	
	Location location;
	
	/**
	 * Stores fields and their config keys
	 * @author Skyler
	 *
	 */
	private enum fields {
		X("x"),
		Y("y"),
		Z("z"),
		PITCH("pitch"),
		YAW("yaw"),
		WORLD("world");
		
		private String key;
		
		private fields(String key) {
			this.key = key;
		}
		
		/**
		 * Returns the configuration key mapped to this field
		 * @return
		 */
		public String getKey() {
			return this.key;
		}
	}
	
	/**
	 * Creates a LocationState with the information from the passed location.
	 * @param location
	 */
	public LocationState(Location location) {
		this.location = location;
	}
	
	/**
	 * Serializes the wrapped location to a format that's able to be saved to a configuration file.
	 */
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> config = new HashMap<String, Object>(6);
		config.put(fields.X.getKey(), location.getX());
		config.put(fields.Y.getKey(), location.getY());
		config.put(fields.Z.getKey(), location.getZ());
		config.put(fields.PITCH.getKey(), location.getPitch());
		config.put(fields.YAW.getKey(), location.getYaw());
		config.put(fields.WORLD.getKey(), location.getWorld().getName());
		return null;
	}
	
	/**
	 * Uses the passed configuration map to instantiate a new location (and wrapper).
	 * @param configMap
	 * @return
	 */
	public static LocationState valueOf(Map<String, Object> configMap) {
		World world = Bukkit.getWorld((String) configMap.get(fields.WORLD.getKey()));
		
		if (world == null) {
			Bukkit.getLogger().info("Unable to create LocationState from passed map!");
			return null;
		}
		
		double x,y,z;
		float pitch, yaw;
		x = (double) configMap.get(fields.X.getKey());
		y = (double) configMap.get(fields.Y.getKey());
		z = (double) configMap.get(fields.Z.getKey());
		pitch = (float) configMap.get(fields.PITCH.getKey());
		yaw = (float) configMap.get(fields.YAW.getKey());
		
		return new LocationState(
				new Location(
						world,
						x,
						y,
						z,
						yaw,
						pitch));
	}
	
	/**
	 * Return the location wrapped by this class
	 * @return
	 */
	public Location getLocation() {
		return location;
	}

}
