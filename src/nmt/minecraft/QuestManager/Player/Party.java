package nmt.minecraft.QuestManager.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A group of players who work together on 
 * @author Skyler
 *
 */
public class Party implements Participant {
	
	
	private List<QuestPlayer> players;
	
	private QuestPlayer leader;
	
	private String name;
	
	public Party() {
		name = "";
		players = new LinkedList<QuestPlayer>();
		leader = null;
	}
	
	public Party(QuestPlayer leader) {
		name = "";
		players = new LinkedList<QuestPlayer>();
		players.add(leader);
		this.leader = leader;
	}
	
	public Party(QuestPlayer leader, Collection<QuestPlayer> players) {
		name = "";
		players = new LinkedList<QuestPlayer>(players);
		this.leader = leader;
		
	}
	
	public QuestPlayer getLeader() {
		return leader;
	}
	
	@Override
	public Collection<QuestPlayer> getParticipants() {
		return players;
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
		for (QuestPlayer p : players) {
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
			party.players.add(
					QuestPlayer.valueOf(qpmap));
		}
		
		
		
		return party;
	}
	
	
	
	
}
