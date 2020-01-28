package abs.component;

public abstract class Component {


    public final void start() {

        initialize();

        run();

        end();
    }

    protected abstract void initialize();

    protected abstract void run();

    protected abstract void end();
}
