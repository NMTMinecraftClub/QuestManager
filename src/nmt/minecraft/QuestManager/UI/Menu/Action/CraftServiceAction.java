package nmt.minecraft.QuestManager.UI.Menu.Action;


import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
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
				if (!player.hasItem(req)) {
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
				player.removeItem(req);
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
	
	private void deny() {
		ChatMenu menu = new SimpleChatMenu(denial.getFormattedMessage());
		
		menu.show(player.getPlayer().getPlayer());
	}

}
