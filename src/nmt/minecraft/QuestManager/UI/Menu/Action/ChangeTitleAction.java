package nmt.minecraft.QuestManager.UI.Menu.Action;

import nmt.minecraft.QuestManager.Player.QuestPlayer;

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
	}

}
