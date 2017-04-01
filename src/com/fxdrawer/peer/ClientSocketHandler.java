package com.fxdrawer.peer;

import com.fxdrawer.packet.PacketRequestLock;
import com.fxdrawer.packet.PacketRequestLockAck;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;

public class ClientSocketHandler extends PeerSocketHandler {

    ClientSocketHandler(Socket socket, Peer peer) throws IOException {
        super(socket, peer);
    }

    public void receivedRequestLockPacket(PacketRequestLock packet) {
        Platform.runLater(() -> view.lock(true));
        if (!bl.isTryingToBlock() || bl.getTimestamp() > packet.getTimestamp()) {
            sendPacket(new PacketRequestLockAck(peer.getPort()));
            bl.setLockInitiator(this.socket);
            bl.setLocked(true);
        }
    }

    protected void connectionClosed() {
        super.connectionClosed();
        if (bl.isLocked()) {
            bl.receivedAck(bl.getLockInitiator(), null, true);
        }
    }

    public void receivedRequestLockAckPacket(PacketRequestLockAck packet) {
        bl.receivedAck(this.socket, packet, false);
    }
}
