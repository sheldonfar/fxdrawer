package com.fxdrawer.util;

import javafx.scene.control.ToggleButton;

public class RadioToggleButton extends ToggleButton {

    private String textValue;

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    @Override
    public void fire() {
        if (getToggleGroup() == null || !isSelected()) {
            super.fire();
        }
    }
}
