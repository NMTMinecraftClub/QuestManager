package nmt.minecraft.QuestManager.UI.Menu.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import nmt.minecraft.QuestManager.Player.QuestPlayer;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * An inventory used with inventory gui's. Contains everything the rendered inventory needs
 * @author Skyler
 *
 */
public class GuiInventory implements ConfigurationSerializable {
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(GuiInventory.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(GuiInventory.class);
	}
	

	private enum aliases {
		FULL("nmt.minecraft.QuestManager.UI.Inventory.GuiInventory"),
		DEFAULT(GuiInventory.class.getName()),
		SHORT("GuiInventory"),
		INFORMAL("GINV");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
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
				Object key = e.getKey();
				if (key == null) {
					continue;
				}
				int val;
				if (key instanceof Integer) {
					val = (Integer) key;
				} else if (key instanceof String) {
					val = Integer.valueOf((String) key);
				} else {
					val = 0;
					System.out.println("invalid key! not string or int!");
				}
				inv.setItem(val, e.getValue().getDisplay(player));
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
	
	public static GuiInventory valueOf(Map<String, Object> configMap) {
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
		
		YamlConfiguration config = new YamlConfiguration();
		config.createSection("top", configMap);
		
		Map<Integer, InventoryItem> map = new HashMap<Integer, InventoryItem>();
		ConfigurationSection conf = config.getConfigurationSection("top");
		
		for (String slotString : conf.getKeys(false)) {
			ConfigurationSection section = conf.getConfigurationSection(slotString);
			if (slotString.startsWith("==")) {
				continue;
			}
			int key = Integer.valueOf(slotString);
			
			int cost, fame;
			ItemStack display, item;
			
			display = section.getItemStack("display");
			item = section.getItemStack("item");
			cost = section.getInt("cost");
			fame = section.getInt("fame");
			
			InventoryItem ii = new InventoryItem(item, display, cost, fame);
			
			map.put(key, ii);
			
		}
		
		return new GuiInventory(map);
		
	}
	
	
}
