package listener;

import abs.command.Payload;
import abs.listener.CommandListener;
import client.CONSTANTS;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;

public class SendCommandListener extends CommandListener {

    protected PrintWriter out;

    public SendCommandListener(Socket socket) throws IOException {
        super(socket);
        this.out = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void update(Payload payload) {
        try {
            super.update(payload);
            out.println(CONSTANTS.COMMAND_PREFIX + command + " " + payload);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        } finally {
            out.flush();
        }
    }
}
