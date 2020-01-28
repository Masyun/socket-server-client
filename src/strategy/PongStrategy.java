package strategy;

import abs.command.Payload;
import abs.strategy.AbsStrategy;
import command.SendCommand;

import java.util.NoSuchElementException;

public class PongStrategy extends AbsStrategy {

    @Override
    public String get() {
        return SendCommand.PONG.get();
    }

    @Override
    protected String format(Payload payload) throws NoSuchElementException {
//        int inputNr = scanner.nextInt();
        return payload.get();
    }
}
