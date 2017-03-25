package com.fxdrawer.packet;

import com.fxdrawer.socket.SocketHandler;
import com.fxdrawer.util.Coordinates;

public class PacketAction extends Packet {
    private final String tool;
    private final int size;
    private final Coordinates coordinates;
    private final String color;

    public PacketAction(String tool, int size, String color, Coordinates coordinates) {
        this.tool = tool;
        this.size = size;
        this.coordinates = coordinates;
        this.color = color;
    }

    @Override
    public void process(SocketHandler handler) {
        handler.receivedActionPacket(this);
    }

    @Override
    public String toString() {
        return tool + "; Size: " + size + "; Color: " + color + "; " + coordinates.toString();
    }

    public String getTool() {
        return tool;
    }

    public int getSize() {
        return size;
    }

    public String getColor() {
        return color;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
