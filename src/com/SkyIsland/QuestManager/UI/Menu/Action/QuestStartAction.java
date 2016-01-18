package com.SkyIsland.QuestManager.UI.Menu.Action;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import com.SkyIsland.QuestManager.QuestManagerPlugin;
import com.SkyIsland.QuestManager.Configuration.QuestConfiguration;
import com.SkyIsland.QuestManager.Configuration.SessionConflictException;
import com.SkyIsland.QuestManager.Fanciful.FancyMessage;
import com.SkyIsland.QuestManager.Player.Participant;
import com.SkyIsland.QuestManager.Player.QuestPlayer;
import com.SkyIsland.QuestManager.Quest.Quest;
import com.SkyIsland.QuestManager.Quest.History.HistoryEvent;

import de.inventivegames.util.tellraw.TellrawConverterLite;
import de.inventivegames.util.title.TitleManager;

public class QuestStartAction implements MenuAction {

	private QuestConfiguration template;
	
	private FancyMessage startingMessage;
	
	private FancyMessage acceptMessage;
	
	private Player player;
	
	private static final String partyDenial = ChatColor.YELLOW + "This quest requires a party..." + ChatColor.RESET;
	
	private static final String sessionDenial = ChatColor.YELLOW + "A session of this quest is already going! Please wait until it's finished." + ChatColor.RESET;
	
	public QuestStartAction(QuestConfiguration questTemplate, FancyMessage start, FancyMessage accept, Player player) {
		this.template = questTemplate;
		this.player = player;
		this.startingMessage = start;
		this.acceptMessage = accept;
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
		} catch (SessionConflictException e) {
			player.sendMessage(sessionDenial);
			return;
		}

		quest.addHistoryEvent(new HistoryEvent(startingMessage.toOldMessageFormat()
				.replaceAll(ChatColor.WHITE + "", ChatColor.BLACK + "")));
		quest.addHistoryEvent(new HistoryEvent(acceptMessage.toOldMessageFormat()
				.replaceAll(ChatColor.WHITE + "", ChatColor.BLACK + "")));
		

        
		QuestManagerPlugin.questManagerPlugin.getManager().registerQuest(quest);
		
		for (QuestPlayer qpe : participant.getParticipants()) {
			qpe.updateQuestBook(false);
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
