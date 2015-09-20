package nmt.minecraft.QuestManager.UI.Menu.Action;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import de.inventivegames.util.tellraw.TellrawConverterLite;
import de.inventivegames.util.title.TitleManager;
import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.QuestConfiguration;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Quest;
import nmt.minecraft.QuestManager.Quest.History.HistoryEvent;

public class QuestStartAction implements MenuAction {

	private QuestConfiguration template;
	
	private FancyMessage startingMessage;
	
	private Player player;
	
	private static final String partyDenial = ChatColor.YELLOW + "This quest requires a party..." + ChatColor.RESET;
	
	public QuestStartAction(QuestConfiguration questTemplate, FancyMessage start, Player player) {
		this.template = questTemplate;
		this.player = player;
		this.startingMessage = start;
	}
	
	@Override
	public void onAction() {
		
		//Instantiate the template
		Quest quest;
		QuestPlayer qp = QuestManagerPlugin.questManagerPlugin.getPlayerManager().getPlayer(player);
		
		//check to make sure this doesn't require a party
		if (template.getRequireParty())
			if (qp.getParty() == null) {
				//TODO make prettier
				player.sendMessage(QuestStartAction.partyDenial);
				return;
		}
		
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

		quest.getHistory().addHistoryEvent(new HistoryEvent(startingMessage.toOldMessageFormat()
				.replaceAll(ChatColor.WHITE + "", ChatColor.BLACK + "")));
		

        
		QuestManagerPlugin.questManagerPlugin.getManager().registerQuest(quest);
		
		for (QuestPlayer qpe : participant.getParticipants()) {
			qpe.updateQuestBook();
			if (qpe.getPlayer().isOnline()) {
				TitleManager.sendTimings(qpe.getPlayer().getPlayer(), 30, 80, 30);

		        TitleManager.sendSubTitle(qpe.getPlayer().getPlayer(), TellrawConverterLite.convertToJSON(
		        		ChatColor.GOLD + template.getDescription()));

		        TitleManager.sendTitle(qpe.getPlayer().getPlayer(), TellrawConverterLite.convertToJSON(
		        		ChatColor.DARK_RED + template.getName()));
			}
		}
		
	}

}
