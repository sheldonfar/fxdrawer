package com.fxdrawer;

import com.fxdrawer.peer.Peer;
import com.fxdrawer.tools.*;
import com.fxdrawer.util.Coordinates;
import com.fxdrawer.util.SplitPaneDividerSlider;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class DrawController implements Initializable {
    public Pane drawPane = new Pane();
    public Slider sizeSlider;
    public SplitPane mainSplitPane;
    public Label userHostLabel;
    public Label userPortLabel;
    public ListView historyView;
    public MenuItem connectBtn;
    public Label peerHostLabel;
    public Label peerPortLabel;
    public JFXToggleButton toggleStateSwitch;
    public JFXColorPicker colorPicker;
    public JFXComboBox toolCombo;
    public JFXComboBox sizeCombo;
    private double oldX;
    private double oldY;
    private SplitPaneDividerSlider rightSplitPaneDividerSlider;
    private Peer peer;
    private Map<String, Tool> toolMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int initialToolSize = (int) sizeSlider.getValue();
        toolMap.put("Pen", new Pen(drawPane, initialToolSize, colorPicker.getValue()));
        toolMap.put("Eraser", new Eraser(drawPane, initialToolSize));

        sizeCombo.setValue(initialToolSize);
        rightSplitPaneDividerSlider = new SplitPaneDividerSlider(mainSplitPane, 0, SplitPaneDividerSlider.Direction.LEFT);

        sizeSlider.valueProperty().addListener((selected, oldSize, newSize) -> {
            int size = newSize.intValue();
            sizeCombo.setValue(size);
            toolMap.get(toolCombo.getValue().toString()).setSize(size);
        });

        colorPicker.setOnAction(t -> toolMap.get(toolCombo.getValue().toString()).setColor(colorPicker.getValue()));
    }

    public void onPanelClick(MouseEvent event) {
        oldX = event.getX();
        oldY = event.getY();

        Coordinates coord = new Coordinates(oldX, oldX, oldY, oldY);

        toolMap.get(toolCombo.getValue().toString()).draw(coord);
    }

    public void onPanelMouseMove(MouseEvent event) {
        oldX = event.getX();
        oldY = event.getY();

    }

    public void onPanelMouseDrag(MouseEvent event) {
        double currentX = event.getX();
        double currentY = event.getY();

        Coordinates coord = new Coordinates(oldX, currentX, oldY, currentY);

        if (peer != null) {
            peer.onAction(toolCombo.getValue().toString(), coord);
        }

        toolMap.get(toolCombo.getValue().toString()).draw(coord);

        oldX = currentX;
        oldY = currentY;
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

    public void onAction(String tool, Coordinates coordinates) {
        toolMap.get(tool).draw(coordinates);
    }
}
