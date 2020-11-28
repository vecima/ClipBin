package clipbin;

public class Event {
	private EventType eventType;
	private Clip clip;

	public Event(EventType eventType, Clip clip) {
		this.eventType = eventType;
		this.clip = clip;
	}

	public EventType getEventType() {
		return this.eventType;
	}

	public Clip getClip() {
		return this.clip;
	}
}