package model;

import java.net.Socket;

/**
 * Class for the user.
 */
public class User {

    // User details
    private String username;
    private String password;
    private Socket socket;
    private boolean online;
    private boolean pong; // Received and send a PONG back to the server

    public User(String username, String password, Socket socket) {
        this.username = username;
        this.password = password;
        this.socket = socket;
        this.online = true;
        this.pong = true;
    }

    /**
     * Get username of user.
     *
     * @return String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get password of user.
     *
     * @return String
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get Socket from user.
     *
     * @return Socket
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Get RemoteSocketAddress from user's Socket in a String.
     *
     * @return String
     */
    public String getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress().toString();
    }

    /**
     * Check if user is online.
     *
     * @return boolean
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * Set online status of user.
     *
     * @param online boolean
     */
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * Get status of the PingPong.
     *
     * @return boolean
     */
    public boolean isPong() {
        return pong;
    }

    /**
     * Set the PONG for the user.
     *
     * @param pong boolean
     */
    public void setPong(boolean pong) {
        this.pong = pong;
    }
//
//    @Override
//    public String toString() {
//        return getUsername() + " (" + getRemoteSocketAddress() + ")";
//    }


    @Override
    public String toString() {
        return "User{" +
                "username=" + username +
                ", online=" + online +
                "}";
    }
}
