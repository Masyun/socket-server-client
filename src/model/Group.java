package model;

import java.util.Vector;

/**
 * Class for a group.
 */
public class Group {

    // Group details
    private String name;
    private User administrator;
    private Vector<User> usersInGroup; // Users by username

    public Group(String name) {
        this.name = name;
        this.usersInGroup = new Vector<>();
    }

    /**
     * Get group name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Set group name.
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get group's administrator in user's username.
     *
     * @return String
     */
    public User getAdministrator() {
        return administrator;
    }

    /**
     * Set group administrator to user's username.
     *
     * @param administrator User
     */
    public void setAdministrator(User administrator) {
        this.administrator = administrator;
    }

    /**
     * Get users in a group in Strings.
     *
     * @return List<User>
     */
    public Vector<User> getUsersInGroup() {
        return usersInGroup;
    }

    /**
     * Add user to a group.
     *
     * @param user User
     */
    public void addUser(User user) {
        System.out.println("Adding user to group");
        this.usersInGroup.add(user);
    }

    /**
     * Remove user from the group.
     *
     * @param user User
     */
    public void removeUser(User user) {
        usersInGroup.remove(user);
    }

    /**
     * @param id
     */
    public void removeUserId(int id) {
        usersInGroup.remove(id);
    }


    @Override
    public String toString() {
        return "Group: " + name + "(" + getUsersInGroup().size() + ") | Admin: " + getAdministrator().getUsername() + "){ " + getUsersInGroup() + " }\n";
    }
}
