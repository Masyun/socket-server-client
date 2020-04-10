package abs.listener;

import abs.command.Payload;

public abstract class CommandListener {
    protected String command;
    protected String resCommand;
//    protected Communicator communicator;

    public CommandListener() {
//        this.communicator = communicator;
    }

    public abstract void update(Payload payload);

    public void setCommand(String command) {
        this.command = command;
        this.resCommand = command + "_res ";
    }

    public String getCommand() {
        return command;
    }

    //    public Socket getSocket(){
//        return communicator.getSocket();
//    }
}
