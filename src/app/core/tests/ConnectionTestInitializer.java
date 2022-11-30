package app.core.tests;

import app.core.data.ConnectionPool;
import app.core.tasks.ConnectionTest;

// Test for testing the connection pool
// Initializes 10 threads where each picks up a connection and restores it.
public class ConnectionTestInitializer extends Thread {

    public void run() {

        ConnectionTest connectionTest = new ConnectionTest();
        ConnectionTest connectionTest2 = new ConnectionTest();
        ConnectionTest connectionTest3 = new ConnectionTest();
        ConnectionTest connectionTest4 = new ConnectionTest();
        ConnectionTest connectionTest5 = new ConnectionTest();
        ConnectionTest connectionTest6 = new ConnectionTest();
        ConnectionTest connectionTest7 = new ConnectionTest();
        ConnectionTest connectionTest8 = new ConnectionTest();
        ConnectionTest connectionTest9 = new ConnectionTest();
        ConnectionTest connectionTest10 = new ConnectionTest();

        connectionTest.start();
        connectionTest2.start();
        connectionTest3.start();
        connectionTest4.start();
        connectionTest5.start();
        connectionTest6.start();
        connectionTest7.start();
        connectionTest8.start();
        connectionTest9.start();
        connectionTest10.start();

        // waiting for the last tester to end his test
        try {
            connectionTest10.thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
