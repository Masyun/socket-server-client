package abs.listener;

import abs.command.Payload;

public interface Publisher {
    void addSubscriber(String prefix, String operation, CommandListener listener);
    void removeSubscriber(String operation);
    void notifySubscribers(String operation, Payload payload);
}
