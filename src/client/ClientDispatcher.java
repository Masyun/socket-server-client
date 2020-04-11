package client;

import AES.AES;
import abs.command.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;
import listener.GenericSender;

import java.io.*;
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
                new CommandListener() {
                    @Override
                    public void update(Payload payload) {

                    }
                });
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
                        ArrayList<String> parameters = parseToArray(payload);
                        String recipient = parameters.get(1);
                        String fileName = parameters.get(2);

                        System.out.println(parameters);

                        try {
                            req.println(payload.get());
                            req.flush();
                            new FileInitiator(recipient, fileName, getSocket()).sendFile();

                            System.out.println("Sending file to server");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


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

        payload = AES.encrypt(payload, CONSTANTS.SECRET);
        return new Payload<>(payload);
    }

    class FileInitiator {
        private String recipient;
        private String filename;
        private long fileSize;
        private File fileBlob;

        private DataOutputStream dos;
        private FileInputStream fis;
        private byte[] buffer;

        public FileInitiator(String recipient, String filename, Socket serverSocket) throws IOException {
            this.recipient = recipient;
            this.filename = filename;
            this.fileBlob = new File("./" + filename);
            this.fileSize = fileBlob.length() + 1;

            this.dos = new DataOutputStream(serverSocket.getOutputStream());
            this.fis = new FileInputStream(fileBlob);
            this.buffer = new byte[4096];

        }

        public void sendFile() {
            try {
                int read;
                while ((read = fis.read(buffer)) > 0) {
                    dos.write(buffer, 0, read);
                }
                dos.flush();
                fis.close();
//                fis.close();
//                dos.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        public String getRecipient() {
            return recipient;
        }

        public String getFilename() {
            return filename;
        }

        public long getFileSize() {
            return fileSize;
        }

        public File getFileBlob() {
            return fileBlob;
        }
    }
}

