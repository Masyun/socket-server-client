package client;

import abs.command.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;
import listener.GenericSender;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ClientDispatcher extends Communicator {

    private Scanner scanner;
    private final PrintWriter req;

    public ClientDispatcher(Socket socket, String name) throws IOException, NullPointerException {
        super(socket, name);
        this.scanner = new Scanner(System.in);
        req = new PrintWriter(getSocket().getOutputStream());
    }

    @Override
    protected void attachListeners() throws IOException {
        addListener("help",
                new GenericSender(this));
        addListener("register",
                new GenericSender(this));
        addListener("user",
                new GenericSender(this));
        addListener("users",
                new GenericSender(this));
        addListener("dm",
                new GenericSender(this));
        addListener("file_init",
                new CommandListener() {
                    @Override
                    public void update(Payload payload) {

                    }
                });
        addListener("file_accept",
                new GenericSender(this));
        addListener("group_create",
                new GenericSender(this));
        addListener("groups",
                new GenericSender(this));
        addListener("group_join",
                new GenericSender(this));
        addListener("group_message",
                new GenericSender(this));
        addListener("group_leave",
                new GenericSender(this));
        addListener("group_kick",
                new GenericSender(this));
        addListener("logout", new CommandListener() {
            @Override
            public void update(Payload payload) {
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
    protected void saveFile(Socket socket, String filename) throws IOException {

    }

    @Override
    protected void sendFile(String file) throws IOException {

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
}

class FileInitiator implements Runnable {
    private String recipient;
    private String filename;
    private int fileSize;
    private File fileBlob;

    public FileInitiator(String recipient, String filename, int fileSize) {
        this.recipient = recipient;
        this.filename = filename;
        this.fileSize = fileSize;
    }

    @Override
    public void run() {

    }
}

