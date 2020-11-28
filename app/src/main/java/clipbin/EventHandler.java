package clipbin;

/**
 * This interface was derived from an example by ShyJ as seen here:
 * https://stackoverflow.com/questions/13483048/creating-a-simple-event-driven-architecture
 */
public interface EventHandler {
	public void handle (Event event);
}