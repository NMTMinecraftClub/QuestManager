package nmt.minecraft.QuestManager.NPC;

import java.util.HashMap;
import java.util.Map;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.EquipmentConfiguration;
import nmt.minecraft.QuestManager.Configuration.Utils.LocationState;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;

/**
 * NPC that offers a simple message to those that interact with it.<br />
 * SimpleChatNPCs do <b>not</b> support menus (they don't do anything if you click chat menu
 * buttons) 
 * @author Skyler
 *
 */
public class SimpleChatNPC extends NPC {

	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(SimpleChatNPC.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(SimpleChatNPC.class);
	}
	

	private enum aliases {
		FULL("nmt.minecraft.QuestManager.NPC.SimpleChatNPC"),
		DEFAULT(SimpleChatNPC.class.getName()),
		SHORT("SimpleChatNPC"),
		INFORMAL("SCNPC");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private FancyMessage chat;
	
	private SimpleChatNPC() {
		super();
	}
		
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>(4);
		
		map.put("name", name);
		map.put("type", entity.getType());
		map.put("location", new LocationState(entity.getLocation()));
		
		EquipmentConfiguration econ;
		
		if (entity instanceof LivingEntity) {
			econ = new EquipmentConfiguration(
					((LivingEntity) entity).getEquipment()
					);
		} else {
			econ = new EquipmentConfiguration();
		}
		
		map.put("equipment", econ);
		
		map.put("message", chat);
	
		
		return map;
	}
	
	public static SimpleChatNPC valueOf(Map<String, Object> map) {
		if (map == null || !map.containsKey("name") || !map.containsKey("type") 
				 || !map.containsKey("location") || !map.containsKey("equipment")
				  || !map.containsKey("message")) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Invalid NPC info! "
					+ (map.containsKey("name") ? ": " + map.get("name") : ""));
			return null;
		}
		
		SimpleChatNPC npc = new SimpleChatNPC();
		
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
		
		npc.entity = loc.getWorld().spawnEntity(loc, type);
		npc.name = (String) map.get("name");
		npc.entity.setCustomName((String) map.get("name"));

		if (npc.entity instanceof LivingEntity) {
			EntityEquipment equipment = ((LivingEntity) npc.entity).getEquipment();
			equipment.setHelmet(econ.getHead());
			equipment.setChestplate(econ.getChest());
			equipment.setLeggings(econ.getLegs());
			equipment.setBoots(econ.getBoots());
			equipment.setItemInHand(econ.getHeld());
			
		}
		
		//UPDATE: We wanna also accept regular strings, too :P
		Object msgObj = map.get("message");
		if (msgObj instanceof FancyMessage) {
			npc.chat = (FancyMessage) map.get("message");
		} else if (msgObj instanceof String){
			npc.chat = new FancyMessage((String) msgObj);
		} else {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning(
					"Invalid message type for Simple Chat NPC: " + npc.name);
		}
		
		return npc;
	}

	@Override
	protected void interact(Player player) {
		ChatMenu messageChat = new SimpleChatMenu(
				new FancyMessage(name)
					.color(ChatColor.DARK_GRAY)
					.style(ChatColor.BOLD)
					//TODO if we could do a COMPASS thing here, that would be so sick!
					//like make it execute /aetaeyaey 316667757 ID
					//which points a players compass to this entity
				.then(":\n")
				.then(chat)				
				);
		messageChat.show(player);
	}

}
