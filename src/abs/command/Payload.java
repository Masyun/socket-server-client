package abs.command;

public class Payload implements Command{

    private final String content;

    public Payload(String content) {
        this.content = content;
    }

    @Override
    public String get() {
        return content;
    }
}
