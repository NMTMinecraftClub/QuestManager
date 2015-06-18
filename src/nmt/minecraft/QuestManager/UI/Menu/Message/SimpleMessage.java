package nmt.minecraft.QuestManager.UI.Menu.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nmt.minecraft.QuestManager.Fanciful.FancyMessage;

/**
 * Wraps arounds a simple, single -use- message.<br />
 * This can contain multiple FancyMessage's, but doesn't provide
 * any functionallity other than displaying the message.
 * @author Skyler
 *
 */
public class SimpleMessage extends Message {

	private Set<FancyMessage> messages;
	
	private FancyMessage label;
	
	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		
		List<FancyMessage> msgs = new ArrayList<FancyMessage>(messages);
		
		map.put("text", msgs);
		
		return map;
	}
	
	public static SimpleMessage valueOf(Map<String, Object> map) {
		Object obj = map.get("text");
		
		SimpleMessage msg = new SimpleMessage();
		
		if (obj instanceof List) {
			@SuppressWarnings("unchecked")
			List<Object> lobj = (List<Object>) map.get("text"); 
			
			if (lobj.isEmpty()) {
				//return empty simple message
				return msg;
			}
			
			for (Object o : lobj) {
				//check if it's a string or a fancy message, and parse as such
				if (o instanceof FancyMessage) {
					msg.messages.add((FancyMessage) o);
					continue;
				}
				
				//assume it's a string!?
				msg.messages.add(new FancyMessage((String) o));
				
			}
			
			return msg;
		} 
		
		//assume it's just a simple string or fancy message then
		
		if (obj instanceof FancyMessage) {
			msg.messages.add((FancyMessage) obj);
			return msg;
		}
		
		//else just assume it's a string!?
		msg.messages.add(new FancyMessage((String) obj));
		return msg;
	}

	
	
	private SimpleMessage() {
		this.messages = new HashSet<FancyMessage>();
	}
	
	@Override
	public void setSourceLabel(FancyMessage label) {
		this.label = label;
	}

	@Override
	public FancyMessage getFormattedMessage() {

		
		
		
		
	}
	
	
	
}
