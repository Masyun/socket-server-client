package server;

import abs.command.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;
import model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ServerReceptor extends Communicator {

    private final BufferedReader req;
    private final PrintWriter res;

    public ServerReceptor(Socket socket, String name) throws IOException {
        super(socket, name);
        req = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        res = new PrintWriter(getSocket().getOutputStream());
    }

    @Override
    protected void attachListeners() throws IOException {
        addListener("register", new CommandListener() {
            @Override
            public void update(Payload payload) {
                System.out.println(command + " received: " + payload.get());

                String username = null;
                String password = null;

                Database.getInstance().insertUser(new User("test", "test", getSocket()));
                User user = Database.getInstance().getUser("test");
                res.println(user);
                res.flush();
            }
        });

        addListener("pong", new CommandListener() {
            @Override
            public void update(Payload payload) {
                System.out.println("Received pong");
            }
        });
//        addListener("", new CommandListener() {
//            @Override
//            public void update(Payload payload) {
//                System.out.println(command + " received: " + payload.get());
//                System.out.println("Preparing response: 200");
//                res.println("/200");
//                res.flush();
//            }
//        });
    }

    @Override
    public void run() {
        while (!getSocket().isClosed() && isRunning()) {
            try {
                String message = req.readLine();
                System.out.println("FROM CLIENT: " + message);

                // Print message
                if (message != null && !message.isEmpty()) {
                    receive(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void receive(String message) {
        ArrayList<String> commands = new ArrayList<>(Arrays.asList(message.split(" ")));
        String mainCommand = commands.remove(0);
//        notifySub(mainCommand, new Payload<String>(message));
        Payload payload = fromStrings(commands);

        notifySub(
                mainCommand,
                payload);
//        res.println("/200");
//        res.flush();
    }

    private Payload<String> fromStrings(ArrayList<String> commands) {
        String payload = commands
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

//        System.out.println("Constructed payload:" + payload);
        return new Payload<>(payload);
    }
}
