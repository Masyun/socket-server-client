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
        for (User u :
                users) {
            if (u.getUsername().equalsIgnoreCase(name)) {
                return u;
            }
        }

        return null;
    }


    /**
     * experimental
     *
     * @param socket
     * @return
     */
    public User getUserBySocket(Socket socket) {

        Vector<User> users = Database.getInstance().getUsers();
        for (User u :
                users) {
            if (u.getSocket().equals(socket)) {
//                System.out.println("[DB] getUserBySocket socket: " + socket + " - " + u);
                return u;
            }
        }

        return null;
    }

    public void createGroup(Group group) {
        groups.add(group);
    }

    public Vector<Group> getGroups() {
        return groups;
    }

    public Group getGroup(String groupName) {
        for (Group g :
                groups) {
            if (g.getName().equalsIgnoreCase(groupName)) {
                return g;
            }
        }

        return null;
    }

    public void addUserToGroup(String groupName, User user) {
        for (Group g :
                groups) {
            if (g.getName().equalsIgnoreCase(groupName)) {
                System.out.println("Adding user to group");
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

    public void logoutUser(Socket socket){
        for (Group g :
                groups) {
            removeUserFromGroup(g.getName(), getUserBySocket(socket));
        }
        getUserBySocket(socket).setOnline(false);
        System.out.println("After logout call from database: " + getUserBySocket(socket));
    }
}
