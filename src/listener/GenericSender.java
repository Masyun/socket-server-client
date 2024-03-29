package listener;

import abs.payload.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;

import java.io.IOException;
import java.io.PrintWriter;

public class GenericSender extends CommandListener {

    private final PrintWriter out;
    private Communicator communicator;

    public GenericSender(Communicator communicator, String params, String description) throws IOException {
        super(params, description);
        this.communicator = communicator;
        out = new PrintWriter(communicator.getSocket().getOutputStream());
    }

    public GenericSender(Communicator communicator) throws IOException {
        this.communicator = communicator;
        out = new PrintWriter(communicator.getSocket().getOutputStream());

    }

    @Override
    public void update(Payload payload) {
        out.println(payload.get());
        out.flush();
    }
}
