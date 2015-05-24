package nmt.minecraft.QuestManager.Quest;

import java.util.Collection;

import org.bukkit.entity.Player;

/**
 * Quest Interface!<br />
 * 
 * 
 * 
 * @breakdown
 * quests run and stop. They save their state and load their state. They 
 * subscribe to events and have objectives. They are completed or failed.
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
public interface Quest {
	
	/**
	 * Requests information about whether the quest is currently running or is
	 * stopped/halted
	 * @return Whether or not the quest is running
	 */
	public boolean isRunning();
	
	/**
	 * Stops the quest softly, optionally performing state-saving procedures
	 * and displaying messages to the involved players. Quests should also 
	 * deliver players back to an area where they are free to roam and return
	 * to homeworld portals (or the equivalent) when they stop.
	 */
	public void stop();
	
	/**
	 * <i>Immediately</i> stops the quest, returning players to a free-roaming
	 * state. Quests are not expected to perform save-state procedures when
	 * halted, but may. <br/>
	 * <b>Quests must immediately stop execution when asked to halt.<b>
	 */
	public void halt();
	
	/**
	 * Return all players involved in this quests.<br />
	 * Involved players are those participating in any way in the quest. For
	 * example, if a player is marked as a pvp target in a quest then they are
	 * involved in the quest.
	 * @return
	 */
	public Collection<Player> getPlayers();
	
	/**
	 * Returns the name of the quest, including text formatters and colors.
	 * @return The name of the quest
	 * @see {@link org.bukkit.ChatColor ChatColor}
	 */
	public String getName();
	
	/**
	 * Returns a (possibly multilined) description of the quest that will be made
	 * visible to players to aid in the quest selection process.<br />
	 * Descriptions should have a list of stakes and rewards, as well as either
	 * a hint or outline of/to objectives.
	 * @return
	 */
	public String getDescription();
}
