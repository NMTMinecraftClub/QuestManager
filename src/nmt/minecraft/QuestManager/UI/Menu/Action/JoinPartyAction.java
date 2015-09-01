package nmt.minecraft.QuestManager.UI.Menu.Action;

import nmt.minecraft.QuestManager.Player.Party;
import nmt.minecraft.QuestManager.Player.QuestPlayer;

/**
 * Adds a player to another player's party, creating it if it doesn't exist
 * @author Skyler
 *
 */
public class JoinPartyAction implements MenuAction {
	
	private QuestPlayer leader;
	
	private QuestPlayer other;
	
	public JoinPartyAction(QuestPlayer leader, QuestPlayer other) {
		this.leader = leader;
		this.other = other;
	}
	
	@Override
	public void onAction() {
		// TODO Auto-generated method stub
		if (leader.getParty() == null) {
			Party party = leader.createParty();
			other.joinParty(party);
		} else {
			other.joinParty(leader.getParty());
		}
	}

}
