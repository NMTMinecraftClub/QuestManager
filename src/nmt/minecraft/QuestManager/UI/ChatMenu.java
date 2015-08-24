package nmt.minecraft.QuestManager.UI;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.UI.Menu.BioptionChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.Message.BioptionMessage;
import nmt.minecraft.QuestManager.UI.Menu.Message.Message;

import org.bukkit.entity.Player;

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
			handler = QuestManagerPlugin.questManagerPlugin.getChatGuiHandler();
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
	
	/**
	 * Defines the menu that goes with most standard message types.<br />
	 * If you do not register your own message types here, they will always default
	 * to simple message menus when using this command!
	 * <p>
	 * It's important to note that any menus created from this command are not allowed to be
	 * provided menu actions. For example, the {@link BioptionChatMenu} that would be instantiated
	 * from a {@link BioptionMessage} would have its menu options as null, meaning no action
	 * would be executed when the options were clicked.
	 * </p>
	 * @param message
	 * @return
	 */
	public static ChatMenu getDefaultMenu(Message message) {
		
		if (message instanceof BioptionMessage) {
			return new BioptionChatMenu((BioptionMessage) message, null, null);
		}

		
		
		
		
		
		//if message instanceof SimpleMessage (or DEFAULT)
		return new SimpleChatMenu(message.getFormattedMessage());
		
	}
	
}
