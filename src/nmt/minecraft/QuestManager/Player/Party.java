package nmt.minecraft.QuestManager.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A group of players who work together on 
 * @author Skyler
 *
 */
public class Party implements Participant {
	
	
	private Set<QuestPlayer> players;
	
	private QuestPlayer leader;
	
	public Party() {
		players = new HashSet<QuestPlayer>();
		leader = null;
	}
	
	public Party(QuestPlayer leader) {
		players = new HashSet<QuestPlayer>();
		players.add(leader);
		this.leader = leader;
	}
	
	public Party(QuestPlayer leader, Collection<QuestPlayer> players) {
		players = new HashSet<QuestPlayer>(players);
		this.leader = leader;
		
	}
	
	public QuestPlayer getLeader() {
		return leader;
	}
	
	@Override
	public Collection<QuestPlayer> getParticipants() {
		return players;
	}
}
