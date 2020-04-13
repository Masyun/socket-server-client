package abs.listener;

import abs.command.Payload;

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

    public abstract void update(Payload payload) throws IndexOutOfBoundsException;

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
