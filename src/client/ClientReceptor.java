package client;

import abs.command.Payload;
import abs.listener.CommandListener;
import listener.EventManager;
import listener.ReceiveCommandListener;

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

    private EventManager events = new EventManager(ClientReceptor.class.getSimpleName());

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
        String mainCommand = commands.remove(0);

        Payload payload = fromStrings(commands);

        if (mainCommand != null) {
            events.notifySubscribers(mainCommand, payload);
        }
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
        events.addSubscriber(CONSTANTS.COMMAND_PREFIX, command, listener);
    }

    private void setupListeners() throws IOException {
        addListener("res_user_create", new ReceiveCommandListener(socket));
        addListener("ping", new ReceiveCommandListener(socket));
    }
}

