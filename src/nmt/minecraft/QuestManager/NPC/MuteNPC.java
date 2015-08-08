package nmt.minecraft.QuestManager.NPC;

import java.util.HashMap;
import java.util.Map;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.EquipmentConfiguration;
import nmt.minecraft.QuestManager.Configuration.Utils.LocationState;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;

/**
 * Basic NPC with no interactivity.
 * @author Skyler
 * @config <i>name</i>: "name"<br />
 * <i>type</i>: "EntityType.VALUE.toString"<br />
 * <i>location</i>: <br />
 * &nbsp;&nbsp;==: org.bukkit.Location or nmt.minecraft.QuestManager.Configuration.Utils.LocationState<br />
 * <i>equipment</i>:<br />
 * &nbsp;&nbsp;[valid {@link nmt.minecraft.QuestManager.Configuration.EquipmentConfiguration}]
 */
public class MuteNPC extends NPC {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(MuteNPC.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(MuteNPC.class);
	}
	

	private enum aliases {
		FULL("nmt.minecraft.QuestManager.NPC.MuteNPC"),
		DEFAULT(MuteNPC.class.getName()),
		SHORT("MuteNPC"),
		INFORMAL("MNPC");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}

	public static MuteNPC valueOf(Map<String, Object> map) {
		if (map == null || !map.containsKey("name") || !map.containsKey("type") 
				 || !map.containsKey("location") || !map.containsKey("equipment")) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Invalid NPC info! "
					+ (map.containsKey("name") ? ": " + map.get("name") : ""));
			return null;
		}
		
		MuteNPC npc = new MuteNPC();
		
		EquipmentConfiguration econ = new EquipmentConfiguration();
		try {
			YamlConfiguration tmp = new YamlConfiguration();
			tmp.createSection("key",  (Map<?, ?>) map.get("equipment"));
			econ.load(tmp.getConfigurationSection("key"));
		} catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		LocationState ls = (LocationState) map.get("location");
		Location loc = ls.getLocation();
		
		EntityType type = EntityType.valueOf((String) map.get("type"));
		

		loc.getChunk();
		npc.setEntity(loc.getWorld().spawnEntity(loc, type));
		npc.getEntity().setCustomName((String) map.get("name"));

		if (npc.getEntity() instanceof LivingEntity) {
			EntityEquipment equipment = ((LivingEntity) npc.getEntity()).getEquipment();
			equipment.setHelmet(econ.getHead());
			equipment.setChestplate(econ.getChest());
			equipment.setLeggings(econ.getLegs());
			equipment.setBoots(econ.getBoots());
			equipment.setItemInHand(econ.getHeld());
			
		}
		
		return npc;
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>(4);
		
		map.put("name", name);
		map.put("type", getEntity().getType());
		map.put("location", new LocationState(getEntity().getLocation()));
		
		EquipmentConfiguration econ;
		
		if (getEntity() instanceof LivingEntity) {
			econ = new EquipmentConfiguration(
					((LivingEntity) getEntity()).getEquipment()
					);
		} else {
			econ = new EquipmentConfiguration();
		}
		
		map.put("equipment", econ);
	
		
		return map;
	}
	
	private MuteNPC() {
		super();
	}

	@Override
	protected void interact(Player player) {
		//we don't do anything on interact
		;		
	}
	
	
	
}
