package org.nolat.scrolls.network;

public interface PacketListener<T> {

    public void onReceivedPacket(T packet);
}
