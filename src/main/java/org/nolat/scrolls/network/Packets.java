package org.nolat.scrolls.network;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class Packets {
    public static final String PING = "Ping";
    public static final String LOBBY_LOOKUP = "LobbyLookup";
    public static final String SERVER_INFO = "ServerInfo";

    private static final Logger log = Logger.getLogger(Packets.class);
    private static final HashMap<String, String> packets;
    private static final HashMap<String, Class<?>> packetTypes;
    private static final Gson gson = new Gson();

    static {
        packets = new HashMap<>();
        packets.put(Ping.class.getSimpleName(), toJson(new Ping()));
        packets.put(LobbyLookup.class.getSimpleName(), toJson(new LobbyLookup()));

        packetTypes = new HashMap<>();
        packetTypes.put(PING, Ping.class);
        packetTypes.put(LOBBY_LOOKUP, LobbyLookup.class);
        packetTypes.put(SERVER_INFO, ServerInfo.class);
    }

    public static String getPacket(Class<?> packetType) {
        String packet = packets.get(packetType.getSimpleName());
        if (packet == null) {
            log.warn("Unable to retrieve packet for packet type '" + packetType + "'");
        }
        return packet;
    }

    public static String getPacket(String packetType) {
        String packet = packets.get(packetType);

        return packet;
    }

    public static Class<?> getPacketType(String packetType) {
        Class<?> clazz = packetTypes.get(packetType);
        if (clazz == null) {
            log.warn("Unknown packet type: " + packetType);
        }
        return clazz;
    }

    public static String toJson(Object packet) {
        return gson.toJson(packet);
    }

    /**
     * Intermediate packet used when parsing that lets the application determine the 'msg'.
     */
    public static class TypeCheck {
        public String msg;
    }

    public static class ServerInfo {
        public String msg = "ServerInfo";
        public String assetURL;
        public String version;
    }

    public static class LobbyLookup {
        public String msg = "LobbyLookup";
        public String ip;
        public String port;
    }

    public static class Ping {
        public String msg = "Ping";
        public long time;
    }

    public static class SignIn {
        public String msg = "SignIn";
        public String email;
        public String password;

        /**
         * Create a SignIn packet and automatically RSA encrypts the email and password using Mojang's Public key
         * 
         * @param email
         *            the email of the user
         * @param password
         *            the password of the user
         */
        public SignIn(String email, String password) {
            this.email = Encryption.encrypt(email);
            this.password = Encryption.encrypt(password);
        }
    }

    public static class GetFriendRequests {
        public String msg = "GetFriendRequests";
        public String[] requests;
    }

    public static class GetBlockedPersons {
        public String msg = "GetBlockedPersons";
        public String[] blocked;
    }

    public static class ProfileInfo {
        public String msg = "ProfileInfo";
        public Profile profile;

        public static class Profile {
            public String id;
            public String userUuid;
            public String name;
            public boolean acceptChallenges;
            public boolean acceptTrades;
            public String adminRole;
            public String userType;
        }
    }

    public static class ProfileDataInfo {
        public String msg = "ProfileDataInfo";
        public ProfileData profileData;

        public static class ProfileData {
            public int gold;
            public int shards;
            public int rating;
            public int selectedPreconstructed;
        }
    }

    public static class Ok {
        public String msg = "Ok";
        public String op;
    }

    public static class RoomEnter {
        public String msg = "RoomEnter";
        public String roomName;
    }

    public static class RoomChatMessage {
        public String msg = "RoomChatMessage";
        public String roomName;
        public String from;
        public String text;
    }
}
