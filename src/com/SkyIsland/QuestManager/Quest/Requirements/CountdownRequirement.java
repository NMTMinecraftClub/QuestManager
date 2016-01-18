package com.SkyIsland.QuestManager.Quest.Requirements;

import java.util.Calendar;
import java.util.Date;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import com.SkyIsland.QuestManager.Configuration.State.RequirementState;
import com.SkyIsland.QuestManager.Configuration.State.StatekeepingRequirement;
import com.SkyIsland.QuestManager.Player.Participant;
import com.SkyIsland.QuestManager.Quest.Goal;
import com.SkyIsland.QuestManager.Quest.Requirements.Factory.RequirementFactory;
import com.SkyIsland.QuestManager.Scheduling.IntervalScheduler;
import com.SkyIsland.QuestManager.Scheduling.Tickable;

/**
 * Requires a certain amount of time to pass before satisfied
 * @author Skyler
 *
 */
public class CountdownRequirement extends Requirement implements Tickable, StatekeepingRequirement {
	
	public static class CountdownFactory extends RequirementFactory<CountdownRequirement> {
		
		public CountdownRequirement fromConfig(Goal goal, ConfigurationSection config) {
			CountdownRequirement req = new CountdownRequirement(goal);
			req.participants = goal.getQuest().getParticipants();
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
	}
	
	private Date targetTime;
	
	private int delay;
	
	private CountdownRequirement(Goal goal) {
		super(goal);
	}
	
	public CountdownRequirement(Participant participants, Goal goal, String description, Date targetTime) {
		super(goal, description);
		state = false;
		this.targetTime = targetTime;
		this.participants = participants;
		
	}
	
	@Override
	public void activate() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTime(new Date());
		cal.add(Calendar.SECOND, delay);
		this.targetTime = cal.getTime();
		IntervalScheduler.getScheduler().register(this);
	}
	
	/**
	 * @return the targetTime
	 */
	public Date getTargetTime() {
		return targetTime;
	}

	/**
	 * @param targetTime the targetTime to set
	 */
	public void setTargetTime(Date targetTime) {
		this.targetTime = targetTime;
	}

	/**
	 * Checks all involved {@link com.SkyIsland.QuestManager.Player.Participant Participant(s)}
	 * to check if the required item & quantity requirements are satisfied.<br />
	 * <b>Note:</b> This does not check if the above quantity-requirement is met <i>across</i>
	 * all members, but instead of any single member has the required number of items.<br />
	 * TODO fix the above noted problem
	 */
	@Override
	protected void update() {
		sync();
		
		if (state) {
			return;
		}
		
		Date current = new Date();
		
		if (current.after(targetTime)) {
			state = true;
			updateQuest();
		}
				
	}

	@Override
	public void fromConfig(ConfigurationSection config) throws InvalidConfigurationException {
		//we'll need start and end times
		//our config is 
		//  type: "countdownr"
		//  delay: <long> second offset
		
		if (!config.contains("type") || !config.getString("type").equals("countdownr")) {
			throw new InvalidConfigurationException("\n  ---Invalid type! Expected 'countdownr' but got " + config.getString("type", "null"));
		}

		desc = config.getString("description", "Wait for a period");
		delay = config.getInt("delay", 10);
		
	}

	@Override
	public void tick() {
		update();
	}

	@Override
	public RequirementState getState() {
		YamlConfiguration config = new YamlConfiguration();
		
		config.set("targetTime", targetTime.getTime());
		
		RequirementState data = new RequirementState(config);
		
		return data;
	}

	@Override
	public void loadState(RequirementState state)
			throws InvalidConfigurationException {
		ConfigurationSection config = state.getConfig();
		
		if (!config.contains("targetTime")) {
			throw new InvalidConfigurationException();
		}
		

		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(new Date(config.getLong("targetTime")));
		targetTime = calendar.getTime();
	}

	@Override
	public void stop() {
		;
	}
	
	@Override
	public String getDescription() {
		Date date = new Date();
		String ret = this.desc;
		if (!state) {
			ret += " (";
			long left = targetTime.getTime() - date.getTime();
			boolean mins = true;
			long value = left / 60000; //get minutes
			if (value == 0) {
				//less than a minute remaining
				value = left / 1000; //get seconds
				mins = false;
			}
			
			ret += value + " ";
			if (mins) {
				ret += "m";
			} else {
				ret += "s";
			}
			ret += ")";
		}
		
		return ret;
	}
	
	
}
