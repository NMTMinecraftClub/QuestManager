package com.SkyIsland.QuestManager.Quest.Requirements;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import com.SkyIsland.QuestManager.Player.Participant;
import com.SkyIsland.QuestManager.Quest.Goal;

/**
 * Represents a specific requirement needed to achieve a goal in a quest.<br />
 * Requirements are the gnitty-gritty details of how to achieve a goal. Examples inlcude:<br />
 * <ul>
 * <li>Kill a boss</li>
 * <li>Reach the village</li>
 * <li>etc</li>
 * </ul>
 * Requirements are <b>required</b> to perform their own event checking and are required to update
 * their containing goal when upon state change. In addition, requirements must 
 * @author Skyler
 *
 */
public abstract class Requirement {
	
	protected String desc;
	
	private Goal goal;
	
	protected boolean state;
	
	protected Participant participants;
	

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
		participants = goal.getQuest().getParticipants();
	}
	
	public abstract void fromConfig(ConfigurationSection config) throws InvalidConfigurationException;
	
	public void sync() {
		this.participants = goal.getQuest().getParticipants();
	}
	
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
	public abstract String getDescription();
	
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
	 * Sets this requirement to be active, listening for events and updating based on them
	 */
	public abstract void activate();
	
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
