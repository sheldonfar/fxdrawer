package com.fxdrawer.util;

import javafx.animation.Transition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public class SplitPaneDividerSlider {

    public static enum Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private final Direction direction;
    private final SplitPane splitPane;
    private final int dividerIndex;
    private BooleanProperty aimContentVisibleProperty;
    private DoubleProperty lastDividerPositionProperty;
    private DoubleProperty currentDividerPositionProperty;
    private Region content;
    private double contentInitialMinWidth;
    private double contentInitialMinHeight;
    private Transition slideTransition;
    private Duration cycleDuration;
    private SplitPane.Divider dividerToMove;
    private double minWidth = 0.0;
    private double minHeight = 0.0;

    public SplitPaneDividerSlider(SplitPane splitPane, int dividerIndex, Direction direction) {
        this(splitPane, dividerIndex, direction, Duration.millis(7000.0));
    }

    public SplitPaneDividerSlider(SplitPane splitPane,
                                  int dividerIndex,
                                  Direction direction,
                                  Duration cycleDuration) {
        this.direction = direction;
        this.splitPane = splitPane;
        this.dividerIndex = dividerIndex;
        this.cycleDuration = cycleDuration;
        init();
    }

    private void init() {
        slideTransition = new SlideTransition(cycleDuration);

        switch (direction) {
            case LEFT:
            case UP:
                content = (Region) splitPane.getItems().get(dividerIndex);
                break;
            case RIGHT:
            case DOWN:
                content = (Region) splitPane.getItems().get(dividerIndex + 1);
                break;
        }
        contentInitialMinHeight = content.getMinHeight();
        contentInitialMinWidth = content.getMinWidth();
        dividerToMove = splitPane.getDividers().get(dividerIndex);

        aimContentVisibleProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue) {
                // store divider position before transition:
                setLastDividerPosition(splitPane.getDividers().get(dividerIndex).getPosition());
                // "arm" current divider position before transition:
                setCurrentDividerPosition(getLastDividerPosition());
            }
            content.setMinSize(minWidth, minHeight);
            slideTransition.play();
        });
    }

    public void setMinWidth(double width) {
        minWidth = width;
    }

    public void setMinHeight(double height) {
        minHeight = height;
    }

    private void restoreContentSize() {
        content.setMinHeight(contentInitialMinHeight);
        content.setMinWidth(contentInitialMinWidth);
        setCurrentDividerPosition(getLastDividerPosition());
    }

    public BooleanProperty aimContentVisibleProperty() {
        if (aimContentVisibleProperty == null) {
            aimContentVisibleProperty = new SimpleBooleanProperty(true);
        }
        return aimContentVisibleProperty;
    }

    public void setAimContentVisible(boolean aimContentVisible) {
        aimContentVisibleProperty().set(aimContentVisible);
    }

    public boolean isAimContentVisible() {
        return aimContentVisibleProperty().get();
    }

    public DoubleProperty lastDividerPositionProperty() {
        if (lastDividerPositionProperty == null) {
            lastDividerPositionProperty = new SimpleDoubleProperty();
        }
        return lastDividerPositionProperty;
    }

    public double getLastDividerPosition() {
        return lastDividerPositionProperty().get();
    }

    public void setLastDividerPosition(double lastDividerPosition) {
        lastDividerPositionProperty().set(lastDividerPosition);
    }

    public DoubleProperty currentDividerPositionProperty() {
        if (currentDividerPositionProperty == null) {
            currentDividerPositionProperty = new SimpleDoubleProperty();
        }
        return currentDividerPositionProperty;
    }

    public double getCurrentDividerPosition() {
        return currentDividerPositionProperty().get();
    }

    public void setCurrentDividerPosition(double currentDividerPosition) {
        currentDividerPositionProperty().set(currentDividerPosition);
        dividerToMove.setPosition(currentDividerPosition);
    }

    private class SlideTransition extends Transition {

        public SlideTransition(final Duration cycleDuration) {
            setCycleDuration(cycleDuration);
        }

        @Override
        protected void interpolate(double d) {
            switch (direction) {
                case LEFT:
                case UP:
                    if (isAimContentVisible()) {
                        if ((getCurrentDividerPosition() + d) <= getLastDividerPosition()) {
                            setCurrentDividerPosition(getCurrentDividerPosition() + d);
                        } else {
                            restoreContentSize();
                            stop();
                        }
                    } else {
                        if (getCurrentDividerPosition() > 0.0) {
                            setCurrentDividerPosition(getCurrentDividerPosition() - d);
                        } else {
                            setCurrentDividerPosition(0.0);
                            stop();
                        }
                    }
                    break;
                case RIGHT:
                case DOWN:
                    if (isAimContentVisible()) {
                        if ((getCurrentDividerPosition() - d) >= getLastDividerPosition()) {
                            setCurrentDividerPosition(getCurrentDividerPosition() - d);
                        } else {
                            restoreContentSize();
                            stop();
                        }
                    } else {
                        if (getCurrentDividerPosition() < 1.0) {
                            setCurrentDividerPosition(getCurrentDividerPosition() + d);
                        } else {
                            setCurrentDividerPosition(1.0);
                            stop();
                        }
                    }
                    break;
            }
        }
    }
}
