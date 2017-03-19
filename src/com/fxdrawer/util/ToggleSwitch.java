package com.fxdrawer.util;

import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ToggleSwitch extends Parent {

    private Boolean switchedOn = false;
    private TranslateTransition translateAnimation = new TranslateTransition(Duration.seconds(0.25));
    private FillTransition fillAnimation = new FillTransition(Duration.seconds(0.25));
    private ParallelTransition animation = new ParallelTransition(translateAnimation, fillAnimation);

    public ToggleSwitch() {
        Rectangle background = new Rectangle(100, 50);
        background.setArcWidth(50);
        background.setArcHeight(50);
        background.setFill(Color.WHITE);
        background.setStroke(Color.LIGHTGRAY);

        Circle trigger = new Circle(25);
        trigger.setCenterX(25);
        trigger.setCenterY(25);
        trigger.setFill(Color.WHITE);
        trigger.setStroke(Color.LIGHTGRAY);

        DropShadow shadow = new DropShadow();
        shadow.setRadius(2);
        trigger.setEffect(shadow);

        translateAnimation.setNode(trigger);
        fillAnimation.setShape(background);

        getChildren().addAll(background, trigger);
    }

    public void toggle() {
        switchedOn = !switchedOn;

        translateAnimation.setToX(switchedOn ? 100 - 50 : 0);
        fillAnimation.setFromValue(switchedOn ? Color.WHITE : Color.LIGHTGREEN);
        fillAnimation.setToValue(switchedOn ? Color.LIGHTGREEN : Color.WHITE);

        animation.play();
    }

    public Boolean isToggled() {
        return switchedOn;
    }
}
