package org.nolat.scrolls;

import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.nolat.scrolls.network.Encryption;
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
        scrolls.sendMessage(new Messages.SignIn("--encrypted email--", "--encrypted password--", false));
        scrolls.sendMessage(new Messages.SetAcceptChallenges(false));
        scrolls.sendMessage(new Messages.SetAcceptTrades(false));
        for (int i = 1; i <= 30; i++) {
            scrolls.sendMessage(new Messages.RoomEnter("trading-" + i));
        }
    }

    @Subscribe
    public void receiveWhisper(Messages.Whisper message) {
        System.out.println("Whisper from " + message.from + ": " + message.text);
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
        if (args.length == 3 && args[0].equalsIgnoreCase("encrypt")) {
            String username = args[1];
            String password = args[2];
            System.out.println(args[1] + " : " + args[2]);
            System.out.println("Encrypted username: " + Encryption.encrypt(username));
            System.out.println("Encrypted password: " + Encryption.encrypt(password));
        } else {
            log.info("Starting " + ScrollsAssistant.class.getSimpleName() + " " + (new Date()).toString());
            new ScrollsAssistant();
        }
    }
}