package com.fxdrawer.packet;

import com.fxdrawer.socket.SocketHandler;
import java.util.Date;

public class PacketRequestLock extends Packet {
    private final long timestamp;

    public PacketRequestLock(long timestamp) {
        this.timestamp = timestamp;
    }
    @Override
    public void process(SocketHandler handler) {
        handler.receivedRequestLockPacket(this);
    }

    @Override
    public String toString() {
        return "Request lock: " + this.timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
