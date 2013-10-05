package org.nolat.scrolls.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ServerConnection implements Runnable, PacketListener {

    private static final Logger log = Logger.getLogger(ServerConnection.class);
    /**
     * Mojang's Amazon EC2 Instance used for load balancing. <br>
     * Hostname: ec2-107-21-58-31.compute-1.amazonaws.com <br>
     * <p>
     * Connecting to this server on port 8081 and sending a 'LobbyLookup' packet returns the IP address of a Lobby server to connect to.
     * </p>
     */
    public static final String SCROLLS_LOAD_BALANCER = "107.21.58.31";
    public static final String SCROLLS_MAIN_SERVER = "54.208.22.193";

    public static final int SCROLLS_PORT = 8081;

    private final List<PacketListener> listeners;

    private PrintWriter out = null;
    private BufferedReader in = null;
    private Socket socket = null;

    /**
     * Opens a connection to the given hostname on the default ServerConnection.SCROLLS_PORT
     * 
     * @param hostname
     *            the hostname or ip to connect to.
     */
    public ServerConnection(String hostname) {
        try {
            log.info("Attempting to open connection to " + hostname + ":" + SCROLLS_PORT);
            socket = new Socket(hostname, SCROLLS_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            new Thread(this, hostname + "-receiver-thread").start();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        listeners = new ArrayList<>();
    }

    /**
     * Opens a connection to the Scrolls load balancing server.
     */
    public ServerConnection() {
        this(SCROLLS_LOAD_BALANCER);
    }

    /**
     * Adds a {@link PacketListener} to the listeners list
     * 
     * @param listener
     */
    public void addPacketListener(PacketListener listener) {
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
        } else {
            out.print(packet); //sends packet to the server
            out.flush();
        }
    }

    /**
     * Disconnects from the server. Make sure to do this when you're done or else there might be leaks
     */
    public void shutdown() {
        try {
            socket.close();
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
                    log.debug("RCVD: '" + packet + "'");
                    onReceivedPacket(packet);
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
        log.info("Finished reading from the socket");
    }

    @Override
    public void onReceivedPacket(String packet) {
        for (PacketListener listener : listeners) {
            listener.onReceivedPacket(packet);
        }
    }
}
