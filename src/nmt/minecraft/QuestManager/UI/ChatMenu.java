package nmt.minecraft.QuestManager.UI;

import org.bukkit.entity.Player;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;

/**
 * A menu represented in chat buttons and links
 * @author Skyler
 *
 * register it with the handler, give it a unique ID and then do specific things based on argument
 * to command received!
 */
public abstract class ChatMenu {
	
	/*
	 * =========================
	 * How to load? How are we gonna load this stuff? If the FancyMessages really are
	 * serializable to config, I can just leave the format for loading/saving up to them. And
	 * just work with how to attach said messages to NPCs and make it do stuff.
	 * 
	 * For example, how from config do you specify that message 'a' starts you on a quest?
	 * It might be worth it to have quest templates specify a special starting NPC that is
	 * created when the template is loaded (and removed when unloaded) and give them a special
	 * menu (child of this, StartQuestMenu or something)
	 * 
	 * How to hook up messages for anything else is harder. Maybe first step is just get the
	 * starting-quest menus to work and don't worry about menus mid-quest for anything.
	 * 
	 */
	
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
	
	
	protected abstract boolean input(Player player, String arg);
	
	public FancyMessage getMessage() {
		return message;
	}
	
}
