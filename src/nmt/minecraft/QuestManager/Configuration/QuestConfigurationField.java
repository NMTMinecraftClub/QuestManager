package nmt.minecraft.QuestManager.Configuration;

import java.util.LinkedList;

import org.bukkit.inventory.ItemStack;

import nmt.minecraft.QuestManager.Quest.Goal;

/**
 * Holds key and default information for defined quest config fields.
 * <p>
 * Fields|Keys|Defaults are:
 * <ul>
 * <li>NAME | name | "Generated Quest"</li>
 * <li>DESCRIPTION | description | "No Description"</li>
 * <li>GOALS | goals | <i>Empty List</i></li>
 * <li>SAVESTATE | savestate | false</li>
 * <li>REPEATABLE | repeatable | false </li>
 * <li>USEPARTY | useparty | false </li>
 * <li>REQUIREPARTY | requireparty | false </li>
 * <li>NPCS | npcs | <i>Empty List</i></li>
 * <li>START | start | <i>null</i> </li>
 * <li>PREREQS | requiredquests | <i>Empty List</i></li>
 * <li> END | end | same </li>
 * <li> FAME | fame | 100 </li>
 * <li> REWARDS | rewards | <i>Empty List</i> </li>
 * </ul>
 * </p>
 * @author Skyler
 *
 */
public enum QuestConfigurationField {
	
	NAME("name", "Generated Quest"),
	DESCRIPTION("description", "No Description"),
	GOALS("goals", new LinkedList<Goal>()),
	SAVESTATE("savestate", false),
	REPEATABLE("repeatable", false),
	NPCS("npcs", new LinkedList<Goal>()),
	START("start", null),
	PREREQS("requiredquests", new LinkedList<String>()),
	REQUIREPARTY("requireparty", false),
	USEPARTY("useparty", false),
	END("end", "same"),
	FAME("fame", 100),
	REWARDS("rewards", new LinkedList<ItemStack>());
	
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
