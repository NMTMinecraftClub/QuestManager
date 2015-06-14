package nmt.minecraft.QuestManager.UI;

import org.bukkit.entity.Player;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import mkremins.fanciful.FancyMessage;

/**
 * A menu represented in chat buttons and links
 * @author Skyler
 *
 * register it with the handler, give it a unique ID and then do specific things based on argument
 * to command received!
 */
public class ChatMenu {
	
	public static ChatGuiHandler handler;
	
	private FancyMessage message;
	
	/**
	 * Constructs a menu around the provided FancyMessage.
	 * @param msg 
	 */
	public ChatMenu(FancyMessage msg) {
		this.message = msg;
		
		if (ChatMenu.handler == null) {
			handler = QuestManagerPlugin.questManagerPlugin.getGuiHandler();
		}
	}
	
	/**
	 * Shows this menu to the provided player.
	 * @param player
	 */
	public void show(Player player) {
		handler.showMenu(player, this);
	}
	
	
	protected boolean input(Player player, String arg) {
		return false; //TODO
	}
	
	public FancyMessage getMessage() {
		return null; //TODO
	}
	
}
