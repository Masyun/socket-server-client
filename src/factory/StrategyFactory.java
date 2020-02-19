//package factory;
//
//import abs.strategy.AbsStrategy;
//import command.ReceiveCommand;
//import command.SendCommand;
//import strategy.*;
//
//public class StrategyFactory {
//
//    public static AbsStrategy spawnClientStrategy(SendCommand command) throws IllegalAccessException {
//        switch (command) {
//            case PONG:
//                return new PongStrategy();
//            case USER:
//                return new UserStrategy();
//            case LOGOUT:
//                return new LogoutStrategy();
//            case GROUP:
//                return new GroupStrategy();
//            default:
//                throw new IllegalAccessException("[StrategyFactory.spawnClientStrategy] could not create appropriate Strategy(" + command + ")");
//        }
//    }
//
//    public static AbsStrategy spawnServerStrategy(ReceiveCommand command) throws IllegalAccessException {
//        switch (command) {
//            case PING:
//                return new PingStrategy();
//            default:
//                throw new IllegalAccessException("[StrategyFactory.spawnServerStrategy] could not create appropriate Strategy(" + command + ")");
//
//        }
//    }
//}
