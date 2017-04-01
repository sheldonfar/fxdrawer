package com.fxdrawer.socket;

import com.fxdrawer.packet.Packet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketException;

public abstract class SocketHandler implements Runnable, Serializable, PacketHandler {
    protected transient final Socket socket;
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

    private void handleConnection() throws IOException {
        try {
            for (Object obj = in.readObject(); obj != null; obj = in.readObject()) {
                Packet packet = (Packet) obj;
                packet.process(this);
                logAction(packet);
                if (!packet.preventBroadcasting) {
                    broadcastAction(packet);
                } else {
                    System.out.println("Preventing broadcasting of a packet " + packet);
                }
            }
        } catch (Exception ignored) {
        } finally {
            connectionClosed();

        }
    }

    protected void connectionClosed() {
        try {
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract void logAction(Packet packet);

    protected abstract void broadcastAction(Packet packet);

    public synchronized void sendPacket(Packet packet) {
        try {
            out.writeObject(packet);
            logAction(packet);
        } catch (SocketException e) {
            connectionClosed();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
