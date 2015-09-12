package nmt.minecraft.QuestManager.UI.Menu.Action;

import org.bukkit.ChatColor;

import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;

public class ChangeTitleAction implements MenuAction {

	private String newTitle;
	
	private QuestPlayer player;
	
	public ChangeTitleAction(QuestPlayer player, String title) {
		this.newTitle = title;
		this.player = player;
	}
	
	@Override
	public void onAction() {
		player.setTitle(newTitle);
		
		if (player.getPlayer().isOnline())
		new SimpleChatMenu(
				new FancyMessage("You changed your title to ")
					.color(ChatColor.GRAY)
				.then(newTitle)).show(player.getPlayer().getPlayer());
	}

}
