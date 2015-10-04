package nmt.minecraft.QuestManager.Quest.Requirements;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import nmt.minecraft.QuestManager.QuestManagerPlugin;
import nmt.minecraft.QuestManager.Configuration.State.RequirementState;
import nmt.minecraft.QuestManager.Configuration.State.StatekeepingRequirement;
import nmt.minecraft.QuestManager.Player.Participant;
import nmt.minecraft.QuestManager.Player.QuestPlayer;
import nmt.minecraft.QuestManager.Quest.Goal;
import nmt.minecraft.QuestManager.Quest.Requirements.Factory.RequirementFactory;

/**
 * Requires the participants to defeat (kill) some number of a type of entity.<br />
 * This requirement is set up to work with parties.<br />
 * This requirement also will check to make sure the name of the entities slaid matches 
 * what is provided. If no name is provided, any entity of the same entity type
 * will be considered valid.
 * @author Skyler
 * @see {@link PositionRequirement}
 */
public class SlayRequirement extends Requirement implements Listener, StatekeepingRequirement {
	
	public static class SlayFactory extends RequirementFactory<SlayRequirement> {

		@Override
		public SlayRequirement fromConfig(Goal goal, ConfigurationSection config) {
			SlayRequirement req = new SlayRequirement(goal);
			try {
				req.fromConfig(config);
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			return req;
		}
		
	}
		
	/**
	 * The type of the entity.
	 */
	private EntityType type;
	
	/**
	 * How many of that entity to kill before this requirement is satisfied
	 */
	private int count;
	
	/**
	 * A custom name to be checked on dying entities
	 */
	private String name;
	
	/**
	 * Internal variable to keep track of progress
	 */
	private int progress;
	
	/**
	 * Super secret private constructor for factory call convenience
	 * @param goal
	 */
	private SlayRequirement(Goal goal) {
		super(goal);	
	}
	
	public SlayRequirement(Goal goal, String description, Participant participants, EntityType type, String name, int count) {
		super(goal, description);
		
		this.participants = participants;
		
		this.type = type;
		this.name = name;
		this.count = count;
		this.progress = 0;
		
	}
	
	@Override
	public void activate() {
		Bukkit.getPluginManager().registerEvents(this, QuestManagerPlugin.questManagerPlugin);
	}

	/**
	 * @return the participants
	 */
	public Participant getParticipants() {
		return participants;
	}

	
	
	/**
	 * @return the type
	 */
	public EntityType getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(EntityType type) {
		this.type = type;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress the progress to set
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent e) {
		
		if (participants == null) {
			return;
		}
		
		if (e.getEntityType().equals(type)) {
			
			//if name is null (SHORT CIRCUIT IF SO) or if the name matches
			if (name == null || (e.getEntity().getCustomName() != null && e.getEntity().getCustomName().equals(name)))
			if (e.getEntity().getKiller() != null) {
				boolean trip = false;
				for (QuestPlayer quester : participants.getParticipants()) {
					if (quester.getPlayer().getUniqueId().equals(e.getEntity().getKiller().getUniqueId())) {
						trip = true;
						break;
					}
				}
				
				if (trip)
				{
					progress++;
					update();
				}
			}
			
		}
		
	}
	
	/**
	 * Adds one to the kill count, and checks for completion
	 */
	@Override
	public void update() {
		
		if (state) {
			return;
		}
		sync();
		if (progress >= count) {
			state = true;
			updateQuest();
			HandlerList.unregisterAll(this);
			return;
		}
		
		state = false;
	}

	@Override
	public void fromConfig(ConfigurationSection config)
			throws InvalidConfigurationException {
		//  keep data about the entity type and name and count
		//  type: "slayr"
		//  entitytype: ENTITY_TYPE.name
		//  forcedname: ''
		//  count: [INT]
			
		if (!config.contains("type") || !config.getString("type").equals("slayr")) {
			throw new InvalidConfigurationException();
		}
		
		String type = config.getString("entityType");
		if (type == null) {
			type = config.getString("entitytype");
		}
		this.type = EntityType.valueOf(type);
		this.count = config.getInt("count");
		
		String tmp = config.getString("name", "");
		
		if (tmp.trim().isEmpty()) {
			this.name = null;
		} else {
			this.name = tmp;
		}
		
		this.desc = config.getString("description", "Slay " + count + " " + 
				this.name == null ? this.type.toString() : this.name);
		
	}

	@Override
	public RequirementState getState() {
		YamlConfiguration config = new YamlConfiguration();
		
		config.set("progress", progress);
		
		
		RequirementState state = new RequirementState(config);
		return state;
	}

	@Override
	public void loadState(RequirementState state)
			throws InvalidConfigurationException {
		
		ConfigurationSection config = state.getConfig();
		
		if (!config.contains("progress")) {
			throw new InvalidConfigurationException();
		}
		
		this.progress = config.getInt("progress", 0);
		
	}

	@Override
	public void stop() {
		; //do nothing!
	}
	
	@Override
	public String getDescription() {
		return this.desc + " (" + this.progress + "/" + this.count + ")";
	}
	
}
