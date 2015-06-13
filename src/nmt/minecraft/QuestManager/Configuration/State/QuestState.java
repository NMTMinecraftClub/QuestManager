package nmt.minecraft.QuestManager.Configuration.State;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import nmt.minecraft.QuestManager.Player.Participant;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Wrapper for state info config
 * @author Skyler
 *
 */
public class QuestState {
	
	private String name;
	
	private List<GoalState> goalState;
	
	private Participant participant;
	
	public QuestState() {
		this.name = "";
		this.goalState = new LinkedList<GoalState>();
	}


	public void load(YamlConfiguration config) throws InvalidConfigurationException {
		
		if (!config.contains("saveTime") || !config.contains("participant") || !config.contains("name") || !config.contains("goals")) {
			throw new InvalidConfigurationException("Some keys were missing in a quest state!"
					+ (config.contains("name") ? config.getString("name") : ""));
		}
		
		this.name = config.getString("name");
		
		this.goalState = new LinkedList<GoalState>();
		
		for (String goalKey : config.getConfigurationSection("goals").getKeys(false)) {
			GoalState gs = new GoalState();
			gs.load(config.getConfigurationSection("goals").getConfigurationSection(goalKey));
			goalState.add(gs);
		}
		
		this.participant = (Participant) config.get("participant");
		
	}
	
	public void save(File file) throws IOException {
		YamlConfiguration config = new YamlConfiguration();
		
		config.set("saveTime", (new Date()).getTime());
		
		config.set("name", name);
		
		int i = 1;
		for (GoalState conf : goalState) {
			config.set("goals." + i, conf.asConfig());
		}
		
		//config.set("goals", goalList);
		
		config.save(file);
	}
	
	public Participant getParticipant() {
		return this.participant;
	}
	
	public void setParticipant(Participant participant) {
		this.participant = participant;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the goalState
	 */
	public List<GoalState> getGoalState() {
		return goalState;
	}
	
	public void addGoalState(GoalState goalState) {
		this.goalState.add(goalState);
	}
	
	
}
