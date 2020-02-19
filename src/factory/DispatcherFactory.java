//package factory;
//
//import abs.listener.CommandListener;
//import command.SendCommand;
//import listener.SendCommandListener;
//
//import java.io.IOException;
//import java.net.Socket;
//
//public class DispatcherFactory {
//    public static CommandListener spawnListener(SendCommand sendCommand, Socket socket) throws RuntimeException, IOException, IllegalAccessException {
//        return new SendCommandListener(socket, StrategyFactory.spawnClientStrategy(sendCommand));
//    }
//}
