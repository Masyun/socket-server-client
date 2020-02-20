package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for a group.
 */
public class Group {

    // Group details
    private String name;
    private String administrator;
    private List<String> usersInGroup; // Users by username

    public Group(String name) {
        this.name = name;
        this.usersInGroup = new ArrayList<>();
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
    public String getAdministrator() {
        return administrator;
    }

    /**
     * Set group administrator to user's username.
     *
     * @param administrator String
     */
    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    /**
     * Get users in a group in Strings.
     *
     * @return List<String>
     */
    public List<String> getUsersInGroup() {
        return usersInGroup;
    }

    /**
     * Add user to a group.
     *
     * @param user User
     */
    public void addUser(User user) {
        this.usersInGroup.add(user.getUsername());
    }

    /**
     * Remove user from the group.
     *
     * @param user User
     */
    public void removeUser(User user) {
        for (int i = 0; i < usersInGroup.size(); i++) {
            if (user.getUsername().equals(usersInGroup.get(i))) {
                usersInGroup.remove(i);
            }
        }
    }

    @Override
    public String toString() {
        return "Group: " + name + " | Admin: " + getAdministrator() + ")";
    }
}
