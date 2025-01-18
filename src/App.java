import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 600;
        int boardHeight = 600;

        JFrame frame = new JFrame("Java");
        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //var currentDir = new File(".");
        //var files = currentDir.listFiles();

        Image i2;
        var f = new File("./res/Euler.png");
        var image = ImageIO.read(f);

        Game game = new Game(boardWidth, boardHeight);


        game.setImage(image);

        frame.add(game);
        frame.pack();
        game.requestFocus();;
    }
}
