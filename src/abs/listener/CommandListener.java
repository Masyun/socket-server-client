package abs.listener;

import abs.command.Payload;

import java.net.Socket;

public abstract class CommandListener {
    protected String command;
    protected Socket socket;

    public CommandListener(Socket socket) {
        this.socket = socket;
    }

    public void update(Payload payload){
        System.out.println("[listener/" + this.command + "]" + payload.get());
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
