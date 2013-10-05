package org.nolat.scrolls.network;

public class Packets {

    public static class ServerInfo {
        public String msg;
        public String assetURL;
        public String version;
    }

    public static class LobbyLookup {
        public String msg;
        public String ip;
        public String port;
    }
}
