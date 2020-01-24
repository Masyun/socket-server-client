package client;

import factory.DispatcherFactory;
import listener.EventManager;
import command.SendCommand;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientDispatcher extends Thread {

    private Socket socket;

    private PrintWriter stream;
    private Scanner scanner;

    private EventManager events = new EventManager(ClientDispatcher.class.getSimpleName(), SendCommand.values());


    public ClientDispatcher(Socket socket) throws IOException, NullPointerException {
        this.socket = socket;
        this.stream = new PrintWriter(socket.getOutputStream());
        this.scanner = new Scanner(System.in);

        setupListeners();
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            String input = scanner.next();
            String[] commands = input.split(" ");

            dispatch(commands);
        }
    }

    public void dispatch(String[] commands) {
        String mainCommand = commands[0];

        if (mainCommand != null) {
            events.notifySubscribers(mainCommand, "Test payload");
        }
    }

    private void setupListeners() {
        try {
            for (SendCommand et : SendCommand.values()) {
                events.addSubscriber('/' + et.get(), DispatcherFactory.spawnListener(et, socket));
            }
        } catch (RuntimeException | IOException | IllegalAccessException rte) {
            rte.printStackTrace();
        }
    }

    public PrintWriter getOut() {
        return stream;
    }

    public void setOut(PrintWriter stream) {
        this.stream = stream;
    }

    public EventManager getEvents() {
        return events;
    }

    public void setEvents(EventManager events) {
        this.events = events;
    }
}

