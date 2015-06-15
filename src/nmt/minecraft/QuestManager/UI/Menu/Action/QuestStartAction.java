package nmt.minecraft.QuestManager.UI.Menu.Action;

import nmt.minecraft.QuestManager.QuestManager;
import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.QuestConfiguration;
import nmt.minecraft.QuestManager.Quest.Quest;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

public class QuestStartAction implements MenuAction {

	private QuestConfiguration template;
	
	private QuestManager manager;
	
	private Player player;
	
	public QuestStartAction(QuestConfiguration questTemplate, QuestManager manager, Player player) {
		this.template = questTemplate;
		this.manager = manager;
		this.player = player;
	}
	
	@Override
	public void onAction() {
		
		//Instantiate the template
		Quest quest;
		try {
			quest = template.instanceQuest(manager);
		} catch (InvalidConfigurationException e) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning(
					"Could not instance quest for player " + player.getName());
			player.sendMessage("An error occured. Please notify your administrator with what you " +
					"did to get this message, and the following message:\n Invalid Quest Template!");
			return;
		}
		
		quest.
		
	}

}
