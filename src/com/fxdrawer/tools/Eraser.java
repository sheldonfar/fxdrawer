package com.fxdrawer.tools;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Eraser extends Tool {
    private Color color = Color.WHITE;

    public Eraser(Pane pane, int size) {
        super(pane);

        setSize(size);
        setCursor();
    }

    public String getCursorPath() {
        return "/com/fxdrawer/images/eraser.png";
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void draw(double oldX, double oldY, double currentX, double currentY) {
        Line line = new Line(oldX, oldY, currentX, currentY);
        line.setStrokeWidth(this.size);
        line.setStroke(color);

        this.pane.getChildren().add(line);
    }

    public void setColor(Color color) {}
}
