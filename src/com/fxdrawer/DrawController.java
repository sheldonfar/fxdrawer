package com.fxdrawer;

import com.fxdrawer.peer.Peer;
import com.fxdrawer.tools.*;
import com.fxdrawer.util.Coordinates;
import com.fxdrawer.util.SplitPaneDividerSlider;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DrawController implements Initializable {
    public Pane drawPane = new Pane();
    public Slider sizeSlider;
    public SplitPane mainSplitPane;
    public Label userHostLabel;
    public Label userPortLabel;
    public MenuItem connectBtn;
    public Label peerHostLabel;
    public Label peerPortLabel;
    public JFXToggleButton toggleStateSwitch;
    public JFXColorPicker colorPicker;
    public JFXComboBox toolCombo;
    public JFXComboBox sizeCombo;
    public ListView log;
    private double oldX;
    private double oldY;
    private SplitPaneDividerSlider rightSplitPaneDividerSlider;
    private Peer peer;
    private long lastPressProcessed = System.currentTimeMillis();
    private Map<String, Tool> toolMap = new HashMap<>();
    private Tool tool;
    private Set<String> stringSet = new HashSet<>();
    private ObservableList observableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int initialToolSize = (int) sizeSlider.getValue();
        toolMap.put("Pen", new Pen(drawPane, initialToolSize, colorPicker.getValue()));
        toolMap.put("Eraser", new Eraser(drawPane, initialToolSize));
        tool = toolMap.get("Pen");
        toolCombo.getSelectionModel().selectFirst();

        rightSplitPaneDividerSlider = new SplitPaneDividerSlider(mainSplitPane, 0, SplitPaneDividerSlider.Direction.LEFT);

        sizeSlider.valueProperty().addListener((selected, oldSize, newSize) -> {
            int size = newSize.intValue();
            sizeCombo.setValue(size);

            tool.setSize(size);
        });

        toolCombo.valueProperty().addListener((selected, oldTool, newTool) -> tool = toolMap.get(((Label) newTool).getText()));

        colorPicker.setOnAction(t -> tool.setColor(colorPicker.getValue()));
    }

    public void onPanelClick(MouseEvent event) {
        oldX = event.getX();
        oldY = event.getY();

        Coordinates coord = new Coordinates(oldX, oldX, oldY, oldY);

        tool.draw(coord);
    }

    public void onPanelMouseMove(MouseEvent event) {
        oldX = event.getX();
        oldY = event.getY();
    }

    public void onPanelMouseDrag(MouseEvent event) {
        if (System.currentTimeMillis() - lastPressProcessed < 50) {
            return;
        }

        double currentX = event.getX();
        double currentY = event.getY();

        Coordinates coord = new Coordinates(oldX, currentX, oldY, currentY);

        String toolName = toolCombo.getValue().toString();

        if (peer != null) {
            peer.onAction(toolName, tool.getSize(), coord);
        }

        tool.draw(coord);

        oldX = currentX;
        oldY = currentY;

        lastPressProcessed = System.currentTimeMillis();
    }

    public void onToggleStateClick() throws IOException {
        if (toggleStateSwitch.isSelected()) {
            FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("layout/createPeerDialog.fxml"));
            TextInputDialog dialog = dialogLoader.load();
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(port -> {
                userHostLabel.setText("localhost");
                userPortLabel.setText(port);
                rightSplitPaneDividerSlider.setMinWidth(drawPane.getBoundsInParent().getWidth() * 0.65);
                rightSplitPaneDividerSlider.setAimContentVisible(false);

                try {
                    peer = new Peer(Integer.parseInt(port));
                    peer.setView(this);
                    peer.serve();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            rightSplitPaneDividerSlider.setMinWidth(drawPane.getBoundsInParent().getWidth());
            rightSplitPaneDividerSlider.setAimContentVisible(true);
        }
    }

    public void openConnection(ActionEvent actionEvent) {
        peer.openConnection("localhost", 9998);
    }

    public void onPeerConnected(String hostName, int portNumber) {
        peerHostLabel.setText(hostName);
        peerPortLabel.setText(String.valueOf(portNumber));
    }

    public void onAction(String toolName, int size, Coordinates coordinates) {
        Tool tool = toolMap.get(toolName);
        int originalSize = tool.getSize();
        tool.setSize(size);
        tool.draw(coordinates);
        tool.setSize(originalSize);
    }

    public void logAction(String action) {
        stringSet.add(action);
        observableList.setAll(stringSet);
        log.setItems(observableList);
    }

    public void clearLog() {
        stringSet.clear();
        log.getItems().clear();
    }
}
