import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ParalelImageDecryption extends Thread {
    private List<Integer> secretKeyForBakerMap;
    private long key;
    private Thread thread;
    private String threadName;
    private int lengthOfImage;
    private int n11;
    private ImageDecryption imageDecryption;
    private static List<ImageObject> imageObjectList=new ArrayList<ImageObject>();
    private double[][] diffusionImage;
    private double[][] cryptedImages;
    private ImageObject imageObject;
    private int XAxis;
    private int YAxis;

    public ParalelImageDecryption(String threadName){
        this.threadName=threadName;
        this.imageDecryption=new ImageDecryption();
        this.imageObject=new ImageObject();
    }

    public void run(){
        try {
            double[][] diffusionImageBakerMap = imageDecryption.generateBakerMap(diffusionImage, lengthOfImage, lengthOfImage, secretKeyForBakerMap);
            double[][] DCTImageBakerMap = imageDecryption.XORTwoImages(cryptedImages, diffusionImageBakerMap, lengthOfImage,
                    lengthOfImage, n11);
            double[][] DCTimage = imageDecryption.decryptBakerMap(DCTImageBakerMap, lengthOfImage, lengthOfImage,
                    secretKeyForBakerMap);
            double[][] decryptedImage = imageDecryption.generateIDCTForImage(DCTimage, lengthOfImage, lengthOfImage);
            BufferedImage finalDecriptedImage = imageDecryption.generateBufferedImageFromDoubleValues(decryptedImage, lengthOfImage, lengthOfImage);
            imageObject.setBufferedImage(finalDecriptedImage);
            imageObject.setXAxis(XAxis);
            imageObject.setYAxis(YAxis);
            imageObject.setWidth(lengthOfImage);
            imageObject.setHeight(lengthOfImage);
            imageObjectList.add(imageObject);
            ViewImage viewImage = new ViewImage();
            viewImage.displayImage(finalDecriptedImage, threadName, lengthOfImage, lengthOfImage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void start () {
        if (thread == null) {
            thread = new Thread (this);
            thread.start ();
        }
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

    public ImageObject getImageObject() {
        return imageObject;
    }

    public void setImageObject(ImageObject imageObject) {
        this.imageObject = imageObject;
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

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public int getLengthOfImage() {
        return lengthOfImage;
    }

    public void setLengthOfImage(int lengthOfImage) {
        this.lengthOfImage = lengthOfImage;
    }

    public int getN11() {
        return n11;
    }

    public void setN11(int n11) {
        this.n11 = n11;
    }

    public ImageDecryption getImageDecryption() {
        return imageDecryption;
    }

    public void setImageDecryption(ImageDecryption imageDecryption) {
        this.imageDecryption = imageDecryption;
    }

    public static List<ImageObject> getImageObjectList() {
        return imageObjectList;
    }

    public static void setImageObjectList(List<ImageObject> imageObjectList) {
        ParalelImageDecryption.imageObjectList = imageObjectList;
    }

    public double[][] getDiffusionImage() {
        return diffusionImage;
    }

    public void setDiffusionImage(double[][] diffusionImage) {
        this.diffusionImage = diffusionImage;
    }

    public double[][] getCryptedImages() {
        return cryptedImages;
    }

    public void setCryptedImages(double[][] cryptedImages) {
        this.cryptedImages = cryptedImages;
    }
}
