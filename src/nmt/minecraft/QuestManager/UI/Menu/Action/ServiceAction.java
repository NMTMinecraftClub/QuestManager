package nmt.minecraft.QuestManager.UI.Menu.Action;


import org.bukkit.Sound;
import org.bukkit.entity.Player;

import nmt.minecraft.QuestManager.NPC.Utils.ServiceTrade;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.Message.Message;

/**
 * Takes a list of requirements and a fee and produces results
 * @author Skyler
 *
 */
public class ServiceAction implements MenuAction {
	
	private ServiceTrade trade;
	
	private QuestPlayer player;
	
	private Message denial;
	
	public ServiceAction(ServiceTrade trade, QuestPlayer player, Message denialMessage) {
		this.trade = trade;
		this.player = player;
		this.denial = denialMessage;
	}

	@Override
	public void onAction() {
		
		//check their money
		if (player.getMoney() >= trade.getCost()) {
			//they have enough money
			
			//play smith sound, take money,
			//search inventory and find all equipment (:S) and repair it
			//and display a title
			
			
			if (!player.getPlayer().isOnline()) {
				System.out.println("Very bad ForgeAction error!!!!!!!!!!!!!");
				return;
			}
			
			
			
			Player p = player.getPlayer().getPlayer();
			
			//Inventory inv = p.getInventory();
			//TODO TODO TODO
			
			player.addMoney(-trade.getCost());
			
			p.playSound(p.getLocation(), Sound.ANVIL_USE, 1, 0);
			
		} else {
			//not enough money
			//show them a menu, sorrow
						
			ChatMenu menu = new SimpleChatMenu(denial.getFormattedMessage());
			
			menu.show(player.getPlayer().getPlayer());
		}
		
	}

}
