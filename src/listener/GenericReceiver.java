package listener;

import abs.payload.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;

public class GenericReceiver extends CommandListener {

    private Communicator communicator;

    public GenericReceiver(Communicator communicator, String params, String description) {
        super(params, description);
        this.communicator = communicator;
    }

    public GenericReceiver(Communicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public void update(Payload payload) {
        System.out.println(payload.get());
    }
}
