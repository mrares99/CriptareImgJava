import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParalelImageEncryption extends Thread{
    private ImageObject imageObject;
    private ImageEncryption imageEncryption;
    private double[][] diffusionImage;
    private List<Integer> secretKeyForBakerMap;
    private long key;
    private Thread thread;
    private String threadName;
    private int lengthOfImage;
    private int n11;
    private static List<double[][]> imageDoubleValues=new ArrayList<double[][]>();
    private static List<ImageObject> imageObjectList=new ArrayList<ImageObject>();

    public ParalelImageEncryption(String threadName){
        this.imageEncryption=new ImageEncryption();
        this.threadName=threadName;
    }

    public int getN11() {
        return n11;
    }

    public void setN11(int n11) {
        this.n11 = n11;
    }


    public String getThreadName() {
        return threadName;
    }

    public static List<double[][]> getImageDoubleValues() {
        return imageDoubleValues;
    }

    public static void setImageDoubleValues(List<double[][]> imageDoubleValues) {
        ParalelImageEncryption.imageDoubleValues = imageDoubleValues;
    }

    public static List<ImageObject> getImageObjectList() {
        return imageObjectList;
    }

    public static void setImageObjectList(List<ImageObject> imageObjectList) {
        ParalelImageEncryption.imageObjectList = imageObjectList;
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

    public ImageObject getImageObject() {
        return imageObject;
    }

    public void setImageObject(ImageObject imageObject) {
        this.imageObject = imageObject;
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
            double[][] DCTimage = imageEncryption.generateDCTForImage(imageObject.getBufferedImage());
            double[][] DCTImageBakerMap = imageEncryption.generateBakerMap(DCTimage, lengthOfImage, lengthOfImage, secretKeyForBakerMap);
            double[][] diffusionImageBakerMap = imageEncryption.generateBakerMap(diffusionImage, lengthOfImage, lengthOfImage, secretKeyForBakerMap);
            double[][] XORedImages = imageEncryption.XORTwoImages(DCTImageBakerMap, diffusionImageBakerMap, lengthOfImage, lengthOfImage, n11);
            BufferedImage imageEncrypted = imageEncryption.generateBufferedImageFromDoubleValues(XORedImages, lengthOfImage, lengthOfImage);
            ViewImage viewImage = new ViewImage();
            viewImage.displayImage(imageEncrypted, threadName, imageEncrypted.getWidth(), imageEncrypted.getHeight());
            imageDoubleValues.add(XORedImages);
            imageObjectList.add(imageObject);
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
