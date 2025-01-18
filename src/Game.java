import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
// import java.util.Random;
import javax.swing.*;
class Pt {
    public double x, y, z;
    public Pt(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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
    public static Pt rotateX(Pt p, double a) {
        var cos = Math.cos(a);
        var sin = Math.sin(a); 
        var x = p.x;
        var y = cos * p.y + sin * p.z;
        var z = -sin * p.y + cos * p.z;
        var n = new Pt(x, y, z);
        return n;
    }
    public static Pt rotateY(Pt p, double a) {
        var cos = Math.cos(a);
        var sin = Math.sin(a); 
        var x = cos * p.x - sin * p.z;
        var y = p.y;
        var z = sin * p.x + cos * p.z;
        var n = new Pt(x, y, z);
        return n;
    }
    public static Pt rotateZ(Pt p, double a) {
        var cos = Math.cos(a);
        var sin = Math.sin(a); 
        var x = cos * p.x - sin * p.y;
        var y = cos * p.y + sin * p.x;
        var z = p.z;
        var n = new Pt(x, y, z);
        return n;
    }
    // calculate the normal of a plane
    public static Pt normal(Pt p1, Pt p2, Pt p3) {
        var v1 = new Pt(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
        var v2 = new Pt(p3.x - p1.x, p3.y - p1.y, p3.z - p1.z);
        var x = v1.y * v2.z - v1.z * v2.y;
        var y = v1.z * v2.x - v1.x * v2.z;
        var z = v1.x * v2.y - v1.y * v2.x;
        var n = new Pt(x, y, z);
        return n;
    }
    // wite a function to calculate the dot product of two vectors
    public static double dot(Pt v1, Pt v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }
    // calculate the magnitude (length/distance) of a vector
    public static double mag(Pt v) {
        return Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
    }
    // calculate the normalized vector of a vector
    public static Pt normalize(Pt v) {
        var m = mag(v);
        var x = v.x / m;
        var y = v.y / m;
        var z = v.z / m;
        return new Pt(x, y, z);
    }
    // calculate the normalized normal of a plane
    public static Pt unitNormal(Pt p1, Pt p2, Pt p3) {
        var n = normal(p1, p2, p3);
        return normalize(n);
    }
    // multiply a scalar by a vector
    public static Pt scaleVector(Pt v, double s) {
        var x = s * v.x;
        var y = s * v.y;
        var z = s * v.z;
        return new Pt(x, y, z);
    }
    // add two vectors
    public static Pt addVector(Pt v1, Pt v2) {
        var x = v1.x + v2.x;
        var y = v1.y + v2.y;
        var z = v1.z + v2.z;
        return new Pt(x, y, z);
    }
    // subtract two vectors
    public static Pt subtractVector(Pt v1, Pt v2) {
        var x = v1.x - v2.x;
        var y = v1.y - v2.y;
        var z = v1.z - v2.z;
        return new Pt(x, y, z);
    }
    // invert a vector
    public static Pt invertVector(Pt v) {
        return new Pt(-v.x, -v.y, -v.z);
    }
    // calculate centroid of a 3d polygon
    public static Pt centroid(ArrayList<Pt> points) {
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
        return new Pt(x, y, z);
    }
}
class Face {
    Cube cube;
    int[] indexes;
    Pt[] model;
    Pt normal;
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
}
class Cube {
    Pt position;
    Rotation rotation;
    double scale;
    Color color;
    Stroke line;
    ArrayList<Pt> model = new ArrayList<Pt>();
    public Cube(double x, double y, double z, double scale) {
        position = new Pt(x, y, z);
        rotation = new Rotation(0, 0, 0);
        this.scale = scale;
        this.color = Color.yellow;
        line = new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        init();
    }
    public Cube(Pt p) {
        position = p;
        init();
    }
    private void init() {
        model.add(new Pt(-0.5, 0.5, -0.5));
        model.add(new Pt(0.5, 0.5, -0.5));
        model.add(new Pt(0.5, -0.5, -0.5));
        model.add(new Pt(-0.5, -0.5, -0.5));

        model.add(new Pt(-0.5, 0.5, 0.5));
        model.add(new Pt(0.5, 0.5, 0.5));
        model.add(new Pt(0.5, -0.5, 0.5));
        model.add(new Pt(-0.5, -0.5, 0.5));
    }
    public void draw(Graphics2D g) {
        g.setColor(color);
        g.setStroke(line);
        rotation.x += 0.03;
        //rotation.y += 0.01;
        //rotation.z += 0.01;

        if (Keyboard.isKeyDown(KeyEvent.VK_UP)) position.y += 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_DOWN)) position.y -= 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_LEFT)) position.x -= 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_RIGHT)) position.x += 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_PAGE_UP)) position.z += 1;
        if (Keyboard.isKeyDown(KeyEvent.VK_PAGE_DOWN)) position.z -= 1;

        var points = new ArrayList<Point>();
        for (var p : model) {
            var lp = toLocalPoint(p);
            var wp = toWorldPoint(lp);
            var xy = to2DPoint(wp);
            points.add(xy);
        }

        // front face
        for (int i = 0; i < 4; ++i) {
            var p1 = points.get(i);
            var p2 = points.get((i + 1) % 4);
            line(g, p1, p2);
        }

        // back face
        for (int i = 4; i < 8; ++i) {
            var p1 = points.get(i);
            var p2 = points.get(((i + 1) % 4) + 4);
            line(g, p1, p2);
        }

        // sides
        var p0 = points.get(0);
        var p1 = points.get(1);
        var p2 = points.get(2);
        var p3 = points.get(3);
        var p4 = points.get(4);
        var p5 = points.get(5);
        var p6 = points.get(6);
        var p7 = points.get(7);

        line(g, p0, p4);
        line(g, p1, p5);
        line(g, p2, p6);
        line(g, p3, p7);
    }
    public Pt toLocalPoint(Pt p) {
        var ry = Utils.rotateY(p, rotation.y);
        var rx = Utils.rotateX(ry, rotation.x);
        var rz = Utils.rotateZ(rx, rotation.z);
        return rz;
    }
    public Pt toWorldPoint(Pt p) {
        final double x = position.x + p.x * scale;
        final double y = position.y + p.y * scale;
        final double z = position.z + p.z * scale;
        return new Pt(x, y, z);
    }
    public Point to2DPoint(Pt p) {
        var height = Game.dimension.height;
        int x = (int)(p.x / p.z * height);
        int y = (int)(p.y / p.z * height);
        return new Point(x, y);
    }
    // public void line (Graphics2D g, Pt p1, Pt p2) {
    //     g.drawLine((int)(p1.x * scale), (int)(p1.y * scale), (int)(p2.x * scale), (int)(p2.y * scale));
    // }
    public void line (Graphics2D g, Point p1, Point p2) {
        g.drawLine(p1.x, p1.y, p2.x, p2.y);
    }
}

public class Game extends JPanel implements ActionListener {
    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    Timer timer;
    Image image = null;
    int x = 0;
    int y = 0;

    public static Dimension dimension;
    private Keyboard keyboard;

    Cube cube = new Cube(0, 0, 20, 5);

    Game(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        keyboard = new Keyboard();
        addKeyListener(keyboard);
        setFocusable(true);



        timer = new Timer(32, this);
        timer.start();

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        dimension = this.getSize();
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform at = new AffineTransform();
        at.translate(dimension.width / 2, dimension.height / 2);
        at.scale(1.0, -1.0);
        g2.setTransform(at);
        draw(g2);
        g2.dispose();
    }
    public void draw(Graphics2D g) {
        // if (this.image != null)  g.drawImage(image, x++, y++, this);

        cube.draw(g);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    /*
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:
            cube.position.z += 1;
            break;
            case KeyEvent.VK_DOWN:
            cube.position.z -= 1;
            break;
            case KeyEvent.VK_LEFT:
            cube.position.x -= 1;
            break;
            case KeyEvent.VK_RIGHT:
            cube.position.x += 1;
            break;        
            case KeyEvent.VK_PAGE_UP:
            cube.position.y += 1;
            break;        
            case KeyEvent.VK_PAGE_DOWN:
            cube.position.y -= 1;
            break;        
            default:
                break;
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    */
    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
class Keyboard implements KeyListener {
    static boolean[] keys = new boolean[256];
    public Keyboard() {
        for (int i = 0; i < keys.length; i++) {
            keys[i] = false;
        }
    }
    public static boolean isKeyDown(int key) {
        return keys[key];
    }
    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
}