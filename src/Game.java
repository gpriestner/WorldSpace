import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.sound.sampled.Line;

import java.util.ArrayList;
// import java.util.Random;
import javax.swing.*;
import javax.swing.event.MouseInputListener;
class Pt2D {
    public double x, y;
    public Pt2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
class Pt3D {
    public double x, y, z;
    public Pt3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public boolean isValid() {
        return !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z) && !isInfinite();
    }
    public boolean isInfinite() {
        return Double.isInfinite(x) || Double.isInfinite(y) || Double.isInfinite(z);
    }
}
class Pt {
    public Pt2D p2d;
    public Pt3D p3d;
    public Pt(Pt2D p2d, Pt3D p3d) {
        this.p2d = p2d;
        this.p3d = p3d;
    }
}
class Rotation {
    public double x, y, z;
    public Rotation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
class Utils {
    public static Pt3D rotateX(Pt3D p, double a) {
        var cos = Math.cos(a);
        var sin = Math.sin(a); 
        var x = p.x;
        var y = cos * p.y + sin * p.z;
        var z = -sin * p.y + cos * p.z;
        var n = new Pt3D(x, y, z);
        return n;
    }
    public static Pt3D rotateY(Pt3D p, double a) {
        var cos = Math.cos(a);
        var sin = Math.sin(a); 
        var x = cos * p.x - sin * p.z;
        var y = p.y;
        var z = sin * p.x + cos * p.z;
        var n = new Pt3D(x, y, z);
        return n;
    }
    public static Pt3D rotateZ(Pt3D p, double a) {
        var cos = Math.cos(a);
        var sin = Math.sin(a); 
        var x = cos * p.x - sin * p.y;
        var y = cos * p.y + sin * p.x;
        var z = p.z;
        var n = new Pt3D(x, y, z);
        return n;
    }
    // calculate the cross product of two vectors
    public static Pt3D cross(Pt3D v1, Pt3D v2) {
        var x = v1.y * v2.z - v1.z * v2.y;
        var y = v1.z * v2.x - v1.x * v2.z;
        var z = v1.x * v2.y - v1.y * v2.x;
        return new Pt3D(x, y, z);
    }
    public static Pt3D rotateYXZ(Pt3D p, Rotation r) {
        if (Math.abs(r.y) + Math.abs(r.x) + Math.abs(r.z) == 0.0) return p;
        var ry = rotateY(p, r.y);
        var rx = rotateX(ry, r.x);
        var rz = rotateZ(rx, r.z);
        return rz;
    }
    public static Pt3D rotateYX(Pt3D p, Rotation r) {
        if (Math.abs(r.y) + Math.abs(r.x) == 0.0) return p;
        var ry = rotateY(p, r.y);
        var rx = rotateX(ry, r.x);
        return rx;
    }
    // calculate the normal of a plane
    public static Pt3D normal(Pt3D p1, Pt3D p2, Pt3D p3) {
        var v1 = new Pt3D(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
        var v2 = new Pt3D(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);
        // var x = v1.y * v2.z - v1.z * v2.y;
        // var y = v1.z * v2.x - v1.x * v2.z;
        // var z = v1.x * v2.y - v1.y * v2.x;
        var n = cross(v1, v2); // new Pt3D(x, y, z);
        return Utils.normalize(n);
    }
    // wite a function to calculate the dot product of two vectors
    public static double dot(Pt3D v1, Pt3D v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }
    // calculate the magnitude (length/distance) of a vector
    public static double mag(Pt3D v) {
        return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }
    // calculate the normalized vector of a vector
    public static Pt3D normalize(Pt3D v) {
        var m = mag(v);
        var x = v.x / m;
        var y = v.y / m;
        var z = v.z / m;
        return new Pt3D(x, y, z);
    }
    // calculate the normalized normal of a plane
    public static Pt3D unitNormal(Pt3D p1, Pt3D p2, Pt3D p3) {
        var n = normal(p1, p2, p3);
        return n;
    }
    // multiply a vector by a scalar
    public static Pt3D scaleVector(Pt3D v, double s) {
        return new Pt3D(v.x * s, v.y * s, v.z * s);
    }
    // add two vectors
    public static Pt3D addVector(Pt3D v1, Pt3D v2) {
        var x = v1.x + v2.x;
        var y = v1.y + v2.y;
        var z = v1.z + v2.z;
        return new Pt3D(x, y, z);
    }
    // subtract a vector from another vector
    public static Pt3D subtractVector(Pt3D v1, Pt3D v2) { // v1 -> v2
        var x = v2.x - v1.x;
        var y = v2.y - v1.y;
        var z = v2.z - v1.z;
        return new Pt3D(x, y, z);
    }
    public static Pt3D subtractVector2(Pt3D v1, Pt3D v2) { // v2 -> v1
        var x = v1.x - v2.x;
        var y = v1.y - v2.y;
        var z = v1.z - v2.z;
        return new Pt3D(x, y, z);
    }
    // invert a vector
    public static Pt3D invertVector(Pt3D v) {
        return new Pt3D(-v.x, -v.y, -v.z);
    }
    // calculate centroid of a 3d polygon
    public static Pt3D centroid(ArrayList<Pt3D> points) {
        var x = 0.0;
        var y = 0.0;
        var z = 0.0;
        for (var p : points) {
            x += p.x;
            y += p.y;
            z += p.z;
        }
        x /= points.size();
        y /= points.size();
        z /= points.size();
        return new Pt3D(x, y, z);
    }
    public static Pt3D toLocalPoint(Pt3D p, Rotation rotation) {
        var ry = Utils.rotateY(p, rotation.y);
        var rx = Utils.rotateX(ry, rotation.x);
        var rz = Utils.rotateZ(rx, rotation.z);
        return rz;
    }
    public static Pt3D toWorldPoint(Camera camera, Pt3D p, Pt3D position, double scale) {
        final double x = position.x + p.x * scale;
        final double y = position.y + p.y * scale;
        final double z = position.z + p.z * scale;
        return new Pt3D(x, y, z);
    }
    public static Pt3D toCameraPoint(Camera camera, Pt3D p) {
        var cp = Utils.subtractVector(camera.position, p);
        var ry = Utils.rotateY(cp, -camera.rotation.y);
        var rx = Utils.rotateX(ry, -camera.rotation.x);
        return rx;
    }
    public static Pt2D to2DPoint(Camera camera, Pt3D p) {
        var height = Game.dimension.height;
        int x = (int)(p.x / p.z * height);
        int y = (int)(p.y / p.z * height);
        return new Pt2D(x, y);
    }
    // calculate the point of intersection between a line and a plane
    public static Pt3D intersect(Pt3D p1, Pt3D p2, Pt3D n, Pt3D p) {
        //var d = dot(n, p);
        var v = subtractVector(p2, p1);
        var t = (dot(n, p1) - dot(n, p)) / dot(n, v);
        return subtractVector(scaleVector(v, t), p1);
    }
    public static Pt3D intersect2(Pt3D p1, Pt3D p2, Pt3D n, Pt3D p) {
        var d = n.x * p.x + n.y * p.y + n.z * p.z;
        var t = (n.x * p1.x + n.y * p1.y + n.z * p1.z - d) / (n.x * (p1.x - p2.x) + n.y * (p1.y - p2.y) + n.z * (p1.z - p2.z));
        var x = p1.x + t * (p2.x - p1.x);
        var y = p1.y + t * (p2.y - p1.y);
        var z = p1.z + t * (p2.z - p1.z);
        return new Pt3D(x, y, z);
    }
    // calculate the angle between two vectors
    public static double angle(Pt3D v1, Pt3D v2) {
        var dot = Utils.dot(v1, v2);
        var m1 = Utils.mag(v1);
        var m2 = Utils.mag(v2);
        return Math.acos(dot / (m1 * m2));
    }
    // calculate the shortest distance between a point (pt) and a plane (p, n)
    public static double distance(Pt3D p, Pt3D n, Pt3D pt) {
        return Utils.dot(Utils.subtractVector(pt, p), n);
    }
    // calculate the shortest signed distance between a point and a plane
    public static double signedDistance(Pt3D p, Pt3D n, Pt3D p0) {
        return Utils.dot(Utils.subtractVector(p0, p), n) / Utils.mag(n);
    }
    // calculate a 2d unit vector representing an angle where zero degrees is 0,1
    public static Pt2D unitVector(double angle) {
        var x = Math.cos(angle);
        var y = Math.sin(angle);
        return new Pt2D(x, y);
    }
    // rotate a 3d point around a unit vector using a quaternion
    public static Pt3D rotate(Pt3D point, Pt3D vector, double angle) {
        var ha = angle / 2;
        var sin = Math.sin(ha);
        var qw = Math.cos(ha);
        //var qw = cos;
        var qx = vector.x * sin;
        var qy = vector.y * sin;
        var qz = vector.z * sin;
        var w2 = qw * qw;
        var x2 = qx * qx;
        var y2 = qy * qy;
        var z2 = qz * qz;
        var wx = qw * qx;
        var wy = qw * qy;
        var wz = qw * qz;
        var xy = qx * qy;
        var xz = qx * qz;
        var yz = qy * qz;

        var x = (w2 + x2 - y2 - z2) * point.x + 2 * (xy - wz) * point.y + 2 * (xz + wy) * point.z;
        var y = 2 * (xy + wz) * point.x + (w2 - x2 + y2 - z2) * point.y + 2 * (yz - wx) * point.z;
        var z = 2 * (xz - wy) * point.x + 2 * (yz + wx) * point.y + (w2 - x2 - y2 + z2) * point.z;
        return new Pt3D(x, y, z);
    }
    // calculate two vectors that are perpendicular to the given vector
    public static Pt3D[] perpendicular(Pt3D v) {
        var x = Math.abs(v.x);
        var y = Math.abs(v.y);
        var z = Math.abs(v.z);
        var other = new Pt3D(0, 0, 0);
        if (x < y && x < z) other.x = 1;
        else if (y < x && y < z) other.y = 1;
        else other.z = 1;
        var perp1 = Utils.unitNormal(v, other, Utils.cross(v, other));
        var perp2 = Utils.unitNormal(v, perp1, Utils.cross(v, perp1));
        return new Pt3D[] {perp1, perp2};
    }
    // calculate which side of a plane a point is on
    public static int side(Pt3D p, Pt3D n, Pt3D pt) {
        var d = Utils.distance(p, n, pt);
        return d > 0 ? 1 : d < 0 ? -1 : 0;
    }
}
class Camera {
    Pt3D position;
    Rotation rotation;
    Graphics2D g;
    public Camera(int x, int y, int z) {
        this.position = new Pt3D(x, y, z);
        this.rotation = new Rotation(0, 0, 0);
    }
    public Pt3D getHeading() {
        var heading = new Pt3D(-Math.sin(-rotation.y), 0, Math.cos(-rotation.y));
        return heading;
    }
    public Pt3D getPitchVector() {
        var pitchVector = new Pt3D(Math.cos(rotation.y), 0, Math.sin(rotation.y));
        return pitchVector;
    }
    public Pt3D getDirection() {
        var cosY = Math.cos(rotation.y);
        var sinY = Math.sin(rotation.y);
        var heading = new Pt3D(-sinY, 0, cosY);
        var pitchVector = new Pt3D(cosY, 0, sinY);
        var direction = Utils.rotate(heading, pitchVector, -rotation.x);
        return direction;
    }
    public Pt3D getForward() {
        var vForward = new Pt3D(0, 0, 1); // forward
        var heading = Utils.rotateY(vForward, rotation.y);
        var pitchVector = getRight();
        var forward = Utils.rotate(heading, pitchVector, rotation.x);
        return forward;
    }
    public Pt3D getRight() {
        var vRight = new Pt3D(1, 0, 0); // right
        var right = Utils.rotateY(vRight, rotation.y);
        return right;
    }
    public void moveForward(double distance) {
        var forward = getDirection(); // getForward();
        position.x += forward.x * distance;
        position.y += forward.y * distance;
        position.z += forward.z * distance;
    }
    public void moveBackward(double distance) {
        moveForward(-distance);
    }
    public void moveRight(double distance) {
        var right = getPitchVector(); // getRight();
        position.x += right.x * distance;
        position.y += right.y * distance;
        position.z += right.z * distance;
    }
    public void moveLeft(double distance) {
        moveRight(-distance);
    }
    public void setGraphics(Graphics2D g) {
        this.g = g;
    }
}
class Face {
    Cube cube;
    int[] indexes;
    Pt3D normal;
    public Face(Cube cube, int[] indexes) {
        this.cube = cube;
        this.indexes = new int[indexes.length];
        for (int i = 0; i < indexes.length; i++) {
            this.indexes[i] = indexes[i];
        }
        // set normal to the normalized cross product of the first three points
        var p1 = cube.model.get(indexes[0]);
        var p2 = cube.model.get(indexes[1]);
        var p3 = cube.model.get(indexes[2]);
        normal = Utils.unitNormal(p1, p2, p3);
    }
    // return the set of points that make up the face determined by the indexes
    public ArrayList<Pt2D> get2dPoints(ArrayList<Pt2D> points) {
        var face = new ArrayList<Pt2D>();
        for (var i : indexes) face.add(points.get(i));
        return face;
    }
    // return the set of 3d points that make up the face determined by the indexes
    public ArrayList<Pt3D> get3dPoints(ArrayList<Pt3D> points) {
        var face = new ArrayList<Pt3D>();
        for (var i : indexes) face.add(points.get(i));
        return face;
    }
    public void draw(Camera cam, ArrayList<Pt3D> points3d, ArrayList<Pt2D> points2d) {
        // calculate the dot product of the normal and the vector from the camera to the face
        //var camera = new Pt3D(0, 0, 0);
        Pt3D vector = Utils.normalize(Utils.subtractVector(cam.position, points3d.get(indexes[0])));

        // calculate normal of the face using points3d
        var p1 = points3d.get(indexes[0]);
        var p2 = points3d.get(indexes[1]);
        var p3 = points3d.get(indexes[2]);
        var n = Utils.unitNormal(p1, p2, p3);

        double dot = Utils.dot(n, vector);
        // if the dot product is negative, the face is facing the camera
        if (dot < 0) drawLines(cam.g, get2dPoints(points2d));
    }
    public void drawLine(Graphics2D g, Pt2D p1, Pt2D p2) {
        g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
    }
    // draw a sequence of lines that make up the face
    public void drawLines(Graphics2D g, ArrayList<Pt2D> points) {
        for (int i = 0; i < indexes.length; i++) {
            var p1 = points.get(i);
            var p2 = points.get((i + 1) % indexes.length);
            drawLine(g, p1, p2);
        }
    }
}
class GroundPlane {
    Pt3D position;
    Pt3D normal;
    int size;
    int divs;
    Color colorA;
    Color colorB;
    boolean wireframe = false;
    ArrayList<Pt3D> model = new ArrayList<Pt3D>();
    public GroundPlane(int x, int y, int z, int size, int divs) {
        this.position = new Pt3D(x, y, z);
        this.size = size;
        this.divs = divs;
        this.normal = new Pt3D(0, 1, 0);
        this.colorA = new Color(224, 224, 224  ); // Color.white;
        this.colorB = Color.lightGray;

        init();
    }
    private void init() {
        var half = size / 2;
        var step = size / divs;
        for (int z = -half; z <= half; z += step) {
            for (int x = -half; x <= half; x += step) {
                model.add(new Pt3D(position.x + x, 0, position.z + z));
            }
        }
    }
    public void draw(Camera camera) {
        // var points3d = new ArrayList<Pt3D>();
        var points2d = new ArrayList<Pt2D>();
        // var steps = size / divs;
        for (var p : model) {
            var cp = Utils.toCameraPoint(camera, p);
            var xy = Utils.to2DPoint(camera, cp);
            points2d.add(xy);
        }

        for (int z = 0; z < divs; z++) {
            for (int x = 0; x < divs; x++) {
                var i = x + z * 11;
                var c = (z%2 ^ x%2) == 0 ? colorA : colorB;
                drawSquare(camera, points2d, c, i, i + 1, i + 12, i + 11);
            }
        }
    }
    public void drawSquare(Camera camera, ArrayList<Pt2D> points2d, Color c, int i1, int i2, int i3, int i4) {
        var p1 = points2d.get(i1);
        var p2 = points2d.get(i2);
        var p3 = points2d.get(i3);
        var p4 = points2d.get(i4);

        Polygon polygon = new Polygon();
        polygon.addPoint((int)p1.x, (int)p1.y);
        polygon.addPoint((int)p2.x, (int)p2.y);
        polygon.addPoint((int)p3.x, (int)p3.y);
        polygon.addPoint((int)p4.x, (int)p4.y);

        camera.g.setColor(c);
        if (wireframe) camera.g.drawPolygon(polygon);
        else camera.g.fillPolygon(polygon);
    }
    public Pt3D getNormal() {
        return Utils.normal(model.get(0), model.get(1), model.get(divs + 1));
    }
    public Pt3D getPosition() {
        return position;
    }
    public Pt3D getPoint() {
        return model.get(0);
    }
}
class Cube {
    Pt3D position;
    Rotation rotation;
    double scale;
    Color color;
    Stroke line;
    ArrayList<Pt3D> model = new ArrayList<Pt3D>();
    ArrayList<Face> faces = new ArrayList<Face>();
    public Cube(double x, double y, double z, double scale) {
        position = new Pt3D(x, y, z);
        rotation = new Rotation(0, 0, 0);
        this.scale = scale;
        this.color = Color.red;
        line = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        init();
    }
    public Cube(Pt3D p) {
        position = p;
        init();
    }
    private void init() {
        model.add(new Pt3D(-0.5, 0.5, -0.5));
        model.add(new Pt3D(0.5, 0.5, -0.5));
        model.add(new Pt3D(0.5, -0.5, -0.5));
        model.add(new Pt3D(-0.5, -0.5, -0.5));

        model.add(new Pt3D(-0.5, 0.5, 0.5));
        model.add(new Pt3D(0.5, 0.5, 0.5));
        model.add(new Pt3D(0.5, -0.5, 0.5));
        model.add(new Pt3D(-0.5, -0.5, 0.5));

        faces.add(new Face(this, new int[] {0, 1, 2, 3})); // front face
        faces.add(new Face(this, new int[] {5, 4, 7, 6})); // back face
        faces.add(new Face(this, new int[] {4, 5, 1, 0})); // top face
        faces.add(new Face(this, new int[] {1, 5, 6, 2})); // right face
        faces.add(new Face(this, new int[] {3, 2, 6, 7})); // bottom face
        faces.add(new Face(this, new int[] {4, 0, 3, 7})); // left face
    }
    public void update() {
        rotation.x += 0.01;
        rotation.y += 0.01;
        //rotation.z += 0.01;
    }
    public void draw(Camera camera) {
        camera.g.setColor(color);
        camera.g.setStroke(line);

        var points2d = new ArrayList<Pt2D>();
        var points3d = new ArrayList<Pt3D>();
        for (var p : model) {
            var lp = Utils.toLocalPoint(p, rotation);
            var wp = Utils.toWorldPoint(camera, lp, position, scale);
            var cp = Utils.toCameraPoint(camera, wp);
            var xy = Utils.to2DPoint(camera, cp);
            points2d.add(xy);
            points3d.add(wp);
        }

        for (var face : faces) {
            face.draw(camera, points3d, points2d);
        }
    }
    public void line (Graphics2D g, Pt2D p1, Pt2D p2) {
        g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
    }
}
class Line {
    Pt3D position;
    Pt3D model;
    double scale;
    Color color;
    GroundPlane ground;
    Rotation rotation = new Rotation(0, 0, 0);
    public Line(double x, double y, double z, double s) {
        position = new Pt3D(x, y, z);
        model = new Pt3D(0, 0, 0.5); // Utils.normalize(d);
        scale = s;
        color = Color.green;
    }
    public void update() {
        // rotation.x += 0.01;
        // rotation.y += 0.01;
    }
    public void draw(Camera camera) {
        var start = Utils.rotateYX(model, rotation);
        var end = Utils.invertVector(start);
        var p1 = Utils.toWorldPoint(camera, start, position, scale);
        var p2 = Utils.toWorldPoint(camera, end, position, scale);
        var cp1 = Utils.toCameraPoint(camera, p1);
        var cp2 = Utils.toCameraPoint(camera, p2);
        var xy1 = Utils.to2DPoint(camera, cp1);
        var xy2 = Utils.to2DPoint(camera, cp2);
        camera.g.setColor(color);
        camera.g.drawLine((int)xy1.x, (int)xy1.y, (int)xy2.x, (int)xy2.y);

        var normal = ground.getNormal();
        var point = ground.getPoint();
        var ip = Utils.intersect(p1, p2, normal, point);
        if (ip.isValid()) {
            var cip = Utils.toCameraPoint(camera, ip);
            var xyip = Utils.to2DPoint(camera, cip);

            camera.g.setColor(Color.red);
            camera.g.fillOval((int)xyip.x - 2, (int)xyip.y - 2, 4, 4);
        }
    }
    public void setGround(GroundPlane ground) {
        this.ground = ground;
    }
}
public class Game extends JPanel implements ActionListener, MouseInputListener {
    Timer timer;
    Image image = null;
    int x = 0;
    int y = 0;
    boolean mouseCaptured = false;
    Robot robot;
    Point centerPoint = new Point();

    public static Dimension dimension;
    private Keyboard keyboard = new Keyboard();

    Camera camera = new Camera(0, 10, 0);
    Cube cube1 = new Cube(0, 10, 80, 5);
    Cube cube2 = new Cube(12, 12, 50, 5);
    Line line1 = new Line(0, 15, 100, 35);
    GroundPlane ground = new GroundPlane(0, 0, 100, 100, 10);

    Game(int boardWidth, int boardHeight) {
        setPreferredSize(new Dimension(800, 800));
        setBackground(Color.darkGray);
        addKeyListener(keyboard);
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);

        timer = new Timer(32, this);
        timer.start();

        try {
            var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            var gs = ge.getScreenDevices();
            robot = new Robot(gs[0]);
            //robot.mouseMove(100, 100);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        line1.setGround(ground);
        // ground.wireframe = true;
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        dimension = this.getSize();
        g.setColor(Color.white);
        drawCompass(g, 10, 20);
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform at = new AffineTransform();
        at.translate(dimension.width / 2, dimension.height / 2);
        at.scale(1.0, -1.0);
        g2.setTransform(at);
        camera.setGraphics(g2);
        // g2.setColor(Color.white);
        // g2.drawString(String.valueOf(camera.rotation.y), 0, 0);
        draw(camera);
        g2.dispose();
    }
    private void drawCompass(Graphics g, int offset, int radius) {
        final int width = radius * 2;
        final int height = radius * 2;
        final int center = offset + radius;
        g.drawArc(offset, offset, width, height, 0, 360);
        var x = center - (int)(radius * Math.sin(camera.rotation.y));
        var y = center - (int)(radius * Math.cos(camera.rotation.y));
        g.drawLine(center, center, x, y);
    }
    public void draw(Camera camera) {
        // if (this.image != null)  g.drawImage(image, x++, y++, this);
        if (Keyboard.isKeyDown(KeyEvent.VK_E)) camera.position.y += 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_C)) camera.position.y -= 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_A)) camera.moveLeft(1);
        if (Keyboard.isKeyDown(KeyEvent.VK_D)) camera.moveRight(1);
        if (Keyboard.isKeyDown(KeyEvent.VK_W)) camera.moveForward(1);
        if (Keyboard.isKeyDown(KeyEvent.VK_S)) camera.moveBackward(1);
        if (Keyboard.isKeyDown(KeyEvent.VK_COMMA)) camera.rotation.y += 0.01;
        if (Keyboard.isKeyDown(KeyEvent.VK_PERIOD)) camera.rotation.y -= 0.01;
        if (Keyboard.isKeyDown(KeyEvent.VK_O)) camera.rotation.x += 0.01;
        if (Keyboard.isKeyDown(KeyEvent.VK_L)) camera.rotation.x -= 0.01;
        
        if (Keyboard.isKeyDown(KeyEvent.VK_UP)) cube1.position.y += 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_DOWN)) cube1.position.y -= 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_LEFT)) cube1.position.x -= 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_RIGHT)) cube1.position.x += 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_PAGE_UP)) cube1.position.z += 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_PAGE_DOWN)) cube1.position.z -= 1;

        if (Keyboard.isKeyDown(KeyEvent.VK_SPACE)) test();
        if (Keyboard.isKeyDown(KeyEvent.VK_ESCAPE)) resetMouse();
        if (Keyboard.isKeyDown(KeyEvent.VK_R)) resetCamera();

        if (Keyboard.isKeyDown(KeyEvent.VK_K)) line1.scale += 0.5;
        if (Keyboard.isKeyDown(KeyEvent.VK_J)) line1.scale -= 0.5;
        if (Keyboard.isKeyDown(KeyEvent.VK_Y)) line1.rotation.x -= 0.1;
        if (Keyboard.isKeyDown(KeyEvent.VK_U)) line1.rotation.x += 0.1;
        if (Keyboard.isKeyDown(KeyEvent.VK_I)) line1.rotation.x += 0.001;
        if (Keyboard.isKeyDown(KeyEvent.VK_T)) line1.rotation.x -= 0.001;
        if (Keyboard.isKeyDown(KeyEvent.VK_G)) line1.rotation.y -= 0.1;
        if (Keyboard.isKeyDown(KeyEvent.VK_B)) line1.rotation.y += 0.1;


        cube1.update();
        cube2.update();
        line1.update();
        ground.draw(camera);
        cube1.draw(camera);
        cube2.draw(camera);
        line1.draw(camera);
    }
    private void resetCamera() {
        camera.position.x = 0;
        camera.position.y = 0;
        camera.position.z = 0;
        camera.rotation.x = 0;
        camera.rotation.y = 0;
        camera.rotation.z = 0;
    }
    private void resetMouse() {
        if (mouseCaptured) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            mouseCaptured = false;
        }
    }
    private void test() {
        var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        var gs = ge.getScreenDevices();
        try {
            robot = new Robot(gs[0]);
            //robot.mouseMove(100, 100);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    public void setImage(BufferedImage image) {
        this.image = image;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if (!mouseCaptured && e.getButton() == MouseEvent.BUTTON1) {
            var tk = Toolkit.getDefaultToolkit();
            var bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            var cursor = tk.createCustomCursor(bi, new Point(), "blank");
            this.setCursor(cursor);
            centerMouse();
            mouseCaptured = true;
        }
    }
    private void centerMouse() {
        var screen = this.getLocationOnScreen();
        var size = this.getSize();
        var midX = size.width / 2;
        var midY = size.height / 2;
        var x = screen.x + midX;
        var y = screen.y + midY;
        centerPoint.x = x;
        centerPoint.y = y;
        robot.mouseMove(x, y);
    }
    @Override
    public void mousePressed(MouseEvent e) { }
    @Override
    public void mouseReleased(MouseEvent e) { }
    public void mouseEntered(MouseEvent e) { }
    @Override
    public void mouseExited(MouseEvent e) { }
    @Override
    public void mouseDragged(MouseEvent e) { }
    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouseCaptured) {
            // var screenPoint = e.getLocationOnScreen();
            // var localPoint = e.getPoint();
            // var topLeft = new Point(screenPoint.x - localPoint.x, screenPoint.y - localPoint.y);
            // var size = this.getSize();
            // var midX = size.width / 2;
            // var midY = size.height / 2;
            // robot.mouseMove(topLeft.x + midX, topLeft.y + midY);

            // var p = e.getPoint();
            var p = e.getLocationOnScreen();
            var dx = p.x - centerPoint.x;
            var dy = p.y - centerPoint.y;

            if (dx != 0) camera.rotation.y -= dx / 1000.0;
            if (dy != 0) camera.rotation.x -= dy / 1000.0;

            centerMouse();
        }
    }
}
class Keyboard implements KeyListener {
    static boolean[] keys = new boolean[256];
    static boolean[] read = new boolean[256];
    // public Keyboard() {
    //     for (int i = 0; i < keys.length; i++) {
    //         keys[i] = false;
    //     }
    // }
    public static boolean isKeyDown(int key) {
        return keys[key];
    }
    public static boolean isKeyHit(int key) {
        if (!keys[key]) return false;
        if (!read[key]) {
            read[key] = true;
            return true;
        }
        return false;
    }    
    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        read[key] = false;
        keys[key] = false;
    }
}