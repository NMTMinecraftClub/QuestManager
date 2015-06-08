package nmt.minecraft.QuestManager.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * holds goal state
 */
public class GoalState {
	
	private String name; //for comparison
	
	private List<RequirementState> requirementStates;
	
	public GoalState() {
		name = "";
		
		requirementStates = new LinkedList<RequirementState>();
	}
	
	@SuppressWarnings("unchecked")
	public void load(YamlConfiguration config) throws InvalidConfigurationException {
		if (!config.contains("type") || !config.getString("type").equals("goalstate") 
				|| !config.contains("name") || !config.contains("requirementStates")) {
			throw new InvalidConfigurationException();
		}
		
		name = config.getString("name");
		
		requirementStates = (List<RequirementState>) config.getList("requirementStates");
	}
	
	public void save(File file) throws IOException {
		YamlConfiguration config = new YamlConfiguration();
		
		config.set("type", "goalstate");
		
		config.set("name", name);
		
		config.set("goals", requirementStates);
		
		config.save(file);
	}
	
	public YamlConfiguration asConfig() {

		YamlConfiguration config = new YamlConfiguration();
		
		config.set("type", "goalstate");
		
		config.set("name", name);
		
		config.set("requirements", requirementStates);
		
		return config;
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
	 * @return the requirementStates
	 */
	public List<RequirementState> getRequirementStates() {
		return requirementStates;
	}
	
	public void addRequirementState(RequirementState state) {
		requirementStates.add(state);
	}
	
	
	
}
