package com.fxdrawer.peer;

import com.fxdrawer.packet.PacketRequestLock;
import com.fxdrawer.packet.PacketRequestLockAck;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;

public class ServerSocketHandler extends PeerSocketHandler {

    ServerSocketHandler(Socket socket, Peer peer) throws IOException {
        super(socket, peer);
    }

    public void receivedRequestLockPacket(PacketRequestLock packet) {
        Platform.runLater(() -> view.lock(true));
        if (!bl.isTryingToBlock() || bl.getTimestamp() > packet.getTimestamp()) {
            bl.setLockInitiator(this.socket);
            bl.setLocked(true);
            bl.receivedAck(null, null, true);

            checkIfAllAcksCollected();
        }
    }

    public void receivedRequestLockAckPacket(PacketRequestLockAck packet) {
        bl.receivedAck(this.socket, packet, true);

        checkIfAllAcksCollected();
    }

    protected void connectionClosed() {
        peer.onConnectionClosed(this.socket);

        if (bl.isLocked() && bl.getLockInitiator() == this.socket) {
            PacketRequestLockAck packet = new PacketRequestLockAck(peer.getPort());
            peer.sendPacket(packet);
            bl.receivedAck(bl.getLockInitiator(), packet, true);
        }
    }

    private void checkIfAllAcksCollected() {
        if (bl.getReceivedAcks() >= bl.getRequiredAcks()) {
            peer.sendPacket(new PacketRequestLockAck(peer.getPort()), bl.getLockInitiator());
        }
    }
}
