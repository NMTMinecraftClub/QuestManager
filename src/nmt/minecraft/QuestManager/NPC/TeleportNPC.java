package nmt.minecraft.QuestManager.NPC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.EquipmentConfiguration;
import nmt.minecraft.QuestManager.Configuration.Utils.LocationState;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.BioptionChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.Action.TeleportAction;
import nmt.minecraft.QuestManager.UI.Menu.Message.BioptionMessage;
import nmt.minecraft.QuestManager.UI.Menu.Message.Message;
import nmt.minecraft.QuestManager.UI.Menu.Message.SimpleMessage;

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
 * NPC which offers to take a player and move them, for a fee?
 * @author Skyler
 *
 */
public class TeleportNPC extends SimpleBioptionNPC {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(TeleportNPC.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(TeleportNPC.class);
	}
	

	private enum aliases {
		FULL("nmt.minecraft.QuestManager.NPC.TeleportNPC"),
		DEFAULT(TeleportNPC.class.getName()),
		SHORT("TeleportNPC"),
		ALT("FerryNPC"),
		INFORMAL("TPNPC");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}

	
	private TeleportNPC(Location startingLoc) {
		super(startingLoc);
	}
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>(4);
		
		map.put("name", name);
		map.put("type", getEntity().getType());
		map.put("cost", cost);
		map.put("destination", new LocationState(destination));
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
		
		map.put("badrequirementmessage", altMessage);
		
		map.put("requiredquests", requirements);
	
		
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public static TeleportNPC valueOf(Map<String, Object> map) {
		if (map == null || !map.containsKey("name") || !map.containsKey("type") 
				 || !map.containsKey("location") || !map.containsKey("equipment")
				  || !map.containsKey("message") || !map.containsKey("cost") 
				|| !map.containsKey("destination") || !map.containsKey("requiredquests")
				|| !map.containsKey("badrequirementmessage")) {
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
		
		TeleportNPC npc = new TeleportNPC(loc);
		
		npc.name = (String) map.get("name");
		
		npc.cost = (int) map.get("cost");
		
		npc.destination = ((LocationState) map.get("destination")).getLocation();
		

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
		
		npc.chat = (BioptionMessage) map.get("message");
		npc.altMessage = (Message) map.get("badrequirementmessage");
		npc.requirements = (List<String>) map.get("requiredquests");
		
		//provide our npc's name, unless we don't have one!
		if (npc.name != null && !npc.name.equals("")) {
			FancyMessage label = new FancyMessage(npc.name);
			npc.chat.setSourceLabel(label);			
			npc.altMessage.setSourceLabel(label);
		}
		
		return npc;
	}
		
	private int cost;
	
	private Location destination;
	
	private Message altMessage;
	
	private List<String> requirements;
		
	@Override
	protected void interact(Player player) {
		
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager()
				.getPlayer(player.getUniqueId());
		
		FancyMessage msg = new FancyMessage("I apologize, but you ")
		.then("don't seem to have enough for fare...")
		.color(ChatColor.DARK_RED);
		
		boolean meetreqs = true;
		
		if (requirements != null && !requirements.isEmpty()) {
			//go through reqs, see if the player has those quests completed
			for (String req : requirements) {
				if (!qp.hasCompleted(req)) {
					meetreqs=false;
					break;
				}
			}
		}
		
		if (!meetreqs) {
			//doesn't have all the required quests done yet!
			ChatMenu messageChat = ChatMenu.getDefaultMenu(altMessage);
			messageChat.show(player);
			return;
		}
			
		SimpleMessage message = new SimpleMessage(msg);
		message.setSourceLabel(new FancyMessage(this.name));
		
		ChatMenu messageChat = new BioptionChatMenu(chat, 
					new TeleportAction(cost, destination, qp, message)
		, null);			

		messageChat.show(player);
	}
	
}
