package good;

import org.jtransforms.dct.DoubleDCT_2D;

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

public class Main {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        ImageOperations imageOperations=new ImageOperations();
        ViewImage viewImage=new ViewImage();
        //Flower;PinkFlower;Daisy;Lenna;Owl;Roses;Smoke;Umbrellas
        BufferedImage inputBufferedImage = imageOperations.readImage();
        int width=inputBufferedImage.getWidth(), height=inputBufferedImage.getHeight();
        viewImage.displayImage(inputBufferedImage,"Original",width,height);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Files.write(Paths.get("TimpRulare.txt"),("\nDate="+dateFormat.format(date)+"\n").getBytes(), StandardOpenOption.APPEND);
        Files.write(Paths.get("TimpRulare.txt"),("Width imagine="+width+" Height imagine="+height+"\n").getBytes(), StandardOpenOption.APPEND);



//
//
//        long startTime=System.currentTimeMillis();
//
//        //criptare
//
//        Encryption encryption=new Encryption();
//        List<Integer> secretKeyForBakerMap = encryption.generateSecretKey(width);
//
//        double[][] DCTImage=encryption.imageToDouble(inputBufferedImage);
//        DCTImage=encryption.createDCTofImage(DCTImage,height,width);
//        int k=1,x0=2,y0=3,n1=4,k1=5,x10=6,y10=7,n11=8;
//        double mean=0,variance=0.5;
//        long key = encryption.generateKey(k, x0, y0, n1, k1, x10, y10, n11);
//        double[][] diffusionImage=encryption.generateDiffusionImage(key,mean,variance,height,width);
//        //creare baker's map
//        DCTImage=encryption.generateBakerMap(DCTImage,width,height,secretKeyForBakerMap);
//        diffusionImage=encryption.generateBakerMap(diffusionImage,width,height,secretKeyForBakerMap);
//        //xor dct cu diffusion
//        diffusionImage=encryption.XORTwoImages(DCTImage,diffusionImage,height,width,n1);
//        //afisarea imaginii criptate
//        BufferedImage encryptedImage=encryption.generateBufferedImageFromDoubleValues(diffusionImage,height,width);
//        viewImage.displayImage(encryptedImage,"encrypted",width,height);
//
//        //terminare criptare
//
//        long endTime=System.currentTimeMillis();
//        NumberFormat formatter=new DecimalFormat("#0.00000");
//        Files.write(Paths.get("TimpRulare.txt"),("Timpul total pentru criptare="+formatter.format((endTime-startTime)/1000d)+" secunde\n").getBytes(), StandardOpenOption.APPEND);
//
//
//
//
//
//
//
//
//
//
//        startTime=System.currentTimeMillis();
//
//        //decriptare
//
//        Decryption decryption=new Decryption();
//        secretKeyForBakerMap = decryption.generateSecretKey(width);
//        //generez diffusion map pe baza cheii/variance/etc
//        diffusionImage=decryption.generateDiffusionImage(key,mean,variance,height,width);
//        diffusionImage=decryption.generateBakerMap(diffusionImage,width,height,secretKeyForBakerMap);
//        DCTImage=decryption.XORTwoImages(diffusionImage,decryption.imageToDouble(encryptedImage),height,width,n1);
//        //decriptez baker's map
//        DCTImage=decryption.decryptBakerMap(DCTImage,width,height,secretKeyForBakerMap);
//        //creez IDCT
//        DCTImage=decryption.createIDCTofImage(DCTImage,height,width);
//        BufferedImage decryptedImage=decryption.generateBufferedImageFromDoubleValues(DCTImage,height,width);
//        viewImage.displayImage(decryptedImage,"decrypted",width,height);
//
//        //terminare decriptare
//
//        endTime=System.currentTimeMillis();
//        formatter=new DecimalFormat("#0.00000");
//        Files.write(Paths.get("TimpRulare.txt"),("Timpul total pentru decriptare="+formatter.format((endTime-startTime)/1000d)+" secunde\n").getBytes(), StandardOpenOption.APPEND);





        double value=9.803402725149393e10;
        int conv=(int)value;
        System.out.println("double to int="+conv);


        DoubleDCT_2D doubleDCT_2D=new DoubleDCT_2D(height,width);

        Encryption encryption=new Encryption();
        List<Integer> secretKeyForBakerMap = encryption.generateSecretKey(width);
        System.out.println("buffered image="+inputBufferedImage.getRGB(0,1)+" "+inputBufferedImage.getRGB(0,2)+" "+inputBufferedImage.getRGB(0,3)
                +" "+inputBufferedImage.getRGB(0,4)+" ");
        double[][] imageDouble=encryption.imageToDouble(inputBufferedImage);
        System.out.println("inainte de dct="+imageDouble[0][1]+" "+imageDouble[0][2]+" "+imageDouble[0][3]+" "+imageDouble[0][4]+" ");
        imageDouble=encryption.createDCTofImage(imageDouble,height,width);
        System.out.println("dct="+imageDouble[0][1]+" "+imageDouble[0][2]+" "+imageDouble[0][3]+" "+imageDouble[0][4]+" ");

        System.out.println("dct to int="+(int)imageDouble[0][1]);

        BufferedImage encryptedImage=encryption.generateBufferedImageFromDoubleValues(imageDouble,height,width);

        System.out.println("encrypted="+encryptedImage.getRGB(0,1)+" "+encryptedImage.getRGB(1,0));

        viewImage.displayImage(encryptedImage,"encryptedImage",width,height);



        System.out.println("\n================== de aici e decriptarea====================\n");



        Decryption decryption=new Decryption();
        System.out.println("encryptedImage="+encryptedImage.getRGB(0,1)+" "+encryptedImage.getRGB(1,0));
        imageDouble=decryption.imageToDouble(encryptedImage);
        System.out.println("inainte de idct=(0,1)"+imageDouble[0][1]+"   (1,0)="+imageDouble[1][0]);
        imageDouble=decryption.createIDCTofImage(imageDouble,height,width);
        System.out.println("idct="+imageDouble[0][1]+" "+imageDouble[1][0]);
        BufferedImage decryptedImage=decryption.generateBufferedImageFromDoubleValues(imageDouble,height,width);
        System.out.println("decryptedImage="+decryptedImage.getRGB(0,1)+" "+decryptedImage.getRGB(1,0));
        viewImage.displayImage(decryptedImage,"decryptedImage",width,height);






    }

}
