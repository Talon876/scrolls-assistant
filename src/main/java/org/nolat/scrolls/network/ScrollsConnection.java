package org.nolat.scrolls.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.nolat.scrolls.network.Messages.Message;

public class ScrollsConnection implements Runnable, RawMessageListener {

    private static final Logger log = Logger.getLogger(ScrollsConnection.class);
    /**
     * Mojang's Amazon EC2 Instance used for load balancing. <br>
     * Hostname: ec2-107-21-58-31.compute-1.amazonaws.com <br>
     * <p>
     * Connecting to this server on port 8081 and sending a 'LobbyLookup' message returns the IP address of a Lobby server to connect to.
     * </p>
     */
    public static final String SCROLLS_LOAD_BALANCER = "107.21.58.31";

    public static final int SCROLLS_PORT = 8081;

    private final List<RawMessageListener> listeners;

    private final String hostname;

    private PrintWriter out = null;
    private BufferedReader in = null;
    private Socket socket = null;
    private Keepalive keepalive = null;
    private final MessageRouter messageRouter;
    private final Thread receiverThread;

    /**
     * Opens a connection to the given hostname on the default ServerConnection.SCROLLS_PORT
     * 
     * @param hostname
     *            the hostname or ip to connect to.
     * @param keepAlive
     *            whether or not to keep the connection alive by sending Ping messages
     */
    public ScrollsConnection(String hostname, boolean keepAlive) {
        this.hostname = hostname;
        listeners = new ArrayList<>();
        messageRouter = new MessageRouter(this);
        try {
            socket = new Socket(hostname, SCROLLS_PORT);
            socket.setSoTimeout(0); //no timeout
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        receiverThread = new Thread(this, hostname + "-receiver");
        receiverThread.start();
        log.info("Opened connection to " + hostname + ":" + SCROLLS_PORT);
        if (keepAlive) {
            keepalive = new Keepalive(this);
        }
    }

    /**
     * Opens a connection to the specified hostname on the default port with keepalive enabled.
     * 
     * @param hostname
     *            the hostname to connect to
     */
    public ScrollsConnection(String hostname) {
        this(hostname, true);
    }

    /**
     * Opens a connection to the Scrolls load balancing server.
     */
    public ScrollsConnection() {
        this(SCROLLS_LOAD_BALANCER, false);
    }

    /**
     * Adds a {@link RawMessageListener} to the listeners list
     * 
     * @param listener
     */
    public void addRawMessageListener(RawMessageListener listener) {
        listeners.add(listener);
    }

    /**
     * Sends a raw string message to the server
     * 
     * @param message
     *            The raw message to be sent to the server
     */
    public void sendRawMessage(String message) {
        log.debug("SEND: '" + message + "'");
        if (socket.isClosed()) { //don't send if socket is closed
            log.error("Socket is closed. Cannot write");
        } else if (message == null || message.isEmpty()) { //or if the message is null/empty
            log.warn("message '" + message + "' is null or empty. Refusing to send");
        } else {
            out.print(message); //sends message to the server
            out.flush();
        }
    }

    /**
     * Sends a Message to the server after serializing it to json.
     * 
     * @param message
     *            the Message to be sent
     */
    public void sendMessage(Message message) {
        sendRawMessage(message.toString());
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
        String message = "";
        try {
            while ((message = in.readLine()) != null) {
                if (!message.isEmpty()) {
                    onReceivedRawMessage(message);
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
    public void onReceivedRawMessage(String message) {
        log.trace("RCVD: '" + message + "'");
        for (RawMessageListener listener : listeners) {
            listener.onReceivedRawMessage(message);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public String getHostname() {
        return hostname;
    }

    public MessageRouter getMessageRouter() {
        return messageRouter;
    }

    /**
     * The thread that the socket is being read/written from/on.
     * 
     * @return the thread
     */
    public Thread getReceiverThread() {
        return receiverThread;
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
