package nmt.minecraft.QuestManager.Quest;

import nmt.minecraft.QuestManager.Quest.Requirements.Requirement;

/**
 * Represents a requirement that may or may not be completed -- thus it holds a state.<br />
 * State requirements differe from regular {@link Requirement Requirements} in that state
 * requirements may bounce back and forth between being satisfied and unsatisfied. Examples
 * include:
 * <ul>
 * <li>Possess an item, or a quantity of items</li>
 * <li>Have an amount of money</li>
 * <li>etc</li>
 * </ul>
 * State requirements work by tracking state information at all times instead of until
 * they are first satisfied.
 * @author Skyler
 *
 */
public abstract class StateRequirement extends Requirement {
	
	private boolean state;
	
	public StateRequirement(Goal goal) {
		this(goal, "");
	}
	
	public StateRequirement(Goal goal, String description) {
		super(goal, description);
		state = false;
	}
	
	/**
	 * Performs a check against requirement information and then returns whether the current
	 * criteria are satisfied
	 * @return
	 */
	public boolean isCleared() {
		update();
		return state;
	}
	
	/**
	 * Check requirement criteria and update state information
	 */
	protected abstract void update();
}
