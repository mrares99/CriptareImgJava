import java.awt.image.BufferedImage;

public class ImageObject {
    private int XAxis;
    private int YAxis;
    private int height;
    private int width;
    private BufferedImage bufferedImage;

    public ImageObject() {
    }

    public ImageObject(int XAxis, int YAxis, int height, int width, BufferedImage bufferedImage) {
        this.XAxis = XAxis;
        this.YAxis = YAxis;
        this.height = height;
        this.width = width;
        this.bufferedImage = bufferedImage;
    }

    public int getXAxis() {
        return XAxis;
    }

    public void setXAxis(int XAxis) {
        this.XAxis = XAxis;
    }

    public int getYAxis() {
        return YAxis;
    }

    public void setYAxis(int YAxis) {
        this.YAxis = YAxis;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }
}
