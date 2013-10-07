package org.nolat.scrolls.network;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class PacketRouter implements RawPacketListener {

    private static final Logger log = Logger.getLogger(PacketRouter.class);
    private final List<PacketListener<Packets.Ping>> listeners;
    private final Gson gson;

    /**
     * Handles conversion and routing of packets at a higher level
     * 
     * @param connection
     *            the connection to monitor packets on
     */
    public PacketRouter(ServerConnection connection) {
        connection.addRawPacketListener(this);
        listeners = new ArrayList<>();
        gson = new Gson();

    }

    @Override
    public void onReceivedRawPacket(String packet) {
        try {
            //convert to general packet to find out 'msg'
            Packets.TypeCheck general = gson.fromJson(packet, Packets.TypeCheck.class);
            log.debug("Received '" + general.msg + "' packet");

            if (general.msg.equals(Packets.PING)) {
                Packets.Ping pingPacket = gson.fromJson(packet, Packets.Ping.class);
                for (PacketListener<Packets.Ping> listener : listeners) {
                    listener.onReceivedPacket(pingPacket);
                }
            }
        } catch (JsonSyntaxException ex) {
            log.warn("Failed to parse packet: '" + packet + "'", ex);
        }
    }

    public void addPingPacketListener(PacketListener<Packets.Ping> packetListener) {
        listeners.add(packetListener);
    }
}
