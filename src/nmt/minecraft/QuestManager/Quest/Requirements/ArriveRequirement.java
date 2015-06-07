package nmt.minecraft.QuestManager.Quest.Requirements;

import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Goal;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Requirement that a participant must arrive at a location (or be within some radius of it)<br />
 * This requirement <b>does not require</b> that a participant <i>stay</i> at the location.
 * It only requires that someone get there at some point.
 * @author Skyler
 *
 */
public class ArriveRequirement extends Requirement implements Listener {
	
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
		
		if (state) {
			return;
		}
		
		for (QuestPlayer player : participants.getParticipants()) {
			if (player.getPlayer().getLocation().distance(destination) < targetRange) {
				state = true;
				updateQuest();
				
				//unregister listener, cause we'll never switch to unsatisfied
				HandlerList.unregisterAll(this);
				return;
			}
		}
		
		state = false;
		updateQuest();
	}
	
	
	
}
