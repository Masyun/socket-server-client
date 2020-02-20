package abs.listener;

import abs.command.Payload;

public abstract class CommandListener {
    protected String command;
//    protected Communicator communicator;

    public CommandListener() {
//        this.communicator = communicator;
    }

    public abstract void update(Payload payload);

    public void setCommand(String command) {
        this.command = command;
    }

//    public Socket getSocket(){
//        return communicator.getSocket();
//    }
}
