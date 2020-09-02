package good;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InterruptedException {
        ImageOperations imageOperations = new ImageOperations();
        ViewImage viewImage = new ViewImage();
        BufferedImage inputBufferedImage = imageOperations.readImage();
        int width = inputBufferedImage.getWidth(), height = inputBufferedImage.getHeight();
        viewImage.displayImage(inputBufferedImage, "Original", width, height);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Files.write(Paths.get("TimpRulare.txt"), ("\nDate=" + dateFormat.format(date) + "\n").getBytes(), StandardOpenOption.APPEND);
        Files.write(Paths.get("TimpRulare.txt"), ("Width imagine=" + width + " Height imagine=" + height + "\n").getBytes(), StandardOpenOption.APPEND);



        //criptare
        long startTime = System.currentTimeMillis();
        Encryption encryption = new Encryption();
        List<Integer> secretKeyForBakerMap = encryption.generateSecretKey(width);
        int seed=8567890;

        inputBufferedImage=encryption.generateBakerMapOrizontal(inputBufferedImage,width,secretKeyForBakerMap);
        inputBufferedImage=encryption.generateBakerMapVertical(inputBufferedImage,width,secretKeyForBakerMap);

        List<BufferedImage> channels = imageOperations.extractColorChannels(inputBufferedImage);

        int[] arnoldParameters = encryption.generateRandomSequenceForArnoldTransform(seed);

        ParallelEncryption parallelEncryption=new ParallelEncryption();
        ExecutorService executorService= Executors.newFixedThreadPool(3);

        parallelEncryption.setArnoldParameterA(arnoldParameters[0]);
        parallelEncryption.setArnoldParameterB(arnoldParameters[1]);
        parallelEncryption.setColorChannel(channels.get(0));
        executorService.execute(parallelEncryption);


        parallelEncryption=new ParallelEncryption();
        parallelEncryption.setArnoldParameterA(arnoldParameters[2]);
        parallelEncryption.setArnoldParameterB(arnoldParameters[3]);
        parallelEncryption.setColorChannel(channels.get(1));
        executorService.execute(parallelEncryption);


        parallelEncryption=new ParallelEncryption();
        parallelEncryption.setArnoldParameterA(arnoldParameters[4]);
        parallelEncryption.setArnoldParameterB(arnoldParameters[5]);
        parallelEncryption.setColorChannel(channels.get(2));
        executorService.execute(parallelEncryption);


        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);


        List<BufferedImage> outputEncryptedImages=parallelEncryption.getOutputEncryptedImageList();
        BufferedImage encryptedImage=imageOperations.constructImageFromRGBChannels(outputEncryptedImages.get(0),
                outputEncryptedImages.get(1),outputEncryptedImages.get(2));



        long endTime = System.currentTimeMillis();
        NumberFormat formatter = new DecimalFormat("#0.00000");
        Files.write(Paths.get("TimpRulare.txt"), ("Timpul total pentru criptare paralela=" + formatter.format((endTime - startTime) / 1000d) + " secunde\n").getBytes(), StandardOpenOption.APPEND);

        viewImage.displayImage(encryptedImage, "encryptedImage", width, height);

        //terminare criptare







        //decriptare

        startTime = System.currentTimeMillis();


        Decryption decryption=new Decryption();
        secretKeyForBakerMap = decryption.generateSecretKey(width);

        channels = imageOperations.extractColorChannels(encryptedImage);

        arnoldParameters = decryption.generateRandomSequenceForArnoldTransform(seed);

        ParallelDecryption parallelDecryption=new ParallelDecryption();
        executorService= Executors.newFixedThreadPool(3);

        parallelDecryption.setArnoldParameterA(arnoldParameters[0]);
        parallelDecryption.setArnoldParameterB(arnoldParameters[1]);
        parallelDecryption.setColorChannel(channels.get(0));
        executorService.execute(parallelDecryption);


        parallelDecryption=new ParallelDecryption();
        parallelDecryption.setArnoldParameterA(arnoldParameters[2]);
        parallelDecryption.setArnoldParameterB(arnoldParameters[3]);
        parallelDecryption.setColorChannel(channels.get(1));
        executorService.execute(parallelDecryption);


        parallelDecryption=new ParallelDecryption();
        parallelDecryption.setArnoldParameterA(arnoldParameters[4]);
        parallelDecryption.setArnoldParameterB(arnoldParameters[5]);
        parallelDecryption.setColorChannel(channels.get(2));
        executorService.execute(parallelDecryption);

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);


        outputEncryptedImages=parallelDecryption.getOutputEncryptedImageList();
        BufferedImage decryptedImage=imageOperations.constructImageFromRGBChannels(outputEncryptedImages.get(0),
                outputEncryptedImages.get(1),outputEncryptedImages.get(2));


        decryptedImage=decryption.decryptBakerMapVertical(decryptedImage,width,secretKeyForBakerMap);
        decryptedImage=decryption.decryptBakerMapOrizontal(decryptedImage,width,secretKeyForBakerMap);


        endTime = System.currentTimeMillis();
        formatter = new DecimalFormat("#0.00000");
        Files.write(Paths.get("TimpRulare.txt"), ("Timpul total pentru decriptare paralela=" + formatter.format((endTime - startTime) / 1000d) + " secunde\n").getBytes(), StandardOpenOption.APPEND);



        viewImage.displayImage(decryptedImage, "decryptedImage", width, height);


        //terminare decriptare

















//        //implementare secventiala
//
//
//        ImageOperations imageOperations = new ImageOperations();
//        ViewImage viewImage = new ViewImage();
//        BufferedImage inputBufferedImage = imageOperations.readImage();
//        int width = inputBufferedImage.getWidth(), height = inputBufferedImage.getHeight();
//        viewImage.displayImage(inputBufferedImage, "Original", width, height);
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        Date date = new Date();
//        Files.write(Paths.get("TimpRulare.txt"), ("\nDate=" + dateFormat.format(date) + "\n").getBytes(), StandardOpenOption.APPEND);
//        Files.write(Paths.get("TimpRulare.txt"), ("Width imagine=" + width + " Height imagine=" + height + "\n").getBytes(), StandardOpenOption.APPEND);
//
//
//
//        //criptare
//        long startTime = System.currentTimeMillis();
//        Encryption encryption = new Encryption();
//        List<Integer> secretKeyForBakerMap = encryption.generateSecretKey(width);
//        int seed=8567890;
//
//        inputBufferedImage=encryption.generateBakerMapOrizontal(inputBufferedImage,width,secretKeyForBakerMap);
//        inputBufferedImage=encryption.generateBakerMapVertical(inputBufferedImage,width,secretKeyForBakerMap);
//
//        List<BufferedImage> channels = imageOperations.extractColorChannels(inputBufferedImage);
//
//        int[] arnoldParameters = encryption.generateRandomSequenceForArnoldTransform(seed);
//
//
//        BufferedImage encryptedRed=encryption.doEncryption(channels.get(0),arnoldParameters[0],arnoldParameters[1]);
//        BufferedImage encryptedGreen=encryption.doEncryption(channels.get(1),arnoldParameters[2],arnoldParameters[3]);
//        BufferedImage encryptedBlue=encryption.doEncryption(channels.get(2),arnoldParameters[4],arnoldParameters[5]);
//
//
//        BufferedImage encryptedImage=imageOperations.constructImageFromRGBChannels(encryptedRed,
//                encryptedGreen,encryptedBlue);
//
//
//
//        long endTime = System.currentTimeMillis();
//        NumberFormat formatter = new DecimalFormat("#0.00000");
//        Files.write(Paths.get("TimpRulare.txt"), ("Timpul total pentru criptare secventiala=" + formatter.format((endTime - startTime) / 1000d) + " secunde\n").getBytes(), StandardOpenOption.APPEND);
//
//        viewImage.displayImage(encryptedImage, "encryptedImage", width, height);
//
//        //terminare criptare
//
//
//
//
//
//
//
//        //decriptare
//
//        startTime = System.currentTimeMillis();
//
//
//        Decryption decryption=new Decryption();
//        secretKeyForBakerMap = decryption.generateSecretKey(width);
//
//        channels = imageOperations.extractColorChannels(encryptedImage);
//        arnoldParameters = decryption.generateRandomSequenceForArnoldTransform(seed);
//
//        BufferedImage decryptedRed=decryption.doDecryption(channels.get(0),arnoldParameters[0], arnoldParameters[1]);
//        BufferedImage decryptedGreen=decryption.doDecryption(channels.get(1),arnoldParameters[2], arnoldParameters[3]);
//        BufferedImage decryptedBlue=decryption.doDecryption(channels.get(2),arnoldParameters[4], arnoldParameters[5]);
//
//
//        BufferedImage decryptedImage=imageOperations.constructImageFromRGBChannels(decryptedRed,
//                decryptedGreen,decryptedBlue);
//
//        decryptedImage=decryption.decryptBakerMapVertical(decryptedImage,width,secretKeyForBakerMap);
//        decryptedImage=decryption.decryptBakerMapOrizontal(decryptedImage,width,secretKeyForBakerMap);
//
//
//        endTime = System.currentTimeMillis();
//        formatter = new DecimalFormat("#0.00000");
//        Files.write(Paths.get("TimpRulare.txt"), ("Timpul total pentru decriptare secventiala=" + formatter.format((endTime - startTime) / 1000d) + " secunde\n").getBytes(), StandardOpenOption.APPEND);
//
//
//
//        viewImage.displayImage(decryptedImage, "decryptedImage", width, height);
//
//
//        //terminare decriptare


    }


}
