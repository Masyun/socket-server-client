package client;

import factory.ReceptorFactory;
import command.ReceiveCommand;
import listener.EventManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientReceptor extends Thread {

    private Socket socket;

    private PrintWriter outStream;
    private BufferedReader inStream;

    private EventManager events = new EventManager(ClientReceptor.class.getSimpleName(), ReceiveCommand.values());

    public ClientReceptor(Socket socket) throws IOException, NullPointerException {
        this.socket = socket;
        this.outStream = new PrintWriter(socket.getOutputStream());
        this.inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        setupListeners();
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void receive(String receiveCommand, String payload) {
        if (receiveCommand != null) {
            events.notifySubscribers(receiveCommand, payload);
        }
    }

    private void setupListeners() throws IOException {
        try {
            for (ReceiveCommand et : ReceiveCommand.values()) {
                events.addSubscriber(et.get(), ReceptorFactory.spawnListener(et, socket));
            }
        } catch (RuntimeException | IllegalAccessException rte) {
            rte.printStackTrace();
        }
    }
}

