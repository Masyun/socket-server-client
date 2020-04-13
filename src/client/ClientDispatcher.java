package client;

import abs.command.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;
import fileOrchestration.FileInitiator;
import listener.GenericSender;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientDispatcher
        extends Communicator {

    private Scanner scanner;
    private final PrintWriter req;
    private FileInitiator fi;


    public ClientDispatcher(Socket socket, String name) throws IOException, NullPointerException {
        super(socket, name,
                new Socket(CONSTANTS.SERVER_ADDRESS, CONSTANTS.SERVER_PORT + 1));

        System.out.println("ClientDispatcher connected to file transfer socket: " + getFileTransferSocket().getLocalSocketAddress().toString());
        this.scanner = new Scanner(System.in);
        req = new PrintWriter(getSocket().getOutputStream());
    }

    @Override
    protected void attachListeners() throws IOException {
        addListener("help",
                new CommandListener(null, "Displays the available API") {
                    @Override
                    public void update(Payload payload) throws IndexOutOfBoundsException {
                        System.out.println(getAPI());
                    }
                });
        addListener("register",
                new GenericSender(this, "[username] [password]", "Registers a user and also logs them into the system"));
        addListener("user",
                new GenericSender(this, null, "Displays the currently logged in user"));
        addListener("users",
                new GenericSender(this, null, "Displays a list of all users"));
        addListener("dm",
                new GenericSender(this, "[username] [message]", "Sends a DM to the specified user"));
        addListener("file_init",
                new CommandListener("[recipient] [file name]", "Transfers a file to the server for further transfer to a receiving client - if they accept") {
                    @Override
                    public void update(Payload payload) throws IndexOutOfBoundsException {
                        ArrayList<String> parameters = parseToArray(payload);
                        String recipient = parameters.get(1);
                        String fileName = parameters.get(2);

                        System.out.println(parameters);

                        req.println(payload.get());
                        req.flush();
                        fi = new FileInitiator(getFileTransferSocket(), "./" + fileName);
                        fi.start();
                    }
                });
        addListener("file_accept",
                new CommandListener("[file id]", "Accepts the specified file id for transfer - recipient is prompted to accept file") {
                    @Override
                    public void update(Payload payload) throws IndexOutOfBoundsException {
//                        try {
//                            String fileName =
//                            fs = new FileStorer(getFileTransferSocket(), "recipient_inbox/" + fileName);
//                        }
                    }
                });
        addListener("group_create",
                new GenericSender(this, "[group name]", "Creates a group with the given name"));
        addListener("groups",
                new GenericSender(this, null, "Displays a list of available groups and their members"));
        addListener("group_join",
                new GenericSender(this, "[group name]", "Adds the user to the specified group"));
        addListener("group_message",
                new GenericSender(this, "[group name] [message]", "Sends the message to everybody in the group"));
        addListener("group_leave",
                new GenericSender(this, "[group name]", "Leaves the specified group"));
        addListener("group_kick",
                new GenericSender(this, "[group name] [user to kick]", "Kicks the specified user from the group - need to be admin of group to do"));
        addListener("logout", new CommandListener(null,
                "Logs out the user and closes the connection to the back-end by sending the appropriate signals and cleanup process of the database/state") {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                try {
                    req.println(CONSTANTS.COMMAND_PREFIX + command);
                    req.flush();
                    getSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void run() {
        while (!getSocket().isClosed() && isRunning()) {
            String input = scanner.nextLine();
            ArrayList<String> commands = new ArrayList<>(Arrays.asList(input.split(" ")));
            dispatch(commands);
        }

        System.out.println("Disconnecting " + getName());
    }

    public void dispatch(ArrayList<String> commands) {
        String mainCommand = commands.get(0);

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

        return new Payload<>(payload);
    }

    @Override
    public String getAPI() {
        StringBuilder sb = new StringBuilder();
        for (CommandListener listener : getAPIListeners().values()) {
            sb.append(CONSTANTS.COMMAND_PREFIX);
            sb.append(listener.getCommand());
            sb.append("\n");
            sb.append(" - ");
            sb.append(listener.getParams() != null ? listener.getParams() : "");
            sb.append(" => ");
            sb.append(listener.getDescription());
            sb.append("\n\n");
        }
        return sb.toString();
    }
}

