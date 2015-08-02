package nmt.minecraft.QuestManager.NPC;

import java.util.HashMap;
import java.util.Map;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.EquipmentConfiguration;
import nmt.minecraft.QuestManager.Configuration.QuestConfiguration;
import nmt.minecraft.QuestManager.Configuration.Utils.LocationState;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Quest;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.BioptionChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.Action.QuestStartAction;
import nmt.minecraft.QuestManager.UI.Menu.Message.BioptionMessage;
import nmt.minecraft.QuestManager.UI.Menu.Message.Message;

import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;

/**
 * NPC that starts a quest :D<br />
 * This simple starting version mounts atop a {@link SimpleBioptionNPC}, and has all the capability
 * and limits defined therein.
 * @author Skyler
 *
 */
public class SimpleQuestStartNPC extends SimpleBioptionNPC {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(SimpleQuestStartNPC.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(SimpleQuestStartNPC.class);
	}
	

	private enum aliases {
		FULL("nmt.minecraft.QuestManager.NPC.SimpleQuestStartNPC"),
		DEFAULT(SimpleQuestStartNPC.class.getName()),
		SHORT("SimpleQuestStartNPC"),
		INFORMAL("QSNPC");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
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
	
	public static SimpleQuestStartNPC valueOf(Map<String, Object> map) {
		if (map == null || !map.containsKey("name") || !map.containsKey("type") 
				 || !map.containsKey("location") || !map.containsKey("equipment")
				  || !map.containsKey("firstmessage") || !map.containsKey("duringmessage")
				  || !map.containsKey("postmessage")) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning("Invalid NPC info! "
					+ (map.containsKey("name") ? ": " + map.get("name") : ""));
			return null;
		}
		
		SimpleQuestStartNPC npc = new SimpleQuestStartNPC();
		npc.isEnd = false;
		
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
		
		npc.name = (String) map.get("name");
		
		npc.entity = loc.getWorld().spawnEntity(loc, type);
		npc.entity.setCustomName((String) map.get("name"));

		if (npc.entity instanceof LivingEntity) {
			EntityEquipment equipment = ((LivingEntity) npc.entity).getEquipment();
			equipment.setHelmet(econ.getHead());
			equipment.setChestplate(econ.getChest());
			equipment.setLeggings(econ.getLegs());
			equipment.setBoots(econ.getBoots());
			equipment.setItemInHand(econ.getHeld());
			
		}
		
		npc.chat = (BioptionMessage) map.get("firstmessage");
		npc.duringMessage = (Message) map.get("duringmessage");
		npc.afterMessage = (Message) map.get("postmessage");
		
		
		//provide our npc's name, unless we don't have one!
		if (npc.name != null && !npc.name.equals("")) {
			FancyMessage label = new FancyMessage(npc.name);
			npc.chat.setSourceLabel(label);
			npc.duringMessage.setSourceLabel(label);
			npc.afterMessage.setSourceLabel(label);
			
		}
		
		return npc;
	}
	
	private QuestConfiguration quest;
	
	private boolean isEnd;
	
	private Message duringMessage;
	
	private Message afterMessage;
	
	private Message finishMessage;
	
	public void setQuestTemplate(QuestConfiguration questTemplate) {
		this.quest = questTemplate;
	}
	
	public void markAsEnd(Message finishMessage) {
		this.finishMessage = finishMessage;
		
		if (name != null && !name.equals("")) {
			this.finishMessage.setSourceLabel(
					new FancyMessage(name));
		}
		
		isEnd = true;
	}
	
	@Override
	protected void interact(Player player) {
		
		//do different things depending on if the player has or is doing the quest
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(
				player.getUniqueId());
		
		ChatMenu messageChat = null;
		
		if (qp.hasCompleted(quest.getName())) {
			//already completed it
			messageChat = ChatMenu.getDefaultMenu(afterMessage);
		} else if (qp.isInQuest(quest.getName()) && !quest.isRepeatable()) {
			//is currently in it
			
			//Is this the possible end?
			
			if (isEnd) {
				//fetch instance of quest
				Quest qInst = null;
				for (Quest q : qp.getCurrentQuests()) {
					if (q.getName().equals(quest.getName())) {
						qInst = q;
						break;
					}
				}
				
				if (qInst == null) {
					//something went wrong!
					QuestManagerPlugin.questManagerPlugin.getLogger().warning(
							"Unable to find matching quest in SimpleQuestStartNPC!!!!!!!");
					return;
				}
				
				//perform check against completion!!!!
				if (qInst.isReady()) {	
					messageChat = ChatMenu.getDefaultMenu(finishMessage);
					qInst.completeQuest(false);
				} else {
					QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player).updateQuestBook();
				}
			}
			if (messageChat == null) {
				messageChat = ChatMenu.getDefaultMenu(duringMessage);
			}
		} else {
			messageChat = new BioptionChatMenu(chat, 
					new QuestStartAction(quest, player), null);			
		}

		messageChat.show(player);
	}
	
}
