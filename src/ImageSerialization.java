import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

public class ImageSerialization implements Serializable {
    private BufferedImage bufferedImage;

    public ImageSerialization(BufferedImage bufferedImage){
        this.bufferedImage=bufferedImage;
    }

    public void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        ImageIcon imageIcon=new ImageIcon(bufferedImage);
        int w = imageIcon.getIconWidth();
        int h = imageIcon.getIconHeight();
        int[] pixels = bufferedImage != null? new int[w * h] : null;

        if (bufferedImage != null) {
            try {
                PixelGrabber pg = new PixelGrabber(bufferedImage, 0, 0, w, h, pixels, 0, w);
                pg.grabPixels();
                if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
                    throw new IOException("failed to load image contents");
                }
            }
            catch (InterruptedException e) {
                throw new IOException("image load interrupted");
            }
        }
        s.writeInt(w);
        s.writeInt(h);
        s.writeObject(pixels);
    }

    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        s.defaultReadObject();

        int w = s.readInt();
        int h = s.readInt();
        int[] pixels = (int[])(s.readObject());

        if (pixels != null) {
            Toolkit tk = Toolkit.getDefaultToolkit();
            ColorModel cm = ColorModel.getRGBdefault();
            bufferedImage = (BufferedImage) tk.createImage(new MemoryImageSource(w, h, cm, pixels, 0, w));
        }
    }
}
