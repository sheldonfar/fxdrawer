package com.fxdrawer.peer;

import com.fxdrawer.DrawController;
import com.fxdrawer.packet.*;
import com.fxdrawer.socket.SocketHandler;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;

public class PeerSocketHandler extends SocketHandler {

    private PeerState state;
    private BoardLock bl;
    private DrawController view;

    PeerSocketHandler(Socket socket, Peer peer) throws IOException {
        super(socket);
        this.state = PeerState.IDLE;
        bl = peer.getBoardLock();
        view = peer.getView();
    }

    public void receivedConnectToPeerPacket(PacketConnectToPeer packet) {
        Platform.runLater(() -> view.onPeerConnected(packet.getHostName(), packet.getPortNumber()));
        this.state = PeerState.CONNECTED;
    }

    public void receivedActionPacket(PacketAction packet) {
        Platform.runLater(() -> view.onAction(packet.getTool(), packet.getSize(), packet.getColor(), packet.getCoordinates()));
    }

    public void receivedRequestLockPacket(PacketRequestLock packet) {
        Platform.runLater(() -> view.lock(true));
        if (!bl.getTryingToLock() || bl.getTimestamp() > packet.getTimestamp()) {
            sendPacket(new PacketRequestLockAck());
        }
        bl.setLocked(true);
    }

    public void receivedRequestLockAckPacket(PacketRequestLockAck packet) {
        if (bl.isLocked()) {
            Platform.runLater(() -> view.lock(false));
        }
        if (bl.getTryingToLock()) {
            bl.blockPermitted();
        }
        bl.setLocked(false);
    }

    PeerState getState() {
        return this.state;
    }

    protected void logAction(Packet packet) {
        Platform.runLater(() -> view.logAction(packet.toString()));
    }
}
