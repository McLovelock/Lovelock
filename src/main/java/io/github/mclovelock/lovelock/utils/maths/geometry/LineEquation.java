package io.github.mclovelock.lovelock.utils.maths.geometry;

public record LineEquation(double a, double b, double c) {
    public LineEquation(double x1, double y1, double x2, double y2) {
        this(y2 - y1, x1 - x2, y1 * (x2 - x1) - (y2 - y1) * x1);
    }

    public double at(double x, double y) {
        return a * x + b * y - c;
    }
}