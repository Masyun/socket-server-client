package abs.strategy;

import abs.command.Payload;

public interface Strategy {
    String execute(String payload);
}
