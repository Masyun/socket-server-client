package server;

import abs.command.Payload;
import abs.listener.CommandListener;
import communicator.Communicator;
import model.User;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerDispatcher extends Communicator {

    private final PrintWriter res;
    private final static int RETRY_ATTEMPTS = 10;

    public ServerDispatcher(Socket socket, String name) throws IOException {
        super(socket, name);
        this.res = new PrintWriter(getSocket().getOutputStream());
    }

    @Override
    protected void attachListeners() {
        addListener("ping", new CommandListener() {
            @Override
            public void update(Payload payload) {
//                Database.getInstance().getUserBySocket(getSocket()).setPong(false);
                res.println(CONSTANTS.COMMAND_PREFIX + command + " " + payload.get());
                res.flush();
            }
        });
    }

    @Override
    public void run() {
        int retry = 0;
        while (!getSocket().isClosed() && isRunning()) {
            try {
                Thread.sleep(1500);

                notifySub(CONSTANTS.COMMAND_PREFIX + "ping", new Payload<>("stub"));

                Thread.sleep(5000); // Wait 3 seconds for PingPong
                /**
                 * Do pong check -> stop dispatcher otherwise
                 */
                User user = Database.getInstance().getUserBySocket(getSocket());

                if (user != null) {
                    if (logging) {
                        System.err.println(user.getUsername() + ": pong received");
                    }
                    Database.getInstance().getUserBySocket(getSocket()).setPong(false);
                } else {
                    res.println(CONSTANTS.COMMAND_PREFIX + "server Please register using | /register [username] [password] | or you will get kicked(" + (RETRY_ATTEMPTS - retry) + ")");
                    res.flush();
                    retry += 1;
                    if (retry >= RETRY_ATTEMPTS) {
                        System.out.println("Terminating " + getName());
                        res.println(CONSTANTS.COMMAND_PREFIX + "logout_res");
                        res.flush();
                        setRunning(false);
                        getSocket().close();
                    }
                }

            } catch (IOException | InterruptedException e) {
                try {
                    System.out.println("Closing socket...");
                    setRunning(false);
                    getSocket().close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        System.out.println("Terminating dispatcher thread" + getName());
    }
}


