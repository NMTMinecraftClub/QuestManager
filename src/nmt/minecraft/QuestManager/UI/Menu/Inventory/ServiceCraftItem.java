package nmt.minecraft.QuestManager.UI.Menu.Inventory;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import nmt.minecraft.QuestManager.NPC.Utils.Service;
import nmt.minecraft.QuestManager.NPC.Utils.ServiceCraft;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.UI.Menu.Action.CraftServiceAction;
import nmt.minecraft.QuestManager.UI.Menu.Action.MenuAction;
import nmt.minecraft.QuestManager.UI.Menu.Message.Message;
import nmt.minecraft.QuestManager.UI.Menu.Message.PlainMessage;

/**
 * Represents a craft the service NPC can perform.
 * @author Skyler
 *
 */
public class ServiceCraftItem extends ServiceItem {
	
	private static final Message denialMessage = new PlainMessage(ChatColor.RED + "You were missing some components of the craft");
	
	private ServiceCraft craft;
	
	public ServiceCraftItem(ServiceCraft craft) {
		super(craft.getResult());
		this.craft = craft;
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
		List<String> lore = new LinkedList<String>();
		lore.add(
				ChatColor.DARK_PURPLE + "Craft  " + (craft.getCost() <= player.getMoney() ? ChatColor.GOLD : ChatColor.DARK_RED) + 
					"      Cost: " + craft.getCost());
		lore.add(ChatColor.DARK_RED + "Requires:");
		
		for (ItemStack item : craft.getRequired()) {
			if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
				lore.add("" + ChatColor.GRAY + item.getAmount() + " x " 
						+ (player.hasItem(item) ? ChatColor.GREEN : ChatColor.RED) 
						+ item.getItemMeta().getDisplayName());
			} else {
				lore.add((item.getAmount() > 0 ? "" + ChatColor.GRAY + item.getAmount() + " x " : "") 
						+ (player.hasItem(item) ? ChatColor.GREEN : ChatColor.RED) 
						+ toCase(item.getType().toString()));
			}
		}
			
		meta.setLore(lore);
		ret.setItemMeta(meta);
		
		return ret;
	}
	
	public ItemStack getResult() {
		return craft.getResult();
	}
	
	public List<ItemStack> getRequired() {
		return craft.getRequired();
	}
	
	@Override
	public MenuAction getAction(QuestPlayer player) {
		return new CraftServiceAction(craft, player, denialMessage);
	}

	/**
	 * @return the cost
	 */
	public int getCost() {
		return craft.getCost();
	}

	@Override
	public Service getService() {
		return craft;
	}
	
	/**
	 * Removes underscores from passed string and convers to title case
	 * @param input
	 * @return
	 */
	public static String toCase(String input) {
		input = input.replace("_", " ");
		input = input.toLowerCase();
		
		String result = "";
		boolean set = false;
		
		for (int pos = 0; pos < input.length(); pos++) {
			if (pos == 0) {
				result += input.substring(0, 1).toUpperCase();
				continue;
			}
			
			if (set) {
				set = false;
				result += input.substring(pos, pos+1).toUpperCase();
				continue;
			}
			
			if (input.charAt(pos) == ' ') {
				set = true;
			}
			
			result += input.charAt(pos);
			
			
			
		}

		return result;
	}
	
}
