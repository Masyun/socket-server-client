package listener;

import abs.command.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;

public class GenericReceiver extends CommandListener {

    private Communicator communicator;

    public GenericReceiver(Communicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public void update(Payload payload) {
        System.out.println(command + ">");
        System.out.println(payload.get());
    }
}
