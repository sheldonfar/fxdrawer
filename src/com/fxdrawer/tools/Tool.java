package com.fxdrawer.tools;

import com.fxdrawer.util.Coordinates;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.net.URL;

public abstract class Tool {
    Pane pane;
    int size;
    private String name = "Tool";
    private ImageCursor imageCursor;


    Tool(Pane pane) {
        this.pane = pane;
    }

    public void createCursor() {
        if (imageCursor == null) {
            URL url = getClass().getResource(getCursorPath());
            Image image = new Image(url.toString(), 32, 32, true, false);
            this.imageCursor = new ImageCursor(image, -image.getWidth(), image.getHeight());
        }
    }

    protected abstract String getCursorPath();

    public abstract void draw(Coordinates coordinates);

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

    public String getName() {
        return this.name;
    }
}
