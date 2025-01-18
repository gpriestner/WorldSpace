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
        line = new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
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
        rotation.x += 0.01;
        //rotation.y += 0.01;
        //rotation.z += 0.01;

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
        var height = 600d;
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

public class Game extends JPanel implements ActionListener, KeyListener {
    int boardWidth;
    int boardHeight;
    int tileSize = 25;

    Timer timer;
    Image image = null;
    int x = 0;
    int y = 0;

    Dimension dimension;

    Cube cube = new Cube(0, 0, 20, 5);

    Game(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);



        timer = new Timer(32, this);
        timer.start();

    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        var d = this.getSize();
        Graphics2D g2 = (Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform at = new AffineTransform();
        at.translate(d.width / 2, d.height / 2);
        at.scale(1.0, -1.0);
        g2.setTransform(at);
        draw(g2);
        g2.dispose();
    }
    public void draw(Graphics2D g) {
        if (this.image != null)  g.drawImage(image, x++, y++, this);


        //Graphics2D g2 = (Graphics2D)g.create();
        if (dimension != null) g.drawString(dimension.width + " " + dimension.height, 0, -50);

        for (int i = 0; i < boardWidth/tileSize; i++) {
            g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
            g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
        }

        g.setColor(Color.red);
        //g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

        cube.draw(g);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP:

            break;
            case KeyEvent.VK_DOWN:

            break;
            case KeyEvent.VK_LEFT:

            break;
            case KeyEvent.VK_RIGHT:

            break;        
            default:
                break;
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
