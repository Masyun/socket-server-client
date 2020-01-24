package strategy;

import abs.command.Payload;
import abs.strategy.AbsStrategy;
import command.SendCommand;

public class UserStrategy extends AbsStrategy {

    public UserStrategy() {
    }

    @Override
    public String get() {
        return SendCommand.USER.get();
    }

    @Override
    public String execute(String payload) {
        return payload + scanner.next();
    }
}