package org.nolat.scrolls.utils;

import org.apache.log4j.Logger;
import org.nolat.scrolls.network.Messages.LobbyLookup;
import org.nolat.scrolls.network.Messages.ServerInfo;
import org.nolat.scrolls.network.ScrollsConnection;

import com.google.common.eventbus.Subscribe;

/**
 * Creates a connection to the Scrolls load balancing server in order to retrieve an IP address for the main lobby.
 */
public class ScrollsLoadBalancer {

    private static final Logger log = Logger.getLogger(ScrollsLoadBalancer.class);
    private static final LobbyLookup LOBBY_LOOKUP_MESSAGE = new LobbyLookup();

    private String scrollsLobbyIp = null;
    private final ScrollsConnection connection;

    public ScrollsLoadBalancer() {
        connection = new ScrollsConnection();
        connection.getMessageRouter().register(this);
        connection.sendMessage(LOBBY_LOOKUP_MESSAGE);
    }

    @Subscribe
    public void onReceiveServerInfo(ServerInfo message) {
        log.info("Load Balancing Server Version: " + message.version);
    }

    @Subscribe
    public void onReceiveLobbyInfo(LobbyLookup message) {
        log.trace("Received lobby ip address: " + message + ":" + message.port);
        scrollsLobbyIp = message.ip;
    }

    /**
     * This method blocks until it receives a message from the server with the correct information
     * 
     * @return the Scrolls lobby IP that was handed out by the load balancing server.
     */
    public String getScrollsLobbyIp() {
        if (scrollsLobbyIp == null) {
            try {
                connection.getReceiverThread().join();
            } catch (InterruptedException e) {
                log.error(
                        "The receiving thread was interrupted before a lobby ip address could be retrieved: "
                                + e.getMessage(), e);
            }
        }
        return scrollsLobbyIp;
    }
}
