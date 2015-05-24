package nmt.minecraft.QuestManager;

import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.QuestManager.Quest.Quest;

public class QuestManager {
	
	private List<Quest> quests;
	
	
	/**
	 * Default constructor. Starts a blank manager
	 */
	public QuestManager() {
		quests = new LinkedList<Quest>();
	}
	
	
	/**
	 * Passes a stop signal to all quest managers, requesting a soft stop.<br />
	 * Soft stops typically save state and perform a padded stopping procedure,
	 * and are not guaranteed to stop all quests.
	 */
	public void stopQuests() {
		if (quests != null && !quests.isEmpty()) {
			for (Quest quest : quests) {
				quest.stop();
			}
		}
	}
	
	/**
	 * Immediately halts all running quests.
	 */
	public void haltQuests() {
		if (quests != null && !quests.isEmpty()) {
			for (Quest quest : quests) {
				quest.halt();
			}
		}
	}
	
}
