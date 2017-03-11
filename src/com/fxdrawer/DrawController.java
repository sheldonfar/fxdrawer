package com.fxdrawer;

import com.fxdrawer.tools.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import java.net.URL;
import java.util.ResourceBundle;

public class DrawController implements Initializable {
    public Pane drawPane = new Pane();
    public ColorPicker colorPicker;
    public ComboBox<String> toolCombo;
    public ComboBox sizeCombo;
    public Slider sizeSlider;
    private double oldX;
    private double oldY;
    private Tool tool;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int initialSize = (int) sizeSlider.getValue();
        tool = new Pen(drawPane, initialSize, colorPicker.getValue());
        sizeCombo.setValue(initialSize);

        toolCombo.valueProperty().addListener((selected, oldTool, newTool) -> {
            if (oldTool != null) {
                tool.destroy();
            }
            if (newTool != null) {
                switch (newTool) {
                    case "Pen":
                        tool = new Pen(drawPane, (int) sizeSlider.getValue(), colorPicker.getValue());
                        break;
                    case "Eraser":
                        tool = new Eraser(drawPane, (int) sizeSlider.getValue());
                        break;
                }
            }
        });

        sizeSlider.valueProperty().addListener((selected, oldSize, newSize) -> {
            int size = newSize.intValue();
            sizeCombo.setValue(size);
            tool.setSize(size);
        });

        colorPicker.setOnAction(t -> tool.setColor(colorPicker.getValue()));
    }

    public void onPanelClick(MouseEvent event) {
        oldX = event.getX();
        oldY = event.getY();

        tool.draw(oldX, oldY, oldX, oldY);
    }

    public void onPanelMouseMove(MouseEvent event) {
        oldX = event.getX();
        oldY = event.getY();

    }

    public void onPanelMouseDrag(MouseEvent event) {
        double currentX = event.getX();
        double currentY = event.getY();

        tool.draw(oldX, oldY, currentX, currentY);

        oldX = currentX;
        oldY = currentY;
    }
}
