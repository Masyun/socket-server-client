package server;

import abs.component.Component;

import java.io.IOException;
import java.net.ServerSocket;

public class Server extends Component {

    private ServerSocket serverSocket;
    private Database database;
    private ServerDispatcher dispatcher;
    private ServerReceptor receptor;

    public Server() {
        try {
            this.serverSocket = new ServerSocket(CONSTANTS.SERVER_PORT);
            System.out.println("Server has been started on port " + serverSocket.getLocalSocketAddress().toString() + "!");
        } catch (IOException e) {
            System.err.println("Server could not be started on port "
                    + CONSTANTS.SERVER_PORT);
            System.exit(-1);
        }
    }

    @Override
    protected void initialize() {
        dispatcher = new ServerDispatcher();
        receptor = new ServerReceptor();
    }

    @Override
    protected void run() {
        dispatcher.start();
        receptor.start();
    }

    @Override
    protected void end() {

    }
}
