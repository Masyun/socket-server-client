package listener;

import abs.command.Payload;
import abs.listener.CommandListener;
import abs.strategy.AbsStrategy;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SendCommandListener extends CommandListener {

    protected PrintWriter out;
    protected AbsStrategy strategy;

    public SendCommandListener(Socket socket, AbsStrategy command) throws IOException {
        super(socket, command.get());
        this.strategy = command;
        this.out = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void update(String payload) {
        super.update(payload);
        String formattedCommand = strategy.execute(payload);

        System.out.println("Final formatted payload to send to server: " + formattedCommand);
    }
}
