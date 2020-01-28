package server;

class Database {

    private static volatile Database instance;
    private static Object mutex = new Object();

    private Database() {
    }

    public static Database getInstance() {
        Database result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null)
                    instance = result = new Database();
            }
        }
        return result;
    }
}
