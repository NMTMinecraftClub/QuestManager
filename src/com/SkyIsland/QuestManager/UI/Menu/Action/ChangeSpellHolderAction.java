package com.SkyIsland.QuestManager.UI.Menu.Action;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.SkyIsland.QuestManager.Fanciful.FancyMessage;
import com.SkyIsland.QuestManager.Player.QuestPlayer;
import com.SkyIsland.QuestManager.Player.Utils.SpellHolder;
import com.SkyIsland.QuestManager.UI.Menu.SimpleChatMenu;
import com.google.common.collect.Lists;

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
		
		if (player.getPlayer().isOnline()) {
			new SimpleChatMenu(
					new FancyMessage("You successfully changed your spell")
						.color(ChatColor.GREEN))
			.show(player.getPlayer().getPlayer());
			
			ItemStack swapHolder = null;
			int slot;
			Inventory inv = player.getPlayer().getPlayer().getInventory();
			for (slot = 0; slot <= 35; slot++) {
				ItemStack item = inv.getItem(slot);
				if (item == null || item.getType() == Material.AIR) {
					continue;
				}
				if (SpellHolder.SpellHolderDefinition.isHolder(item)
						&& item.getType() == holder.getType()) {
					swapHolder = item;
					break;
				}
			}
			
			if (swapHolder == null) {
				//unable to find it!
				return;
			}
			
			ItemMeta meta = holder.getItemMeta();
			meta.setLore(Lists.newArrayList("Current Spell:", "  " + ChatColor.DARK_RED + newSpell));
			holder.setItemMeta(meta);
			
			inv.setItem(slot, holder);
			
		}
	}

}
