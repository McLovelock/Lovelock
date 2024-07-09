package io.github.mclovelock.lovelock.utils.maths;

public final class Numerics {

    public static long cantorPair(long a, long b) {
        return (((a + b) * (a + b + 1)) / 2) + b;
    }

    public static long signedCantorPair(long a, long b) {
        a = (a >= 0) ? 2 * a : -2 * a - 1;
        b = (b >= 0) ? 2 * b : -2 * b - 1;
        return cantorPair(a, b);
    }

}
