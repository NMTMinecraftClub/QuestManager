package nmt.minecraft.QuestManager.Player.Utils;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
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
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.History.HistoryEvent;

/**
 * A quest journal keeps track of the current target quest's history<br />
 * This class provides nice helper functions for making that happen
 * @author Skyler
 * @see QuestLog
 */
public class QuestJournal {
	
	public static final String escq = "\\\"";
	
	public static void addQuestJournal(QuestPlayer qp) {
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
		
		for (ItemStack item : inv.all(Material.BOOK_AND_QUILL).values()) {
			if (item.hasItemMeta()) {
				BookMeta meta = (BookMeta) item.getItemMeta();
				if (meta.getTitle().equals("Journal")
						&& meta.getAuthor().equals(play.getName())
						&& item.getEnchantmentLevel(Enchantment.LUCK) == 5) {
					book = item;
					break;
				}
			}
		}
		
		if (book == null) {
		
			book = new ItemStack(Material.BOOK_AND_QUILL);
			BookMeta bookMeta = (BookMeta) book.getItemMeta();
			
			bookMeta.setTitle("Journal");
			bookMeta.setAuthor(play.getName());
			
			book.setItemMeta(bookMeta);
			
			book.addUnsafeEnchantment(Enchantment.LUCK, 5);
			
			inv.addItem(book);
			
			play.sendMessage(ChatColor.GRAY + "A " + ChatColor.DARK_GREEN 
					+ "Quest Journal" + ChatColor.GRAY + " has been added to your inventory."
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
			if (item.hasItemMeta() && item.getType() == Material.BOOK_AND_QUILL) {
				BookMeta meta = (BookMeta) item.getItemMeta();
				if (meta.getTitle().equals("Journal")
						&& meta.getAuthor().equals(play.getName())
						&& item.getEnchantmentLevel(Enchantment.LUCK) == 5) {
					book = item;
					break;
				}
			}
		}
		
		if (book == null) {
			//they don't have a quest journal
			return;
		}
		
		String builder = "replaceitem entity ";
		builder += play.getName() + " ";
		
		builder += getSlotString(slot) + " writable_book 1 0 ";
		
		//now start putting pages
		builder += "{pages:[";
		
		//get title page
		FancyMessage title = new FancyMessage("      Journal\n  " + play.getName() + "\n\n  My own journal with details about my active quest")
				.color(ChatColor.BLACK);
		builder += generatePageJSON(title.toJSONString().replace("\"", escq));
		
		builder += ",";
		
		//get recent page
		title = new FancyMessage(" Recent events:\n" + qp.getPlayer().getName())
					.color(ChatColor.BLACK);
		List<HistoryEvent> events;
		
		events = qp.getHistory().events();
		
		if (events == null || events.isEmpty()) {
			title.then(" Nothing recent!");
		} else {
			for (HistoryEvent event : events.subList(events.size() - 6, events.size() - 1)) {
				title.then("-" + event.getDescription())
					.color(ChatColor.BLACK);
			}
		}
		
		builder += generatePageJSON(title.toJSONString().replace("\"", escq));
		
		//add quests
		if (qp.getFocusQuest() == null) {
			builder += ",";
			builder += generatePage("\nYou are not focused on any quest!");
		} else {
			for (HistoryEvent event : qp.getFocusQuest().getHistory().events())  {
				builder += ",";
				builder += generatePage(event.getDescription());
			}
		}
		
		builder += ",";
		
		//add player notes title
		title = new FancyMessage("\n  Player Notes\n\n\n")
				.color(ChatColor.BLACK)
			.then("  Notes left after this page will be kept")
				.color(ChatColor.BLACK);
		
		builder += generatePageJSON(title.toJSONString().replace("\"", escq));
		
		if (qp.getPlayerNotes() != null && qp.getPlayerNotes().isEmpty()) {
			for (String page : qp.getPlayerNotes()) {
				builder += ",";
				builder += generatePage(page);
			}
		}
		
		//bind
		builder += "], title:\"Journal\",author:" + play.getName() + ",ench:[{id:61s,lvl:5s}]}";

		System.out.println(builder);
		Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), builder);
		

		play.sendMessage(ChatColor.GRAY + "Your "
				+ ChatColor.DARK_GREEN + "Journal" + ChatColor.GRAY + " has been"
				+ " updated!" + ChatColor.RESET);
		play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(1, Tone.C));
		play.playNote(play.getLocation(), Instrument.PIANO, Note.natural(1, Tone.A));
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
