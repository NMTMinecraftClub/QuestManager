package nmt.minecraft.QuestManager.Quest.History;

import java.util.LinkedList;
import java.util.List;

public class History {
	
	private List<HistoryEvent> events;
	
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
	
}
