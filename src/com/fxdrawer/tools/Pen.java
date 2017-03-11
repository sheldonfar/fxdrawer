package com.fxdrawer.tools;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Pen extends Tool {
    private Color color;

    public Pen(Pane pane, int size, Color color) {
        super(pane);

        setCursor();
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

    public void draw(double oldX, double oldY, double currentX, double currentY) {
        Line line = new Line(oldX, oldY, currentX, currentY);
        line.setStrokeWidth(size);
        line.setStroke(color);

        pane.getChildren().add(line);
    }
}
