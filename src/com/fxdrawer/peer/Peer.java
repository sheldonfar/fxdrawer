package com.fxdrawer.peer;

import com.fxdrawer.DrawController;
import com.fxdrawer.packet.*;
import com.fxdrawer.util.Coordinates;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Peer {
    private Map<Socket, ServerSocketHandler> serverHandlers = new HashMap<>();
    private ClientSocketHandler clientHandler;

    private ServerSocket peerSocket;
    private int port;
    private DrawController view;
    private BoardLock boardLock = new BoardLock();

    public Peer(int port) throws IOException {
        this.port = port;
    }

    public void serve() throws IOException {
        // final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);
        Runnable serverTask = () -> {
            try {
                peerSocket = new ServerSocket(port);
                while (true) {
                    final Socket socket = peerSocket.accept();
                    ServerSocketHandler handler = new ServerSocketHandler(socket, this);
                    new Thread(handler).start();
                    handler.sendPacket(new PacketConnectToPeer("localhost", getPort()));
                    serverHandlers.put(socket, handler);
                    boardLock.setRequiredAcks(serverHandlers.size());
                }
            } catch (BindException e) {
                System.out.println("Port already in use!");
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
            clientHandler = new ClientSocketHandler(new Socket(hostName, portNumber), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(clientHandler).start();

        PacketConnectToPeer packet = new PacketConnectToPeer("localhost", this.port);
        clientHandler.sendPacket(packet);
    }

    public void onAction(String tool, int size, String color, Coordinates coordinates) {
        sendPacket(new PacketAction(tool, size, color, coordinates));
    }

    public void setView(DrawController view) {
        this.view = view;
        this.boardLock.setView(view);
    }

    DrawController getView() {
        return this.view;
    }

    int getPort() {
        return this.port;
    }

    public void lock(Boolean lock) {
        if (lock) {
            boardLock.init();
            sendPacket(new PacketRequestLock(boardLock.getTimestamp()));
        } else {
            boardLock.unblock();
            sendPacket(new PacketRequestLockAck(port));
        }
    }

    void sendPacket(Packet packet) {
        if (clientHandler != null && clientHandler.getState() == PeerState.CONNECTED) {
            clientHandler.sendPacket(packet);
        } else if (serverHandlers.size() != 0) {
            for (ServerSocketHandler h : serverHandlers.values()) {
                h.sendPacket(packet);
            }
        }
    }

    void sendPacket(Packet packet, Socket socket) {
        for (Object o : serverHandlers.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if (pair.getKey() == socket) {
                ((ServerSocketHandler) pair.getValue()).sendPacket(packet);
            }
        }
    }

    public BoardLock getBoardLock() {
        return this.boardLock;
    }

    void broadcastAction(Socket socket, Packet packet) {
        for (Object o : serverHandlers.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if (pair.getKey() != socket) {
                ((ServerSocketHandler) pair.getValue()).sendPacket(packet);
            }
        }
    }

    void onConnectionClosed(Socket socket) {
        serverHandlers.remove(socket);
        boardLock.setRequiredAcks(serverHandlers.size());
    }
}
