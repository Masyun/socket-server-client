package listener;

import abs.command.Payload;
import abs.listener.CommandListener;
import abs.strategy.AbsStrategy;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;

public class SendCommandListener extends CommandListener {

    protected PrintWriter out;

    public SendCommandListener(Socket socket, AbsStrategy command) throws IOException {
        super(socket, command);
        this.out = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void update(Payload payload) {
        try {
            String formattedCommand = strategy.execute(payload);
            super.update(payload);
            out.println(client.CONSTANTS.COMMAND_PREFIX + strategy.get() + " " + formattedCommand);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        } finally {
            out.flush();
        }
    }
}
