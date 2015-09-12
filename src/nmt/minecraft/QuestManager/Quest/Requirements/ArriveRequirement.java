package nmt.minecraft.QuestManager.Quest.Requirements;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.Utils.LocationState;
import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Requirements.Factory.RequirementFactory;

/**
 * Requirement that a participant must arrive at a location (or be within some radius of it)<br />
 * This requirement <b>does not require</b> that a participant <i>stay</i> at the location.
 * It only requires that someone get there at some point.
 * @author Skyler
 * @see {@link PositionRequirement}
 */
public class ArriveRequirement extends Requirement implements Listener {
	
	public static class ArriveFactory extends RequirementFactory<ArriveRequirement> {

		@Override
		public ArriveRequirement fromConfig(Goal goal, ConfigurationSection config) {
			ArriveRequirement req = new ArriveRequirement(goal);
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
		
	}
		
	
	/**
	 * Where they need to go
	 */
	private Location destination;
	
	/**
	 * How close they can be to the destination to call it good (in blocks)
	 */
	private double targetRange;
	
	/**
	 * Super secret private constructor for factory call convenience
	 * @param goal
	 */
	private ArriveRequirement(Goal goal) {
		super(goal);	
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
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
		if (participants == null) {
			return;
		}
		
		for (QuestPlayer qp : participants.getParticipants()) {
			if (qp.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {	
				update();
				return;
			}
		}
	}
	
	/**
	 * Checks if any of the involved participants is within range of the location.
	 */
	@Override
	public void update() {
		
		if (state) {
			return;
		}
		sync();
		for (QuestPlayer player : participants.getParticipants()) {
			if (player.getPlayer().isOnline())
			if (player.getPlayer().getPlayer().getLocation().getWorld().getName().equals(destination.getWorld().getName()))
			if ((player.getPlayer().getPlayer()).getLocation().distance(destination) <= targetRange) {
				state = true;
				updateQuest();
				
				//unregister listener, cause we'll never switch to unsatisfied
				HandlerList.unregisterAll(this);
				return;
			}
		}
		
		state = false;
	}

	@Override
	public void fromConfig(ConfigurationSection config)
			throws InvalidConfigurationException {
		// same of Position Requirements's loading
		//  type: "arrr"
		//  range: [double]
		//  destination: [location]
			
		if (!config.contains("type") || !config.getString("type").equals("arrr")) {
			throw new InvalidConfigurationException();
		}
			
		this.targetRange = config.getDouble("range", 1.0);
		this.destination = ((LocationState) config.get("destination")).getLocation();
		
	}
	
	
	
}
