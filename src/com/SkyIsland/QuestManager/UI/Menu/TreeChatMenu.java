package com.SkyIsland.QuestManager.UI.Menu;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.SkyIsland.QuestManager.Fanciful.FancyMessage;
import com.SkyIsland.QuestManager.UI.ChatGuiHandler;
import com.SkyIsland.QuestManager.UI.ChatMenu;
import com.SkyIsland.QuestManager.UI.Menu.Message.Message;
import com.SkyIsland.QuestManager.UI.Menu.Message.TreeMessage;
import com.SkyIsland.QuestManager.UI.Menu.Message.TreeMessage.Option;

/**
 * Menu with multiple options that can lead to other menus.
 * @author Skyler
 *
 */
public class TreeChatMenu extends ChatMenu implements RespondableMenu {
	
	/**
	 * A map between unique menu keys and options
	 */
	private Map<String, Option> options;
	
	private int keyindex;
	
	/**
	 * Creates (but does not show!) a menu with zero or more options. Each option is associated with<br />
	 * The provided MenuActions allow for more control over the action of the menu buttons. If
	 * there is no desired action for a corresponding action, <i>null</i> should be passed.
	 * @param msg The fully-encoded message used for menu text
	 * @param opt1 Action enacted when option 1 is clicked by the user
	 * @param opt2 Action enacted when option 2 is clicked by the user
	 */
	public TreeChatMenu(Message body, Option option) {
		super(body.getFormattedMessage());
		
		options = new TreeMap<String, Option>();
		addOption(option);
		
		keyindex = 1;
		
		this.setMessage(formatMessage(body));
	}
	
	public TreeChatMenu(TreeMessage body) {
		this(body, body.getOptions());
	}
	
	public TreeChatMenu(Message body, Option ... options) {
		super(body.getFormattedMessage());

		
		this.options = new TreeMap<String, Option>();
		
		keyindex = 1;
		
		for (Option opt : options) {
			addOption(opt);
		}
		
		this.setMessage(formatMessage(body));
	}
	
	public TreeChatMenu(Message body, Collection<Option> options) {
		super(body.getFormattedMessage());

		
		this.options = new TreeMap<String, Option>();
		
		keyindex = 1;
		
		for (Option opt : options) {
			addOption(opt);
		}
		
		this.setMessage(formatMessage(body));
	}
	
	/**
	 * Adds the given option to the list of options used in the menu.
	 * @param option
	 */
	private void addOption(Option option) {
		this.options.put(genKey(), option);
	}

	@Override
	protected boolean input(Player player, String arg) {
		
		if (options.isEmpty()) {
			return false;
		}
		
		for (String key : options.keySet()) {
			if (key.equals(arg)) {
				Option opt = options.get(key);
				Message msg = opt.getResult();
				ChatMenu.getDefaultMenu(msg).show(player);
				return true;
			}
		}
		
		
		player.sendMessage("Something went wrong! [Invalid Mopt Argument!]");
		return false;
		
	}
	
	/**
	 * Uses the internal key index to generate the next key for registration
	 * @return
	 */
	private String genKey() {
		String key = "M" + keyindex;
		keyindex++;
		
		return key;
		//return "M" + keyindex++;
	}

	private FancyMessage formatMessage(Message rawBody) {
		FancyMessage msg = new FancyMessage("--------------------------------------------\n")
					.style(ChatColor.BOLD)
				.then(rawBody.getFormattedMessage())
				.then("\n\n");
				
		if (!options.isEmpty())
		for (String key : options.keySet()) {
			Option opt = options.get(key);
			msg.then("    ")
			.then(opt.getLabel()).command(ChatGuiHandler.cmdBase + " " + key)
				.color(ChatColor.DARK_GREEN)
				.style(ChatColor.ITALIC);
		}
		
		msg.then("--------------------------------------------\n")
		.style(ChatColor.BOLD);
		
		return msg;
	}
	
//	@Override
//	public FancyMessage getMessage(){
//		//return formatMessage(t);
//	}
}
