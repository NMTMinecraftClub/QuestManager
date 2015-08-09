package nmt.minecraft.QuestManager.UI.Menu.Action;


import java.util.ListIterator;

import net.minecraft.server.v1_8_R3.Material;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.Message.Message;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import de.inventivegames.util.tellraw.TellrawConverterLite;
import de.inventivegames.util.title.TitleManager;

/**
 * Repairs a player's equipment
 * @author Skyler
 *
 */
public class ForgeAction implements MenuAction {

	private int cost;
	
	private QuestPlayer player;
	
	private Message denial;
	
	public ForgeAction(int cost, QuestPlayer player, Message denialMessage) {
		this.cost = cost;
		this.player = player;
		this.denial = denialMessage;
	}
	
	@Override
	public void onAction() {
		
		//check their money
		if (player.getMoney() >= cost) {
			//they have enough money
			
			//play smith sound, take money,
			//search inventory and find all equipment (:S) and repair it
			//and display a title
			
			
			if (!player.getPlayer().isOnline()) {
				System.out.println("Very bad ForgeAction error!!!!!!!!!!!!!");
				return;
			}
			
			
			
			Player p = player.getPlayer().getPlayer();
			
			Inventory inv = p.getInventory();
			ListIterator<ItemStack> items = inv.iterator();
			
			ItemStack item;
			int count = 0;
			
			while (items.hasNext()) {
				item = items.next();
				if (item == null) {
					continue;
				}
				if (!item.getType().equals(Material.AIR))
				if (item.getDurability() > 0) {
					item.setDurability((short) 0);
					count++;
				}
			}
			
			//make sure they had items to repair
			if (count == 0) {
				//no items were repaired!
				ChatMenu whoops = new SimpleChatMenu(
						new FancyMessage("Actually, you don't seem to have any equipment"
								+ " in need of repair!"));
				whoops.show(p);
				return;
			}
			
			player.addMoney(-cost);
			
			p.playSound(p.getLocation(), Sound.ANVIL_USE, 1, 0);
			
			TitleManager.sendTimings(p, 20, 40, 20);
			
			TitleManager.sendSubTitle(p, TellrawConverterLite.convertToJSON(
					ChatColor.BLUE + "" + count + " item(s) have been repaired"));

	        TitleManager.sendTitle(p, TellrawConverterLite.convertToJSON(
	        		ChatColor.GREEN + "Forge"));
			
		} else {
			//not enough money
			//show them a menu, sorrow
						
			ChatMenu menu = new SimpleChatMenu(denial.getFormattedMessage());
			
			menu.show(player.getPlayer().getPlayer());
		}
		
	}

}
