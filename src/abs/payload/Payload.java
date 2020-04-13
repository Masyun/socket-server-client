package abs.payload;

import java.io.Serializable;

/**
 * Generic payload container
 * @param <T> The type of the contents of this payload
 */
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
