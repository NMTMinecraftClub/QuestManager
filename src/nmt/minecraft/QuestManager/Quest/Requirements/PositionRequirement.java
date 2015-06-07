package nmt.minecraft.QuestManager.Quest.Requirements;

import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Requirement;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Requirement that a participant must be at the provided location.<br />
 * Unlike the {@link ArriveRequirement} this requirement is that someone be located there
 * for this to be completed. That means if they leave, the requirement will no longer
 * be satisfied!
 * @author Skyler
 * @see {@link ArriveRequirement}
 */
public class PositionRequirement extends Requirement implements Listener {
	
	/**
	 * Who must satisfy the requirement?
	 */
	private Participant participants;
	
	/**
	 * Where the participant must be
	 */
	private Location destination;
	
	/**
	 * How close they can be to the destination to count as satisfied
	 */
	private double targetRange;
	
	public PositionRequirement(Goal goal, Participant participants, Location destination, double range) {
		this(goal, "", participants, destination, range);
	}
	
	public PositionRequirement(Goal goal, String description, Participant participants, Location destination, double range) {
		super(goal, description);
		this.participants = participants;
		this.destination = destination;
		this.targetRange = range;
		this.state = false;
	}

	/**
	 * @return the participants
	 */
	public Participant getParticipants() {
		return participants;
	}

	/**
	 * @return the destination
	 */
	public Location getDestination() {
		return destination;
	}

	/**
	 * @return the targetRange
	 */
	public double getTargetRange() {
		return targetRange;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		update();
		updateQuest();
	}
	
	/**
	 * Checks whether at least one participant is in the required area
	 */
	@Override
	public void update() {
		
		for (QuestPlayer player : participants.getParticipants()) {
			if (player.getPlayer().getLocation().distance(destination) <= targetRange) {
				state = true;
				return;
			}
		}
		
		state = false;
	}
}
