package org.nolat.scrolls.network;

import org.apache.log4j.Logger;
import org.nolat.scrolls.network.Packets.Ping;

/**
 * Keeps a {@link ServerConnection} alive by sending Ping packets periodically.
 */
public class Keepalive implements Runnable {

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
        self = new Thread(this, connection.getHostname() + "-keepalive");
        self.start();

        PacketListener<Packets.Ping> pingListener = new PacketListener<Packets.Ping>() {
            @Override
            public void onReceivedPacket(Ping packet) {
                ping = (int) (packet.time - epoch) / 1000;
                System.out.println("Server Time: " + packet.time + "; Ping: " + ping + "ms");
            }
        };
        this.connection.getPacketRouter().addPingPacketListener(pingListener);
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
            connection.sendPacket(Packets.getPacket(Packets.PING));
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
