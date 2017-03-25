package com.fxdrawer.tools;

import com.fxdrawer.util.Coordinates;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Pen extends Tool {
    private Color color;
    String name = "Pen";

    public Pen(Pane pane, int size, Color color) {
        super(pane);
        createCursor();
        setSize(size);
        setColor(color);
    }

    public String getCursorPath() {
        return "/com/fxdrawer/images/pen.png";
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public void draw(Coordinates coordinates) {
        Line line = new Line(coordinates.getX1(), coordinates.getY1(), coordinates.getX2(), coordinates.getY2());
        line.setStrokeWidth(size);
        line.setStroke(color);

        pane.getChildren().add(line);
    }

    public String getName() {
        return this.name;
    }
}
