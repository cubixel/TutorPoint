package application.controller.services;

import java.io.IOException;

/**
 * CLASS DESCRIPTION:
 *
 * @author Daniel Bishop
 *
 */
public class Heartbeat extends Thread {

    private MainConnection connection;
    private boolean connected;

    public Heartbeat(MainConnection connection) {
        setDaemon(true);
        this.connection = connection;
        this.connected = true;
    }

    public void stopHeartbeat(){
        this.connected = false;
    }

    @Override
    public void run() {
        while (connected) {
            try {
                connection.sendString("Heartbeat");
            } catch (IOException e) {
                connected = false;
                e.printStackTrace();
            }

            try {
				sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }

    }
}
