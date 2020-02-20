//package listener;
//
//import abs.command.Payload;
//import abs.listener.CommandListener;
//import communicator.Communicator;
//
//import java.util.NoSuchElementException;
//
//public class Receiver extends CommandListener {
//
//    public Receiver(Communicator communicator) {
//        super(communicator);
////        this.out = new PrintWriter(socket.getOutputStream());
////        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//    }
//
//    @Override
//    public void update(Payload payload) {
//        try {
////            objectInputStream.writeObject(CONSTANTS.COMMAND_PREFIX + command + " " + payload);
//        } catch (NoSuchElementException e) {
//            System.err.println("Exception thrown in sender# dispatcher:running true->false");
//            communicator.setRunning(false);
//            e.printStackTrace();
//        }
//    }
//}
