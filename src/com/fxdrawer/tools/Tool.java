package com.fxdrawer.tools;

import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.awt.*;
import java.net.URL;

public abstract class Tool {
    Pane pane;
    int size;
    private ImageCursor imageCursor;

    Tool(Pane pane) {
        this.pane = pane;
    }

    public void setCursor() {
        if (imageCursor == null) {
            URL url = getClass().getResource(getCursorPath());
            System.out.println("URLLLLLL " + url);
            Image image = new Image(url.toString(), 32, 32, true, false);
            this.imageCursor = new ImageCursor(image, -image.getWidth(), image.getHeight());
        }
        this.pane.setCursor(imageCursor);
    }

    protected abstract String getCursorPath();

    public abstract void draw(double oldX, double oldY, double currentX, double currentY);

    public abstract void setColor(Color color);

    public void setSize(int size) {
        this.size = size;
    }

    public void destroy() {
        this.pane.setCursor(Cursor.DEFAULT);
    }
}
