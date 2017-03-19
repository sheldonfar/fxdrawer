package com.fxdrawer.packet;

import com.fxdrawer.socket.SocketHandler;
import com.fxdrawer.tools.Tool;
import com.fxdrawer.util.Coordinates;

public class PacketAction extends Packet {
    private final String tool;
    private final Coordinates coordinates;

    public PacketAction(String tool, Coordinates coordinates) {
        this.tool = tool;
        this.coordinates = coordinates;
    }
    @Override
    public void process(SocketHandler handler) {
        handler.receivedActionPacket(this);
    }

    public String getTool() {
        return tool;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }
}
