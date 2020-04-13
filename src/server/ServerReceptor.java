package server;

import abs.payload.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;
import fileOrchestration.FileInitiator;
import fileOrchestration.FileStorer;
import model.Group;
import model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * ServerReceptor is responsible for received data from a client to the server
 * It processes this data by doing IO operations, database operations, scheduling tasks and forwarding data to other clients
 */
public class ServerReceptor extends Communicator {

    private final BufferedReader req;
    private final PrintWriter res;
    private FileStorer fs;
    private FileInitiator fi;

    private AtomicInteger lastFileId = new AtomicInteger(1);
    private ArrayList<String> fileRequestNames = new ArrayList<>();
    private BlockingQueue<TransferRequest> queue = new ArrayBlockingQueue<>(1024);

    private static HashMap<String, Boolean> fileRequestResponses = new HashMap<>();

    private ArrayList<ScheduledFuture<Boolean>> fileRequestTasks = new ArrayList<>();
    private boolean pong;


    public ServerReceptor(Socket socket, String name) throws IOException {
        super(socket, name);
        req = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        res = new PrintWriter(getSocket().getOutputStream());
    }

    public ServerReceptor(Socket socket, String name, Socket fileTransferSocket) throws IOException {
        super(socket, name, fileTransferSocket);
        System.out.println("Server receptor connected to file transfer socket: " + getFileTransferSocket());
//        fs = new FileStorer(getFileTransferSocket());
        req = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        res = new PrintWriter(getSocket().getOutputStream());
    }

    @Override
    protected void attachListeners() {
//        addListener("help", new CommandListener() {
//            @Override
//            public void update(Payload payload) throws IndexOutOfBoundsException  {
//                String api = getEventsDescription();
//                res.println("/server " + api);
//                res.flush();
//            }
//        });
        addListener("ping_res", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                /**
                 * PingPong
                 */
                User user = Database.getInstance().getUserBySocket(getSocket());
                if (user != null) {
                    Database.getInstance().getUserBySocket(getSocket()).setPong(true);
                }
            }
        });
        addListener("register", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                ArrayList<String> parameters = parseToArray(payload);

                String username = parameters.remove(0);
                String password = parameters.remove(0);

                if (loggedIn == null) {
                    Database.getInstance().insertUser(new User(username, password, getSocket(), getFileTransferSocket()));
                    loggedIn = Database.getInstance().getUser(username);
                    res.println(CONSTANTS.COMMAND_PREFIX + "server " + "Created user  " + loggedIn);
                    res.flush();
                } else {
                    loggedIn = Database.getInstance().getUser(username);
                    res.println(CONSTANTS.COMMAND_PREFIX + "server " + "User already logged in " + loggedIn);
                    res.flush();
                }
            }
        });
        addListener("user", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                if (!isAuth()) {
                    return;
                }

                res.println("/server " + Database.getInstance().getUser(loggedIn.getUsername()));
                res.flush();
            }
        });
        addListener("users", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                Vector<User> users = Database.getInstance().getUsers();
                res.println(CONSTANTS.COMMAND_PREFIX + resCommand + users.toString());
                res.flush();
            }
        });
        addListener("dm", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                if (!isAuth()) {
                    return;
                }
                ArrayList<String> parameters = parseToArray(payload);

                String usernameToDm = parameters.remove(0);
                String message = parameters.remove(0);

                try {
                    PrintWriter res = new PrintWriter(Database.getInstance().getUser(usernameToDm).getSocket().getOutputStream());
                    res.println(CONSTANTS.COMMAND_PREFIX + resCommand + loggedIn.getUsername() + ": " + message);
                    res.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    res.println("/server dm sent");
                    res.flush();
                }
            }
        });
        addListener("file_init", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                if (!isAuth()) {
                    return;
                }
                // recipient name
                // file name
                ArrayList<String> parameters = parseToArray(payload);
                String recipientName = parameters.remove(0);
                String fileName = parameters.remove(0);


                User recipient = Database.getInstance().getUser(recipientName);

                if (recipient != null) {
                    System.out.println("recipient found: " + recipient);
                    fs = new FileStorer(getFileTransferSocket(), "file_buffer/" + fileName);
                    fs.start();

                    final int fileId = lastFileId.getAndIncrement();

                    try {
                        queue.put(new TransferRequest(
                                fileId,
                                Database.getInstance().getUser(loggedIn.getUsername()),
                                Database.getInstance().getUser(recipient.getUsername()),
                                "file_buffer/" + fileName));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    fileRequestResponses.put(fileName, false);
                    fileRequestNames.add(fileName);

                    /**
                     * Schedule a task for X seconds in the future:
                     * If the recipient responded to the file transfer request, do the file sending
                     * if recipient didn't respond, dont do anything and cancel/remove the file
                     */
                    ScheduledFuture<Boolean> fileTask;
                    fileTask = scheduledExecutorService.schedule(
                            () -> {
                                if (!queue.isEmpty()) {
                                    TransferRequest req = queue.take();
                                    System.out.println("Found transfer request: " + req);
                                    fi = new FileInitiator(req.getReceiver().getSocket(), req.getFileName());
                                }
//                                boolean accepted = fileRequestResponses.get(fileName);
                                System.out.println("[TASK] FILE SUCCESS CHECK: " + fileRequestResponses.get(fileName));

                                if (!queue.isEmpty()) {
                                    TransferRequest req = queue.take();
                                    fi = new FileInitiator(req.getReceiver().getSocket(), req.getFileName());
                                    fi.start();
                                }
                                return true;
                            },
                            10,
                            TimeUnit.SECONDS);

                    fileRequestTasks.add(fileTask);
                    res.println(CONSTANTS.COMMAND_PREFIX + resCommand + loggedIn.getUsername() + " wants to send you file: " + fileName);
                    res.println(CONSTANTS.COMMAND_PREFIX + resCommand + "reply with [/file_accept " + fileId + "] to accept file");
                    res.flush();
                }
            }
        });
        addListener("file_accept", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                ArrayList<String> parameters = parseToArray(payload);
                int fileReqId = Integer.parseInt(parameters.remove(0)) - 1;
                String fileName = fileRequestNames.get(fileReqId);

                System.out.println("parsed id param: " + fileReqId);
                System.out.println("filename: " + fileName);

                if (fileName == null) {
                    fileRequestTasks.get(fileReqId).cancel(false);
                    System.err.println("Recipient provided wrong id");
                    res.println("/server file transfer interrupted");
                    res.flush();
                    return;
                }
                fileRequestResponses.put(fileName, true);
                System.out.println("Received file confirmation for file " + fileName);


                res.println("/server " + fileName);
                res.flush();

            }
        });
        addListener("file_decline", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                if (!isAuth()) {
                    return;
                }
                ArrayList<String> parameters = parseToArray(payload);
                int fileReqId = Integer.parseInt(parameters.remove(0)) - 1;
                String fileName = fileRequestNames.get(fileReqId);
                if (fileName != null) {
                    fileRequestTasks.get(fileReqId).cancel(true);
                    System.out.println("Recipient declined file transfer");
                    res.println("/server file transfer declined");
                    res.flush();
                }

            }
        });
        addListener("group_create", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                if (!isAuth()) {
                    return;
                }
                ArrayList<String> parameters = parseToArray(payload);

                String groupName = parameters.remove(0);
                String adminName = loggedIn.getUsername();
                User admin = Database.getInstance().getUser(adminName);

                Group group = new Group(groupName);
                group.addUser(loggedIn);
                group.setAdministrator(admin);

                Database.getInstance().createGroup(group);
                res.println("/server group created");
                res.flush();
            }
        });
        addListener("groups", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                Vector<Group> groupVector = Database.getInstance().getGroups();
                res.println(CONSTANTS.COMMAND_PREFIX + resCommand + groupVector.toString());
                res.flush();
            }
        });

        addListener("group_join", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                if (!isAuth()) {
                    return;
                }
                System.out.println("Group join request by " + loggedIn);
                ArrayList<String> parameters = parseToArray(payload);
                String groupName = parameters.remove(0);
                Database.getInstance().getGroup(groupName).addUser(loggedIn);
//                Database.getInstance().addUserToGroup(groupName, loggedIn);
                res.println("/server group joined");
                res.flush();
            }
        });
        addListener("group_message", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                if (!isAuth()) {
                    return;
                }
                ArrayList<String> parameters = parseToArray(payload);
                Group groupToMsg = Database.getInstance().getGroup(parameters.remove(0));

                if (groupToMsg != null) {
                    groupToMsg.getUsersInGroup().forEach(u -> {
                        try {
                            PrintWriter res = new PrintWriter(u.getSocket().getOutputStream());
                            res.println(CONSTANTS.COMMAND_PREFIX + resCommand + groupToMsg.getName() + " | " + loggedIn.getUsername() + ": " + parameters.remove(0));
                            res.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        });
        addListener("group_leave", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                if (!isAuth()) {
                    return;
                }
                ArrayList<String> parameters = parseToArray(payload);
                String groupName = parameters.remove(0);

                Group group = Database.getInstance().getGroup(groupName);

                if (group != null) {
                    if (Database.getInstance().removeUserFromGroup(groupName, loggedIn)) {
                        System.out.println(loggedIn.getUsername() + " has left group " + groupName);
                        System.out.println(Database.getInstance().getGroup(groupName).getUsersInGroup());
                        receive(CONSTANTS.COMMAND_PREFIX + "group_message " + group.getName() + " | " + loggedIn.getUsername() + " has left group");

                        res.println(CONSTANTS.COMMAND_PREFIX + resCommand + "You have left " + group.getName());
                        res.flush();
                    }
                }
            }
        });
        addListener("group_kick", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                if (!isAuth()) {
                    return;
                }
                ArrayList<String> parameters = parseToArray(payload);

                String groupName = parameters.remove(0);
                String usernameToKick = parameters.remove(0);


                if ((Database.getInstance().getGroup(groupName) != null) && Database.getInstance().getGroup(groupName).getAdministrator().equals(loggedIn)) { // user issuing kick payload is admin of this group
                    User userToKick = Database.getInstance().getUser(usernameToKick);
                    Database.getInstance().getGroup(groupName).removeUser(userToKick);
                    res.print(CONSTANTS.COMMAND_PREFIX + resCommand + loggedIn.getUsername() + " kicked " + usernameToKick + " from group " + groupName);
                    res.flush();
                }
//                Vector<Group> groups = Database.getInstance().getGroups();
//
//                groups.forEach(group -> {
//                    if (group.getName().equalsIgnoreCase(groupName)){
//                        if (group.getAdministrator().equals(loggedIn)){
//                            User userToKick = Database.getInstance().getUser(usernameToKick);
//                            Database.getInstance().getGroup(groupName).removeUser(userToKick);
//                            res.print(loggedIn.getUsername() + " kicked " + usernameToKick + " from group " + groupName);
//                            res.flush();
//                        }
//                    }
//                });
            }
        });
        addListener("logout", new CommandListener() {
            @Override
            public void update(Payload payload) throws IndexOutOfBoundsException {
                try {
                    Database.getInstance().logoutUser(getSocket());
                    setRunning(false);
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
//            System.out.println(fileRequestTasks);
            try {

                /**
                 * if Task queue is empty, readLine
                 */
                String message = req.readLine();

                if (message != null && !message.isEmpty()) {
                    receive(message);
                }

            } catch (IOException e) {
                try {
                    System.out.println("Closing connection " + getName() + "...");
                    setRunning(false);
                    getSocket().close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        System.out.println("Terminating receptor thread" + getName());
    }

    private void receive(String message) {
        if (message != null && !message.isEmpty()) {
            ArrayList<String> commands = new ArrayList<>(Arrays.asList(message.split(" ")));
            String mainCommand = commands.remove(0);
//        notifySub(mainCommand, new Payload<String>(message));
            Payload<String> payload = fromStrings(commands);

            notifySub(
                    mainCommand,
                    payload);
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

    private boolean isAuth() {
        if (loggedIn == null) {
            res.println("FORBIDDEN");
            res.flush();
            return false;
        }

        return true;
    }

    @Override
    public String getAPI() {
        StringBuilder sb = new StringBuilder();
        sb.append("API STUB");
        for (CommandListener listener : getAPIListeners().values()) {
//            sb.append(CONSTANTS.COMMAND_PREFIX);
//            sb.append(listener.getCommand());
//            sb.append(" - ");
//            sb.append(listener.)
        }
        return sb.toString();
    }

    public void setPong(boolean pong) {
        this.pong = pong;
    }

    public boolean getPong() {
        return pong;
    }

//    class FileStorer {
//
//        private Socket socket;
//        private String fileName;
//        private DataInputStream dis;
//        private FileOutputStream fos;
//
//        public FileStorer(String fileName) {
//            this.fileName = fileName;
//            try {
//                this.dis = new DataInputStream(getSocket().getInputStream());
//                this.fos = new FileOutputStream(fileName);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void saveFile() throws IOException {
//            try {
//                byte[] buffer = new byte[4096];
//
//                int filesize = 15123; // Send file size in separate msg
//                int read = 0;
//                int totalRead = 0;
//                int remaining = filesize;
//                while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
//                    totalRead += read;
//                    remaining -= read;
//                    System.out.println("read " + totalRead + " bytes.");
//                    fos.write(buffer, 0, read);
//                }
//            } catch (IOException ioe) {
//                ioe.printStackTrace();
//                System.err.println("File save procedure corrupted");
//            }finally {
//                fos.flush();
//                fos.close();
//                dis.close();
//            }
//
//        }
//
//        public String getFileName() {
//            return fileName;
//        }
//    }


}
