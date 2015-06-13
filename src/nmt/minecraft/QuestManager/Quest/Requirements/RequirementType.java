package nmt.minecraft.QuestManager.Quest.Requirements;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import nmt.minecraft.QuestManager.Quest.Goal;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;


/**
 * List of requirement types used when creating requirements from config
 * @author Skyler
 *
 */
public enum RequirementType {
	
	ARRIVE(ArriveRequirement.class),
	POSITION(PositionRequirement.class),
	POSSESS(PossessRequirement.class),
	VANQUISH(VanquishRequirement.class);
	
	private Class<? extends Requirement> c;
	
	private RequirementType(Class<? extends Requirement> c) {
		this.c = c;
	}
	
	public Requirement instance(Goal goal, YamlConfiguration config) throws InvalidConfigurationException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		try {
			Constructor con = c.getConstructor(Goal.class);
			Requirement r = (Requirement) con.newInstance(goal);
			r.fromConfig(config);
			return r;
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
