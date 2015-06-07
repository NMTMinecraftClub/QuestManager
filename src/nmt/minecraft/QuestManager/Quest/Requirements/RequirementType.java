package nmt.minecraft.QuestManager.Quest.Requirements;

import nmt.minecraft.QuestManager.Quest.Requirement;

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
	
	public Requirement instance() {
		try {
			return c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}
