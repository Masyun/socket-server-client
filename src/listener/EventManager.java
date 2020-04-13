package listener;

import abs.payload.Payload;
import abs.listener.CommandListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Observable/Publisher container class - is responsible for the event loop
 */
public class EventManager implements Publisher {
    private final String identifier;
    private Map<String, CommandListener> listeners = new HashMap<>();

    public EventManager(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void addSubscriber(String prefix, String operation, CommandListener listener) {
        listeners.put(prefix + operation, listener);

    }

    @Override
    public void removeSubscriber(String operation) {
        listeners.remove(operation);
    }

    public Map<String, CommandListener> getListeners() {
        return listeners;
    }

    /**
     * Notify the corresponding listener and invoke the {update()} call
     * @param operation
     * @param payload
     */
    @Override
    public void notifySubscribers(String operation, Payload payload) {
        CommandListener listener = listeners.get(operation);
        if (listener != null) {
            try{
                listener.update(payload);
            }catch (IndexOutOfBoundsException ioobe){
                System.err.println("Invalid parameters supplied! StackTrace:");
                ioobe.printStackTrace();
            }
        }
    }
}
