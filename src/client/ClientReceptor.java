package client;

import abs.command.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;
import fileOrchestration.FileStorer;
import listener.GenericReceiver;

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
    private FileStorer fs;

    public ClientReceptor(Socket socket, String name) throws IOException {
        super(socket, name, new Socket(CONSTANTS.SERVER_ADDRESS, CONSTANTS.SERVER_PORT + 1));
        res = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        req = new PrintWriter(getSocket().getOutputStream());
    }

    @Override
    protected void attachListeners() throws IOException {
        addListener("server", new CommandListener() {
            @Override
            public void update(Payload payload) {
                System.out.println(payload.get());
            }
        });
        addListener("ping", new CommandListener() {
            @Override
            public void update(Payload payload) {
                req.println(CONSTANTS.COMMAND_PREFIX + resCommand);
                req.flush();
            }
        });
        addListener("logout_res", new CommandListener() {
            @Override
            public void update(Payload payload) {
                try {
                    getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        addListener("server_res", new GenericReceiver(this));
        addListener("register_res", new GenericReceiver(this));
        addListener("users_res", new GenericReceiver(this));
        addListener("dm_res", new GenericReceiver(this));
        addListener("file_init_res", new GenericReceiver(this));
//        addListener("file_receive", null);
        addListener("group_create_res", new GenericReceiver(this));
        addListener("group_message_res", new GenericReceiver(this));
        addListener("groups_res", new GenericReceiver(this));
        addListener("group_join_res", new GenericReceiver(this));
        addListener("group_message_res", new GenericReceiver(this));
        addListener("group_leave_res", new GenericReceiver(this));
        addListener("group_kick_res", new GenericReceiver(this));
    }

    @Override
    public void run() {
        while (!getSocket().isClosed() && isRunning()) {
            try {
                // Read message from server
                String message = res.readLine();

                // Print message
                if (message != null && !message.isEmpty()) {
//                    if (!message.contains("ping")) {
//                        System.out.println("FROM SERVER: " + message);
//                    }


                    receive(message);
                }
                System.out.print(">");
            } catch (IOException e) {
                try {
                    System.out.println("Closing socket...");
                    getSocket().close();
                    setRunning(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        System.out.println("Disconnecting " + getName());
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
}

