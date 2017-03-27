package com.fxdrawer.tools;

import com.fxdrawer.peer.Peer;
import com.fxdrawer.util.Coordinates;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.net.URL;

public abstract class Tool {
    Pane pane;
    int size;
    private ImageCursor imageCursor;
    private double oldX;
    private double oldY;
    private long lastPressProcessed = System.currentTimeMillis();
    private Peer peer;

    Tool(Pane pane) {
        this.pane = pane;
    }

    void createCursor() {
        if (imageCursor == null) {
            URL url = getClass().getResource(getCursorPath());
            Image image = new Image(url.toString(), 32, 32, true, false);
            this.imageCursor = new ImageCursor(image, -image.getWidth(), image.getHeight());
        }
    }

    protected abstract String getCursorPath();

    public abstract void draw(Coordinates coordinates);

    private void sendAction(Coordinates coordinates) {
        if (peer != null) {
            if (!peer.getBoardLock().isLocked()) {
                peer.onAction(getName(), getSize(), getColor().toString(), coordinates);
            }
        }
    }

    public abstract void setColor(Color color);

    public abstract Color getColor();

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return this.size;
    }

    public ImageCursor getCursor() {
        return this.imageCursor;
    }

    public Cursor getDefaultCursor() {
        return Cursor.DEFAULT;
    }

    public abstract String getName();

    public void onMouseClicked(MouseEvent event) {
        oldX = event.getX();
        oldY = event.getY();

        Coordinates coordinates = new Coordinates(oldX, oldX, oldY, oldY);
        draw(coordinates);
        sendAction(coordinates);
    }

    public void onMouseDragged(MouseEvent event) {
        if (System.currentTimeMillis() - lastPressProcessed < 50) {
            return;
        }

        double currentX = event.getX();
        double currentY = event.getY();

        Coordinates coordinates = new Coordinates(oldX, currentX, oldY, currentY);
        draw(coordinates);
        sendAction(coordinates);

        oldX = currentX;
        oldY = currentY;

        lastPressProcessed = System.currentTimeMillis();
    }

    public void onMouseReleased(MouseEvent event) {
    }

    public void onMouseMoved(MouseEvent event) {
        oldX = event.getX();
        oldY = event.getY();
    }

    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    Peer getPeer() {
        return peer;
    }
}
