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
        ImageDecryption imageDecryption=new ImageDecryption();
        //Flower;Flower2;Flower3;PinkFlower;Daisy;Lenna;Owl;Roses;Smoke;Umbrellas;testHeight;testWidth
        BufferedImage bufferedImage = imageEncryption.readImage(new File("D:/An4/Licenta/TestImages/testHeightScurt.png"));
        int originalImageHeight=bufferedImage.getHeight(),originalImageWidth=bufferedImage.getWidth();
        ViewImage viewImage=new ViewImage();
        viewImage.displayImage(bufferedImage,"Imaginea initiala",bufferedImage.getWidth(),bufferedImage.getHeight());

        long startTime=System.currentTimeMillis();

        List<ImageObject> imageObjectList=imageEncryption.createSquareImagesWithMaximumEdgeLength(bufferedImage);
        int lenghtOfSquareImage=imageObjectList.get(0).getWidth();
        int k=1,x0=2,y0=3,n1=4,k1=5,x10=6,y10=7,n11=8;
        long key = imageEncryption.generateKey(k, x0, y0, n1, k1, x10, y10, n11);
        double mean=0,variance=0.5;
        imageEncryption.setN11(n11);
        double[][] diffusionImage = imageEncryption.generateDiffusionImage(key, mean, variance, lenghtOfSquareImage, lenghtOfSquareImage);
        List<Integer> secretKeyForBakerMap = imageEncryption.generateSecretKey(lenghtOfSquareImage);

        ExecutorService executorService=Executors.newFixedThreadPool(imageObjectList.size());

        for(int i=0;i<imageObjectList.size();i++) {
            bufferedImage=imageObjectList.get(i).getBufferedImage();
            viewImage.displayImage(bufferedImage,"Img "+i,bufferedImage.getWidth(),bufferedImage.getHeight());

            ParalelImageEncryption paralelImageEncryption=new ParalelImageEncryption("Img criptata "+i);
            paralelImageEncryption.setImageObject(imageObjectList.get(i));
            paralelImageEncryption.setSecretKeyForBakerMap(secretKeyForBakerMap);
            paralelImageEncryption.setDiffusionImage(diffusionImage);
            paralelImageEncryption.setKey(key);
            paralelImageEncryption.setN11(imageEncryption.getN11());
            paralelImageEncryption.setLengthOfImage(bufferedImage.getWidth());
            executorService.execute(paralelImageEncryption);//paralelImageEncryption.start()

            //double[][] DCTimage = imageEncryption.generateDCTForImage(imageObjectList.get(i).getBufferedImage());
            //double[][] DCTImageBakerMap = imageEncryption.generateBakerMap(DCTimage, bufferedImage.getWidth(), bufferedImage.getHeight(), secretKeyForBakerMap);=
            //double[][] diffusionImageBakerMap = imageEncryption.generateBakerMap(diffusionImage, bufferedImage.getWidth(), bufferedImage.getHeight(), secretKeyForBakerMap);=
            //double[][] XORedImages = imageEncryption.XORTwoImages(DCTImageBakerMap, diffusionImageBakerMap, bufferedImage.getHeight(), bufferedImage.getWidth(), key%10);=
            //BufferedImage imageEncrypted = imageEncryption.generateBufferedImageFromDoubleValues(XORedImages, bufferedImage.getHeight(), bufferedImage.getWidth());
            //viewImage.displayImage(imageEncrypted, "Imaginea criptata "+i,imageEncrypted.getWidth(),imageEncrypted.getHeight());
        }

        executorService.shutdown();
        executorService.awaitTermination(10,TimeUnit.MINUTES);
        long endTime=System.currentTimeMillis();
        NumberFormat formatter=new DecimalFormat("#0.00000");
        System.out.println("Timpul total de rulare pentru criptare="+ formatter.format((endTime-startTime)/1000d)+" secunde");


        List<ImageObject> informationForEveryCryptedImage=ParalelImageEncryption.getImageObjectList();
        List<double[][]> cryptedImages=ParalelImageEncryption.getImageDoubleValues();


        ExecutorService executorServiceDecryption=Executors.newFixedThreadPool(cryptedImages.size());
        startTime=System.currentTimeMillis();
        for(int i=0;i<cryptedImages.size();i++){
            ParalelImageDecryption paralelImageDecryption=new ParalelImageDecryption("Img decriptata "+i);
            paralelImageDecryption.setDiffusionImage(diffusionImage);
            paralelImageDecryption.setKey(key);
            paralelImageDecryption.setCryptedImages(cryptedImages.get(i));
            paralelImageDecryption.setSecretKeyForBakerMap(secretKeyForBakerMap);
            paralelImageDecryption.setN11(n11);
            paralelImageDecryption.setLengthOfImage(informationForEveryCryptedImage.get(i).getHeight());
            paralelImageDecryption.setImageObject(informationForEveryCryptedImage.get(i));
            executorServiceDecryption.execute(paralelImageDecryption);

//            double[][] diffusionImageBakerMap = imageEncryption.generateBakerMap(diffusionImage, bufferedImage.getWidth(), bufferedImage.getHeight(), secretKeyForBakerMap);
//            double[][] DCTImageBakerMap =imageEncryption.XORTwoImages(cryptedImages.get(i), diffusionImageBakerMap, informationForEveryCryptedImage.get(i).getHeight(),
//                    informationForEveryCryptedImage.get(i).getWidth(), key%10);
//            double[][] DCTimage=imageDecryption.decryptBakerMap(DCTImageBakerMap,informationForEveryCryptedImage.get(i).getWidth(),informationForEveryCryptedImage.get(i).getHeight(),
//                    secretKeyForBakerMap);
//            double[][] decryptedImage=imageDecryption.generateDCTForImage(DCTimage,informationForEveryCryptedImage.get(i).getHeight(),informationForEveryCryptedImage.get(i).getWidth());
//            BufferedImage finalDecriptedImage=imageDecryption.generateBufferedImageFromDoubleValues(decryptedImage,informationForEveryCryptedImage.get(i).getHeight(),informationForEveryCryptedImage.get(i).getWidth());
//            viewImage.displayImage(finalDecriptedImage,"final "+i,informationForEveryCryptedImage.get(i).getWidth(),informationForEveryCryptedImage.get(i).getHeight());
        }

        executorServiceDecryption.shutdown();
        executorServiceDecryption.awaitTermination(10,TimeUnit.MINUTES);
        endTime=System.currentTimeMillis();
        formatter=new DecimalFormat("#0.00000");
        System.out.println("Timpul total de rulare pentru decriptare="+ formatter.format((endTime-startTime)/1000d)+" secunde");

        List<ImageObject> finalImageObject=ParalelImageDecryption.getImageObjectList();
        for(int i=0;i<finalImageObject.size();i++){
            viewImage.displayImage(finalImageObject.get(i).getBufferedImage(),"final "+i,finalImageObject.get(i).getBufferedImage().getWidth(),
                    finalImageObject.get(i).getBufferedImage().getHeight());
        }

        BufferedImage finalDecryptedImage= imageDecryption.reconstructImage(finalImageObject,originalImageHeight,originalImageWidth);
        viewImage.displayImage(finalDecryptedImage,"Final decrypt",finalDecryptedImage.getWidth(),finalDecryptedImage.getHeight());

    }
}
