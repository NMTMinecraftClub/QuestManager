package nmt.minecraft.QuestManager.Player.Utils;

import java.util.LinkedList;

import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Quest;

/**
 * Utility class for the quest log.<br />
 * Provides nice, simple wrapper functions for the elaborate workings of the Quest Log
 * @author Skyler
 *
 */
public class QuestLog {

	
	public static void addQuestlog(QuestPlayer qp) {
		if (!qp.getPlayer().isOnline()) {
			return;
		}
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
				.getWorlds().contains(qp.getPlayer().getPlayer().getWorld().getName())) {
			return;
		}
		
		Player play = qp.getPlayer().getPlayer();
		Inventory inv = play.getInventory();
		
		if (inv.firstEmpty() == -1) {
			//no room!
			return;
		}
		
		ItemStack book = null;
		
		for (ItemStack item : inv.all(Material.WRITTEN_BOOK).values()) {
			if (item.hasItemMeta()) {
				BookMeta meta = (BookMeta) item.getItemMeta();
				if (meta.getTitle().equals("Quest Log")
						&& meta.getAuthor().equals(play.getName())) {
					book = item;
					break;
				}
			}
		}
		
		if (book == null) {
		
			book = new ItemStack(Material.WRITTEN_BOOK);
			BookMeta bookMeta = (BookMeta) book.getItemMeta();
			
			bookMeta.setTitle("Quest Log");
			bookMeta.setAuthor(play.getName());
			
			book.setItemMeta(bookMeta);
			
			book.addUnsafeEnchantment(Enchantment.LUCK, 5);
			
			inv.addItem(book);
			
			play.sendMessage(ChatColor.GRAY + "A " + ChatColor.DARK_GREEN 
					+ "Quest Log" + ChatColor.GRAY + " has been added to your inventory."
					 + ChatColor.RESET);
		}
		
		updateQuestlog(qp);
	}
	
	public static void updateQuestlog(QuestPlayer qp) {
		if (!qp.getPlayer().isOnline()) {
			return;
		}
		if (!QuestManagerPlugin.questManagerPlugin.getPluginConfiguration()
				.getWorlds().contains(qp.getPlayer().getPlayer().getWorld().getName())) {
			return;
		}
		
		Player play = qp.getPlayer().getPlayer();
		Inventory inv = play.getInventory();
		ItemStack book = null;
		
		for (ItemStack item : inv.all(Material.WRITTEN_BOOK).values()) {
			if (item.hasItemMeta()) {
				BookMeta meta = (BookMeta) item.getItemMeta();
				if (meta.getTitle().equals("Quest Log")
						&& meta.getAuthor().equals(play.getName())) {
					book = item;
					break;
				}
			}
		}
		
		if (book == null) {
			//they don't have a quest log
			return;
		}
		
		
		
		BookMeta bookMeta = (BookMeta) book.getItemMeta();
		bookMeta.setPages(new LinkedList<String>());
		
		
		//generate the first page
		bookMeta.addPage("      Quest Log\n  " 
				+ ChatColor.RESET + "\n\n"
						+ "  This book details your current quest progress & history.");
		
		//generate the stats page
		bookMeta.addPage(ChatColor.DARK_PURPLE + " " + qp.getPlayer().getName() + " - "
				+ ChatColor.DARK_RED + qp.getTitle()
				+ "\n-----\n  " + ChatColor.GOLD + "Fame: " + qp.getFame()
				+ "\n  "			+ ChatColor.GOLD + "Gold: " + qp.getMoney()
				+ ChatColor.DARK_GREEN + "\n\n  Current Quests: " + qp.getCurrentQuests().size()
				+ ChatColor.DARK_BLUE + "\n\n  Completed Quests: " + qp.getCompletedQuests().size()
				+ ChatColor.RESET);	
			
		
		
		//now do quest info
		//Quest Name
		//Quest Description
			//Goal Description? :S
		
		if (qp.getCurrentQuests().isEmpty()) {
			bookMeta.addPage("\nYou do not have any active quests!");
		} else {
			for (Quest quest : qp.getCurrentQuests())  {
				bookMeta.addPage(quest.getDescription());
			}
		}
		
		
		
		book.setItemMeta(bookMeta);
		play.sendMessage(ChatColor.GRAY + "Your "
				+ ChatColor.DARK_GREEN + "Quest Log" + ChatColor.GRAY + " has been"
				+ " updated!" + ChatColor.RESET);
		play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(1, Tone.C));
		play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(1, Tone.A));
		
		play.setLevel(qp.getMoney());
	}
}
