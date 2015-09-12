package nmt.minecraft.QuestManager.UI.Menu.Action;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import de.inventivegames.util.tellraw.TellrawConverterLite;
import de.inventivegames.util.title.TitleManager;
import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.QuestConfiguration;
import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Quest;

public class QuestStartAction implements MenuAction {

	private QuestConfiguration template;
	
	private Player player;
	
	public QuestStartAction(QuestConfiguration questTemplate, Player player) {
		this.template = questTemplate;
		this.player = player;
	}
	
	@Override
	public void onAction() {
		
		//Instantiate the template
		Quest quest;
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
        Participant participant; 
        
		if (template.getUseParty() && qp.getParty() != null) {
        	participant = qp.getParty();
        } else {
			participant = qp;
        }
		
		try {
			quest = template.instanceQuest(participant);
		} catch (InvalidConfigurationException e) {
			QuestManagerPlugin.questManagerPlugin.getLogger().warning(
					"Could not instance quest for player " + player.getName());
			player.sendMessage("An error occured. Please notify your administrator with what you " +
					"did to get this message, and the following message:\n Invalid Quest Template!");
			return;
		}

		
		TitleManager.sendTimings(player, 20, 40, 20);

        TitleManager.sendSubTitle(player, TellrawConverterLite.convertToJSON(
        		ChatColor.GOLD + template.getDescription()));

        TitleManager.sendTitle(player, TellrawConverterLite.convertToJSON(
        		ChatColor.DARK_RED + template.getName()));
        
		QuestManagerPlugin.questManagerPlugin.getManager().registerQuest(quest);
		
	}

}
