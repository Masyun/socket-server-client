package server;

import abs.component.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Component {

    private ServerSocket serverSocket;

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
//        receptor = new Thread(new ServerReceptor(socket, "Server dispatcher"));
    }

    @Override
    protected void run() {
        while (true) {
            try {
                // Wait for an incoming client-connection request (blocking)
                Socket socket = serverSocket.accept();
                System.out.println("\nNew incoming client-connection " + socket.getRemoteSocketAddress().toString());
                startClient(socket);
////                ServerReceptor receptor = new ServerReceptor(socket, socket.getRemoteSocketAddress().toString());
//                // Message processing thread for each connecting client
//                ServerReceptor serverReceptor = new ServerReceptor(socket, socket.getRemoteSocketAddress().toString());
//                serverReceptor.setLogging(true);
//                new Thread(serverReceptor).start();
//
//                new Thread(new ServerDispatcher(socket, socket.getRemoteSocketAddress().toString())).start();
//

                System.out.println("Processing threads started for " +
                        socket.getRemoteSocketAddress().toString() + "!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void end() {
        System.out.println("Server end()");
    }

    private void startClient(Socket socket){
        ServerReceptor serverReceptor;
        ServerDispatcher serverDispatcher;
        try {
            serverReceptor = new ServerReceptor(socket, socket.getRemoteSocketAddress().toString());
            serverReceptor.setLogging(true);
            new Thread(serverReceptor).start();

            serverDispatcher = new ServerDispatcher(socket, socket.getRemoteSocketAddress().toString());
            serverDispatcher.setLogging(true);
            new Thread(serverDispatcher).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
