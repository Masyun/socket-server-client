package server;

import model.Group;
import model.User;

import java.net.Socket;
import java.util.Vector;

public class Database {

    //    private static Database instance;
    private static volatile Database instance;
    private Vector<User> users = new Vector<>();
    private Vector<Group> groups = new Vector<>();


    private Database() {
    }

    synchronized public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void insertUser(User user) {
        users.add(user);
    }

    public Vector<User> getUsers() {
        return users;
    }

    public User getUser(String name) {
        User user = null;

        for (User u :
                users) {
            if (u.getUsername().equalsIgnoreCase(name)) {
                user = new User(u.getUsername(), u.getPassword(), u.getSocket());
                break;
            }
        }

        return user;
    }

    /**
     * experimental
     *
     * @param socket
     * @return
     */
    public User getSocketConnection(Socket socket) {
        User user = null;
        Vector<User> users = Database.getInstance().getUsers();
        for (User u :
                users) {
            if (u.getSocket().equals(socket)) {
//                System.out.println("[DB] getSocketConnection socket: " + socket + " - " + u);
                user = u;
                break;
            }
        }

        return user;
    }

//    public void logoutUser(User loggedIn) throws IOException {
//        User user = getUser(loggedIn.getUsername());
//        if (user != null) {
//            user.setOnline(false);
//            user.getSocket().close();
//        }
//    }

    public void createGroup(Group group) {
        groups.add(group);
    }

    public Vector<Group> getGroups() {
        return groups;
    }

    public Group getGroup(String groupName) {
        Group group = null;
        for (Group g :
                groups) {
            if (g.getName().equalsIgnoreCase(groupName)) {
                group = g;
                break;
            }
        }

        return group;
    }

    public void addUserToGroup(String groupName, User user) {
        for (Group g :
                groups) {
            if (g.getName().equalsIgnoreCase(groupName)) {
                g.addUser(user);
                break;
            }
        }
    }

    public boolean removeUserFromGroup(String groupName, User user) {
        for (Group g : groups) {
            if (g.getName().equalsIgnoreCase(groupName)) {

                for (User u : g.getUsersInGroup()) {
                    if (u.getUsername().equalsIgnoreCase(user.getUsername())) {
                        g.removeUser(u);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void logoutUser(Socket socket) {
        for (Group g :
                groups) {
            removeUserFromGroup(g.getName(), getSocketConnection(socket));
        }
        users.remove(getSocketConnection(socket));
        System.out.println("After deletion from socket: " + getSocketConnection(socket));
    }
}
