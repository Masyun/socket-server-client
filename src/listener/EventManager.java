package listener;

import abs.command.Command;
import abs.listener.CommandListener;
import abs.listener.Publisher;

import java.util.HashMap;
import java.util.Map;


public class EventManager implements Publisher {
    private final String identifier;
    private Map<String, CommandListener> listeners = new HashMap<>();

    public EventManager(String identifier, Command... operations) {
        for (Command operation : operations) {
            this.listeners.put(operation.get(), null);
        }

        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void addSubscriber(String operation, CommandListener listener) {
        System.out.println("Setting handler for [" + operation + "] commands on " + this.identifier);
        listeners.put(operation, listener);

    }

    @Override
    public void removeSubscriber(String operation) {
        listeners.remove(operation);
    }

    @Override
    public void notifySubscribers(String operation, String payload) {
        CommandListener listener = listeners.get(operation);
        if (listener != null) {
            listener.update(payload);
        }
    }
}
