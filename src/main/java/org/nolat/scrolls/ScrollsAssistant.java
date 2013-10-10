package org.nolat.scrolls;

import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.nolat.scrolls.network.Messages;
import org.nolat.scrolls.network.ScrollsConnection;

import com.google.common.eventbus.Subscribe;

public class ScrollsAssistant {

    private static final Logger log = Logger.getLogger(ScrollsAssistant.class);

    public ScrollsAssistant() {
        ScrollsConnection connection = new ScrollsConnection();
        connection.getMessageRouter().register(this);
        pause(30000);
        connection.sendMessage(Messages.getMessage(Messages.LOBBY_LOOKUP));

    }

    @Subscribe
    public void logServerInfo(Messages.ServerInfo serverInfo) {
        log.info("Server Version: " + serverInfo.version);
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