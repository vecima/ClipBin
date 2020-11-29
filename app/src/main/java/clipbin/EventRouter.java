package clipbin;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class was derived from an example by ShyJ as seen here:
 * https://stackoverflow.com/questions/13483048/creating-a-simple-event-driven-architecture
 */
public class EventRouter {
	//null key in this map is for handlers of every type of event.
	private final Map<EventType, List<EventHandler>> handlerMap = new HashMap<EventType, List<EventHandler>> ();

	public void sendEvent (final Event event) {
		if (this.handlerMap.containsKey(event.getEventType()))
			this.handlerMap.get (event.getEventType()).forEach(eventHandler -> eventHandler.handle(event));
		if (this.handlerMap.containsKey(null))
			this.handlerMap.get (null).forEach(eventHandler -> eventHandler.handle(event));
	}

	public void registerHandler (final EventType eventType, final EventHandler handler) {
		List<EventHandler> handlerList = this.handlerMap.get(eventType);
		if (handlerList == null) {
			handlerList = new ArrayList<EventHandler>();
			this.handlerMap.put(eventType, handlerList);
		}
		handlerList.add(handler);
	}

	public void unRegisterHandler (final EventType eventType, final EventHandler handler) {
		List<EventHandler> handlerList = this.handlerMap.get(eventType);
		if (handlerList != null) {
			handlerList.remove(handler);
		}
	}
}