package client;

import abs.component.Component;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Component {

    private Scanner scanner;
    private Socket socket;
    private Thread dispatcher;
    private Thread receptor;

    public Client() {
        try {
            this.socket = new Socket(CONSTANTS.SERVER_ADDRESS, CONSTANTS.SERVER_PORT);
            System.out.println("Connected on socket address " + socket.getLocalSocketAddress().toString());
        } catch (IOException e) {
            System.err.println("Connection could not be established at "
                    + CONSTANTS.SERVER_ADDRESS
                    + ":"
                    + CONSTANTS.SERVER_PORT);
            System.exit(-1);
        }

        scanner = new Scanner(System.in);
    }

    @Override
    protected void initialize() {
        try {
            // Sender Thread
            dispatcher = new Thread(new ClientDispatcher(socket));

            // Receiver thread
            receptor = new Thread(new ClientReceptor(socket));

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
        dispatcher.start();
        receptor.start();
    }

    @Override
    protected void end() {

    }

}
