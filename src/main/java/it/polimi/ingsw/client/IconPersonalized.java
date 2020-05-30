package it.polimi.ingsw.client;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class IconPersonalized extends ImageIcon {

    int height;
    int width;

        public IconPersonalized( int width, int height) {


            Graphics2D g2d = (Graphics2D) godChosenYellow.getImage().getGraphics();
            g2d.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2d.drawImage(getImage(), 0, 0, width, height, null);
            setImage(godChosenYellow.getImage());
        }

        public int getIconHeight() {
            return height;
        }

        public int getIconWidth() {
            return width;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.drawImage(getImage(), x, y, c);
        }


    ImageIcon godChosenYellow = new ImageIcon(this.getClass().getClassLoader().getResource("Images/YELLOW.jpg"));
}
