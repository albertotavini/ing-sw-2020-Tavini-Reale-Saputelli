package it.polimi.ingsw.client;





import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ImageResize {

    /*
        public static void main(String[] args) {
            try {
                URL url = new URL("http://i.stack.imgur.com/L5DGx.png");
                BufferedImage image = ImageIO.read(url);
                ImageIO.write(resizeImage(image, 32, 32), "png", new File("D:/picture3.png"));
            } catch (IOException e) {

                e.printStackTrace();
            }
        }*/
/*
        private static BufferedImage resize(BufferedImage image, int width, int height) {
            int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
            BufferedImage resizedImage = new BufferedImage(width, height, type);
            Graphics2D g = resizedImage.createGraphics();

            g.setComposite(AlphaComposite.Src);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(image, 0, 0, width, height, null);
            g.dispose();
            return resizedImage;
        }

        public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
            image = createCompatibleImage(image);
            image = resize(image, 100, 100);
            image = blurImage(image);
            return resize(image, width, height);
        }

        public static BufferedImage blurImage(BufferedImage image) {
            float ninth = 1.0f/9.0f;
            float[] blurKernel = {
                    ninth, ninth, ninth,
                    ninth, ninth, ninth,
                    ninth, ninth, ninth
            };

            Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
            map.put(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            map.put(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);
            map.put(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            RenderingHints hints = new RenderingHints(map);
            BufferedImageOp op = new ConvolveOp(new Kernel(3, 3, blurKernel), ConvolveOp.EDGE_NO_OP, hints);
            return op.filter(image, null);
        }

        private static BufferedImage createCompatibleImage(BufferedImage image) {
            GraphicsConfiguration gc = BufferedImageGraphicsConfig.getConfig(image);
            int w = image.getWidth();
            int h = image.getHeight();
            BufferedImage result = gc.createCompatibleImage(w, h, Transparency.TRANSLUCENT);
            Graphics2D g2 = result.createGraphics();
            g2.drawRenderedImage(image, null);
            g2.dispose();
            return result;
        }
        */

}
