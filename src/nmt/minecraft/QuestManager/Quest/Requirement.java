package nmt.minecraft.QuestManager.Quest;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Represesnts a specific requirement needed to acheive a goal in a quest.<br />
 * Requirements are the gnitty-gritty details of how to achieve a goal. Examples inlcude:<br />
 * <ul>
 * <li>Kill a boss</li>
 * <li>Reach the village</li>
 * <li>etc</li>
 * </ul>
 * Requirements are required to perform their own event checking and are required to update
 * their containing goal when upon state change.
 * @author Skyler
 *
 */
public abstract class Requirement {
	
	private String desc;
	
	private Goal goal;
	
	protected boolean state;
	

	/**
	 * Creates a requirement with an empty string for a description
	 * @param goal
	 */
	public Requirement(Goal goal) {
		this(goal, "");
	}
	
	/**
	 * Creates the parameterized requirement
	 * @param goal
	 * @param description
	 */
	public Requirement(Goal goal, String description) {
		this.goal = goal;
		this.desc = description;
	}
	
	public abstract void fromConfig(YamlConfiguration config);
	
	/**
	 * Returns the goal this requirement belongs to
	 * @return
	 */
	public Goal getGoal() {
		return goal;
	}
	
	/**
	 * Returns the description of this requirement
	 * @return
	 */
	public String getDescription() {
		return desc;
	}
	
	/**
	 * Returns whether or not the current requirement is completed.<br />
	 * Requirements that may change back and forth may return false even after a call
	 * to this method had previously returned true. As a result, this method should always 
	 * be called each time a parent goal is checking its own completion status.<br />
	 * This method makes an internal call to update state information to make sure that the
	 * value returned is current.
	 * @return
	 */
	public boolean isCompleted() {
		update();
		return state;
	}
	
	/**
	 * Notifies the parent goal of a status chain, usually causing a re-evaluation of criteria
	 * to update the goal's status
	 */
	protected void updateQuest() {
		RequirementUpdateEvent e = new RequirementUpdateEvent(this);
		Bukkit.getPluginManager().callEvent(e);
	}
	
	/**
	 * Perform a check against requirement criteria to update state information with correct
	 * value.
	 */
	protected abstract void update();
	
}
