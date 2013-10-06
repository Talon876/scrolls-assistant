package org.nolat.scrolls;

import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.nolat.scrolls.network.ServerConnection;

public class ScrollsAssistant {

    private static final Logger log = Logger.getLogger(ScrollsAssistant.class);

    public ScrollsAssistant() {
        ServerConnection connection = new ServerConnection();
        String LOBBY_LOOKUP = "{\"msg\":\"LobbyLookup\"}";

        //        connection.sendPacket(PING);
        //        pause(1500);
        //        connection.sendPacket(PING);
        //        pause(1500);
    }

    public static void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Entry point for the Scrolls Assistant
     * 
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        log.info("Starting " + ScrollsAssistant.class.getSimpleName() + " " + (new Date()).toString());
        new ScrollsAssistant();
    }
}