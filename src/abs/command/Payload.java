package abs.command;

import java.io.Serializable;

public class Payload<T> implements Serializable {

    private final T content;

    public Payload(T content) {
        this.content = content;
    }

    public T get() {
        return content;
    }

    @Override
    public String toString() {
        return "Payload{" +
                "content=" + content +
                '}';
    }
}
