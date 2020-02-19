package client;

import abs.command.Payload;
import abs.listener.CommandListener;
import listener.EventManager;
import listener.SendCommandListener;

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

    private EventManager events = new EventManager(ClientDispatcher.class.getSimpleName());


    public ClientDispatcher(Socket socket) throws IOException, NullPointerException {
        this.socket = socket;
        this.stream = new PrintWriter(socket.getOutputStream());
        this.scanner = new Scanner(System.in);

        setupListeners();
    }

    @Override
    public void run() {
        dispatch(new ArrayList<>(Arrays.asList("/user_create whaddup bitch".split(" "))));

        while (!socket.isClosed()) {
            String input = scanner.nextLine();
            ArrayList<String> commands = new ArrayList<>(Arrays.asList(input.split(" ")));
            dispatch(commands);
        }
    }

    public void dispatch(ArrayList<String> commands) {
        String mainCommand = commands.remove(0);

        Payload payload = fromStrings(commands);
        System.out.println(mainCommand + ": " + payload.get());

        events.notifySubscribers(
                mainCommand, payload);

    }

    private Payload<String> fromStrings(ArrayList<String> commands) {
        String payload = commands
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

//        System.out.println("Constructed payload:" + payload);
        return new Payload<>(payload);
    }

    public void addListener(String command, CommandListener listener){
        listener.setCommand(command);
        events.addSubscriber(CONSTANTS.COMMAND_PREFIX, command, listener);
    }

    private void setupListeners() throws IOException {
        addListener("user_create", new SendCommandListener(socket));
        addListener("pong", new SendCommandListener(socket));
    }
}

