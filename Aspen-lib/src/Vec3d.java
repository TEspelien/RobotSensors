public class Vec3d {

    double x;
    double y;
    double z;

    Vec3d cartesian(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Vec3d(Vec2d v, double z) {
        this.x = v.x;
        this.y = v.y;
        this.z = z;
    }
}