package com.fxdrawer.packet;

import com.fxdrawer.socket.SocketHandler;

public class PacketConnectToPeer extends Packet {
    private final String hostName;
    private final int portNumber;

    public PacketConnectToPeer(String hostName, int portNumber) {
        this.hostName = hostName;
        this.portNumber = portNumber;
    }
    @Override
    public void process(SocketHandler handler) {
        handler.receivedConnectToPeerPacket(this);
    }

    public String getHostName() {
        return hostName;
    }

    public int getPortNumber() {
        return portNumber;
    }
}
