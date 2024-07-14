package io.github.mclovelock.lovelock.utils.maths.geometry;

import org.joml.Vector2d;

public final class Geometry {

    public static Vector2d lineIntersection(LineEquation l1, LineEquation l2) {
        double y = (-l2.a() / l1.a() * l1.c() +  l2.c()) / (l2.b() - l2.a() / l1.a() * l1.b());
        double x = (-l1.b() * y + l1.c()) / l1.a();
        return new Vector2d(x, y);
    }

}
