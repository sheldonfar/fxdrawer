package com.fxdrawer.peer;

import com.fxdrawer.DrawController;
import com.fxdrawer.packet.*;
import com.fxdrawer.socket.SocketHandler;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;

public class ServerSocketHandler extends SocketHandler {

    private PeerState state;
    private BoardLock bl;
    private Peer peer;
    private DrawController view;

    ServerSocketHandler(Socket socket, Peer peer) throws IOException {
        super(socket);
        this.state = PeerState.IDLE;
        this.peer = peer;
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
        if (!bl.isTryingToBlock() || bl.getTimestamp() > packet.getTimestamp()) {
            bl.setLockInitiator(this.socket);
            bl.setLocked(true);
        }
    }

    public void receivedRequestLockAckPacket(PacketRequestLockAck packet) {
        bl.receivedAck(this.socket, packet, true);

        if (bl.getReceivedAcks() >= bl.getRequiredAcks()) {
            peer.sendPacket(new PacketRequestLockAck(peer.getPort()), bl.getLockInitiator());
        }
    }

    PeerState getState() {
        return this.state;
    }

    protected void logAction(Packet packet) {
        Platform.runLater(() -> view.logAction(packet.toString()));
    }

    protected void broadcastAction(Packet packet) {
        peer.broadcastAction(this.socket, packet);
    }

}
