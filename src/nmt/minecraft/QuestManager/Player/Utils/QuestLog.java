package nmt.minecraft.QuestManager.Player.Utils;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Quest;

/**
 * Utility class for the quest log.<br />
 * Provides nice, simple wrapper functions for the elaborate workings of the Quest Log
 * @author Skyler
 *
 */
public class QuestLog {
	
	private static String escq = "\\\"";
	
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
		int slot = 0;
		
		for (slot = 0; slot <= 35; slot++) {
			ItemStack item = inv.getItem(slot);
			if (item.hasItemMeta() && item.getType() == Material.WRITTEN_BOOK) {
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
		
		String builder = "replaceitem entity ";
		builder += play.getName() + " ";
		
		builder += getSlotString(slot) + " written_book 1 0 ";
		
		//now start putting pages
		builder += "{pages:[";
		
		//get title page
		FancyMessage title = new FancyMessage("      Quest Log\n\n\n  This book details your current quest progress & history.")
				.color(ChatColor.BLACK);
		builder += generatePageJSON(title.toJSONString().replace("\"", escq));
		
		builder += ",";
		
		//get stats page
		title = new FancyMessage(" " + qp.getPlayer().getName())
				.color(ChatColor.DARK_PURPLE)
			.then(" - ")
				.color(ChatColor.BLACK)
			.then(qp.getTitle())
				.color(ChatColor.DARK_RED)
			.then("\n-----\n  Fame: " + qp.getFame() + "\n  Gold: " + qp.getMoney())
				.color(ChatColor.GOLD)
			.then("\n\n  Current Quests: " + qp.getCurrentQuests().size())
				.color(ChatColor.DARK_GREEN)
			.then("\n\n  Completed Quests: " + qp.getCompletedQuests().size())
				.color(ChatColor.DARK_BLUE)
				.tooltip(qp.getCompletedQuests());
		
		builder += generatePageJSON(title.toJSONString().replace("\"", escq));
		
		//add quests
		if (qp.getCurrentQuests().isEmpty()) {
			builder += ",";
			builder += generatePage("\nYou do not have any active quests!");
		} else {
			for (Quest quest : qp.getCurrentQuests())  {
				builder += ",";
				builder += generatePageJSON(quest.getJSONDescription().replace("\"", escq));
			}
		}
		
		
		//bind
		builder += "], title:\"Quest Log\",author:" + play.getName() + "}";

		System.out.println(builder);
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), builder);
		
//		BookMeta bookMeta = (BookMeta) book.getItemMeta();
//		bookMeta.setPages(new LinkedList<String>());
//		
//		
//		//generate the first page
//		bookMeta.addPage("      Quest Log\n  " 
//				+ ChatColor.RESET + "\n\n"
//						+ "  This book details your current quest progress & history.");
//		
//		//generate the stats page
//		bookMeta.addPage(ChatColor.DARK_PURPLE + " " + qp.getPlayer().getName() + " - "
//				+ ChatColor.DARK_RED + qp.getTitle()
//				+ "\n-----\n  " + ChatColor.GOLD + "Fame: " + qp.getFame()
//				+ "\n  "			+ ChatColor.GOLD + "Gold: " + qp.getMoney()
//				+ ChatColor.DARK_GREEN + "\n\n  Current Quests: " + qp.getCurrentQuests().size()
//				+ ChatColor.DARK_BLUE + "\n\n  Completed Quests: " + qp.getCompletedQuests().size()
//				+ ChatColor.RESET);	
//			
//		
//		
//		//now do quest info
//		//Quest Name
//		//Quest Description
//			//Goal Description? :S
//		
//		if (qp.getCurrentQuests().isEmpty()) {
//			bookMeta.addPage("\nYou do not have any active quests!");
//		} else {
//			for (Quest quest : qp.getCurrentQuests())  {
//				bookMeta.addPage(quest.getDescription());
//			}
//		}
//		
//		
//		
//		book.setItemMeta(bookMeta);
//		play.sendMessage(ChatColor.GRAY + "Your "
//				+ ChatColor.DARK_GREEN + "Quest Log" + ChatColor.GRAY + " has been"
//				+ " updated!" + ChatColor.RESET);
//		play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(1, Tone.C));
//		play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(1, Tone.A));
//		
//		play.setLevel(qp.getMoney());
	}
	
	private static String getSlotString(int rawslot) {
		if (rawslot < 0) {
			return "invalid.slot";
		}
		if (rawslot < 9) {
			return "slot.hotbar." + rawslot;
		}
		
		return "slot.inventory." + (rawslot - 9);
	}
	
	
	private static String generatePageJSON(String JSON) {
		String ret = "\"[" + escq + escq + ",";
		
		if (JSON != null) {
			ret += JSON;
		}
		
		ret += "]\"";
		
		return ret;
	}
	
	/**
	 * Used to build pages for primitive strings
	 * @param line
	 * @return
	 */
	private static String generatePage(String line) {
		String ret = "\"[" + escq + escq + ",";
		
		if (line != null) {
			ret += formatText(line);
		}
		
		ret += "]\"";
		
		return ret;
	}
	
	@SuppressWarnings("unused")
	private static String generatePage(List<String> lines) {
		String ret = "\"[" + escq + escq + ",";
		
		String line = "";
		
		if (lines != null && !lines.isEmpty()) {
			Iterator<String> it = lines.iterator();
			
			while (it.hasNext()) {
				line += (it.next());
				if (it.hasNext()) {
					line += "\n";
				}
			}
		}
			
		ret += formatText(line);
		ret += "]\"";
		
		return ret;
		
		//"[\"\",{\"text\":\"Title Page\"},{\"text\":\"page1\"}]"
	}
		
	private static String formatText(String str) {
		return "{" + escq + "text" + escq + ": " + escq + str + escq + "}";
	}
}
