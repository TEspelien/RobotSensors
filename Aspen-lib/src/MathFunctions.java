public class MathFunctions {

    public double mag(Vec2d v) {
        return Mat.sqrt(v.x * v.x + v.y * v.y);
    }

    public double mag(Vec3d v) {
        return Mat.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }


    public double dot(Vec2d v1, Vec2d v2) {
        //be wary that if the vectors are perpendicular this could be very close but not equal to 0 due to rounding
        return v1.x * v2.x + v1.y * v2.y;
    }

    public double dot(Vec3d v1, Vec3d v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public Vec3d cross(Vec3d v1, Vec3d v2) {
        double x = v1.y * v2.z - v1.z * v2.y;
        double y = v1.z * v2.x - v1.x * v2.z;
        double z = v1.x * v2.y - v1.y * v2.x;
        return new cartesian(x, y, z);
    }


    public Vec2d scalarMult(double k, Vec2d v) {
        return new cartesian(v.x * k, v.y * k);
    }

    public Vec3d scalarMult(double k, Vec3d v) {
        return new cartesian(v.x * k, v.y * k, v * z * k);
    }

    public Vec2d matrixMult(double[][] m, Vec2d v) {
        double x = v.x * m[0][0] + v.y * m[0][1];
        double y = v.x * m[1][0] + v.y * m[1][1];
        return new cartesian(x, y);
    }

    public Vec2d matrixMult(double[][] m, Vec3d v) {
        double x = v.x * m[0][0] + v.y * m[0][1] + v.z * m[0][2];
        double y = v.x * m[1][0] + v.y * m[1][1] + v.z * m[1][2];
        double z = v.x * m[2][0] + v.y * m[2][1] + v.z * m[2][2];
        return new cartesian(x, y, z);
    }


    public Vec2d add(Vec2d v1, Vec2d v2) {
        return cartesian(v1.x + v2.x, v1.y + v2.y);
    }

    public Vec2d subtract(Vec2d v1, Vec2d v2) {
        return cartesian(v2.x - v1.x, v2.y - v1.y);
    }

    public Vec3d add(Vec3d v1, Vec3d v2) {
        return cartesian(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    public Vec2d subtract(Vec2d v1, Vec2d v2) {
        return cartesian(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
    }


    public double angle(Vec2d v1, Vec2d v2) {
        return Math.abs(v1.angle - v2.angle);
        // return dot(v1, v2) / (mag(v1) * mag(v2));
    }

    public double angle(Vec3d v1, Vec3d v2) {
        return dot(v1, v2) / (mag(v1) * mag(v2));
    }


}