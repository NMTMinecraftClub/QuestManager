package nmt.minecraft.QuestManager.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * A group of players who work together on 
 * @author Skyler
 *
 */
public class Party implements Participant {
	
	
	//private List<QuestPlayer> players;
	
	private List<QuestPlayer> members;
	
	private QuestPlayer leader;
	
	private Scoreboard partyBoard;
	
	private String name;
	
	private Team tLeader, tMembers;
	
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
	}
	
	public Party(QuestPlayer leader) {
		name = "";
		members = new LinkedList<QuestPlayer>();
		this.leader = leader;
		tLeader.addPlayer(leader.getPlayer());
	}
	
	public Party(String name, QuestPlayer leader) {
		this.name = name;
		members = new LinkedList<QuestPlayer>();
		this.leader = leader;
		tLeader.addPlayer(leader.getPlayer());
	}
	
	public Party(String name, QuestPlayer leader, Collection<QuestPlayer> players) {
		this.name = "";
		this.members = new LinkedList<QuestPlayer>(players);
		this.leader = leader;
		tLeader.addPlayer(leader.getPlayer());
		for (QuestPlayer p : players) {
			members.add(p);
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
	
	
	
	
}
