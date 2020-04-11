package server;

import AES.AES;
import abs.command.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;
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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ServerReceptor extends Communicator {

    private final BufferedReader req;
    private final PrintWriter res;

    private AtomicInteger lastFileId = new AtomicInteger(1);
    private ArrayList<String> fileRequestNames = new ArrayList<>();
    private HashMap<String, Boolean> fileRequestResponses = new HashMap<>();
    private ArrayList<ScheduledFuture<Boolean>> fileRequestTasks = new ArrayList<>();


    public ServerReceptor(Socket socket, String name) throws IOException {
        super(socket, name);
        req = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
        res = new PrintWriter(getSocket().getOutputStream());
    }

    @Override
    protected void attachListeners() {
//        addListener("help", new CommandListener() {
//            @Override
//            public void update(Payload payload) {
//                String api = getEventsDescription();
//                System.out.println(">>>>>>>>>\n" + api);
//                res.println("/server " + api);
//                res.flush();
//            }
//        });
        addListener("ping_res", new CommandListener() {
            @Override
            public void update(Payload payload) {
                /**
                 * PingPong
                 */
                User user = Database.getInstance().getUserBySocket(getSocket());
                if (user != null) {
                    user.setPong(true);
                }
            }
        });
        addListener("register", new CommandListener() {
            @Override
            public void update(Payload payload) {
                ArrayList<String> parameters = parseToArray(payload);

                String username = parameters.remove(0);
                String password = parameters.remove(0);

                Database.getInstance().insertUser(new User(username, password, getSocket()));
                loggedIn = Database.getInstance().getUser(username);
                res.println("/server " + loggedIn);
                res.flush();
            }
        });
        addListener("user", new CommandListener() {
            @Override
            public void update(Payload payload) {
                if (!isAuth()) {
                    return;
                }

                res.println("/server " + loggedIn);
                res.flush();
            }
        });
        addListener("users", new CommandListener() {
            @Override
            public void update(Payload payload) {
                Vector<User> users = Database.getInstance().getUsers();
                res.println(CONSTANTS.COMMAND_PREFIX + resCommand + users.toString());
                res.flush();
            }
        });
        addListener("dm", new CommandListener() {
            @Override
            public void update(Payload payload) {
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
            public void update(Payload payload) {
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

                    FileStorer fileStorer = new FileStorer(getSocket(), "file_buffer/" + fileName);
                    try {
                        fileStorer.saveFile();
                    } catch (IOException e) {
                        res.println("/server file transfer corrupted");
                        res.flush();
                        e.printStackTrace();
                    }

                    try {
                        PrintWriter res = new PrintWriter(recipient.getSocket().getOutputStream());
                        final int fileId = lastFileId.getAndIncrement();
                        res.println(CONSTANTS.COMMAND_PREFIX + resCommand + loggedIn.getUsername() + " wants to send you file: " + fileName);
                        res.println(CONSTANTS.COMMAND_PREFIX + resCommand + "reply with [/file_accept " + fileId + "] to accept file");
                        res.flush();

                        fileRequestResponses.put(fileName, false);
                        fileRequestNames.add(fileName);

                        /**
                         * Schedule a task for 15 seconds in the future:
                         * If the recipient responded to the file transfer request, do the file sending
                         * if recipient didn't respond, dont do anything and cancel/remove the file
                         */
                        ScheduledFuture<Boolean> fileTask = scheduledExecutorService.schedule(
                                () -> {
//                                    /**
//                                     * If client response was received/accepted -> do execute
//                                     * if client didnt respond with "/file_accept <id>" cancel the task
//                                     */
//                                    boolean recipientResponse = fileRequestResponses.get(fileName);
//                                    System.out.println("recipientResponse: " + recipientResponse);
//
//                                    if (!recipientResponse) {
//                                        do cleanup of file on server here
//                                        return false;
//                                    }
//
//                                    System.out.println("starting file transfer:\n" + fileName + " to " + recipientName);
////                                    String datePrefix = new SimpleDateFormat("yyyyMMddHHmm").format(new Date()) + "." + fileType;
//                                    File myFile = new File(fileName);
//                                    byte[] mybytearray = new byte[(int) myFile.length()];
//
//                                    FileInputStream fis = new FileInputStream(myFile);
//                                    BufferedInputStream bis = new BufferedInputStream(fis);
//
//                                    bis.read(mybytearray, 0, mybytearray.length);
//
//                                    System.out.println("Sending " + fileName + "(" + mybytearray.length + " bytes)");
////
////                                    try {
////                                        PrintWriter res = new PrintWriter(Database.getInstance().getUser(usernameToDm).getSocket().getOutputStream());
////                                        res.println(CONSTANTS.COMMAND_PREFIX + resCommand + loggedIn.getUsername() + ": " + message);
////                                        res.flush();
////                                    } catch (IOException e) {
////                                        e.printStackTrace();
////                                    }
//
//
//                                    recipient.getSocket().getOutputStream().write(mybytearray, 0, mybytearray.length);
//                                    recipient.getSocket().getOutputStream().flush();
//                                    System.out.println("Done.");
//
//                                    System.out.println("Executed!");
                                    return true;
                                },
                                10,
                                TimeUnit.SECONDS);

                        fileRequestTasks.add(fileTask);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        addListener("file_accept", new CommandListener() {
            @Override
            public void update(Payload payload) {
                if (!isAuth()) {
                    return;
                }

                try {
                    ArrayList<String> parameters = parseToArray(payload);
                    int fileReqId = Integer.parseInt(parameters.remove(0)) - 1;
                    String fileName = fileRequestNames.get(fileReqId);
                    fileRequestResponses.put(fileName, true);
//                    fileRequestTasks.get(fileReqId).cancel();
                    System.out.println("Received file confirmation for file " + fileName);
                } catch (IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }

                res.println("/server file accepted");
                res.flush();

            }
        });
        addListener("group_create", new CommandListener() {
            @Override
            public void update(Payload payload) {
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
            public void update(Payload payload) {
                Vector<Group> groupVector = Database.getInstance().getGroups();
                res.println(CONSTANTS.COMMAND_PREFIX + resCommand + groupVector.toString());
                res.flush();
            }
        });

        addListener("group_join", new CommandListener() {
            @Override
            public void update(Payload payload) {
                if (!isAuth()) {
                    return;
                }
                System.out.println("Group join request by " + loggedIn);
                ArrayList<String> parameters = parseToArray(payload);
                String groupName = parameters.remove(0);
                Database.getInstance().addUserToGroup(groupName, loggedIn);
                res.println("/server group joined");
                res.flush();
            }
        });
        addListener("group_message", new CommandListener() {
            @Override
            public void update(Payload payload) {
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
            public void update(Payload payload) {
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
            public void update(Payload payload) {
                if (!isAuth()) {
                    return;
                }
                ArrayList<String> parameters = parseToArray(payload);
                String groupName = parameters.remove(0);
                String usernameToKick = parameters.remove(0);

                if (Database.getInstance().getGroup(groupName).getAdministrator().equals(loggedIn)) { // user issuing kick command is admin of this group
                    User userToKick = Database.getInstance().getUser(usernameToKick);
                    Database.getInstance().getGroup(groupName).removeUser(userToKick);
                    res.print(loggedIn.getUsername() + " kicked " + usernameToKick + " from group " + groupName);
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
            public void update(Payload payload) {
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
            try {

                /**
                 * if Task queue is empty, readLine
                 */
                String message = req.readLine();

                System.out.println("RECEIVED(ENCRYPTED) MESSAGE: " + message);
                message = AES.decrypt(message, CONSTANTS.SECRET);
                System.out.println("DECRYPTED MESSAGE: " + message);

                if (message != null && !message.isEmpty()) {
                    receive(message);
                }

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    System.out.println("Closing socket...");
                    setRunning(false);
                    getSocket().close();
                    System.out.println("Socket closed! :)");
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


}
