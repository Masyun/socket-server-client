package client;

import abs.component.Component;
import communicator.Communicator;

import java.io.IOException;
import java.net.Socket;

/**
 * Main client component, which is bootstrapped in client.App
 * It has 2 threads, one read thread and one write thread
 */
public class Client extends Component {

    private Socket socket;
    private Communicator clientDispatcher;
    private Communicator clientReceptor;
    private Thread dispatcherThread;
    private Thread receptorThread;

    public Client() {
        while (socket == null) {
            try {
                this.socket = new Socket(CONSTANTS.SERVER_ADDRESS, CONSTANTS.SERVER_PORT);
                System.out.println("Connected on socket address " + socket.getLocalSocketAddress().toString());
            } catch (IOException e) {
                System.err.println("Connection could not be established at "
                        + CONSTANTS.SERVER_ADDRESS
                        + ":"
                        + CONSTANTS.SERVER_PORT);
                System.err.println("Retrying connection in 3 seconds");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException waitingException) {
                    waitingException.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void initialize() {
        try {
            // Sender Thread
            clientDispatcher = new ClientDispatcher(socket, " Client Dispatcher");
            clientDispatcher.setLogging(true);
            dispatcherThread = new Thread(clientDispatcher);

            // Receiver thread
            clientReceptor = new ClientReceptor(socket, " Client Receptor");
            clientReceptor.setLogging(true);
            receptorThread = new Thread(clientReceptor);

//            while (true) {
//                System.out.print("Enter your username: ");
//                String username = scanner.next();
//                System.out.print("Enter your password: ");
//                String password = scanner.next();
//
//                dispatcher.println("/user create " + username + " " + password);
//                dispatcher.flush();
//
//                String response = serverOutput.readLine();
//                if (response.equals("Wrong login credentials!")) {
//                    System.out.println(response);
//                } else if (response.equals("User is already online!")) {
//                    System.out.println(response);
//                } else {
//                    System.out.println("Welcome, " + username + ". See /commands for the list of available commands!");
//                    break;
//                }
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        dispatcherThread.start();
        receptorThread.start();
    }

    @Override
    protected void end() {

    }

}
