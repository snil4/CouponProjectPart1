package app.core.data;

import app.core.exceptions.CouponSystemException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

public class ConnectionPool {

    // Edit these lines
    // Database Url (make sure to leave the "coupons_db" at the end)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/coupons_db";
    // Database username
    private static final String DB_USER = "root";
    // Database Password
    private static final String DB_PASSWORD = "12345678";
    private static ConnectionPool instance = null;
    private final int CONNECTIONS_NUM = 5;
    private final Set<Connection> connections = new HashSet<>();

    private ConnectionPool() {
        try {

            for (int i = 0; i < CONNECTIONS_NUM; i++) {
                connections.add(DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ConnectionPool getInstance() {
        if (instance == null) {
            instance = new ConnectionPool();
        }

        return instance;
    }

    /**
     * @return A Ready connection from the pool
     */
    public synchronized Connection getConnection() throws CouponSystemException {
        try {
            while (true) {

                for (Connection connection : connections) {
                    if (!connection.isClosed()) {
                        connections.remove(connection);
                        return connection;
                    }
                }

                System.out.println("waiting for connection...");
                wait();
            }
        } catch (InterruptedException | SQLException e) {
            throw new CouponSystemException("Can't get a connection ", e);
        }
    }

    /**
     * Return a used connection to the pool
     *
     * @param connection The connection to restore
     */
    public synchronized void restoreConnection(Connection connection) {
        connections.add(connection);
        notify();
    }

    /**
     * Close all connections in the pool
     */
    public synchronized void closeAllConnections() throws CouponSystemException {

        try {
            while (true) {
                if (connections.size() == CONNECTIONS_NUM) {
                    // Checking if all the connections are back in the list
                    for (Connection connection : connections) {
                        connection.close();
                    }
                    break;
                } else {
                    throw new CouponSystemException("Not all the connections are in the list");
                }
            }
        } catch (SQLException e) {
            throw new CouponSystemException("Can't close all connections", e);
        }

    }
}
