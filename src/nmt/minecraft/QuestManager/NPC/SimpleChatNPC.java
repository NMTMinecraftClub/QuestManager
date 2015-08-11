package nmt.minecraft.QuestManager.NPC;

import java.util.HashMap;
import java.util.Map;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.EquipmentConfiguration;
import nmt.minecraft.QuestManager.Configuration.Utils.LocationState;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.Message.Message;
import nmt.minecraft.QuestManager.UI.Menu.Message.SimpleMessage;

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
public class SimpleChatNPC extends SimpleNPC {

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
	
	private SimpleMessage chat;
	
	private SimpleChatNPC(Location startingLoc) {
		super(startingLoc);
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
		
		
		SimpleChatNPC npc = new SimpleChatNPC(loc);

		loc.getChunk();
		npc.setEntity(loc.getWorld().spawnEntity(loc, type));
		npc.setStartingLoc(loc);
		npc.name = (String) map.get("name");
		npc.getEntity().setCustomName((String) map.get("name"));

		if (npc.getEntity() instanceof LivingEntity) {
			EntityEquipment equipment = ((LivingEntity) npc.getEntity()).getEquipment();
			equipment.setHelmet(econ.getHead());
			equipment.setChestplate(econ.getChest());
			equipment.setLeggings(econ.getLegs());
			equipment.setBoots(econ.getBoots());
			equipment.setItemInHand(econ.getHeld());
			
		}
		
		//UPDATE: We wanna also accept regular strings, too :P
		Object msgObj = map.get("message");
		if (msgObj instanceof Message) {
			npc.chat = (SimpleMessage) map.get("message");
		} else if (msgObj instanceof FancyMessage) { 
			npc.chat = new SimpleMessage((FancyMessage) msgObj);
		} else if (msgObj instanceof String){
			npc.chat = new SimpleMessage((String) msgObj);
		} else {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning(
					"Invalid message type for Simple Chat NPC: " + npc.name);
		}
		
		if (npc.chat != null || npc.name != null || !npc.name.trim().isEmpty()) {
			npc.chat.setSourceLabel(new FancyMessage(npc.name));
		}
		
		return npc;
	}

	@Override
	protected void interact(Player player) {
		ChatMenu messageChat = new SimpleChatMenu(chat.getFormattedMessage());
		messageChat.show(player);
	}
	
	@Override
	public void tick() {
		
	}

}
