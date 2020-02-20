//package factory;
//
//import abs.listener.CommandListener;
//import command.SendCommand;
//import listener.Sender;
//
//import java.io.IOException;
//import java.net.Socket;
//
//public class DispatcherFactory {
//    public static CommandListener spawnListener(SendCommand sendCommand, Socket socket) throws RuntimeException, IOException, IllegalAccessException {
//        return new Sender(socket, StrategyFactory.spawnClientStrategy(sendCommand));
//    }
//}
