package server;

import abs.command.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerDispatcher extends Communicator {

    private final PrintWriter res;

    public ServerDispatcher(Socket socket, String name) throws IOException {
        super(socket, name);
        this.res = new PrintWriter(getSocket().getOutputStream());
    }

    @Override
    protected void attachListeners() throws IOException {
        addListener("ping", new CommandListener() {
            @Override
            public void update(Payload payload) {
                res.println("/ping " + payload.get());
                res.flush();
            }
        });
    }

    @Override
    public void run() {
        while (!getSocket().isClosed() && isRunning()) {
            try {
                Thread.sleep(2000);
                notifySub("/ping", new Payload<>("stub"));

                Thread.sleep(3000); // Wait 3 seconds for PingPong

//                if (user.isPong()) {
//                    System.out.println("PONG received from " + user);
//                    user.setPong(false);
//                } else {
//                    System.out.println("Client will be disconnected. No PONG received!");
//                    user.setOnline(false);
//                    user.getSocket().close();
//                    break;
//                }
            } catch (InterruptedException e) {
                setRunning(false);
                System.err.println("Stopping server dispatcher");
                e.printStackTrace();
            }
        }
    }
}


