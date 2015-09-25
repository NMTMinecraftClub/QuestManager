package nmt.minecraft.QuestManager.UI.Menu.Inventory;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.Inventory;

import nmt.minecraft.QuestManager.Player.QuestPlayer;

/**
 * An inventory used with inventory gui's. Contains everything the rendered inventory needs.<br />
 * Implementations should provide a way to load and save their information, as well as how to format the inventory for
 * display.
 * @author Skyler
 *
 */
public abstract class GuiInventory implements ConfigurationSerializable {
	
	private Map<Integer, InventoryItem> items;
	
	protected GuiInventory() {
		this.items = new HashMap<Integer, InventoryItem>();
	}
	
	protected GuiInventory(Map<Integer, InventoryItem> items) {
		this.items = items;
	}
	
	protected Map<Integer, InventoryItem> getItems() {
		return items;
	}
	
	public InventoryItem getItem(Integer pos) {
		return items.get(pos);
	}
	
	public abstract Inventory getFormattedInventory(QuestPlayer player);
		
}
