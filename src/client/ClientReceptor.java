package client;

import abs.command.Payload;
import command.ReceiveCommand;
import factory.ReceptorFactory;
import listener.EventManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClientReceptor implements Runnable {

    private Socket socket;

    private PrintWriter outStream;
    private BufferedReader inStream;

    private EventManager events = new EventManager(ClientReceptor.class.getSimpleName(), ReceiveCommand.values());

    public ClientReceptor(Socket socket) throws IOException, NullPointerException {
        this.socket = socket;
        this.outStream = new PrintWriter(socket.getOutputStream());
        this.inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        setupListeners();
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                String message = inStream.readLine();
                ArrayList<String> commands = new ArrayList<>(Arrays.asList(message.split(" ")));
                receive(commands);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void receive(ArrayList<String> commands) {
        String mainCommand = commands.get(0);

        Payload payload = constructPayload(commands);

        if (mainCommand != null) {
            events.notifySubscribers(mainCommand, payload);
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

    private void setupListeners() throws IOException {
        try {
            for (ReceiveCommand et : ReceiveCommand.values()) {
                events.addSubscriber(CONSTANTS.COMMAND_PREFIX + et.get(), ReceptorFactory.spawnListener(et, socket));
            }
        } catch (RuntimeException | IllegalAccessException rte) {
            rte.printStackTrace();
        }
    }
}

