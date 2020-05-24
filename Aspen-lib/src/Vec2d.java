public class Vec2d {

    double x;
    double y;

    double length;
    double angle;

    Vec2d cartesian(double x, double y) {
        this.x = x;
        this.y = y;
        this.length = Math.sqrt(x*x+y*y);
        this.angle = Math.atan2(y,x);
    }

    Vec2d polar(double l, double a) {
        this.length = l;
        this.angle = a;
        this.x = l*Math.cos(a);
        this.y = l*Math.sin(a);
    }
}