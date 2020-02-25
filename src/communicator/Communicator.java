package communicator;

import abs.command.Payload;
import abs.listener.CommandListener;
import server.CONSTANTS;
import listener.EventManager;
import model.User;

import java.io.IOException;
import java.net.Socket;

public abstract class Communicator implements Runnable {

    private final String name;
    private final Socket socket;
    private boolean running;
    private final EventManager events;
    protected User loggedIn = null;

    public Communicator(Socket socket, String name) throws IOException {
        this.name = name;
        this.socket = socket;
        this.events = new EventManager(name);

        attachListeners();
        setRunning(true);
    }

    protected abstract void attachListeners() throws IOException;

    protected void addListener(String command, CommandListener listener){
        listener.setCommand(command);
        events.addSubscriber(CONSTANTS.COMMAND_PREFIX, command, listener);
    }

    protected void notifySub(String command, Payload payload){
        events.notifySubscribers(command, payload);
    }

    public Socket getSocket() {
        return socket;
    }

    public String getName() {
        return name;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        System.out.println("Setting communicator status: " + (running?"running":"stopped"));
        this.running = running;
    }
}
