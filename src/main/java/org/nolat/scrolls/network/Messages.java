package org.nolat.scrolls.network;

import java.util.HashMap;

import com.google.gson.Gson;

@SuppressWarnings("unused")
public class Messages {
    private Messages() {
    }

    private static final HashMap<String, Class<? extends Message>> messageTypes;
    private static final Gson gson = new Gson();

    static {
        messageTypes = new HashMap<>();

        messageTypes.put("ServerInfo", ServerInfo.class);
        messageTypes.put("LobbyLookup", LobbyLookup.class);
        messageTypes.put("Ping", Ping.class);
        messageTypes.put("SignIn", SignIn.class);
        messageTypes.put("GetFriendRequests", GetFriendRequests.class);
        messageTypes.put("GetBlockedPersons", GetBlockedPersons.class);
        messageTypes.put("ProfileInfo", ProfileInfo.class);
        messageTypes.put("ProfileDataInfo", ProfileDataInfo.class);
        messageTypes.put("Ok", Ok.class);
        messageTypes.put("RoomEnter", RoomEnter.class);
        messageTypes.put("RoomChatMessage", RoomChatMessage.class);
        messageTypes.put("Whisper", Whisper.class);
        messageTypes.put("GetFriends", GetFriends.class);
        messageTypes.put("RoomInfo", RoomInfo.class);
    }

    /**
     * Finds the class corresponding for the Message type obtained from "msg" parameter
     * 
     * @param messageType
     *            the "msg" parameter in the json string
     * @return the Class the message should be deserialized to. May be null
     */
    public static Class<? extends Message> getMessageType(String messageType) {
        return messageTypes.get(messageType);
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

    //---Message Classes---
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
         * Create a SignIn message using pre-encrypted credentials, or automatically encrypt the credentials using Mojang's Public Key
         * 
         * @param email
         *            the email of the user
         * @param password
         *            the password of the user
         * @param encrypt
         *            whether or not to encrypt the provided email and password
         */
        public SignIn(String email, String password, boolean encrypt) {
            if (encrypt) {
                this.email = Encryption.encrypt(email);
                this.password = Encryption.encrypt(password);
            } else {
                this.email = email;
                this.password = password;
            }
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

        /**
         * 
         * @param roomName
         *            the name of a room to join
         */
        public RoomEnter(String roomName) {
            this.roomName = roomName;
        }
    }

    public static class RoomChatMessage extends Message {
        public String msg = "RoomChatMessage";
        public String roomName;
        public String from;
        public String text;
    }

    public static class Whisper extends Message {
        public String msg = "Whisper";
        public String toProfilename;
        public String from;
        public String text;
    }

    public static class GetFriends extends Message {
        public String msg = "GetFriends";
        public FriendProfile[] friends;

        private static class FriendProfile {
            public Profile profile;
            public boolean isOnline;
            public boolean isInBattle;
        }
    }

    public static class SetAcceptChallenges extends Message {
        public String msg = "SetAcceptChallenges";
        public boolean acceptChallenges;

        /**
         * 
         * @param acceptChallenges
         *            whether or not the client will accept challenge requests
         */
        public SetAcceptChallenges(boolean acceptChallenges) {
            this.acceptChallenges = acceptChallenges;
        }
    }

    public static class SetAcceptTrades extends Message {
        public String msg = "SetAcceptTrades";
        public boolean acceptTrades;

        /**
         * 
         * @param acceptTrades
         *            whether or not the client will accept challenge requests
         */
        public SetAcceptTrades(boolean acceptTrades) {
            this.acceptTrades = acceptTrades;
        }
    }

    public static class RoomInfo extends Message {
        public String msg = "RoomInfo";
        public String roomName;
        public boolean reset;
        public Profile[] updated;
    }

    //---Data Classes---
    private static class Profile {
        public String id;
        public String userUuid;
        public String name;
        public boolean acceptChallenges;
        public boolean acceptTrades;
        public String adminRole;
        public String userType;
    }
}
