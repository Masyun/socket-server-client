package listener;

import abs.command.Payload;
import abs.listener.CommandListener;
import abs.strategy.AbsStrategy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ReceiveCommandListener extends CommandListener {

    protected PrintWriter out;
    protected BufferedReader in;
    protected AbsStrategy strategy;

    public ReceiveCommandListener(Socket socket, AbsStrategy command) throws IOException {
        super(socket, command.get());
        this.strategy = command;
        this.out = new PrintWriter(socket.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void update(String payload) {
        super.update(payload);
        strategy.execute(payload);
    }
}
