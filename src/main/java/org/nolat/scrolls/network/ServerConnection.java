package org.nolat.scrolls.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ServerConnection implements Runnable, RawPacketListener {

    private static final Logger log = Logger.getLogger(ServerConnection.class);
    /**
     * Mojang's Amazon EC2 Instance used for load balancing. <br>
     * Hostname: ec2-107-21-58-31.compute-1.amazonaws.com <br>
     * <p>
     * Connecting to this server on port 8081 and sending a 'LobbyLookup' packet returns the IP address of a Lobby server to connect to.
     * </p>
     */
    public static final String SCROLLS_LOAD_BALANCER = "107.21.58.31";

    public static final int SCROLLS_PORT = 8081;

    private final List<RawPacketListener> listeners;

    private final String hostname;

    private PrintWriter out = null;
    private BufferedReader in = null;
    private Socket socket = null;
    private Keepalive keepalive = null;
    private final PacketRouter packetRouter;

    /**
     * Opens a connection to the given hostname on the default ServerConnection.SCROLLS_PORT
     * 
     * @param hostname
     *            the hostname or ip to connect to.
     * @param keepAlive
     *            whether or not to keep the connection alive by sending Ping messages
     */
    public ServerConnection(String hostname, boolean keepAlive) {
        this.hostname = hostname;
        listeners = new ArrayList<>();
        try {
            log.info("Attempting to open connection to " + hostname + ":" + SCROLLS_PORT);
            socket = new Socket(hostname, SCROLLS_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(this, hostname + "-receiver").start();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        packetRouter = new PacketRouter(this);
        if (keepAlive) {
            keepalive = new Keepalive(this, 2500);
        }

    }

    /**
     * Opens a connection to the Scrolls load balancing server.
     */
    public ServerConnection() {
        this(SCROLLS_LOAD_BALANCER, true); //TODO load balancer won't require keepalive
    }

    /**
     * Adds a {@link RawPacketListener} to the listeners list
     * 
     * @param listener
     */
    public void addRawPacketListener(RawPacketListener listener) {
        listeners.add(listener);
    }


    /**
     * Sends a packet to the server
     * 
     * @param packet
     *            The packet to be sent to the server
     */
    public void sendPacket(String packet) {
        log.debug("SEND: '" + packet + "'");
        if (socket.isClosed()) {
            log.error("Socket is closed. Cannot write");
        } else if (packet == null || packet.isEmpty()) {
            log.warn("Packet '" + packet + "' is null or empty. Refusing to send");
        } else {
            out.print(packet); //sends packet to the server
            out.flush();
        }
    }

    /**
     * Disconnects from the server. Make sure to do this when you're done or else there might be leaks
     */
    public void shutdown() {
        log.info("Shutting down server connection");
        try {
            socket.close();
            if (keepalive != null) {
                keepalive.stop();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void run() {
        String packet = "";
        try {
            while ((packet = in.readLine()) != null) {
                if (!packet.isEmpty()) {
                    onReceivedRawPacket(packet);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            try {
                log.error("Shutting down socket.");
                socket.close();
            } catch (IOException ex) {
                log.error(e.getMessage(), ex);
            }
        }
        shutdown();
    }

    @Override
    public void onReceivedRawPacket(String packet) {
        log.trace("RCVD: '" + packet + "'");
        for (RawPacketListener listener : listeners) {
            listener.onReceivedRawPacket(packet);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public String getHostname() {
        return hostname;
    }

    public PacketRouter getPacketRouter() {
        return packetRouter;
    }

    /**
     * Gets the ping if keepalive is enabled, otherwise returns 0;
     * 
     * @return the ping if keepalive is enabled, otherwise 0.
     */
    public int getPing() {
        return keepalive != null ? keepalive.getPing() : 0;
    }

    @Override
    public String toString() {
        return hostname + ":" + SCROLLS_PORT;
    }
}
