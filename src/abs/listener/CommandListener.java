package abs.listener;

import abs.command.Payload;
import abs.strategy.AbsStrategy;

import java.net.Socket;

public abstract class CommandListener {
    protected Socket socket;
    protected String command;

    public CommandListener(Socket socket, String command) {
        this.socket = socket;
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void update(String payload){
        System.out.println("[listener/" + this.command + "]" + payload);
    }
}
