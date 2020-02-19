//package strategy.user;
//
//import abs.strategy.AbsStrategy;
//import command.SendCommand;
//
//public class CreateUser extends AbsStrategy {
//    @Override
//    protected String format(String payload) {
//        System.out.println("Hello from inside strategy: " + get() + "{" + payload + "}");
//        return payload;
//    }
//
//    @Override
//    public String get() {
//        return SendCommand.USER_CREATE.get();
//    }
//}
