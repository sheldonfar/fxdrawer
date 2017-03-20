package com.fxdrawer.util;

import java.io.Serializable;

public class Coordinates implements Serializable {
    private double x1;
    private double x2;
    private double y1;
    private double y2;

    public Coordinates(double x1, double x2, double y1, double y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public double getY1() {
        return y1;
    }

    public double getY2() {
        return y2;
    }

    public String toString() {
        return x1 + "  " + x2 + "  " + y1 + "  " + y2;
    }
}
