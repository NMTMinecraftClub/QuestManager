package nmt.minecraft.QuestManager.UI.Menu.Message;

import java.util.HashMap;
import java.util.Map;

import nmt.minecraft.QuestManager.Fanciful.FancyMessage;
import nmt.minecraft.QuestManager.UI.ChatGuiHandler;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Wrapping/formatting class for a message with two options.
 * @author Skyler
 *
 */
public class BioptionMessage extends Message {

	public static final String OPTION1 = "1";
	
	public static final String OPTION2 = "2";
	
	/**
	 * Registers this class as configuration serializable with all defined 
	 * {@link aliases aliases}
	 */
	public static void registerWithAliases() {
		for (aliases alias : aliases.values()) {
			ConfigurationSerialization.registerClass(BioptionMessage.class, alias.getAlias());
		}
	}
	
	/**
	 * Registers this class as configuration serializable with only the default alias
	 */
	public static void registerWithoutAliases() {
		ConfigurationSerialization.registerClass(BioptionMessage.class);
	}
	

	private enum aliases {
		DEFAULT(BioptionMessage.class.getName()),
		SIMPLE("BioptionMessage");
		
		private String alias;
		
		private aliases(String alias) {
			this.alias = alias;
		}
		
		public String getAlias() {
			return alias;
		}
	}
	
	private FancyMessage body;
	
	private FancyMessage option1Msg;
	
	private FancyMessage option2Msg;
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		map.put("body", body);
		map.put("option1", option1Msg);
		map.put("option2", option2Msg);
		
		return map;
	}
	
	public static BioptionMessage valueOf(Map<String, Object> map) {
		//a little more work, cause we want to accept regular strings too!
		Object oBody = map.get("body");
		Object oOption1 = map.get("option1");
		Object oOption2 = map.get("option2");
		
		BioptionMessage msg = new BioptionMessage();
		
		//load body
		if (oBody instanceof String) {
			msg.body = new FancyMessage((String) oBody);
		} else {
			msg.body = (FancyMessage) oBody;
		}
		
		//load options
		if (oOption1 instanceof String) {
			msg.option1Msg = new FancyMessage((String) oOption1);
		} else {
			msg.option1Msg = (FancyMessage) oOption1;
		}

		if (oOption2 instanceof String) {
			msg.option2Msg = new FancyMessage((String) oOption2);
		} else {
			msg.option2Msg = (FancyMessage) oOption2;
		}
		
		return msg;
	}

	@Override
	public FancyMessage getFormattedMessage() {
		return body.then("\n----------\n   -")
				.then(option1Msg).command(ChatGuiHandler.cmdBase + " " + OPTION1)
				.then("   -   ")
				.then(option2Msg).command(ChatGuiHandler.cmdBase + " " + OPTION2)
				;
	}

}
