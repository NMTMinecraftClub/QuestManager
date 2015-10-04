package nmt.minecraft.QuestManager.UI.Menu.Action;


import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.inventivegames.util.tellraw.TellrawConverterLite;
import de.inventivegames.util.title.TitleManager;
import nmt.minecraft.QuestManager.NPC.Utils.ServiceOffer;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.Message.Message;

/**
 * Trades an itemstack for some currency
 * @author Skyler
 *
 */
public class OfferServiceAction implements MenuAction {
	
	private ServiceOffer offer;
	
	private QuestPlayer player;
	
	private Message denial;
	
	public OfferServiceAction(ServiceOffer offer, QuestPlayer player, Message denialMessage) {
		this.offer = offer;
		this.player = player;
		this.denial = denialMessage;
	}

	@Override
	public void onAction() {
		if (!player.getPlayer().isOnline()) {
			System.out.println("Very bad Service error!!!!!!!!!!!!!");
			return;
		}

		Player p = player.getPlayer().getPlayer();
		
		//check if they have the required item
		if (!hasItem(p.getInventory(), offer.getItem())) {
			deny();
			return;
		}
		
		//had the item
		
		//play exp sound, give money,
		//deduct required items
		
		removeItem(p.getInventory(), offer.getItem());

		player.addMoney(offer.getPrice());
		
		p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
		
		TitleManager.sendTimings(p, 10, 25, 10);
		
		TitleManager.sendSubTitle(p, TellrawConverterLite.convertToJSON(
				ChatColor.GOLD + " "));

        TitleManager.sendTitle(p, TellrawConverterLite.convertToJSON(
        		ChatColor.GOLD + "Sale"));

		
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
