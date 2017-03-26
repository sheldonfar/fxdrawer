package com.fxdrawer.packet;

import com.fxdrawer.socket.SocketHandler;

import java.io.Serializable;

public abstract class Packet implements Serializable {
    public Boolean preventBroadcasting = false;

    public abstract void process(SocketHandler handler);

    public abstract String toString();
}
