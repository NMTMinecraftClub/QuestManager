package nmt.minecraft.QuestManager.Configuration;

import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Holds state information about a requirement.<br />
 * It's worth noting that most requirements don't need to store state information! They'll
 * usually be re-evaluated constantly, so there's no need unless they need something
 * special when restarting-from-state. Examples include:<br />
 * <ul>
 * <li>A boss requirement, which needs to recreate teh boss in the same locations (and same hp?)
 * <li>A time limit requirement, which needs to store how much time is left</li>
 * </ul>
 * 
 * @author Skyler
 *
 */
public class RequirementState extends YamlConfiguration {
	
}
