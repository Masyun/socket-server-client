package listener;

import abs.command.Payload;
import abs.listener.CommandListener;
import abs.listener.Publisher;

import java.util.HashMap;
import java.util.Map;


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
//        System.out.println("Setting handler for [" + operation + "] commands on " + getIdentifier());
        listeners.put(prefix + operation, listener);

    }

    @Override
    public void removeSubscriber(String operation) {
        listeners.remove(operation);
    }

    public Map<String, CommandListener> getListeners() {
        return listeners;
    }

    @Override
    public void notifySubscribers(String operation, Payload payload) {
//        System.out.println("[handler" + operation + "] - " + payload);
        CommandListener listener = listeners.get(operation);
        if (listener != null) {
//            System.out.println("[handler" + operation + "] - " + payload);
            try{
                listener.update(payload);
            }catch (IndexOutOfBoundsException ioobe){
                ioobe.printStackTrace();
            }
        }
    }
}
