package abs.strategy;

import abs.command.Command;
import abs.command.Payload;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public abstract class AbsStrategy implements Strategy, Command {

    protected Scanner scanner;

    protected abstract String format(Payload payload) throws InputMismatchException;

    private void flush() {
        scanner = new Scanner(System.in);
    }

    @Override
    public String execute(Payload payload) throws NoSuchElementException {
        flush();
        return format(payload);
    }
}
