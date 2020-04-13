package communicator;

import abs.payload.Payload;
import abs.listener.CommandListener;
import listener.EventManager;
import model.User;
import server.CONSTANTS;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Communicator class is an abstraction that allows us to connect to sockets without giving the concrete stream source
 * This allows us to extend a class from communicator and it will have all the necessary data and behaviour to send OR receive data over a socket connection.
 * The data is processed through an event loop which fires events in the {CommandListener}'s in EventManager
 */
public abstract class Communicator implements Runnable {

    private final String name;
    private final Socket socket;
    private final Socket fileTransferSocket;
    private boolean running;
    private final EventManager events;
    protected User loggedIn = null;
    protected ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(5);
    protected boolean logging = false;

    public Communicator(Socket socket, String name) throws IOException {
        this.name = name;
        this.socket = socket;
        this.fileTransferSocket = null;
        this.events = new EventManager(name);

        attachListeners();
        setRunning(true);
    }

    public Communicator(Socket socket, String name, Socket fileTransferSocket) throws IOException {
        this.name = name;
        this.fileTransferSocket = fileTransferSocket;
        this.socket = socket;
        this.events = new EventManager(name);

        attachListeners();
        setRunning(true);
    }

    protected abstract void attachListeners() throws IOException;

    protected void addListener(String command, CommandListener listener) {
        listener.setCommand(command);
        events.addSubscriber(CONSTANTS.COMMAND_PREFIX, command, listener);
    }

    protected void notifySub(String command, Payload payload) {
        if ((!command.contains("/ping") && !command.contains("/ping_res"))
                && command.startsWith(CONSTANTS.COMMAND_PREFIX)) {
            if (logging) {
                System.err.println("[" + name + "] event " + command + ": " + payload);
            }
            events.notifySubscribers(command, payload);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    protected String getName() {
        return name;
    }

    protected boolean isRunning() {
        return running;
    }

    protected Map<String, CommandListener> getAPIListeners(){
        return events.getListeners();
    }

    protected String getAPI(){
        return "API Description for " + getName() + "\n";
    }

    public void setRunning(boolean running) {
        System.out.println("Setting communicator status: " + (running ? "running" : "stopped"));
        this.running = running;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public Socket getFileTransferSocket() {
        return fileTransferSocket;
    }

    protected ArrayList<String> parseToArray(Payload payload) {
        String content = (String) payload.get();
        return new ArrayList<String>(Arrays.asList(content.split(" ")));
    }
}
