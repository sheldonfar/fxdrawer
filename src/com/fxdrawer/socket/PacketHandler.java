package com.fxdrawer.socket;

import com.fxdrawer.packet.PacketAction;
import com.fxdrawer.packet.PacketConnectToPeer;

public interface PacketHandler {
    void receivedConnectToPeerPacket(PacketConnectToPeer packet);
    void receivedActionPacket(PacketAction packet);
}
