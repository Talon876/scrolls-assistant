package org.nolat.scrolls;

import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.nolat.scrolls.network.Messages;
import org.nolat.scrolls.network.ScrollsConnection;
import org.nolat.scrolls.utils.ScrollsLoadBalancer;

import com.google.common.eventbus.Subscribe;

public class ScrollsAssistant {

    private static final Logger log = Logger.getLogger(ScrollsAssistant.class);

    private final ScrollsConnection scrolls;

    public ScrollsAssistant() {
        ScrollsLoadBalancer balancer = new ScrollsLoadBalancer();
        scrolls = new ScrollsConnection(balancer.getScrollsLobbyIp());
        scrolls.getMessageRouter().register(this);
    }

    @Subscribe
    public void logServerInfo(Messages.ServerInfo serverInfo) {
        log.info("Scrolls Server Version: " + serverInfo.version);
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