package server;

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
import java.util.Vector;
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
    protected void attachListeners() {
        addListener("server", new CommandListener() {
            @Override
            public void update(Payload payload) {
                res.println(CONSTANTS.COMMAND_PREFIX + resCommand + " " + payload.get());
                res.flush();
            }
        });
//        addListener("ping_res", new CommandListener() {
//            @Override
//            public void update(Payload payload) {
//                /**
//                 * Stuff with a pingpong state in the database/loggedIn instance
//                 */
//                System.out.println("/pong received");
//                User user = Database.getInstance().getSocketConnection(getSocket());
//                if (user != null){
//                    Database.getInstance().getSocketConnection(getSocket()).setPong(true);
//                }
//            }
//        });
        addListener("register", new CommandListener() {
            @Override
            public void update(Payload payload) {
                ArrayList<String> parameters = parsePayload(payload);

                String username = parameters.remove(0);
                String password = parameters.remove(0);

                Database.getInstance().insertUser(new User(username, password, getSocket()));
                loggedIn = Database.getInstance().getUser(username);
                res.println(loggedIn);
                res.flush();
            }
        });
        addListener("user", new CommandListener() {
            @Override
            public void update(Payload payload) {
                if (loggedIn == null){
                    res.println("AUTH");
                    res.flush();
                    return;
                }

                System.out.println("loggedIn: " + loggedIn);
                res.println(loggedIn.getUsername());
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
                if (loggedIn == null){
                    res.println("AUTH");
                    res.flush();
                    return;
                }
                ArrayList<String> parameters = parsePayload(payload);

                String usernameToDm = parameters.remove(0);
                String message = parameters.remove(0);

                try {
                    PrintWriter res = new PrintWriter(Database.getInstance().getUser(usernameToDm).getSocket().getOutputStream());
                    res.println(CONSTANTS.COMMAND_PREFIX + resCommand + loggedIn.getUsername() + ": " + message);
                    res.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        addListener("group_create", new CommandListener() {
            @Override
            public void update(Payload payload) {
                if (loggedIn == null){
                    res.println("AUTH");
                    res.flush();
                    return;
                }
                ArrayList<String> parameters = parsePayload(payload);

                String groupName = parameters.remove(0);
                String adminName = loggedIn.getUsername();
                User admin = Database.getInstance().getUser(adminName);

                Group group = new Group(groupName);
                group.setAdministrator(admin);

                Database.getInstance().createGroup(group);
                res.println("201 - OK");
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
                if (loggedIn == null){
                    res.println("AUTH");
                    res.flush();
                    return;
                }
                System.out.println("Group join request by " + loggedIn);
                ArrayList<String> parameters = parsePayload(payload);
                String groupName = parameters.remove(0);
                Database.getInstance().addUserToGroup(groupName, loggedIn);
            }
        });
        addListener("group_message", new CommandListener() {
            @Override
            public void update(Payload payload) {
                if (loggedIn == null){
                    res.println("AUTH");
                    res.flush();
                    return;
                }
                ArrayList<String> parameters = parsePayload(payload);
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
                if (loggedIn == null){
                    res.println("AUTH");
                    res.flush();
                    return;
                }
                ArrayList<String> parameters = parsePayload(payload);
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
                if (loggedIn == null){
                    res.println("AUTH");
                    res.flush();
                    return;
                }
                ArrayList<String> parameters = parsePayload(payload);
                String groupName = parameters.remove(0);
                String usernameToKick = parameters.remove(0);

                if (Database.getInstance().getGroup(groupName).getAdministrator().equals(loggedIn)){ // user issuing kick command is admin of this group
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
                String message = req.readLine();
//                System.out.println("FROM CLIENT: " + message);

                // Print message
                if (message != null && !message.isEmpty()) {
                    receive(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    System.out.println("Closing socket...");
                    getSocket().close();
                    System.out.println("Socket closed! :)");
                    setRunning(false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        System.out.println("Terminating receptor thread" + getName());
    }

    private void receive(String message) {
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

    private ArrayList<String> parsePayload(Payload payload) {
        String content = (String) payload.get();
        return new ArrayList<>(Arrays.asList(content.split(" ")));
    }
}
