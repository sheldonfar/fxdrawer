package com.fxdrawer.peer;

import com.fxdrawer.DrawController;
import com.fxdrawer.packet.Packet;
import javafx.application.Platform;

import java.net.Socket;
import java.util.Date;

public class BoardLock {
    private Boolean isBlocking = false;
    private Boolean isLocked = false;

    Socket getLockInitiator() {
        return lockInitiator;
    }

    private Boolean isTryingToBlock = false;
    private long timestamp;

    int getReceivedAcks() {
        return receivedAcks;
    }

    int getRequiredAcks() {
        return requiredAcks;
    }

    private int receivedAcks = 0;
    private int requiredAcks = 1;
    private Socket lockInitiator;
    private DrawController view;

    public Boolean isLocked() {
        return isLocked;
    }

    public Boolean isTryingToBlock() {
        return isTryingToBlock;
    }

    long getTimestamp() {
        return timestamp;
    }

    void init() {
        timestamp = new Date().getTime();
        isTryingToBlock = true;
        receivedAcks = 0;
        lockInitiator = null;
        isBlocking = false;
    }

    void setLockInitiator(Socket socket) {
        lockInitiator = socket;
    }

    void setLocked(Boolean locked) {
        isLocked = locked;
    }

    private void tryToBlock() {
        if (receivedAcks >= requiredAcks) {
            isBlocking = true;
            isTryingToBlock = false;
            Platform.runLater(() -> view.lock(true));
        }
    }

    public Boolean isBlocking() {
        return isBlocking;
    }

    void unblock() {
        isBlocking = false;
        isTryingToBlock = false;
        receivedAcks = 0;
        Platform.runLater(() -> view.lock(false));
    }

    void receivedAck(Socket socket, Packet packet, Boolean isMultiplexing) {
        if (isTryingToBlock()) {
            receivedAcks++;
            tryToBlock();
            if (packet != null) {
                packet.preventBroadcasting = true;
            }
        }

        if (socket != null && lockInitiator == socket) {
            setLocked(false);
            if (!isBlocking) {
                Platform.runLater(() -> view.lock(false));
            }
            receivedAcks = 0;
        } else if (isMultiplexing) {
            receivedAcks++;
            if (packet != null) {
                packet.preventBroadcasting = true;
            }
        }
    }

    void setRequiredAcks(int count) {
        requiredAcks = count;
    }

    void setView(DrawController view) {
        this.view = view;
    }
}
