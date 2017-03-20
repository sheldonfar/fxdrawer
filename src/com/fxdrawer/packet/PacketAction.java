package com.fxdrawer.packet;

import com.fxdrawer.socket.SocketHandler;
import com.fxdrawer.tools.Tool;
import com.fxdrawer.util.Coordinates;

public class PacketAction extends Packet {
    private final String tool;
    private final int size;
    private final Coordinates coordinates;

    public PacketAction(String tool, int size, Coordinates coordinates) {
        this.tool = tool;
        this.size = size;
        this.coordinates = coordinates;
    }
    @Override
    public void process(SocketHandler handler) {
        handler.receivedActionPacket(this);
    }

    @Override
    public String toString() {
        return "Action: " + tool + "  ; Size: " + size + " " + coordinates.toString();
    }

    public String getTool() {
        return tool;
    }

    public int getSize() {
        return size;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
