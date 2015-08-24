package nmt.minecraft.QuestManager.UI.Menu.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Player.QuestPlayer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * An inventory used with inventory gui's. Contains everything the rendered inventory needs
 * @author Skyler
 *
 */
public class GuiInventory implements ConfigurationSerializable {
	
	private Map<Integer, InventoryItem> items;
	
	public GuiInventory() {
		this.items = new HashMap<Integer, InventoryItem>();
	}
	
	public GuiInventory(Map<Integer, InventoryItem> items) {
		this.items = items;
	}
	
	public InventoryItem getItem(Integer pos) {
		return items.get(pos);
	}
	
	public Inventory getFormattedInventory(QuestPlayer player) {
		if (!player.getPlayer().isOnline()) {
			System.out.println("Cannot fetch inventory for offline player [GuiInventory@getFormattedInventory]");
			return null;
		}
		
		Player p = player.getPlayer().getPlayer();
		
		Inventory inv = Bukkit.createInventory(p, 45);
		if (!items.isEmpty()) {
			for (Entry<Integer, InventoryItem> e : items.entrySet()) {
				inv.setItem(e.getKey(), e.getValue().getDisplay(player));
			}
		}
		
		return inv;
	}

	@Override
	public Map<String, Object> serialize() {
		/*
		 * 4:
		 * 	display:
		 * 		==: itemstack
		 * 	item:
		 * 		==: itemstack
		 * 	cost: INTEGER
		 *  fame: INTEGER
		 * 8:
		 * 	""
		 */
		Map<String, Object> map = new HashMap<String, Object>();
		
		if (items.isEmpty()) {
			return map;
		}
		
		for (Entry<Integer, InventoryItem> e : items.entrySet()) {
			Map<String, Object> subMap = new HashMap<String, Object>(4);
			
			//create subMap as specified in comments above
			subMap.put("display", e.getValue().getDisplay(null));
			subMap.put("item", e.getValue().getItem());
			subMap.put("cost", e.getValue().getCost());
			subMap.put("fame", e.getValue().getFamecost());
			
			map.put(e.getKey().toString(), subMap);
		}
		
		
		
		return map;
	}
	
	public GuiInventory valueOf(Map<String, Object> configMap) {
		/*
		 * 4:
		 * 	display:
		 * 		==: itemstack
		 * 	item:
		 * 		==: itemstack
		 * 	cost: INTEGER
		 *  fame: INTEGER
		 * 8:
		 * 	""
		 */
		//TODO instead of jumping right into display, item, etc
		//make multiple inventory items. E.g. a purchase one, an inn one, etc
		
		Map<Integer, InventoryItem> map = new HashMap<Integer, InventoryItem>();
		for (Entry<String, Object> e : configMap.entrySet()) {
			Object obj = e.getValue();
			if (!(obj instanceof Map<?, ?>)) {
				QuestManagerPlugin.questManagerPlugin.getLogger().warning("Unable to load GuiInventory"
						+ " from config! Was looking for map, found:\n" + obj.toString());
				return null;
			}
			
			@SuppressWarnings("unchecked")
			Map<String, Object> itemMap = (Map<String, Object>) obj;
			int key = Integer.valueOf(e.getKey());
			
			int cost, fame;
			ItemStack display, item;
			
			display = (ItemStack) itemMap.get("display");
			item = (ItemStack) itemMap.get("item");
			cost = (int) itemMap.get("cost");
			fame = (int) itemMap.get("fame");
			
			InventoryItem ii = new InventoryItem(display, item, cost, fame);
			
			map.put(key, ii);
			
		}
		
		return new GuiInventory(map);
		
	}
	
	
}
