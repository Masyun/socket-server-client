package abs.component;


/**
 * Abstraction over the bigger components of the system, like the client and server
 * It allows us to give a standard flow to both of our modules by invoking one method
 */
public abstract class Component {

    /**
     * The driver code for any component
     * By giving the correct implementation in the subclasses, the flow will always be the same
     */
    public final void start() {

        initialize();

        run();

        end();
    }

    protected abstract void initialize();

    protected abstract void run();

    protected abstract void end();
}
