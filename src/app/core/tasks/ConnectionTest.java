package app.core.tasks;

import app.core.data.ConnectionPool;
import app.core.exceptions.CouponSystemException;

import java.sql.Connection;
import java.util.concurrent.TimeUnit;

// Thread to test the connection pool
// picks up a connection and restores it
public class ConnectionTest implements Runnable {

    public Thread thread = new Thread(this, "Connection test");
    ConnectionPool connectionPool = ConnectionPool.getInstance();

    @Override
    public void run() {
        Connection connection;

        try {
            connection = connectionPool.getConnection();
            System.out.println("got connection " + connection);

        } catch (CouponSystemException e) {
            throw new RuntimeException("Can't get a connection", e);
        }

        try {
            TimeUnit.SECONDS.sleep(5);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            connectionPool.restoreConnection(connection);
        }

    }

    public void start() {
        thread.start();

    }
}
