package nmt.minecraft.QuestManager.UI.Menu;

import org.bukkit.entity.Player;

import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.UI.ChatMenu;

/**
 * A basic text-only menu.
 * @author Skyler
 *
 */
public class SimpleChatMenu extends ChatMenu {

	public SimpleChatMenu(FancyMessage msg) {
		super(msg);
	}

	@Override
	protected boolean input(Player player, String arg) {
		return true; //do nothing. Just a text menu
	}

}
