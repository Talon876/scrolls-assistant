package org.nolat.scrolls.network;

import org.apache.log4j.Logger;
import org.nolat.scrolls.network.Messages.Ping;

import com.google.common.eventbus.Subscribe;

/**
 * Keeps a {@link ScrollsConnection} alive by sending Ping messages periodically.
 */
public class Keepalive implements Runnable {

    private static final Logger log = Logger.getLogger(Keepalive.class);
    private static final int DEFAULT_TICK = 15000; //default 15 second delay between Ping
    private static final Ping PING_MESSAGE = new Ping();
    private final ScrollsConnection connection;
    private final int tick;
    private int ping = 0;
    private long epoch = 0;
    private final Thread self;

    /**
     * 
     * @param connection
     *            the connection to keep alive
     * @param tick
     *            time in ms between Ping requests
     */
    public Keepalive(ScrollsConnection connection, int tick) {
        log.info("Starting Keepalive for " + connection.toString() + " with " + tick + "ms intervals.");
        this.connection = connection;
        this.tick = tick;
        this.connection.getMessageRouter().register(this);
        self = new Thread(this, connection.getHostname() + "-keepalive");
        self.start();
    }

    /**
     * This method is executed whenever a Ping message is received.
     * 
     * @param message
     */
    @Subscribe
    public void updateLatency(Messages.Ping message) {
        ping = (int) (message.time - epoch) / 1000;
        log.trace("Ping: " + ping + "ms");
    }

    /**
     * Creates a Keepalive session using the default tick rate
     * 
     * @param connection
     *            the connection to keep alive
     */
    public Keepalive(ScrollsConnection connection) {
        this(connection, DEFAULT_TICK);
    }

    @Override
    public void run() {
        while (!connection.getSocket().isClosed()) {
            epoch = System.currentTimeMillis();
            connection.sendMessage(PING_MESSAGE);
            try {
                Thread.sleep(tick);
            } catch (InterruptedException e) {
                log.warn("Keepalive sleep interrupted - stopping");
            }
        }
        log.info("Ending keepalive thread");
    }

    /**
     * Gets the ping in ms to the server
     * 
     * @return the ping in ms
     */
    public int getPing() {
        return ping;
    }

    /**
     * Interrupts the keepalive thread. Use this to immediately kill the connection rather than waiting for the next ping
     */
    public void stop() {
        self.interrupt();
    }
}
