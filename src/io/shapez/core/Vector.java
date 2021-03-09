package io.shapez.core;

public class Vector {
    public final double x;
    public final double y;
    double xy;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        this.xy = x*x+y*y;
    }

    public double length() {
        return Math.sqrt(xy);
    }
}
