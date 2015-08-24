package nmt.minecraft.QuestManager.UI.Menu.Inventory;

import java.util.Arrays;

import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.UI.Menu.Action.MenuAction;
import nmt.minecraft.QuestManager.UI.Menu.Action.PurchaseAction;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * An item used in an inventory menu.<br />
 * This is composed of an actual item stack, a display item type, and the cost & fame requirement
 * of the item.
 * @author Skyler
 *
 */
public class InventoryItem {
	
	private ItemStack displayItem;
	
	private ItemStack item;
	
	private int cost;
	
	private int famecost;
	
	public InventoryItem(ItemStack item, ItemStack displayItem, int cost, int famecost) {
		this.item = item;
		this.displayItem = displayItem;
		this.cost = cost;
		this.famecost = famecost;
	}
	
	/**
	 * Returns the item that should be used to display the item to the given player.<br />
	 * This method formats the lore, etc to display correctly (and with correct colors) to
	 * the provided player given their fame and money.<br /><br />
	 * 
	 * If the passed player is null, the item without lore is returned.
	 * @param player
	 * @return
	 */
	public ItemStack getDisplay(QuestPlayer player) {
		if (player == null) {
			return displayItem;
		}
		ItemStack ret = displayItem.clone();
		ItemMeta meta = ret.getItemMeta();
		meta.setLore(Arrays.asList(
				(cost <= player.getMoney() ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + 
					"Cost:          " + cost,
				(famecost <= player.getFame() ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) +
					"Fame Required: "));
		ret.setItemMeta(meta);
		
		return ret;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public MenuAction getAction(QuestPlayer player) {
		return new PurchaseAction(player, displayItem, cost, famecost);
	}

	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * @return the famecost
	 */
	public int getFamecost() {
		return famecost;
	}
	
	
	
}
