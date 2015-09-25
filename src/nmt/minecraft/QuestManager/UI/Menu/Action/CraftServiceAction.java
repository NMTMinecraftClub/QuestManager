package nmt.minecraft.QuestManager.UI.Menu.Action;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.inventivegames.util.tellraw.TellrawConverterLite;
import de.inventivegames.util.title.TitleManager;
import nmt.minecraft.QuestManager.NPC.Utils.ServiceCraft;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.Message.Message;

/**
 * Takes a list of requirements and a fee and produces results
 * @author Skyler
 *
 */
public class CraftServiceAction implements MenuAction {
	
	private ServiceCraft trade;
	
	private QuestPlayer player;
	
	private Message denial;
	
	public CraftServiceAction(ServiceCraft trade, QuestPlayer player, Message denialMessage) {
		this.trade = trade;
		this.player = player;
		this.denial = denialMessage;
	}

	@Override
	public void onAction() {
		if (!player.getPlayer().isOnline()) {
			System.out.println("Very bad Service error!!!!!!!!!!!!!");
			return;
		}
		//check their money
		if (player.getMoney() >= trade.getCost()) {
			//they have enough money
			
			Player p = player.getPlayer().getPlayer();
			
			//check if they have the required items
			boolean pass = true;
			for (ItemStack req : trade.getRequired()) {
				if (!hasItem(p.getInventory(), req)) {
					pass = false;
					break;
				}
			}
			
			if (!pass) {
				deny();
				return;
			}
			
			//had money, had items
			
			//play smith sound, take money,
			//deduct required items
			//give new item
			
			for (ItemStack req : trade.getRequired()) {
				removeItem(p.getInventory(), req);
			}
			player.addMoney(-trade.getCost());
			
			p.getInventory().addItem(trade.getResult());
			
			p.playSound(p.getLocation(), Sound.ANVIL_USE, 1, 1);
			
			TitleManager.sendTimings(p, 20, 40, 20);
			
			TitleManager.sendSubTitle(p, TellrawConverterLite.convertToJSON(
					ChatColor.GOLD + " "));

	        TitleManager.sendTitle(p, TellrawConverterLite.convertToJSON(
	        		ChatColor.GOLD + "Item Crafted"));
			
		} else {
			//not enough money
			//show them a menu, sorrow
						
			deny();
		}
		
	}
	
	/**
	 * Checks whether the passed inventory has enough of the provided item.<br />
	 * This method checks the name of the item when calculating how much they have
	 * @param searchItem
	 * @return
	 */
	private boolean hasItem(Inventory inv, ItemStack searchItem) {
		int count = 0;
		String itemName = null;
		
		if (searchItem.hasItemMeta() && searchItem.getItemMeta().hasDisplayName()) {
			itemName = searchItem.getItemMeta().getDisplayName();
		}
		
		for (ItemStack item : inv.all(searchItem.getType()).values()) {
			if ((itemName == null && (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName())) || 
					(item.hasItemMeta() && item.getItemMeta().getDisplayName() != null 
					  && item.getItemMeta().getDisplayName().equals(itemName))) {
				count += item.getAmount();
			}
		}
		
		if (count >= searchItem.getAmount()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes the passed item from the player's inventory.<br />
	 * This method also uses item lore to make sure the correct items are removed
	 * @param inv
	 * @param item
	 */
	private void removeItem(Inventory inv, ItemStack searchItem) {
		//gotta go through and find ones that match the name
		int left = searchItem.getAmount();
		String itemName = null;
		ItemStack item;
		
		if (searchItem.hasItemMeta() && searchItem.getItemMeta().hasDisplayName()) {
			itemName = searchItem.getItemMeta().getDisplayName();
		}
		
		for (int i = 0; i <= 35; i++) {
			item = inv.getItem(i);
			if (item != null && item.getType() == searchItem.getType())
			if (  (itemName == null && (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()))
				|| (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(itemName))	
					) {
				//deduct from this item stack as much as we can, up to 'left'
				//but if there's more than 'left' left, just remove it
				int amt = item.getAmount();
				if (amt <= left) {
					//gonna remove entire stack
					item.setType(Material.AIR);
					item.setAmount(0);
					item.setItemMeta(null);
				} else {
					item.setAmount(amt - left);
				}
				
				inv.setItem(i, item);
				left-=amt;
				
				if (left <= 0) {
					break;
				}
			}
		}
	}
	
	private void deny() {
		ChatMenu menu = new SimpleChatMenu(denial.getFormattedMessage());
		
		menu.show(player.getPlayer().getPlayer());
	}

}
