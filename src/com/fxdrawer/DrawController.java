package com.fxdrawer;

import com.fxdrawer.peer.BoardLock;
import com.fxdrawer.peer.Peer;
import com.fxdrawer.tools.Eraser;
import com.fxdrawer.tools.Pen;
import com.fxdrawer.tools.Tool;
import com.fxdrawer.util.Coordinates;
import com.fxdrawer.util.RadioToggleButton;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

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
    public ToggleGroup toolToggleGroup;
    public JFXComboBox sizeCombo;
    public ListView log;
    public JFXButton lockButton;
    public FontAwesomeIconView lockIcon;
    private double oldX;
    private double oldY;
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
        drawPane.setCursor(tool.getCursor());

        sizeSlider.valueProperty().addListener((selected, oldSize, newSize) -> {
            int size = newSize.intValue();
            sizeCombo.valueProperty().setValue(size);

            tool.setSize(size);
        });

        toolToggleGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            if (toolToggleGroup.getSelectedToggle() != null) {
                tool = toolMap.get(((RadioToggleButton) toolToggleGroup.getSelectedToggle()).getTextValue());
            }
        });

        drawPane.setOnMouseEntered(me -> drawPane.setCursor(tool.getCursor()));

        drawPane.setOnMouseExited(me -> drawPane.setCursor(tool.getDefaultCursor()));

        colorPicker.setOnAction(t -> tool.setColor(colorPicker.getValue()));
    }

    public void onPanelClick(MouseEvent event) {
        oldX = event.getX();
        oldY = event.getY();

        draw(new Coordinates(oldX, oldX, oldY, oldY));
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

        draw(new Coordinates(oldX, currentX, oldY, currentY));

        oldX = currentX;
        oldY = currentY;

        lastPressProcessed = System.currentTimeMillis();
    }

    private void draw(Coordinates coord) {
        if (peer != null) {
            if (!peer.getBoardLock().isLocked()) {
                tool.draw(coord);
                peer.onAction(tool.getName(), tool.getSize(), tool.getColor().toString(), coord);
            }
        } else {
            tool.draw(coord);
        }
    }

    public void onToggleStateClick() throws IOException {
        if (toggleStateSwitch.isSelected()) {
            FXMLLoader dialogLoader = new FXMLLoader(getClass().getResource("layout/createPeerDialog.fxml"));
            TextInputDialog dialog = dialogLoader.load();
            Optional<String> result = dialog.showAndWait();

            result.ifPresent(port -> {
                userHostLabel.setText("localhost");
                userPortLabel.setText(port);
                lockButton.setVisible(true);

                try {
                    peer = new Peer(Integer.parseInt(port));
                    peer.setView(this);
                    peer.serve();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            lockButton.setVisible(false);
        }
    }

    public void openConnection(ActionEvent actionEvent) {
        peer.openConnection("localhost", 9998);
    }

    public void onPeerConnected(String hostName, int portNumber) {
        peerHostLabel.setText(hostName);
        peerPortLabel.setText(String.valueOf(portNumber));
    }

    public void onAction(String toolName, int size, String color, Coordinates coordinates) {
        Tool tool = toolMap.get(toolName);
        int originalSize = tool.getSize();
        Color originalColor = tool.getColor();
        tool.setSize(size);
        tool.setColor(Color.valueOf(color));
        tool.draw(coordinates);
        tool.setSize(originalSize);
        tool.setColor(originalColor);
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

    public void onLockButtonClick() {
        BoardLock bl = peer.getBoardLock();
        if (!bl.isLocked()) {
            Boolean wasLocked = bl.isTryingToBlock() || bl.isBlocking();
            peer.lock(!wasLocked);
        }
    }

    public void lock(Boolean lock) {
        lockIcon.setGlyphName(lock ? "LOCK" : "UNLOCK_ALT");
    }
}
