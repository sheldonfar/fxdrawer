package com.fxdrawer.peer;

import java.util.Date;

public class BoardLock {
    private Boolean isBlocking = false;
    private Boolean isLocked = false;
    private Boolean isTryingToLock = false;
    private long timestamp;

    public Boolean isLocked() {
        return isLocked;
    }

    public Boolean getTryingToLock() {
        return isTryingToLock;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void tryToLock() {
        this.timestamp = new Date().getTime();
        this.isTryingToLock = true;
    }

    public void setLocked(Boolean locked) {
        this.isLocked = locked;
    }

    public void blockPermitted() {
        this.isBlocking = true;
        this.isTryingToLock = false;
    }

    public Boolean isBlocking() {
        return this.isBlocking;
    }

    public void unblock() {
        this.isBlocking = false;
        this.isTryingToLock = false;
    }
}
