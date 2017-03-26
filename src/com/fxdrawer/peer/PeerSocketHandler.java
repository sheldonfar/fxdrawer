package com.fxdrawer.peer;

import com.fxdrawer.DrawController;
import com.fxdrawer.packet.Packet;
import com.fxdrawer.packet.PacketAction;
import com.fxdrawer.packet.PacketConnectToPeer;
import com.fxdrawer.socket.SocketHandler;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;

public abstract class PeerSocketHandler extends SocketHandler {

    private PeerState state;
    BoardLock bl;
    Peer peer;
    DrawController view;

    PeerSocketHandler(Socket socket, Peer peer) throws IOException {
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
