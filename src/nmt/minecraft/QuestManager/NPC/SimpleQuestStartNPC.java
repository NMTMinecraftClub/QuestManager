package nmt.minecraft.QuestManager.NPC;

import org.bukkit.entity.Player;

import nmt.minecraft.QuestManager.Configuration.QuestConfiguration;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.BioptionChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.Action.QuestStartAction;

/**
 * NPC that starts a quest :D<br />
 * This simple starting version mounts atop a {@link SimpleBioptionNPC}, and has all the capability
 * and limits defined therein.
 * @author Skyler
 *
 */
public class SimpleQuestStartNPC extends SimpleBioptionNPC {
	
	private QuestConfiguration quest;
	
	public void setQuestTemplate(QuestConfiguration questTemplate) {
		this.quest = questTemplate;
	}
	
	@Override
	protected void interact(Player player) {
		ChatMenu messageChat = new BioptionChatMenu(chat, 
				new QuestStartAction(quest, player), null);
		messageChat.show(player);
	}
	
}
