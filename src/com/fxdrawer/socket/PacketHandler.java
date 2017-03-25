package com.fxdrawer.socket;

import com.fxdrawer.packet.*;

public interface PacketHandler {
    void receivedConnectToPeerPacket(PacketConnectToPeer packet);
    void receivedActionPacket(PacketAction packet);
    void receivedRequestLockPacket(PacketRequestLock packet);
    void receivedRequestLockAckPacket(PacketRequestLockAck packet);
}
