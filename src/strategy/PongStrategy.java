package strategy;

import abs.command.Payload;
import abs.strategy.AbsStrategy;
import command.SendCommand;

public class PongStrategy extends AbsStrategy {

    @Override
    public String get() {
        return SendCommand.PONG.get();
    }

    @Override
    public String execute(String payload) {
        return payload + scanner.next();
    }
}
