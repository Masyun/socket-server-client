package communicator;

import abs.command.Payload;
import abs.listener.CommandListener;
import listener.EventManager;
import model.User;
import server.CONSTANTS;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class Communicator implements Runnable {

    private final String name;
    private final Socket socket;
    private boolean running;
    private final EventManager events;
    protected User loggedIn = null;
    protected ScheduledExecutorService scheduledExecutorService =
            Executors.newScheduledThreadPool(5);
    protected boolean logging = false;

    public Communicator(Socket socket, String name) throws IOException {
        this.name = name;
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
                System.out.println("[" + name + "] event " + command + ": " + payload);
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

    public void setRunning(boolean running) {
        System.out.println("Setting communicator status: " + (running ? "running" : "stopped"));
        this.running = running;
    }

    public void setLogging(boolean logging) {
        System.out.println("Enabling logging for " + name);
        this.logging = logging;
    }

    protected ArrayList<String> parseToArray(Payload payload) {
        String content = (String) payload.get();
        return new ArrayList<String>(Arrays.asList(content.split(" ")));
    }

//    private void saveFile(Socket clientSock) throws IOException {
//        DataInputStream dis = new DataInputStream(clientSock.getInputStream());
//        FileOutputStream fos = new FileOutputStream("testfile.jpg");
//        byte[] buffer = new byte[4096];
//
//        int filesize = 15123; // Send file size in separate msg
//        int read = 0;
//        int totalRead = 0;
//        int remaining = filesize;
//        while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
//            totalRead += read;
//            remaining -= read;
//            System.out.println("read " + totalRead + " bytes.");
//            fos.write(buffer, 0, read);
//        }
//
//        fos.close();
//        dis.close();
//    }

//    public void sendFile(String file) throws IOException {
//        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//        FileInputStream fis = new FileInputStream(file);
//        byte[] buffer = new byte[4096];
//
//        while (fis.read(buffer) > 0) {
//            dos.write(buffer);
//        }
//
//        fis.close();
//        dos.close();
//    }
}
