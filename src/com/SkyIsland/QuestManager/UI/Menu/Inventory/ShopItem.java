package com.SkyIsland.QuestManager.UI.Menu.Inventory;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.SkyIsland.QuestManager.Player.QuestPlayer;
import com.SkyIsland.QuestManager.UI.Menu.Action.MenuAction;
import com.SkyIsland.QuestManager.UI.Menu.Action.PurchaseAction;

/**
 * An item used in an inventory menu.<br />
 * This is composed of an actual item stack, a display item type, and the cost & fame requirement
 * of the item.
 * @author Skyler
 *
 */
public class ShopItem extends InventoryItem {
	
	private ItemStack item;
	
	private int cost;
	
	private int famecost;
	
	public ShopItem(ItemStack item, ItemStack displayItem, int cost, int famecost) {
		super(displayItem);
		this.item = item;
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
	@Override
	public ItemStack getDisplay(QuestPlayer player) {
		if (player == null) {
			return getRawDisplayItem();
		}
		ItemStack ret = getRawDisplayItem().clone();
		ItemMeta meta = ret.getItemMeta();
		meta.setLore(Arrays.asList(
				(cost <= player.getMoney() ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) + 
					"Cost:               " + cost,
				(famecost <= player.getFame() ? ChatColor.DARK_GREEN : ChatColor.DARK_RED) +
					"Fame Required: " + famecost));
		ret.setItemMeta(meta);
		
		return ret;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	@Override
	public MenuAction getAction(QuestPlayer player) {
		return new PurchaseAction(player, item, cost, famecost);
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
