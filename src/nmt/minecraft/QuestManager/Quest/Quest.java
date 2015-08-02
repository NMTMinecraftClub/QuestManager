package nmt.minecraft.QuestManager.Quest;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.State.GoalState;
import nmt.minecraft.QuestManager.Configuration.State.QuestState;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.Party;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.History.History;
import nmt.minecraft.QuestManager.Quest.Requirements.Requirement;
import nmt.minecraft.QuestManager.Quest.Requirements.RequirementUpdateEvent;
import nmt.minecraft.QuestManager.UI.ChatMenu;
import nmt.minecraft.QuestManager.UI.Menu.SimpleChatMenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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
 * @TODO maybe split this into 'involved quests' and 'casual quests', where 'involved quests'
 * would not require any teleports out of dungeons, etc and 'involved quests' take you to
 * a special location you cannot otherwise access? If so, this should be made abstract  again
 * and the methods addPlayer and removePlayer should be made abstract and defined in subclasses,
 * where casualQuests would just remove them from the list (like this class does) and 
 * involvedQuests would teleport them out too
 * 
 * @author Skyler
 *
 */
public class Quest implements Listener {
	
	private int ID;
	
	private String name;
	
	private String description;
	
	private boolean running;
	
	private Set<QuestPlayer> players;
	
	private List<Goal> goals;	
	
	private int fame;
	
	private List<ItemStack> itemRewards;
	
	private History history;
	
	private boolean ready;
	
	//private Set<NPC> npcs;
	
	/**
	 * Whether or not this quest should be triggered on and then never evaluated again,
	 * or if it can go between completed and not completed depending on its requirements.<br />
	 * In other words, does this quest be ready to turn in and never can be un-ready after.
	 * <p>
	 * As a specific example, consider a quest to deliver 10 apples. This quest can be ready
	 * to turn in by the player obtaining 10 apples. If the player drops some of the apples,
	 * however, the quest is no longer ready to be turned in. In this case, keepState is false.
	 */
	private boolean keepState;
	
	public Quest(String name, String description, boolean keepState) {
		this.name = name;
		this.description = description;
		
		this.running = false;
		this.goals = new LinkedList<Goal>();
		//this.npcs = new HashSet<NPC>();
		
		this.history = new History();
		ready = false;
		this.keepState = keepState;
		
		players = new HashSet<QuestPlayer>();
		itemRewards = new LinkedList<ItemStack>();
		
		this.ID = (int) (Math.random() * Integer.MAX_VALUE);
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}
	
	/**
	 * @throws InvalidConfigurationException 
	 * Loads quest/objective/requirement state from the provided file
	 * @param state
	 * @throws  
	 */
	public void loadState(QuestState state) throws InvalidConfigurationException {
		
		if (!this.name.equals(state.getName())) {
			QuestManagerPlugin.questManagerPlugin.getLogger()
				.warning("Attempting to load state information from a mismatched quest!");
			QuestManagerPlugin.questManagerPlugin.getLogger()
			.info("[" + this.name + "] <-/-> [" + state.getName() + "]");
		
		}
		Participant pant = state.getParticipant();
		if (pant != null) {
			
			if (pant instanceof Party) {
				players.add(((Party) pant).getLeader());
				for (QuestPlayer p : ((Party) pant).getMembers()) {
					players.add(p);
					p.addQuest(this);
				}
			} else {
				players.add((QuestPlayer) pant);
				((QuestPlayer) pant).addQuest(this);
			}
		}
		
		ListIterator<GoalState> states = state.getGoalState().listIterator();
		
		for (Goal goal : goals) {
			goal.loadState(states.next());
		}
		
		
	}

	public QuestState getState() {
		//we need to definitely save goal state information (and requirement state). We also
		//and... that's kind of it actually
		QuestState state = new QuestState();
		state.setName(name);
		
		if (goals.isEmpty()) {
			return state;
		}
		
		for (Goal goal : goals) {
			state.addGoalState(
					goal.getState());
		}
		
		
		
		state.setParticipant(getParticipants());
		
		return state;
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
	 * Returns whether or not the quest is ready to turn in.<br />
	 * This method causes its goals to be re-evaluated to guarantee the returned result
	 * is accurate at the time the method is called
	 * @return
	 */
	public boolean isReady() {
		update();
		return ready;
	}
	
	/**
	 * Completes the quest, dispensing rewards to involved players.
	 * @param force Should this method execute even if the quest has incomplete objectives?
	 */
	public void completeQuest(boolean force) {
		
		if (!force && !isReady()) {
			return;
		}
		
		//go through and give each of the players involved their rewards
		for (QuestPlayer qp : getParticipants().getParticipants()) {
			if (qp.getPlayer().isOnline()) {
				Player player = qp.getPlayer().getPlayer();
				
				//item rewards
				ItemStack[] items = itemRewards.toArray(new ItemStack[0]);
				Map<Integer, ItemStack> returned = player.getInventory().addItem(items);
				
				if (!returned.isEmpty()) {
					//couldn't fit all of the items, so drop them on the ground
					player.sendMessage("Unable to fit all rewards! All rewards"
							+ " that couldn't fit are at your feet.");
					for (ItemStack item : returned.values()) {
						player.getWorld().dropItem(
								player.getEyeLocation(), item);
					}
				}
				
				//cash reward
				//TODO how can we implement cash rewards without tying it to an economy?
				
				//fame reward
				qp.addFame(fame);
				
				qp.completeQuest(this);
				
				qp.updateQuestBook();
				
			    ChatMenu menu = new SimpleChatMenu(
						new FancyMessage("")
						  .then("You've just completed the quest: ")
						  	.color(ChatColor.DARK_PURPLE)
						  	.style(ChatColor.BOLD)
						  .then(name)
						    .color(ChatColor.LIGHT_PURPLE)
						  .then("\nYou received ")
						    .color(ChatColor.DARK_PURPLE)
						  .then(fame + " fame")
						  	.color(ChatColor.GOLD)
						  .then(itemRewards.isEmpty() ? "!" : 
							  " and some item rewards!")
							.color(ChatColor.DARK_PURPLE)
								
								
						);
			    
			    menu.show(player);
			    
			    QuestManagerPlugin.questManagerPlugin.getManager().removeQuest(this);
			    
			    halt();
			}
		}
		
	}
	
	/**
	 * Stops the quest softly, optionally performing state-saving procedures
	 * and displaying messages to the involved players. Quests should also 
	 * deliver players back to an area where they are free to roam and return
	 * to homeworld portals (or the equivalent) when they stop.
	 */
	public void stop() {
		
		HandlerList.unregisterAll(this);
		
		//get config location!
		File saveLoc = new File(QuestManagerPlugin.questManagerPlugin.getManager()
				.getSaveLocation(), name + "_" + ID + ".yml");
		
		QuestState state = getState();
			
		if (!goals.isEmpty()) {	
			for (Goal goal : goals) {
				goal.stop();
			}
		}
		
		QuestManagerPlugin.questManagerPlugin.getLogger().info("Saving quest state: " + 
				saveLoc.getAbsolutePath());
		try {
			state.save(saveLoc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (players.isEmpty()) {
			return;
		}
		
		//do player stuff
		if (!players.isEmpty())
		for (QuestPlayer player : players) {
			removePlayer(player);
		}
		
		//remove NPCs
//		if (!npcs.isEmpty()) 
//		for (NPC npc : npcs) {
//			npc.getEntity().remove();
//		}
		
	}
	
	/**
	 * <i>Immediately</i> stops the quest, returning players to a free-roaming
	 * state. Quests are not to perform save-state procedures when
	 * halted. <br/>
	 * <b>Quests must immediately stop execution when asked to halt.<b>
	 */
	public void halt() {

		HandlerList.unregisterAll(this);
		
		if (players.isEmpty()) {
			return;
		}
		
		//just remove players
		for (QuestPlayer player : players) {
			removePlayer(player);
		}
		
		//remove NPCs
//		if (!npcs.isEmpty()) 
//		for (NPC npc : npcs) {
//			npc.getEntity().remove();
//		}
				
		//stop goals
		if (!goals.isEmpty()) {	
			for (Goal goal : goals) {
						goal.stop();
			}
		}
		
	}
	
	/**
	 * Return all players involved in this quests.<br />
	 * Involved players are those participating in any way in the quest. For
	 * example, if a player is marked as a pvp target in a quest then they are
	 * involved in the quest.
	 * @return
	 */
	public Collection<QuestPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * Add a player to the quest.<br />
	 * This typically involves moving the player to a starting location or giving them
	 * starting equipment?
	 * @param player
	 */
	public void addPlayer(QuestPlayer player) {
		players.add(player);
		
		if (!goals.isEmpty())
		for (Goal goal : goals) {
			goal.sync();
		}
		
		//TODO starting location, etc?
	}
	
	/**
	 * Removes a player from the quest.<br />
	 * This might involve removing them from a dungeon, etc;
	 * @param player Which player to remove
	 * @return Whether or not the player was successfully removed
	 */
	public boolean removePlayer(QuestPlayer player) {
		
		//TODO get them out of a dungeon, etc?
		
		return players.remove(player);
	}
	
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
	public void addGoal(Goal goal) {
		goals.add(goal);
	}
	
//	public void addNPC(NPC npc) {
//		npcs.add(npc);
//	}
	
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
		
	/**
	 * @return the fame
	 */
	public int getFame() {
		return fame;
	}

	/**
	 * @param fame the fame to set
	 */
	public void setFame(int fame) {
		this.fame = fame;
	}


	/**
	 * @return the itemRewards
	 */
	public List<ItemStack> getItemRewards() {
		return itemRewards;
	}

	/**
	 * @param itemRewards the itemRewards to set
	 */
	public void setItemRewards(List<ItemStack> itemRewards) {
		this.itemRewards = itemRewards;
	}
	
	public void addItemReward(ItemStack reward) {
		itemRewards.add(reward);
	}

	@EventHandler
	public void onRequirementUpdate(RequirementUpdateEvent e) {
		if (e.getRequirement() == null || e.getRequirement().getGoal().getQuest().equals(this)) {
			if (keepState && ready) {
				return;
			}
			
			
			update();

			for (QuestPlayer p : players) {
				p.addQuestBook();
				p.updateQuestBook();
			}
		}
		System.out.println();
	}
	
	/**
	 * Updates the quest information, including contained goals and requirements.
	 */
	protected void update() {
		
		//check if keepState is active and the quest is already ready
		if (keepState && ready) {
			return;
		}
		
		//if there are no goals, default to ready to turn in
		if (goals.isEmpty()) {
			ready = true;
			return;
		}
		
		for (Goal goal : goals) {
			//as soon as a single goal isn't ready, the quest is not ready
			if (!goal.isComplete()) {
				ready = false;
				return;
			}
		}
		
		ready = true;
	}
	
	public Participant getParticipants() {
		Participant part = null;
		if (players.size() == 1) {
			part = players.iterator().next();
		} else if (players.size() == 0) {
			return null;
		} else {
			QuestPlayer leader;
			List<QuestPlayer> members = new LinkedList<QuestPlayer>();
			Iterator<QuestPlayer> it = players.iterator();
			
			leader = it.next();
			
			while (it.hasNext()) {
				members.add(it.next());
			}
			
			part = new Party("quest_created_temp_quest[" + name + "]", leader, members);
		}
		return part;
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
