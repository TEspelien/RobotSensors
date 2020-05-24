public class Vec3d {

    double x;
    double y;
    double z;
    double length;
    double phi;
    double theta;

    //conversions from here https://math.libretexts.org/Bookshelves/Calculus/Book%3A_Calculus_(OpenStax)/12%3A_Vectors_in_Space/12.7%3A_Cylindrical_and_Spherical_Coordinates
    Vec3d cartesian(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.length = Math.sqrt(x * x + y * y + z * z);
        this.phi = Math.atan2(x, y);
        this.theta = Math.atan2(z, Math.sqrt(x * x + y * y));
        //https://docs.oracle.com/javase/7/docs/api/java/lang/Math.html#atan2(double,%20double)
    }

    Vec3d polar(double r, double phi, double theta) {
        this.length = r;
        this.phi = phi;
        this.theta = theta;
        this.x = r * Math.sin(phi) * Math.cos(theta);
        this.y = r * Math.sin(phi) * Math.sin(theta);
        this.z = r * Math.cos(phi);
    }

    Vec3d(Vec2d v, double z) {
        this.x = v.x;
        this.y = v.y;
        this.z = z;
    }
}