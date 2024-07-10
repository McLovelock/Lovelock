package io.github.mclovelock.lovelock.utils.maths;

public final class Maths {

    public static final double TOLERANCE = 1e-7;

    public static double angleOnUnitCircle(double x, double y) {
        if (y >= 0) {
            return Math.cos(x);
        } else {
            return Math.cos(x) + Math.PI;
        }
    }

    public static boolean isEqual(double a, double b, double tolerance) {
        double norm = Math.max(Math.abs(a), Math.abs(b));
        return (norm < tolerance) || (Math.abs(a - b) < (tolerance * norm));
    }

    public static boolean isEqual(double a, double b) {
        return isEqual(a, b, TOLERANCE);
    }

}
