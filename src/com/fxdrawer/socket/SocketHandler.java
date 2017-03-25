package com.fxdrawer.socket;

import com.fxdrawer.packet.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

public abstract class SocketHandler implements Runnable, Serializable, PacketHandler {
    private transient final Socket socket;
    private transient final ObjectOutputStream out;
    private transient final ObjectInputStream in;

    protected SocketHandler(Socket socket) throws IOException {
        this.socket = socket;

        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public final void run() {
        try {
            handleConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void handleConnection() throws IOException {
        try {
            for (Object obj = in.readObject(); obj != null; obj = in.readObject()) {
                Packet packet = (Packet) obj;
                packet.process(this);
                logAction(packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connectionClosed();
            out.close();
            in.close();
        }
    }

    protected void connectionClosed() {
    }

    protected void logAction(Packet packet) {

    }

    public synchronized void sendPacket(Packet packet) {
        try {
            out.writeObject(packet);
            logAction(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
