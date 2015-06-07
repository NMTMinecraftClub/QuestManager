package nmt.minecraft.QuestManager.Configuration;

import java.util.LinkedList;

import nmt.minecraft.QuestManager.Quest.Goal;

/**
 * Holds key and default information for defined quest config fields.
 * <p>
 * Fields|Keys|Defaults are:
 * <ul>
 * <li>NAME | name | "Generated Quest"</li>
 * <li>DESCRIPTION | description | "No Description"</li>
 * <li>GOALS | goals | <i>Empty List</i></li>
 * </ul>
 * </p>
 * @author Skyler
 *
 */
public enum QuestConfigurationField {
	
	NAME("name", "Generated Quest"),
	DESCRIPTION("description", "No Description"),
	GOALS("goals", new LinkedList<Goal>());
	
	private Object def;
	
	private String key;
	
	private QuestConfigurationField(String key, Object def) {
		this.def = def;
		this.key = key;
	}
	
	public Object getDefault() {
		return def;
	}
	
	public String getKey() {
		return key;
	}
}
