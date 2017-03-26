package com.fxdrawer.socket;

import com.fxdrawer.packet.PacketAction;
import com.fxdrawer.packet.PacketConnectToPeer;
import com.fxdrawer.packet.PacketRequestLock;
import com.fxdrawer.packet.PacketRequestLockAck;

public interface PacketHandler {
    void receivedConnectToPeerPacket(PacketConnectToPeer packet);

    void receivedActionPacket(PacketAction packet);

    void receivedRequestLockPacket(PacketRequestLock packet);

    void receivedRequestLockAckPacket(PacketRequestLockAck packet);
}
