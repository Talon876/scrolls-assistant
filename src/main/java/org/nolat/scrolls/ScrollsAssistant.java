package org.nolat.scrolls;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.nolat.scrolls.network.Encryption;
import org.nolat.scrolls.network.Messages;
import org.nolat.scrolls.network.ScrollsConnection;
import org.nolat.scrolls.utils.ScrollsLoadBalancer;
import org.nolat.scrolls.utils.Settings;

import com.google.common.eventbus.Subscribe;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

public class ScrollsAssistant {
    public static final String CLIENT_VERSION = "0.105.0";
    private static final Logger log = Logger.getLogger(ScrollsAssistant.class);

    private final ScrollsConnection scrolls;
    private final Settings settings;

    public ScrollsAssistant(Settings settings) {
        this.settings = settings;
        ScrollsLoadBalancer balancer = new ScrollsLoadBalancer();
        scrolls = new ScrollsConnection(balancer.getScrollsLobbyIp());
        scrolls.getMessageRouter().register(this);
        scrolls.sendMessage(new Messages.SignIn(this.settings.getEncryptedEmail(),
                this.settings.getEncryptedPassword(), false));
        scrolls.sendMessage(new Messages.SetAcceptChallenges(false));
        scrolls.sendMessage(new Messages.SetAcceptTrades(false));
        for (int i = 1; i <= 30; i++) {
            scrolls.sendMessage(new Messages.RoomEnter("trading-" + i));
        }
    }

    public Settings getSettings() {
        return settings;
    }

    @Subscribe
    public void receiveChat(Messages.RoomChatMessage message) {
        System.out.println(message.from + "@" + message.roomName + ": " + message.text);
    }

    @Subscribe
    public void receiveWhisper(Messages.Whisper message) {
        System.out.println("Whisper from " + message.from + ": " + message.text);
    }

    @Subscribe
    public void logServerInfo(Messages.ServerInfo serverInfo) {
        if (!serverInfo.version.equals(CLIENT_VERSION)) {
            log.warn("This client was designed for version " + CLIENT_VERSION + ", things may be unstable.");
        }
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
        JSAP jsap = null;
        try {
            jsap = createJSAP();
            JSAPResult config = jsap.parse(args);
            if (config.success()) {
                if (config.getBoolean("encrypt")) {
                    System.out.println("Encrypting Username/Password for settings.json file:");
                    String username = config.getString("username");
                    String password = config.getString("password");
                    System.out.println("Encrypted username: " + Encryption.encrypt(username));
                    System.out.println("Encrypted password: " + Encryption.encrypt(password));
                } else {
                    String filePath = config.getString("file");
                    System.out.println(filePath);
                    Settings settings = new Settings(filePath);
                    new ScrollsAssistant(settings);
                }
            } else {
                help(jsap);
            }
        } catch (JSAPException ex) {
            log.error("Could not parse arguments", ex);
        }
    }

    private static void help(JSAP jsap) {
        System.out.println("Usage: java -jar scrolls-assistant.jar [options]");
        System.out.println("Examples: Run: java -jar scrolls-assistant.jar -f settings.json");
        System.out.println("\tEncrypt: java -jar scrolls-assistant.jar -e -u someone@somewhere.net -p myp@ssw0rd!");
        System.out.println("\nHelp:");
        System.out.println(jsap.getHelp());
    }

    private static JSAP createJSAP() throws JSAPException {
        JSAP jsap = new JSAP();

        Switch switchOpt = new Switch("help");
        switchOpt.setShortFlag('h');
        switchOpt.setLongFlag("help");
        switchOpt.setHelp("Displays this help message");
        jsap.registerParameter(switchOpt);

        Switch switchOptEncrypt = new Switch("encrypt");
        switchOptEncrypt.setShortFlag('e');
        switchOptEncrypt.setLongFlag("encrypt");
        switchOptEncrypt.setHelp("Encrypt's username/password");
        jsap.registerParameter(switchOptEncrypt);

        FlaggedOption fileOpt = new FlaggedOption("file");
        fileOpt.setStringParser(JSAP.STRING_PARSER);
        fileOpt.setShortFlag('f');
        fileOpt.setLongFlag("file");
        fileOpt.setHelp("The path to the file to load settings from");
        fileOpt.setDefault("settings.json");
        jsap.registerParameter(fileOpt);

        FlaggedOption usernameOpt = new FlaggedOption("username");
        usernameOpt.setStringParser(JSAP.STRING_PARSER);
        usernameOpt.setShortFlag('u');
        usernameOpt.setLongFlag("username");
        usernameOpt.setHelp("The username to encrypt when using --encrypt");
        jsap.registerParameter(usernameOpt);

        FlaggedOption passwordOpt = new FlaggedOption("password");
        passwordOpt.setStringParser(JSAP.STRING_PARSER);
        passwordOpt.setShortFlag('p');
        passwordOpt.setLongFlag("password");
        passwordOpt.setHelp("The password to encrypt when using --encrypt");
        jsap.registerParameter(passwordOpt);
        return jsap;
    }
}