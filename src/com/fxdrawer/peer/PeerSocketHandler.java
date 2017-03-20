package com.fxdrawer.peer;

import com.fxdrawer.packet.Packet;
import com.fxdrawer.packet.PacketAction;
import com.fxdrawer.packet.PacketConnectToPeer;
import com.fxdrawer.socket.SocketHandler;
import javafx.application.Platform;

import java.io.IOException;
import java.net.Socket;

public class PeerSocketHandler extends SocketHandler {

    private PeerState state;
    private Peer peer;

    PeerSocketHandler(Socket socket, Peer peer) throws IOException {
        super(socket);
        this.peer = peer;
        this.state = PeerState.IDLE;
    }

    public void receivedConnectToPeerPacket(PacketConnectToPeer packet) {
        Platform.runLater(() -> peer.getView().onPeerConnected(packet.getHostName(), packet.getPortNumber()));
        this.state = PeerState.CONNECTED;
    }

    public void receivedActionPacket(PacketAction packet) {
        Platform.runLater(() -> peer.getView().onAction(packet.getTool(), packet.getCoordinates()));
    }

    PeerState getState() {
        return this.state;
    }

    protected void logAction(Packet packet) {
        Platform.runLater(() -> peer.getView().logAction(packet.toString()));
    }
}
