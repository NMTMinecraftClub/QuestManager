package nmt.minecraft.QuestManager.Quest.History;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class History {
	
	private List<HistoryEvent> events;
	
	@SuppressWarnings("unchecked")
	public static History fromConfig(YamlConfiguration config) throws InvalidConfigurationException {
		if (config == null) {
			return null;
		}
		if (!config.contains("HistoryEvents")) {
			throw new InvalidConfigurationException();
		}
		
		History history = new History();
		
		List<HistoryEvent> list;
		
		list = (List<HistoryEvent>) config.get("HistoryEvents");
		
		history.events = list;
		
		return history;
	}
	
	/**
	 * Creates a new history with no events
	 */
	public History() {
		this.events = new LinkedList<HistoryEvent>();
	}
	
	public List<HistoryEvent> events() {
		return this.events;
	}
	
	public void addHistoryEvent(HistoryEvent event) {
		events.add(event);
	}
	
	/**
	 * Returns a formatted description of this history composed of each contained event's
	 * description
	 */
	@Override
	public String toString() {
		if (events == null || events.isEmpty()) {
			return "";
		}
		
		String builder = "";
		
		for (HistoryEvent event : events) {
			builder += event.getDescription() + "\n";
		}
		
		//get rid of trailing newline
		builder = builder.substring(0, builder.length() - 3);
		
		return builder;
	}
	
	/**
	 * Returns a config file (section) that stores the information contained in this history
	 * @return
	 */
	public YamlConfiguration toConfig() {
		
		YamlConfiguration config = new YamlConfiguration();
		
		if (events.isEmpty()) {
			return config;
		}
		List<String> list = new ArrayList<String>(events.size());
		
		for (HistoryEvent e : events) {
			list.add(e.getDescription());
		}
		
		config.set("HistoryEvents", list);
		
		return config;
	}
	
}
