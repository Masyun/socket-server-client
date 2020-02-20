package client;

import abs.command.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ClientReceptor extends Communicator {

    private final BufferedReader res;
    private final PrintWriter req;
    public ClientReceptor(Socket socket, String name) throws IOException {
        super(socket, name);
        res = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        req = new PrintWriter(getSocket().getOutputStream());
    }

    @Override
    protected void attachListeners() throws IOException {
        addListener("ping", new CommandListener() {
            @Override
            public void update(Payload payload) {
                req.println("/pong");
                req.flush();
            }
        });
    }

    @Override
    public void run() {
        while (!getSocket().isClosed() && isRunning()) {
            try {
                // Read message from server
                String message = res.readLine();
                System.out.println("FROM SERVER: " + message);
                // Print message
                if (message != null && !message.isEmpty()) {
                    receive(message);
                }
//                    if (message.equals("/ping")) {
//                        serverInput.println("/pong");
//                        serverInput.flush();
//                    } else {
//                        System.out.println(message);
//                    }
//                }
//            } catch (SocketException se) {
//                System.out.println("Server has disconnected...");
//                break;
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//            }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    System.out.println("Closing socket...");
                    getSocket().close();
                    System.out.println("Socket closed! :)");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        System.out.println("Client receiver is closed!");
    }

    public void receive(String message) {
        ArrayList<String> commands = new ArrayList<>(Arrays.asList(message.split(" ")));
        String mainCommand = commands.remove(0);
        Payload payload = fromStrings(commands);

        notifySub(
                mainCommand,
                payload);
    }

    private Payload<String> fromStrings(ArrayList<String> commands) {
        String payload = commands
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

//        System.out.println("Constructed payload:" + payload);
        return new Payload<>(payload);
    }


//    private Socket socket;
//
//    private BufferedReader inStream;
//
//    private EventManager events = new EventManager(ClientReceptor.class.getSimpleName());

//    public ClientReceptor(Socket socket) throws IOException, NullPointerException {
//        this.socket = socket;
//        this.inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//        setupListeners();
//    }
//
//    @Override
//    public void run() {
//        while (!socket.isClosed()) {
//            try {
//                String message = inStream.readLine();
//                ArrayList<String> commands = new ArrayList<>(Arrays.asList(message.split(" ")));
//                receive(commands);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    public void receive(ArrayList<String> commands) {
//        String mainCommand = commands.remove(0);
//
//        Payload payload = fromStrings(commands);
//
//
//
//        System.out.println(events.getIdentifier() + ": " + mainCommand + " received " + payload.get().toString());
//
////        if (mainCommand != null) {
////            events.notifySubscribers(mainCommand, payload);
////        }
//    }
//
//    private Payload<String> fromStrings(ArrayList<String> commands) {
//        String payload = commands
//                .stream()
//                .map(String::valueOf)
//                .collect(Collectors.joining(" "));
//
////        System.out.println("Constructed payload:" + payload);
//        return new Payload<>(payload);
//    }
//
//    @Override
//    protected void attachListeners() throws IOException {
//
//    }
//
//    public void addListener(String command, CommandListener listener){
//        events.addSubscriber(CONSTANTS.COMMAND_PREFIX, command, listener);
//    }
//
//    private void setupListeners() throws IOException {
////        addListener("res_user_create", new Receiver(this));
////        addListener("ping", new Receiver(this));
//    }
}

