package nmt.minecraft.QuestManager.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

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
	
	public QuestState() {
		this.name = "";
		this.goalState = new LinkedList<GoalState>();
	}
	
	@SuppressWarnings("unchecked")
	public void load(YamlConfiguration config) throws InvalidConfigurationException {
		
		if (!config.contains("saveTime") || !config.contains("name") || !config.contains("goals")) {
			throw new InvalidConfigurationException();
		}
		
		this.name = config.getString("name");
		
		this.goalState = (List<GoalState>) config.getList("goals");
		
	}
	
	public void save(File file) throws IOException {
		YamlConfiguration config = new YamlConfiguration();
		
		config.set("saveTime", (new Date()).getTime());
		
		config.set("name", name);
		
		config.set("goals", goalState);
		
		config.save(file);
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
