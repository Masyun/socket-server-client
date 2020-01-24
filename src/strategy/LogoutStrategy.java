package strategy;

import abs.command.Payload;
import abs.strategy.AbsStrategy;
import command.SendCommand;

public class LogoutStrategy extends AbsStrategy {
    @Override
    public String get() {
        return SendCommand.LOGOUT.get();
    }

    @Override
    public String execute(String payload) {
        return null;
    }
}
