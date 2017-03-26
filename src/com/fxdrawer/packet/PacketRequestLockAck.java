package com.fxdrawer.packet;

import com.fxdrawer.socket.SocketHandler;

import java.util.Date;

public class PacketRequestLockAck extends Packet {
    private final long timestamp;

    public int getPort() {
        return port;
    }

    private final int port;

    public PacketRequestLockAck(int port) {
        this.timestamp = new Date().getTime();
        this.port = port;
    }

    @Override
    public void process(SocketHandler handler) {
        handler.receivedRequestLockAckPacket(this);
    }

    @Override
    public String toString() {
        return "Request lock: " + this.timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
