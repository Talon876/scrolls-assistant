package org.nolat.scrolls.network;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class PacketRouter implements RawPacketListener {

    private static final Logger log = Logger.getLogger(PacketRouter.class);
    private final Gson gson;

    /**
     * Handles conversion and routing of packets at a higher level
     * 
     * @param connection
     *            the connection to monitor packets on
     */
    public PacketRouter(ServerConnection connection) {
        connection.addRawPacketListener(this);
        gson = new Gson();
    }

    @Override
    public void onReceivedRawPacket(String packet) {
        try {
            //convert to general packet to find out 'msg'
            Packets.TypePacket general = gson.fromJson(packet, Packets.TypePacket.class);
            log.debug("Received '" + general.msg + "' packet");
        } catch (JsonSyntaxException ex) {
            log.warn("Failed to parse packet: '" + packet + "'", ex);
        }
    }
}
