package abs.listener;

import abs.payload.Payload;

/**
 * Abstract listener class for anonymous implementation for our event loop
 * These are invoked on both server and client side based on {payload}
 * A Command listener describes a possible action that a module(client/server) should listen on
 * so the API description is also provided here when creating a listener.
 */
public abstract class CommandListener {
    protected String command;
    protected String resCommand;
    protected String params;
    protected String description;

    public CommandListener(String params, String description) {
        this.params = params;
        this.description = description;
    }

    public CommandListener() {
    }

    /**
     * Abstract update method that is invoked automatically based on {payload}
     * The implementation will be given in the Communicator sub classes
     * @param payload
     * @throws IndexOutOfBoundsException
     */
    public abstract void update(Payload payload) throws IndexOutOfBoundsException;

    /**
     * The resCommand is used for automatically responding to events from client to server or vice versa
     * @param command
     */
    public void setCommand(String command) {
        this.command = command;
        this.resCommand = command + "_res ";
    }

    public String getCommand() {
        return command;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
