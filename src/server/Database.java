package server;

import model.Group;
import model.User;

import java.util.Vector;

class Database {

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

    public User getUser(String name) {
        User user = null;

        for (User u :
                users) {
            if (u.getUsername().equalsIgnoreCase(name)) {
                user = u;
            }
        }

        return user;
    }


    public void createGroup(Group group) {
        groups.add(group);
    }

    public void addUserToGroup(String groupName, User user) {
        for (Group g :
                groups) {
            if (g.getName().equalsIgnoreCase(groupName)) {
                g.addUser(user);
            }
        }
    }

    public Vector<User> getUsers() {
        return users;
    }

    public Vector<Group> getGroups() {
        return groups;
    }
}
