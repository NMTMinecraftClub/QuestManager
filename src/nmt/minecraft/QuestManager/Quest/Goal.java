package nmt.minecraft.QuestManager.Quest;

import java.util.LinkedList;
import java.util.List;

/**
 * Tracks objectives in a quest.<br />
 * Goals have specific requirements that must be met before they are considered clear.
 * @author Skyler
 *
 */
public abstract class Goal {
	
	private List<Requirement> requirements;
	
	private String name;
	
	private String description;
	
	private Quest quest;
	
	
	public Goal(Quest quest, String name, String description) {
		this.quest = quest;
		this.name = name;
		this.description = description;
		
		this.requirements = new LinkedList<Requirement>();
	}
	
	public Goal(Quest quest, String name) {
		this(quest, name, "");
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the quest
	 */
	public Quest getQuest() {
		return quest;
	}
	
	/**
	 * Adds a new requirement to this goal
	 * @param requirement
	 */
	public void addRequirement(Requirement requirement) {
		requirements.add(requirement);
	}
	
	/**
	 * Causes a reevaluation of 
	 */
	public void update() {
		
	}
	
	/**
	 * Assesses and reports whether the goal has been completed.<br />
	 * Please note that goals that have no requirements defaultly return true.
	 * @return
	 */
	public boolean isComplete() {
		if (requirements.isEmpty()) {
			return true;
		}
		
		for (Requirement req : requirements) {
			if (req.isCompleted() == false) {
				return false;
			}
		}
		
		return true;
	}
	
	
	
	
}
