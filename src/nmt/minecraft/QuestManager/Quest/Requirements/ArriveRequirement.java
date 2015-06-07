package nmt.minecraft.QuestManager.Quest.Requirements;

import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Goal;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class ArriveRequirement extends Requirement {
	
	/**
	 * Who's involved
	 */
	private Participant participants;
	
	/**
	 * Where they need to go
	 */
	private Location destination;
	
	/**
	 * How close they can be to the destination to call it good (in blocks)
	 */
	private double targetRange;
	
	public ArriveRequirement(Goal goal, Participant participants, Location location, double range) {
		this(goal, "", participants, location, range);
	}
	
	public ArriveRequirement(Goal goal, String description, Participant participants, Location location, double range) {
		super(goal, description);
		
		this.participants = participants;
		this.destination = location;
		this.targetRange = range;
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
	}
	
	/**
	 * Checks if any of the involved participants is within range of the location.
	 */
	@Override
	public void update() {
		
		for (QuestPlayer player : participants.getParticipants()) {
			if (player.getPlayer().getLocation().distance(destination) < targetRange) {
				state = true;
				updateQuest();
				return;
			}
		}
		
		state = false;
		updateQuest();
	}
	
	
	
}
