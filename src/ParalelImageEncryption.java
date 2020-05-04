import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class ParalelImageEncryption extends Thread{
    private BufferedImage bufferedImage;
    private ImageEncryption imageEncryption;
    private double[][] diffusionImage;
    private List<Integer> secretKeyForBakerMap;
    private long key;
    private Thread thread;
    private String threadName;
    private int lengthOfImage;

    public ParalelImageEncryption(String threadName){
        this.imageEncryption=new ImageEncryption();
        this.threadName=threadName;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public String getThreadName() {
        return threadName;
    }

    public int getLengthOfImage() {
        return lengthOfImage;
    }

    public void setLengthOfImage(int lengthOfImage) {
        this.lengthOfImage = lengthOfImage;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public ImageEncryption getImageEncryption() {
        return imageEncryption;
    }

    public void setImageEncryption(ImageEncryption imageEncryption) {
        this.imageEncryption = imageEncryption;
    }

    public double[][] getDiffusionImage() {
        return diffusionImage;
    }

    public void setDiffusionImage(double[][] diffusionImage) {
        this.diffusionImage = diffusionImage;
    }

    public List<Integer> getSecretKeyForBakerMap() {
        return secretKeyForBakerMap;
    }

    public void setSecretKeyForBakerMap(List<Integer> secretKeyForBakerMap) {
        this.secretKeyForBakerMap = secretKeyForBakerMap;
    }

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }

    public void run()  {
        try {
            double[][] DCTimage = imageEncryption.generateDCTForImage(bufferedImage);
            double[][] DCTImageBakerMap = imageEncryption.generateBakerMap(DCTimage, lengthOfImage, lengthOfImage, secretKeyForBakerMap);
            double[][] diffusionImageBakerMap = imageEncryption.generateBakerMap(diffusionImage, lengthOfImage, lengthOfImage, secretKeyForBakerMap);
            double[][] XORedImages = imageEncryption.XORTwoImages(DCTImageBakerMap, diffusionImageBakerMap, lengthOfImage, lengthOfImage, key % 10);
            BufferedImage imageEncrypted = imageEncryption.generateBufferedImageFromDoubleValues(XORedImages, lengthOfImage, lengthOfImage);
            ViewImage viewImage = new ViewImage();
            viewImage.displayImage(imageEncrypted, threadName, imageEncrypted.getWidth(), imageEncrypted.getHeight());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void start () {
        if (thread == null) {
            thread = new Thread (this);
            thread.start ();
        }
    }

}
