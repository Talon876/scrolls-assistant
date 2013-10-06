package org.nolat.scrolls.network;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class Packets {

    private static final Logger log = Logger.getLogger(Packets.class);
    private static final Gson gson = new Gson();
    private static final HashMap<String, String> packets;

    static {
        packets = new HashMap<String, String>();
        packets.put(Ping.class.getSimpleName(), toJson(new Ping()));
        packets.put(LobbyLookup.class.getSimpleName(), toJson(new LobbyLookup()));
    }

    public static String getPacket(Class<? extends Packet> packetType) {
        String packet = packets.get(packetType.getSimpleName());
        if (packet == null) {
            log.warn("Unable to retrieve packet for packet type '" + packetType + "'");
        }
        return packet;
    }

    public static String toJson(Object packet) {
        return gson.toJson(packet);
    }

    /**
     * Intermediate packet used when parsing that lets the application determine the 'msg'.
     */
    public static class TypePacket {
        public String msg;
    }

    public static abstract class Packet {
    }

    public static class ServerInfo extends Packet {
        public String msg = "ServerInfo";
        public String assetURL;
        public String version;
    }

    public static class LobbyLookup extends Packet {
        public String msg = "LobbyLookup";
        public String ip;
        public String port;
    }

    public static class Ping extends Packet {
        public String msg = "Ping";
        public String time;
    }
}
