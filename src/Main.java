import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String args[]) throws Exception {
        ImageEncryption imageEncryption=new ImageEncryption();
        //Flower;Flower2;Flower3;PinkFlower;Daisy;Lenna;Owl;Roses;Smoke;Umbrellas;testHeight;testWidth
        BufferedImage bufferedImage = imageEncryption.readImage(new File("D:/An4/Licenta/TestImages/testWidth.png"));
        int originalImageHeight=bufferedImage.getHeight(),originalImageWidth=bufferedImage.getWidth();
        ViewImage viewImage=new ViewImage();
        viewImage.displayImage(bufferedImage,"Imaginea initiala",bufferedImage.getWidth(),bufferedImage.getHeight());

        long startTime=System.currentTimeMillis();

        List<ImageObject> imageObjectList=imageEncryption.createSquareImagesWithMaximumEdgeLength(bufferedImage);
        int lenghtOfSquareImage=imageObjectList.get(0).getWidth();
        long key = imageEncryption.generateKey(1, 2, 3, 4, 5, 6, 7, 8);
        double[][] diffusionImage = imageEncryption.generateDiffusionImage(key, 0, 0.5, lenghtOfSquareImage, lenghtOfSquareImage);
        List<Integer> secretKeyForBakerMap = imageEncryption.generateSecretKey(lenghtOfSquareImage);

//        List<Integer> secretKey=imageEncryption.generateSecretKey(8);
//        for(int i=0;i<secretKey.size();i++) {
//            System.out.print(secretKey.get(i)+" ");
//        }
//        System.out.println("");


//        int sum=0;
//        for(int i=0;i<secretKeyForBakerMap.length;i++){
//            System.out.print(secretKeyForBakerMap[i]+" ");
//            sum+=secretKeyForBakerMap[i];
//        }
//        System.out.println(" sum="+sum);


//        for(int i=0;i<imageObjectList.size();i++){
//            BufferedImage aux=imageObjectList.get(i).getBufferedImage();
//            viewImage.displayImage(aux,"img "+i,aux.getWidth(),aux.getHeight());
//            System.out.println("height="+aux.getHeight()+" width="+aux.getWidth());
//        }
//
//        BufferedImage reconstruct=imageEncryption.reconstructImage(imageObjectList,originalImageHeight,originalImageWidth);
//        viewImage.displayImage(reconstruct,"Reconstruct",originalImageWidth,originalImageHeight);


        ExecutorService executorService=Executors.newFixedThreadPool(imageObjectList.size());

        for(int i=0;i<imageObjectList.size();i++) {
            bufferedImage=imageObjectList.get(i).getBufferedImage();
            viewImage.displayImage(bufferedImage,"Img "+i,bufferedImage.getWidth(),bufferedImage.getHeight());

            ParalelImageEncryption paralelImageEncryption=new ParalelImageEncryption("Img criptata "+i);
            paralelImageEncryption.setBufferedImage(bufferedImage);
            paralelImageEncryption.setSecretKeyForBakerMap(secretKeyForBakerMap);
            paralelImageEncryption.setDiffusionImage(diffusionImage);
            paralelImageEncryption.setKey(key);
            paralelImageEncryption.setLengthOfImage(bufferedImage.getWidth());
            executorService.execute(paralelImageEncryption);//paralelImageEncryption.start()

            //double[][] DCTimage = imageEncryption.generateDCTForImage(imageObjectList.get(i).getBufferedImage());
            //double[][] DCTImageBakerMap = imageEncryption.generateBakerMap(DCTimage, bufferedImage.getWidth(), bufferedImage.getHeight(), secretKeyForBakerMap);
            //double[][] diffusionImageBakerMap = imageEncryption.generateBakerMap(diffusionImage, bufferedImage.getWidth(), bufferedImage.getHeight(), secretKeyForBakerMap);
            //double[][] XORedImages = imageEncryption.XORTwoImages(DCTImageBakerMap, diffusionImageBakerMap, bufferedImage.getHeight(), bufferedImage.getWidth(), key%10);
            //BufferedImage imageEncrypted = imageEncryption.generateBufferedImageFromDoubleValues(XORedImages, bufferedImage.getHeight(), bufferedImage.getWidth());
            //viewImage.displayImage(imageEncrypted, "Imaginea criptata "+i,imageEncrypted.getWidth(),imageEncrypted.getHeight());
        }

        executorService.shutdown();
        executorService.awaitTermination(10,TimeUnit.MINUTES);
        long endTime=System.currentTimeMillis();
        NumberFormat formatter=new DecimalFormat("#0.00000");
        System.out.println("Timpul total de rulare="+ formatter.format((endTime-startTime)/1000d)+" secunde");
    }
}
