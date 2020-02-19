//package abs.event.dispatcher;
//
//import listener.EventManager;
//
//import java.net.Socket;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.concurrent.BlockingQueue;
//
//public abstract class Dispatcher implements Runnable {
//
//    protected Socket socket;
//    private final String name;
//    protected EventManager events;
//    protected final BlockingQueue queue;
//
//    public Dispatcher(String name, Socket socket, BlockingQueue queue, String[] protocol) {
//        this.name = name;
//        this.socket = socket;
//        this.queue = queue;
//        events = new EventManager(name, socket, protocol);
//
////        setupListeners(protocol);
//    }
//
//    public void dispatch(String input) {
//        ArrayList<String> commands = new ArrayList<>(Arrays.asList(input.split(" ")));
//        for (String c :
//                commands) {
//            System.out.println(c);
//        }
//
//        String mainCommand = commands.get(0); // extract listener prefix
//
//        events.notifySubscribers(
//                mainCommand, input);
//
//    }
//
////    private void setupListeners(String... commands) {
////        try {
////            for (String c : commands) {
////                // DispatcherFactory.spawnListener(c, socket)
////                events.addSubscriber(CONSTANTS.COMMAND_PREFIX + c, null);
////            }
////        } catch (RuntimeException rte) {
////            rte.printStackTrace();
////        }
////    }
//}
