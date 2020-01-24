package strategy;

import abs.command.Payload;
import abs.strategy.AbsStrategy;
import command.SendCommand;

public class GroupStrategy extends AbsStrategy {

    @Override
    public String get() {
        return SendCommand.GROUP.get();
    }

    @Override
    public String execute(String payload) {
        return null;
    }
}