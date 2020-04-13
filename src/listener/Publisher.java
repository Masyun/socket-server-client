package listener;

import abs.listener.CommandListener;
import abs.payload.Payload;

/**
 * Standard Publisher/Observable pattern
 */
public interface Publisher {
    void addSubscriber(String prefix, String operation, CommandListener listener);
    void removeSubscriber(String operation);
    void notifySubscribers(String operation, Payload payload);
}
