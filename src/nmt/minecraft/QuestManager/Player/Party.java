package nmt.minecraft.QuestManager.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.Note.Tone;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.Utils.GUID;
import nmt.minecraft.QuestManager.Fanciful.FancyMessage;

/**
 * A group of players who work together on 
 * @author Skyler
 *
 */
public class Party implements Participant, Listener {
	
	
	//private List<QuestPlayer> players;
	
	public static int maxSize = 4;
	
	private List<QuestPlayer> members;
	
	private QuestPlayer leader;
	
	private Scoreboard partyBoard;
	
	private String name;
	
	private Team tLeader, tMembers;
	
	private Objective board;
	
	private GUID id;
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(Party.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(Party.class);
	}
	

	private enum aliases {
		FULL("nmt.minecraft.QuestManager.Player.Party"),
		DEFAULT(Party.class.getName()),
		SIMPLE("Party"),
		INFORMAL("P"),
		QUALIFIED_INFORMAL("QPP");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	public Party() {
		name = "";
		members = new LinkedList<QuestPlayer>();
		leader = null;
		partyBoard = Bukkit.getScoreboardManager().getNewScoreboard();
		tLeader = partyBoard.registerNewTeam("Leader");
		tMembers = partyBoard.registerNewTeam("members");
		
		tLeader.setPrefix(ChatColor.GOLD.toString());
		tMembers.setPrefix(ChatColor.DARK_GREEN.toString());
		
		board = partyBoard.registerNewObjective("side", "dummy");
		board.setDisplayName("Party");
		board.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		id = GUID.generateGUID();
		
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
		
		QuestManagerPlugin.questManagerPlugin.getPlayerManager().addParty(this);
		
	}
	
	public Party(QuestPlayer leader) {
		this();
		this.leader = leader;
		tLeader.addPlayer(leader.getPlayer());
		updateScoreboard();
	}
	
	public Party(String name, QuestPlayer leader) {
		this(leader);
		this.name = name;
		updateScoreboard();
	}
	
	public Party(String name, QuestPlayer leader, Collection<QuestPlayer> players) {
		this(name, leader);
		
		members.addAll(players);
		
		for (QuestPlayer p : players) {
			tMembers.addPlayer(p.getPlayer());
		}
		
		updateScoreboard();
		
	}
	
	public void updateScoreboard() {
		if (leader == null) {
			return;
		}
		
		if (leader.getPlayer().isOnline()) {
			(leader.getPlayer().getPlayer()).setScoreboard(partyBoard);
		}
		if (!members.isEmpty())
		for (QuestPlayer member : members) {
			if (member.getPlayer().isOnline()) {
				( member.getPlayer().getPlayer()).setScoreboard(partyBoard);
			}
		}
		
		//now that everyone's registered, let's update health
		Objective side = partyBoard.getObjective(DisplaySlot.SIDEBAR);
		if (leader.getPlayer().isOnline()) {
			side.getScore(leader.getPlayer().getName()).setScore((int) leader.getPlayer().getPlayer().getHealth());
			}
		if (!members.isEmpty())
		for (QuestPlayer member : members) {
			if (member.getPlayer().isOnline()) {
				side.getScore(member.getPlayer().getName()).setScore((int) member.getPlayer().getPlayer().getHealth());
				}
		}
	}
	
	public QuestPlayer getLeader() {
		return leader;
	}
	
	@Override
	public Collection<QuestPlayer> getParticipants() {
		Set<QuestPlayer> set = new HashSet<QuestPlayer>(members);
		set.add(leader);
		return set;
	}
	
	public Collection<QuestPlayer> getMembers() {
		return members;
	}
	
	/**
	 * Creates a map representation of this party, for saving to config
	 */
	@Override
	public Map<String, Object> serialize() {
		//party name,
		//party leader,
		//party members
		Map<String, Object> map = new HashMap<String, Object>(3);
		
		map.put("name", name);
		map.put("leader", leader.serialize());
		
		List<Map<String, Object>> sl = new LinkedList<Map<String, Object>>();
		for (QuestPlayer p : members) {
			sl.add(
					p.serialize()
					);
		}
		
		map.put("members", sl);
		
		return map;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Party valueOf(Map<String, Object> map) {
		if (map == null || !map.containsKey("leader")) {
			return null;
		}
		
		Party party = new Party();
		
		party.name= (String) map.get("name");
		party.leader = QuestPlayer.valueOf(
				(Map<String, Object>) map.get("leader"));
		
		List<Map<String, Object>> pl = (List<Map<String, Object>>) map.get("members");
		
		if (pl.isEmpty()) {
			return party;
		}
		
		for (Map<String, Object> qpmap : pl) {
			party.members.add(
					QuestPlayer.valueOf(qpmap));
		}
		
		
		
		return party;
	}

	@Override
	public String getIDString() {
		return id.toString();
	}
	
	public GUID getID() {
		return id;
	}
	
	/**
	 * Adds the player to the party, returning true if successful. If the player cannot be added,
	 * false is returned instead.
	 * @param player
	 * @return true if successful
	 */
	public boolean addMember(QuestPlayer player) {
		if (members.size() < Party.maxSize) {
			tellMembers(
					new FancyMessage(player.getPlayer().getName())
						.color(ChatColor.DARK_BLUE)
						.then(" has joined the party")
					);
			members.add(player);
			tMembers.addPlayer(player.getPlayer());
			updateScoreboard();
			return true;
		} else {
			tellMembers(
					new FancyMessage("Unable to add ")
						.then(player.getPlayer().getName())
						.color(ChatColor.DARK_BLUE)
						.then(" becuase the party is full!")
					);
		}
		
		return false;
	}
	
	public int getSize() {
		return members.size();
	}
	
	public boolean isFull() {
		return (members.size() >= Party.maxSize);
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.isCancelled() || !(e.getEntity() instanceof Player)) {
			return;
		}
		
		Player p = (Player) e.getEntity();
		if (leader.getPlayer().getUniqueId().equals(p.getUniqueId())) {
			double old = p.getHealth();
			p.setHealth(old - e.getFinalDamage());
			updateScoreboard();
			p.setHealth(old);
			return;
		}
		if (!members.isEmpty())
		for (QuestPlayer qp : members) {
			if (qp.getPlayer().getUniqueId().equals(p.getUniqueId())) {
				double old = p.getHealth();
				p.setHealth(old - e.getFinalDamage());
				updateScoreboard();
				p.setHealth(old);
				return;
			}
		}
	}
	
	@EventHandler
	public void onPlayerRegen(EntityRegainHealthEvent e) {
		if (e.isCancelled() || !(e.getEntity() instanceof Player)) {
			return;
		}
		
		Player p = (Player) e.getEntity();
		if (leader.getPlayer().getUniqueId().equals(p.getUniqueId())) {
			double old = p.getHealth();
			double to = Math.min(20.0, old + e.getAmount());
			p.setHealth(to);
			updateScoreboard();
			p.setHealth(old);
			return;
		}
		if (!members.isEmpty())
		for (QuestPlayer qp : members) {
			if (qp.getPlayer().getUniqueId().equals(p.getUniqueId())) {
				double old = p.getHealth();
				double to = Math.min(20.0, old + e.getAmount());
				p.setHealth(to);
				updateScoreboard();
				p.setHealth(old);
				return;
			}
		}
	}
	
	public boolean removePlayer(QuestPlayer player, String exitMessage) {
		
		player.leaveParty(exitMessage);
		
		if (player.getIDString().equals(leader.getIDString())) {
			
			tLeader.removePlayer(leader.getPlayer());
			partyBoard.resetScores(leader.getPlayer().getName());
			
			if (members.size() == 1) {
				//close party
				members.get(0).leaveParty("The party has been closed");
				clean();
				return true;
			}
			
			leader = members.get(0);
			tMembers.removePlayer(leader.getPlayer());
			members.remove(0);
			updateScoreboard();
			tellMembers(
					new FancyMessage(player.getPlayer().getName())
						.color(ChatColor.DARK_BLUE)
						.then(" has left the party")
					);
			return true;
		}
		
		if (members.isEmpty()) {
			return false;
		}
		
		ListIterator<QuestPlayer> it = members.listIterator();
		QuestPlayer qp;
		
		while (it.hasNext()) {
			qp = it.next();
			if (qp.getIDString().equals(player.getIDString())) {
				tMembers.removePlayer(qp.getPlayer());
				partyBoard.resetScores(qp.getPlayer().getName());
				it.remove();
				tellMembers(
						new FancyMessage(player.getPlayer().getName())
							.color(ChatColor.DARK_BLUE)
							.then(" has left the party")
						);
				
				//make sure leader isn't the only one left
				if (members.isEmpty()) {
					leader.leaveParty("The party has been closed.");
					clean();
				} else {
					updateScoreboard();
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public void disband() {
		for (QuestPlayer player : this.members) {
			player.leaveParty("The party has disbanded.");
		}
		leader.leaveParty("The party has disbanded");
		
		clean();
	}
	
	private void clean() {
		QuestManagerPlugin.questManagerPlugin.getPlayerManager().removeParty(this);
		Bukkit.getPluginManager().callEvent(
				new PartyDisbandEvent(this));
	}
	
	public void tellMembers(String message) {
		tellMembers(new FancyMessage(message));
	}
	
	public void tellMembers(FancyMessage message) {
		if (leader != null) {
			Player l = leader.getPlayer().getPlayer();
			message.send(l);
			l.playNote(l.getLocation(), Instrument.PIANO, Note.natural(1, Tone.C));
			l.playNote(l.getLocation(), Instrument.PIANO, Note.natural(1, Tone.G));
			l.playNote(l.getLocation(), Instrument.PIANO, Note.natural(1, Tone.E));
		}
		if (members.isEmpty()) {
			return;
		}
		for (QuestPlayer qp : members) {
			if (!qp.getPlayer().isOnline()) {
				continue;
			}
			Player p = qp.getPlayer().getPlayer();
			message.send(qp.getPlayer().getPlayer());
			p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Tone.C));
			p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Tone.G));
			p.playNote(p.getLocation(), Instrument.PIANO, Note.natural(1, Tone.E));
		}
	}
	
}
