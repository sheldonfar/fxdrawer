package com.fxdrawer.peer;

import com.fxdrawer.DrawController;
import com.fxdrawer.packet.PacketAction;
import com.fxdrawer.packet.PacketConnectToPeer;
import com.fxdrawer.tools.Tool;
import com.fxdrawer.util.Coordinates;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Peer {
    private PeerSocketHandler serverHandler;
    private PeerSocketHandler clientHandler;

    private ServerSocket peerSocket;
    private int port;
    private DrawController view;

    public Peer(int port) throws IOException {
        this.port = port;
    }

    public void serve() throws IOException {
        // final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
        Runnable serverTask = () -> {
            try {
                peerSocket = new ServerSocket(port);
                System.out.println("Waiting for clients to connect on port " + port);
                while (true) {
                    final Socket socket = peerSocket.accept();
                    serverHandler = new PeerSocketHandler(socket, this);
                    new Thread(serverHandler).start();
                    serverHandler.sendPacket(new PacketConnectToPeer("localhost", getPort()));
                }
            } catch (IOException e) {
                System.err.println("Unable to process client request");
                e.printStackTrace();
            }
        };
        Thread serverThread = new Thread(serverTask);
        serverThread.start();
    }

    public void openConnection(String hostName, int portNumber) {
        try {
            System.out.println("OPEN CONNECTION ");
            clientHandler = new PeerSocketHandler(new Socket(hostName, portNumber), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(clientHandler).start();

        PacketConnectToPeer packet = new PacketConnectToPeer("localhost", this.port);
        clientHandler.sendPacket(packet);
    }

    public void onAction(String tool, Coordinates coordinates) {
        PacketAction packet = new PacketAction(tool, coordinates);
        if (clientHandler != null && clientHandler.getState() == PeerState.CONNECTED) {
            clientHandler.sendPacket(packet);
        } else if (serverHandler != null && serverHandler.getState() == PeerState.CONNECTED) {
            serverHandler.sendPacket(packet);
        }
    }

    public void setView(DrawController view) {
        this.view = view;
    }

    public DrawController getView() {
        return this.view;
    }

    public int getPort() {
        return this.port;
    }
}
