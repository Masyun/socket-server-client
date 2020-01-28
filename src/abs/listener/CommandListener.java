package abs.listener;

import abs.command.Payload;
import abs.strategy.AbsStrategy;

import java.net.Socket;

public abstract class CommandListener {
    protected Socket socket;
    protected String command;
    protected AbsStrategy strategy;

    public CommandListener(Socket socket, AbsStrategy command) {
        this.socket = socket;
        this.command = command.get();
        this.strategy = command;
    }

    public String getCommand() {
        return command;
    }

    public void update(Payload payload){
        System.out.println("[listener/" + this.command + "]" + payload.get());
    }
}
