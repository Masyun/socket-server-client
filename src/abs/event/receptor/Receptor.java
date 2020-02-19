//package abs.event.receptor;
//
//import listener.EventManager;
//
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.concurrent.BlockingQueue;
//
//public abstract class Receptor implements Runnable {
//
//    protected Socket socket;
//    private final String name;
//    protected EventManager events;
//    protected final BlockingQueue<String> queue;
//
//    public Receptor(String name, Socket socket, BlockingQueue<String> queue, String[] protocol) {
//        this.name = name;
//        this.socket = socket;
//        this.queue = queue;
//        events = new EventManager(name, socket, protocol);
//
////        setupListeners(protocol);
//    }
//
//    public void receive(String input) {
//        System.out.println("Received payload: " + input);
//        ArrayList<String> commands = new ArrayList<>(Arrays.asList(input.split(" ")));
//
//        String mainCommand = commands.get(0); // extract listener prefix
////        String protocolCompatRequest = commands.get(0);
//        events.notifySubscribers(
//                mainCommand, input);
//
//    }
//
////    private void setupListeners() {
////        try {
////            for (ReceiveCommand rc : ReceiveCommand.values()) {
//////                ReceptorFactory.spawnListener(rc, socket)
////                events.addSubscriber(CONSTANTS.COMMAND_PREFIX + rc.get(), null);
////            }
////        } catch (RuntimeException rte) {
////            rte.printStackTrace();
////        }
////    }
//}
