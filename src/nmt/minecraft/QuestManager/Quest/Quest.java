package nmt.minecraft.QuestManager.Quest;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nmt.minecraft.QuestManager.Quest.History.History;
import nmt.minecraft.QuestManager.Quest.Requirements.Requirement;
import nmt.minecraft.QuestManager.Quest.Requirements.RequirementUpdateEvent;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Quest Interface!<br />
 * 
 * 
 * 
 * @breakdown
 * quests run and stop. They save their state and load their state. They 
 * subscribe to events and have {@link Requirement Requirements}. They are completed or failed.
 * They have rewards, disperse rewards, and collect tolls. They do whatever
 * the heck they want.
 * 
 * Specifically the quest interface specifies that quests can be started,
 * stopped, and halted. Quests must also keep track of involved players and
 * any parts of the quest involved (future work?). <br />
 * 
 * It doesn't have to, but I see this going as follows:<br />
 * Another quest type class/iterface implements the quest interface and makes
 * it more specific (duh). For example, maybe a PVPQuest interface or an
 * ExplorationQuest interface, etc. And then down in the gnitty gritty
 * specifics, maybe a whole host of quest classes that implement the interface
 * and have the actual implementation stuff? <br />
 * It might make more sense to put all the specifics in another project
 * entirely and let this one by the 'QuestManager' that allows for quests to
 * be plugged in! We create an API! And then quest implementations could come
 * as their own plugins and be drag and drop into the plugins folder. For
 * example, say I made a set of 10 quests and called them DoveQuest lol. The
 * server admin would have to pop in QuestManager, and then DoveQuest. And maybe
 * they also like this other quest pack called QuestsByTrig and dropped that
 * in too. And then the quest manager was just set to collect the quests and
 * load them up! (future work)
 * 
 * @author Skyler
 *
 */
public abstract class Quest implements Listener {
	
	private int ID;
	
	private String name;
	
	private String description;
	
	private boolean running;
	
	private Set<Player> players;
	
	private List<Goal> goals;	
	
	private History history;
	
	
	public Quest(String name, String description) {
		this.name = name;
		this.description = description;
		
		this.running = false;
		this.players = new HashSet<Player>();
		this.goals = new LinkedList<Goal>();
		
		this.history = new History();
		
		this.ID = (int) (Math.random() * Integer.MAX_VALUE);
	}
	
	/**
	 * Requests information about whether the quest is currently running or is
	 * stopped/halted
	 * @return Whether or not the quest is running
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Stops the quest softly, optionally performing state-saving procedures
	 * and displaying messages to the involved players. Quests should also 
	 * deliver players back to an area where they are free to roam and return
	 * to homeworld portals (or the equivalent) when they stop.
	 */
	public abstract void stop();
	
	/**
	 * <i>Immediately</i> stops the quest, returning players to a free-roaming
	 * state. Quests are not expected to perform save-state procedures when
	 * halted, but may. <br/>
	 * <b>Quests must immediately stop execution when asked to halt.<b>
	 */
	public abstract void halt();
	
	/**
	 * Return all players involved in this quests.<br />
	 * Involved players are those participating in any way in the quest. For
	 * example, if a player is marked as a pvp target in a quest then they are
	 * involved in the quest.
	 * @return
	 */
	public Collection<Player> getPlayers() {
		return players;
	}
	
	/**
	 * Add a player to the quest.<br />
	 * This typically involves moving the player to a starting location or giving them
	 * starting equipment?
	 * @param player
	 */
	public abstract void addPlayer(Player player);
	
	/**
	 * Removes a player from the quest.<br />
	 * This might involve removing them from a dungeon, etc;
	 * @param player Which player to remove
	 * @return Whether or not the player was successfully removed
	 */
	public abstract boolean removePlayer(Player player);
	
	/**
	 * Returns the name of the quest, including text formatters and colors.
	 * @return The name of the quest
	 * @see {@link org.bukkit.ChatColor ChatColor}
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns a list of enlisted goals
	 * @return
	 */
	public List<Goal> getGoals() {
		return goals;
	}
	
	/**
	 * Appends the provided goal to the current list of goals
	 * @param goal
	 */
	protected void addGoal(Goal goal) {
		goals.add(goal);
	}
	
	/**
	 * Returns a (possibly multilined) description of the quest that will be made
	 * visible to players to aid in the quest selection process.<br />
	 * Descriptions should have a list of stakes and rewards, as well as either
	 * a hint or outline of/to objectives.
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Returns the current history for reading or changing
	 * @return
	 */
	public History getHistory() {
		return history;
	}
	
	
	@EventHandler
	public void onRequirementUpdate(RequirementUpdateEvent e) {
		if (e.getRequirement() == null || e.getRequirement().getGoal().getQuest().equals(this)) {
			update();
		}
	}
	
	protected void update() {
		//TODO should this be abstract? Should this be used with displaying quest
		//status? how is that going to work? :S
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Quest)) {
			return false;
		}
		
		Quest other = (Quest) o;
		
		if (other.ID == ID && other.getName().equals(this.getName())) {
			return true;
		}
		
		return false;
	}
}
