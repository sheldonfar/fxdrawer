package com.fxdrawer.tools;

import com.fxdrawer.util.Coordinates;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class Line extends Tool {
    private Color color;

    public Line(Pane pane, int size, Color color) {
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

    public void onMouseDragged(MouseEvent event) {
    }

    public void onMouseReleased(MouseEvent event) {
        super.onMouseDragged(event);
    }

    public void draw(Coordinates coordinates) {
        if (getPeer() != null && getPeer().getBoardLock().isLocked()) {
            return;
        }
        javafx.scene.shape.Line line = new javafx.scene.shape.Line(coordinates.getX1(), coordinates.getY1(), coordinates.getX2(), coordinates.getY2());
        line.setStrokeWidth(size);
        line.setStroke(color);

        pane.getChildren().add(line);
    }

    public String getName() {
        return "Line";
    }
}
