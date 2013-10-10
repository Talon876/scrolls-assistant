package org.nolat.scrolls.network;

import org.apache.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Responsible for parsing raw strings in to packet classes and sending them to the event bus.
 */
public class MessageRouter extends EventBus implements RawMessageListener {

    private static final Logger log = Logger.getLogger(MessageRouter.class);
    private final Gson gson;

    /**
     * Handles conversion and routing of messages at a higher level
     * 
     * @param connection
     *            the connection to monitor messages on
     */
    public MessageRouter(ScrollsConnection connection) {
        super();
        connection.addRawMessageListener(this);
        gson = new Gson();
    }

    @Override
    public void onReceivedRawMessage(String rawMessage) {
        try {
            //convert to general packet to find out 'msg'
            Messages.TypeCheck general = gson.fromJson(rawMessage, Messages.TypeCheck.class);
            log.debug("Received '" + general.msg + "' message: " + rawMessage);

            if (general.msg.equals(Messages.PING)) {
                Messages.Ping pingPacket = gson.fromJson(rawMessage, Messages.Ping.class);
                post(pingPacket);
            } else if (general.msg.equals(Messages.SERVER_INFO)) {
                Messages.ServerInfo serverInfoPacket = gson.fromJson(rawMessage, Messages.ServerInfo.class);
                post(serverInfoPacket);
            } else if (general.msg.equals(Messages.LOBBY_LOOKUP)) {
                Messages.LobbyLookup lobbyLookup = gson.fromJson(rawMessage, Messages.LobbyLookup.class);
                post(lobbyLookup);
            }
        } catch (JsonSyntaxException ex) {
            log.warn("Failed to parse message: '" + rawMessage + "'", ex);
        }
    }
}
