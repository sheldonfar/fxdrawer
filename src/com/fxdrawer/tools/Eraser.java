package com.fxdrawer.tools;

import com.fxdrawer.util.Coordinates;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Eraser extends Tool {
    private Color color = Color.WHITE;
    String name = "Eraser";

    public Eraser(Pane pane, int size) {
        super(pane);
        setSize(size);
        createCursor();
    }

    public String getCursorPath() {
        return "/com/fxdrawer/images/eraser.png";
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void draw(Coordinates coordinates) {
        Line line = new Line(coordinates.getX1(), coordinates.getY1(), coordinates.getX2(), coordinates.getY2());
        line.setStrokeWidth(this.size);
        line.setStroke(color);

        this.pane.getChildren().add(line);
    }

    public void setColor(Color color) {}

    public String getName() {
        return this.name;
    }
}
