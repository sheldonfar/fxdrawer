package com.fxdrawer.peer;

import com.fxdrawer.DrawController;
import com.fxdrawer.packet.Packet;
import javafx.application.Platform;

import java.net.Socket;
import java.util.Date;

public class BoardLock {
    private Boolean isBlocking = false;
    private Boolean isLocked = false;

    public Socket getLockInitiator() {
        return lockInitiator;
    }

    private Boolean isTryingToBlock = false;
    private long timestamp;

    public int getReceivedAcks() {
        return receivedAcks;
    }

    public int getRequiredAcks() {
        return requiredAcks;
    }

    private int receivedAcks = 0;
    private int requiredAcks = 0;
    private Socket lockInitiator;
    private DrawController view;

    public Boolean isLocked() {
        return isLocked;
    }

    public Boolean isTryingToBlock() {
        return isTryingToBlock;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void init() {
        timestamp = new Date().getTime();
        isTryingToBlock = true;
    }

    public void setLockInitiator(Socket socket) {
        lockInitiator = socket;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public void tryToBlock() {
        if (receivedAcks >= requiredAcks) {
            isBlocking = true;
            isTryingToBlock = false;
            Platform.runLater(() -> view.lock(true));
        }
    }

    public Boolean isBlocking() {
        return isBlocking;
    }

    public void unblock() {
        isBlocking = false;
        isTryingToBlock = false;
        receivedAcks = 0;
        Platform.runLater(() -> view.lock(false));
    }

    public void receivedAck(Socket socket, Packet packet, Boolean isMultiplexing) {
        if (isTryingToBlock()) {
            receivedAcks++;
            tryToBlock();
            if (packet != null) {
                packet.preventBroadcasting = true;
            }
        } else if (isMultiplexing) {
            receivedAcks++;
        }

        if (socket != null && lockInitiator == socket) {
            isLocked = false;
            if (!isBlocking) {
                Platform.runLater(() -> view.lock(false));
            }
            receivedAcks = 0;
        }
    }

    public void setRequiredAcks(int count) {
        requiredAcks = count;
    }

    public void setView(DrawController view) {
        this.view = view;
    }
}
