package org.nolat.scrolls.network;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

public class Messages {
    public static final String PING = "Ping";
    public static final String LOBBY_LOOKUP = "LobbyLookup";
    public static final String SERVER_INFO = "ServerInfo";

    private static final Logger log = Logger.getLogger(Messages.class);
    private static final HashMap<String, String> messages;
    private static final HashMap<String, Class<? extends Message>> messageTypes;
    private static final Gson gson = new Gson();

    static {
        messages = new HashMap<>();
        messages.put(Ping.class.getSimpleName(), toJson(new Ping()));
        messages.put(LobbyLookup.class.getSimpleName(), toJson(new LobbyLookup()));

        messageTypes = new HashMap<>();
        messageTypes.put(PING, Ping.class);
        messageTypes.put(LOBBY_LOOKUP, LobbyLookup.class);
        messageTypes.put(SERVER_INFO, ServerInfo.class);
    }

    public static String getMessage(Class<?> messageType) {
        String message = messages.get(messageType.getSimpleName());
        if (message == null) {
            log.warn("Unable to retrieve message for message type '" + messageType + "'");
        }
        return message;
    }

    public static String getMessage(String messageType) {
        String message = messages.get(messageType);

        return message;
    }

    public static Class<? extends Message> getMessageType(String messageType) {
        Class<? extends Message> clazz = messageTypes.get(messageType);
        if (clazz == null) {
            log.warn("Unknown message type: " + messageType);
        }
        return clazz;
    }

    public static String toJson(Object message) {
        return gson.toJson(message);
    }

    /**
     * Intermediate message used when parsing that lets the application determine the 'msg'.
     */
    public static class TypeCheck {
        public String msg;
    }

    public static abstract class Message {
        @Override
        public String toString() {
            return toJson(this);
        }
    }

    public static class ServerInfo extends Message {
        public String msg = "ServerInfo";
        public String assetURL;
        public String version;
    }

    public static class LobbyLookup extends Message {
        public String msg = "LobbyLookup";
        public String ip;
        public String port;
    }

    public static class Ping extends Message {
        public String msg = "Ping";
        public long time;
    }

    public static class SignIn extends Message {
        public String msg = "SignIn";
        public String email;
        public String password;

        /**
         * Create a SignIn message and automatically RSA encrypts the email and password using Mojang's Public key
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

    public static class GetFriendRequests extends Message {
        public String msg = "GetFriendRequests";
        public String[] requests;
    }

    public static class GetBlockedPersons extends Message {
        public String msg = "GetBlockedPersons";
        public String[] blocked;
    }

    public static class ProfileInfo extends Message {
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

    public static class ProfileDataInfo extends Message {
        public String msg = "ProfileDataInfo";
        public ProfileData profileData;

        public static class ProfileData {
            public int gold;
            public int shards;
            public int rating;
            public int selectedPreconstructed;
        }
    }

    public static class Ok extends Message {
        public String msg = "Ok";
        public String op;
    }

    public static class RoomEnter extends Message {
        public String msg = "RoomEnter";
        public String roomName;
    }

    public static class RoomChatMessage extends Message {
        public String msg = "RoomChatMessage";
        public String roomName;
        public String from;
        public String text;
    }
}
