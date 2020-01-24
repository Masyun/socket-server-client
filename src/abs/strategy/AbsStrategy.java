package abs.strategy;

import abs.command.Command;
import abs.command.Payload;

import java.util.Scanner;

public abstract class AbsStrategy implements Strategy, Command {

    protected static Scanner scanner = new Scanner(System.in);

    public AbsStrategy() {
    }

    @Override
    public abstract String execute(String payload);
}
