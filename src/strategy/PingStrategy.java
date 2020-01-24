package strategy;

import abs.command.Payload;
import abs.strategy.AbsStrategy;
import command.ReceiveCommand;

public class PingStrategy extends AbsStrategy {

    @Override
    public String get() {
        return ReceiveCommand.PING.get();
    }

    @Override
    public String execute(String payload) {
        return null;
    }
}