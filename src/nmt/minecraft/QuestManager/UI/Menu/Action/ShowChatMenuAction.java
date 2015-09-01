package nmt.minecraft.QuestManager.UI.Menu.Action;

import nmt.minecraft.QuestManager.UI.ChatMenu;

import org.bukkit.entity.Player;

/**
 * Actions that causes a chat menu to be shown to a player
 * @author Skyler
 *
 */
public class ShowChatMenuAction implements MenuAction {

	private Player player;
	
	private ChatMenu menu;
	
	public ShowChatMenuAction(ChatMenu menu, Player player) {
		this.player = player;
		this.menu = menu;
	}
	
	@Override
	public void onAction() {
		menu.show(player);
	}

}
