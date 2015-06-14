package nmt.minecraft.QuestManager.UI.Menu;

import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.MenuAction;
import nmt.minecraft.QuestManager.UI.Menu.Message.BioptionMessage;

import org.bukkit.entity.Player;

public class BioptionChatMenu extends ChatMenu {
	
	private MenuAction opt1;
	
	private MenuAction opt2;
	
	public BioptionChatMenu(BioptionMessage msg, MenuAction opt1, MenuAction opt2) {
		super(msg.getFormattedMessage());
		this.opt1 = opt1;
		this.opt2 = opt2;
	}
	
	private BioptionChatMenu(FancyMessage msg) {
		super(msg);
	}

	@Override
	protected boolean input(Player player, String arg) {

		//do different things based on our argument. We are only bioption, so we only have
		//two things to do. 
		if (arg.equals(BioptionMessage.OPTION1)) {
			opt1.onAction();
			return true;
		} else if (arg.equals(BioptionMessage.OPTION2)) {
			opt2.onAction();
			return true;
		} else {
			player.sendMessage("Something went wrong! [Invalid Biopt Argument!]");
			return false;
		}
		
	}

}
