package com.fxdrawer;

import com.fxdrawer.peer.BoardLock;
import com.fxdrawer.peer.Peer;
import com.fxdrawer.tools.Eraser;
import com.fxdrawer.tools.Line;
import com.fxdrawer.tools.Pen;
import com.fxdrawer.tools.Tool;
import com.fxdrawer.util.Coordinates;
import com.fxdrawer.util.RadioToggleButton;
import com.jfoenix.controls.JFXColorPicker;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

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
    public RadioToggleButton lockButton;
    public FontAwesomeIconView lockIcon;
    public TextField hostTextField;
    public TextField portTextField;
    private Peer peer;
    private Map<String, Tool> toolMap = new HashMap<>();
    private Tool tool;
    private Set<String> stringSet = new HashSet<>();
    private ObservableList observableList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int initialToolSize = (int) sizeSlider.getValue();
        toolMap.put("Pen", new Pen(drawPane, initialToolSize, colorPicker.getValue()));
        toolMap.put("Eraser", new Eraser(drawPane, initialToolSize));
        toolMap.put("Line", new Line(drawPane, initialToolSize, colorPicker.getValue()));

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
                tool.setColor(colorPicker.getValue());
            }
        });

        drawPane.setOnMouseEntered(me -> drawPane.setCursor(tool.getCursor()));

        drawPane.setOnMouseExited(me -> drawPane.setCursor(tool.getDefaultCursor()));

        colorPicker.setOnAction(t -> tool.setColor(colorPicker.getValue()));
    }

    public void onPanelMouseClicked(MouseEvent event) {
        tool.onMouseClicked(event);
    }

    public void onPanelMouseMoved(MouseEvent event) {
        tool.onMouseMoved(event);
    }

    public void onPanelMouseDragged(MouseEvent event) {
        tool.onMouseDragged(event);
    }

    public void onPanelMouseReleased(MouseEvent event) {
        tool.onMouseReleased(event);
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

                    for (Tool tool : toolMap.values()) {
                        tool.setPeer(peer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            lockButton.setVisible(false);
        }
    }

    public void onConnectButtonClick() throws IOException {
        if (peer.isConnected()) {
            peer.disconnect();
            connectBtn.setText("Connect");
        } else {
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Open Connection");
            dialog.setHeaderText("Enter host & port");

            //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

            ButtonType okButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);

            TextField host = new TextField("localhost");
            host.setPromptText("Host");
            TextField port = new TextField("9998");
            port.setPromptText("Port");

            grid.add(new Label("Host:"), 0, 0);
            grid.add(host, 1, 0);
            grid.add(new Label("Port:"), 0, 1);
            grid.add(port, 1, 1);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == okButtonType) {
                    return new Pair<>(host.getText(), port.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(hostPort -> {
                peer.openConnection(hostPort.getKey(), Integer.parseInt(hostPort.getValue()));
                connectBtn.setText("Disconnect");
            });
        }
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
        tool.draw(coordinates, true);
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
