package org.nolat.scrolls.network;

import org.apache.log4j.Logger;
import org.nolat.scrolls.network.Messages.Message;

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
            Class<? extends Message> messageType = Messages.getMessageType(general.msg);
            if (messageType != null) {
                post(gson.fromJson(rawMessage, messageType));
            } else {
                log.warn("Unable to find Message type for '" + general.msg
                        + "'. Is it registered in the Messages list?");
            }

        } catch (JsonSyntaxException ex) {
            log.warn("Failed to parse message: '" + rawMessage + "'", ex);
        }
    }
}
