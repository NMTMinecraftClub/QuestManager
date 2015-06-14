package nmt.minecraft.QuestManager.UI;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import mkremins.fanciful.FancyMessage;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Organizes, catches, and dispatches chat click events to the responsible menus
 * @author Skyler
 *
 */
public class ChatGuiHandler implements CommandExecutor {
	
	public final static CharSequence idReplace = "=ID=";
	
	public final static CharSequence cmdBase = "=CMD=";
		
	public static enum Commands {
		MENU("qmchatmenu");
		
		private String command;
		
		private Commands(String command) {
			this.command = command;
		}
		
		public String getCommand() {
			return command;
		}
	}
	
	/**
	 * Internal record class for bringing together all required information about a menu.
	 * @author Skyler
	 *
	 */
	private static class MenuRecord {
		
		private ChatMenu menu;
		
		private boolean ticked;
		
		private int key;
		
		public MenuRecord(ChatMenu menu, int key) {
			this.menu = menu;
			this.key = key;
			
			this.ticked = false;
		}

		/**
		 * @return the menu
		 */
		public ChatMenu getMenu() {
			return menu;
		}

		/**
		 * @return the ticked
		 */
		public boolean isTicked() {
			return ticked;
		}

		/**
		 * @param ticked the ticked to set
		 */
		public void tick() {
			this.ticked = true;
		}

		/**
		 * @return the key
		 */
		public int getKey() {
			return key;
		}
		
		
	}
	
	private Map<UUID, MenuRecord> menus;
	
	private static Random rand;
	
	public ChatGuiHandler(JavaPlugin plugin) {
		for (Commands command : Commands.values()) {
			plugin.getCommand(command.getCommand()).setExecutor(this);
		}
		
		menus = new HashMap<UUID, MenuRecord>();
		if (ChatGuiHandler.rand == null) {
			ChatGuiHandler.rand = new Random();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase(Commands.MENU.getCommand())) {
			if (args.length != 2) {
				sender.sendMessage("Something went wrong! [Invalid Option Length!]");
				return true;
			}
			return menuCommand(sender, args);
		}
		
		
		return false;
		
	}
	
	/**
	 * Executes the menu command with the given parameters
	 * @param sender Who send the command
	 * @param args All passes arguments
	 * @return 
	 */
	private boolean menuCommand(CommandSender sender, String[] args) {
		
		if (!(sender instanceof Player)) {
			sender.sendMessage("Only players can execute this command!");
			return true;
		}
		
		int menuID = Integer.parseInt(args[0]);
		String arg = args[1];
		
		Player player = (Player) sender;
		
		if (!menus.containsKey(player.getUniqueId())) {
			sender.sendMessage("This menu has expired!");
			return true;
		}
		
		MenuRecord record = menus.get(player.getUniqueId());
		
		if (record.getKey() != menuID) {
			sender.sendMessage("This menu has expired!");
			return true;
		}
		
		ChatMenu menu = record.getMenu();
		menus.remove(player.getUniqueId());
		
		return menu.input(player, arg);
		
	}
	
	
	
	/**
	 * Registers the menu 
	 * @param menu
	 */
	public void showMenu(Player player, ChatMenu menu) {
		if (player == null || menu == null) {
			return;
		}
		
		if (!player.isOnline()) {
			return;
		}
		
		int id = rand.nextInt();
		
		menus.put(player.getUniqueId(), 
				new MenuRecord(menu, id));
		
		FancyMessage preformat = menu.getMessage();
		String raw = preformat.toJSONString();
		raw.replace(cmdBase, Commands.MENU.getCommand() + " " + id + " ");
		raw.replace(idReplace, "" + id);
		FancyMessage postformat = FancyMessage.deserialize(raw);
		
		postformat.send(player);
		
	}
	
	/**
	 * Performs internal timer check and update on menu entries.<br />
	 * Calling this method in a manner inconsistent with a timeout timer results in 
	 * undefined behavior, and is not recommended.
	 */
	protected void tick() {
		
		if (menus.isEmpty()) {
			return;
		}
		
		Iterator<Entry<UUID, MenuRecord>> it = menus.entrySet().iterator();
		while (it.hasNext()) {
			Entry<UUID, MenuRecord> entry = it.next();
			
			
			//check if they've already been ticked
			if (entry.getValue().isTicked()) {
				menus.remove(entry.getKey());
			} else {
				entry.getValue().tick();
			}
		}
	}
	
	
}
