package client;

import abs.command.Payload;
import command.SendCommand;
import factory.DispatcherFactory;
import listener.EventManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientDispatcher implements Runnable {

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
            String input = scanner.nextLine();
            ArrayList<String> commands = new ArrayList<>(Arrays.asList(input.split(" ")));
            dispatch(commands);
        }
    }

    public void dispatch(ArrayList<String> commands) {
        String mainCommand = commands.remove(0);

        Payload payload = constructPayload(commands);
        System.out.println(mainCommand + ": " + payload.get());
        if (mainCommand != null) {
            events.notifySubscribers(
                    mainCommand, payload);
        }
    }

    private Payload constructPayload(ArrayList<String> commands) {
        String payload = commands
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

//        System.out.println("Constructed payload:" + payload);
        return new Payload(payload);
    }

    private void setupListeners() {
        try {
            for (SendCommand et : SendCommand.values()) {
                events.addSubscriber(CONSTANTS.COMMAND_PREFIX + et.get(), DispatcherFactory.spawnListener(et, socket));
            }
        } catch (RuntimeException | IOException | IllegalAccessException rte) {
            rte.printStackTrace();
        }
    }
}

