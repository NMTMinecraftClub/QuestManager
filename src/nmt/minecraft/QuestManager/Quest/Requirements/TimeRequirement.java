package nmt.minecraft.QuestManager.Quest.Requirements;

import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Requirements.Factory.RequirementFactory;
import nmt.minecraft.QuestManager.Scheduling.IntervalScheduler;
import nmt.minecraft.QuestManager.Scheduling.Tickable;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

/**
 * Requirement that a specific time range be current. This is not a stateful requirement, and can be used
 * to great effect with other requirements (kill 10 things at night, etc)
 * @author Skyler
 *
 */
public class TimeRequirement extends Requirement implements Tickable {
	
	public static class TimeFactory extends RequirementFactory<TimeRequirement> {
		
		public TimeRequirement fromConfig(Goal goal, ConfigurationSection config) {
			TimeRequirement req = new TimeRequirement(goal);
			req.participants = goal.getQuest().getParticipants();
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
	}
	
	private long startTime;
	
	private long endTime;
	
	private TimeRequirement(Goal goal) {
		super(goal);
		IntervalScheduler.getScheduler().register(this);
	}
	
	public TimeRequirement(Participant participants, Goal goal, String description, long start, long end) {
		super(goal, description);
		state = false;
		this.startTime = start;
		this.endTime = end;
		this.participants = participants;
		IntervalScheduler.getScheduler().register(this);
		
	}
	
	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	/**
	 * Checks all involved {@link nmt.minecraft.QuestManager.Player.Participant Participant(s)}
	 * to check if the required item & quantity requirements are satisfied.<br />
	 * <b>Note:</b> This does not check if the above quantity-requirement is met <i>across</i>
	 * all members, but instead of any single member has the required number of items.<br />
	 * TODO fix the above noted problem
	 */
	@Override
	protected void update() {
		sync();
				
		for (QuestPlayer player : participants.getParticipants()) {
			if (player.getPlayer().isOnline()) {
				Player p = player.getPlayer().getPlayer();
				long time = p.getWorld().getTime();
				if (time >= startTime && time <= endTime) {
					if (state) {
						return;
					}
					state = true;
					updateQuest();
				} else {
					if (!state) {
						return;
					} 
					state = false;
					updateQuest();
				}
			}
		}
				
	}

	@Override
	public void fromConfig(ConfigurationSection config) throws InvalidConfigurationException {
		//we'll need start and end times
		//our config is 
		//  type: "timer"
		//  startTime: <long>
		//  endTime: <long>
		
		if (!config.contains("type") || !config.getString("type").equals("timer")) {
			throw new InvalidConfigurationException("\n  ---Invalid type! Expected 'timer' but got " + config.getString("type", "null"));
		}
		
		this.startTime = config.getLong("startTime");
		this.endTime = config.getLong("endTime");
		
	}

	@Override
	public void tick() {
		update();
	}
	
	
	
}
