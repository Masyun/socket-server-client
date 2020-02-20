//package factory;
//
//import abs.listener.CommandListener;
//import command.ReceiveCommand;
//import listener.Receiver;
//
//import java.io.IOException;
//import java.net.Socket;
//
//public class ReceptorFactory {
//    public static CommandListener spawnListener(ReceiveCommand receiveCommand, Socket socket) throws IOException, RuntimeException, IllegalAccessException {
//        return new Receiver(socket, StrategyFactory.spawnServerStrategy(receiveCommand));
//    }
//}
