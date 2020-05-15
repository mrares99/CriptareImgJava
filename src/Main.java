import java.awt.image.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String args[]) throws Exception {
        ImageEncryption imageEncryption=new ImageEncryption();
        ImageDecryption imageDecryption=new ImageDecryption();
        //Flower;Flower2;Flower3;PinkFlower;Daisy;Lenna;Owl;Roses;Smoke;Umbrellas;testHeight;testWidth;testHeightScurt;testHeightScurtUmbrellas
        BufferedImage bufferedImage = imageEncryption.readImage(new File("D:/An4/Licenta/TestImages/testHeightScurtUmbrellas.png"));
        Files.write(Paths.get("TimpRulare.txt"),("Width imagine="+bufferedImage.getWidth()+" Height imagine="+bufferedImage.getHeight()+"\n").getBytes(), StandardOpenOption.APPEND);
        int originalImageHeight=bufferedImage.getHeight(),originalImageWidth=bufferedImage.getWidth();
        ViewImage viewImage=new ViewImage();
        viewImage.displayImage(bufferedImage,"Imaginea initiala",bufferedImage.getWidth(),bufferedImage.getHeight());
        long startTime=System.currentTimeMillis();
        List<ImageObject> imageObjectList=imageEncryption.createSquareImagesWithMaximumEdgeLength(bufferedImage);
        Files.write(Paths.get("TimpRulare.txt"),("Numarul de subimagini este="+imageObjectList.size()+" si au latura="+imageObjectList.get(0).getWidth()+"\n").getBytes(), StandardOpenOption.APPEND);
        int lenghtOfSquareImage=imageObjectList.get(0).getWidth();
        int k=1,x0=2,y0=3,n1=4,k1=5,x10=6,y10=7,n11=8;
        long key = imageEncryption.generateKey(k, x0, y0, n1, k1, x10, y10, n11);
        double mean=0,variance=0.5;
        double[][] diffusionImage = imageEncryption.generateDiffusionImage(key, mean, variance, lenghtOfSquareImage, lenghtOfSquareImage);
        List<Integer> secretKeyForBakerMap = imageEncryption.generateSecretKey(lenghtOfSquareImage);

        ExecutorService executorService=Executors.newFixedThreadPool(imageObjectList.size());

        for(int i=0;i<imageObjectList.size();i++) {
            bufferedImage=imageObjectList.get(i).getBufferedImage();
            ParalelImageEncryption paralelImageEncryption=new ParalelImageEncryption("Img criptata "+i);
            paralelImageEncryption.setImageObject(imageObjectList.get(i));
            paralelImageEncryption.setSecretKeyForBakerMap(secretKeyForBakerMap);
            paralelImageEncryption.setDiffusionImage(diffusionImage);
            paralelImageEncryption.setKey(key);
            paralelImageEncryption.setLengthOfImage(bufferedImage.getWidth());
            executorService.execute(paralelImageEncryption);
        }

        executorService.shutdown();
        executorService.awaitTermination(10,TimeUnit.MINUTES);
        long endTime=System.currentTimeMillis();
        NumberFormat formatter=new DecimalFormat("#0.00000");
        Files.write(Paths.get("TimpRulare.txt"),("Timpul total pentru criptare="+formatter.format((endTime-startTime)/1000d)+" secunde\n").getBytes(), StandardOpenOption.APPEND);
        List<ImageObject> informationForEveryCryptedImage=ParalelImageEncryption.getImageObjectList();
        List<double[][]> cryptedImages=ParalelImageEncryption.getImageDoubleValues();

        executorService= Executors.newFixedThreadPool(cryptedImages.size());
        startTime=System.currentTimeMillis();
        for(int i=0;i<cryptedImages.size();i++){
            ParalelImageDecryption paralelImageDecryption=new ParalelImageDecryption("Img decriptata "+i);
            paralelImageDecryption.setDiffusionImage(diffusionImage);
            paralelImageDecryption.setKey(key);
            paralelImageDecryption.setCryptedImages(cryptedImages.get(i));
            paralelImageDecryption.setSecretKeyForBakerMap(secretKeyForBakerMap);
            paralelImageDecryption.setXAxis(informationForEveryCryptedImage.get(i).getXAxis());
            paralelImageDecryption.setYAxis(informationForEveryCryptedImage.get(i).getYAxis());
            //paralelImageDecryption.setN11(n11);
            paralelImageDecryption.setLengthOfImage(informationForEveryCryptedImage.get(i).getHeight());
            executorService.execute(paralelImageDecryption);
        }
        executorService.shutdown();
        executorService.awaitTermination(10,TimeUnit.MINUTES);
        endTime=System.currentTimeMillis();
        //System.out.println("Timpul total de rulare pentru decriptare="+ formatter.format((endTime-startTime)/1000d)+" secunde");
        Files.write(Paths.get("TimpRulare.txt"),("Timpul total pentru decriptare="+formatter.format((endTime-startTime)/1000d)+" secunde \n \n").getBytes(), StandardOpenOption.APPEND);
        BufferedImage finalDecryptedImage= imageDecryption.reconstructImage(informationForEveryCryptedImage,originalImageHeight,originalImageWidth);
        viewImage.displayImage(finalDecryptedImage,"Imaginea decriptata",finalDecryptedImage.getWidth(),finalDecryptedImage.getHeight());
    }
}
