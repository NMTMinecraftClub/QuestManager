package com.SkyIsland.QuestManager.UI.Menu.Action;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import com.SkyIsland.QuestManager.Fanciful.FancyMessage;
import com.SkyIsland.QuestManager.Player.QuestPlayer;
import com.SkyIsland.QuestManager.UI.Menu.SimpleChatMenu;

public class ChangeSpellHolderAction implements MenuAction {

	private String newSpell;
	
	private QuestPlayer player;
	
	private ItemStack holder;
	
	public ChangeSpellHolderAction(QuestPlayer player, ItemStack holder, String newSpell) {
		this.newSpell = newSpell;
		this.holder = holder;
		this.player = player;
	}
	
	@Override
	public void onAction() {
		player.getStoredSpells().put(holder.getType(), newSpell);
		
		if (player.getPlayer().isOnline())
		new SimpleChatMenu(
				new FancyMessage("You successfully changed your spell")
					.color(ChatColor.GREEN))
		.show(player.getPlayer().getPlayer());
	}

}
