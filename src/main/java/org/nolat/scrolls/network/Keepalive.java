package org.nolat.scrolls.network;

import org.apache.log4j.Logger;

/**
 * Keeps a {@link ServerConnection} alive by sending Ping packets periodically.
 */
public class Keepalive implements Runnable, RawPacketListener {

    private static final Logger log = Logger.getLogger(Keepalive.class);
    private static final int DEFAULT_TICK = 15000; //default 15 second delay between Ping

    private final ServerConnection connection;
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
    public Keepalive(ServerConnection connection, int tick) {
        log.info("Starting Keepalive for " + connection.toString());
        this.connection = connection;
        this.tick = tick;
        this.connection.addRawPacketListener(this); //register self as packet listener to receive Ping packets from server
        self = new Thread(this, connection.getHostname() + "-keepalive");
        self.start();
    }

    /**
     * Creates a Keepalive session using the default tick rate
     * 
     * @param connection
     *            the connection to keep alive
     */
    public Keepalive(ServerConnection connection) {
        this(connection, DEFAULT_TICK);
    }

    @Override
    public void run() {
        while (!connection.getSocket().isClosed()) {
            epoch = System.currentTimeMillis();
            connection.sendPacket(Packets.getPacket(Packets.Ping.class));
            try {
                Thread.sleep(tick);
            } catch (InterruptedException e) {
                log.warn("Keepalive sleep interrupted - stopping");
            }
        }
        log.info("Ending keepalive thread");
    }

    @Override
    public void onReceivedRawPacket(String packet) {
        if (packet.contains("Ping")) {
            ping = (int) (Math.random() * 140); //TODO actually calculate ping
            log.info("Ping=" + ping + " ms");
        }
    }

    /**
     * Gets the ping in ms to the server
     * 
     * @return the ping in ms
     */
    public int getPing() {
        return ping;
    }

    public void stop() {
        self.interrupt();
    }
}
