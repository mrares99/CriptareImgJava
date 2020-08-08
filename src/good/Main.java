package good;

import com.esotericsoftware.kryo.util.DefaultClassResolver;
import org.jtransforms.dct.DoubleDCT_2D;
import water.util.MathUtils;

import java.awt.image.BufferedImage;
import java.io.File;
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

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {

        ImageOperations imageOperations=new ImageOperations();
        ViewImage viewImage=new ViewImage();
        //Flower;Flower2;Flower3;PinkFlower;Daisy;Lenna;Owl;Roses;Smoke;Umbrellas;testHeight;testWidth;testHeightScurt;testHeightScurtUmbrellas;1PixelHeight
        //2height4width;5height10width
        BufferedImage inputBufferedImage = imageOperations.readImage(new File("D:/An4/Licenta/TestImages/Lenna.png"));
        int width=inputBufferedImage.getWidth(), height=inputBufferedImage.getHeight();
        viewImage.displayImage(inputBufferedImage,"Original",width,height);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Files.write(Paths.get("TimpRulare.txt"),("\nDate="+dateFormat.format(date)+"\n").getBytes(), StandardOpenOption.APPEND);
        Files.write(Paths.get("TimpRulare.txt"),("Width imagine="+width+" Height imagine="+height+"\n").getBytes(), StandardOpenOption.APPEND);





        long startTime=System.currentTimeMillis();

        //criptare

        Encryption encryption=new Encryption();
        List<Integer> secretKeyForBakerMap = encryption.generateSecretKey(width);

        double[][] DCTImage=encryption.imageToDouble(inputBufferedImage);
        DCTImage=encryption.createDCTofImage(DCTImage,height,width);
        int k=1,x0=2,y0=3,n1=4,k1=5,x10=6,y10=7,n11=8;
        double mean=0,variance=0.5;
        long key = encryption.generateKey(k, x0, y0, n1, k1, x10, y10, n11);
        double[][] diffusionImage=encryption.generateDiffusionImage(key,mean,variance,height,width);
        //creare baker's map
        DCTImage=encryption.generateBakerMap(DCTImage,width,height,secretKeyForBakerMap);
        diffusionImage=encryption.generateBakerMap(diffusionImage,width,height,secretKeyForBakerMap);
        //xor dct cu diffusion
        diffusionImage=encryption.XORTwoImages(DCTImage,diffusionImage,height,width,n1);
        //afisarea imaginii criptate
        BufferedImage encryptedImage=encryption.generateBufferedImageFromDoubleValues(diffusionImage,height,width);
        viewImage.displayImage(encryptedImage,"encryptedImage",width,height);

        //terminare criptare

        long endTime=System.currentTimeMillis();
        NumberFormat formatter=new DecimalFormat("#0.00000");
        Files.write(Paths.get("TimpRulare.txt"),("Timpul total pentru criptare="+formatter.format((endTime-startTime)/1000d)+" secunde\n").getBytes(), StandardOpenOption.APPEND);










        startTime=System.currentTimeMillis();

        //decriptare

        Decryption decryption=new Decryption();
        secretKeyForBakerMap = decryption.generateSecretKey(width);
        //generez diffusion map pe baza cheii/variance/etc
        diffusionImage=decryption.generateDiffusionImage(key,mean,variance,height,width);
        diffusionImage=decryption.generateBakerMap(diffusionImage,width,height,secretKeyForBakerMap);
        DCTImage=decryption.XORTwoImages(diffusionImage,decryption.imageToDouble(encryptedImage),height,width,n1);
        //decriptez baker's map
        DCTImage=decryption.decryptBakerMap(DCTImage,width,height,secretKeyForBakerMap);
        //creez IDCT
        DCTImage=decryption.createIDCTofImage(DCTImage,height,width);
        BufferedImage decryptedImage=decryption.generateBufferedImageFromDoubleValues(DCTImage,height,width);
        viewImage.displayImage(decryptedImage,"decryptedImage",width,height);

        //terminare decriptare

        endTime=System.currentTimeMillis();
        formatter=new DecimalFormat("#0.00000");
        Files.write(Paths.get("TimpRulare.txt"),("Timpul total pentru criptare="+formatter.format((endTime-startTime)/1000d)+" secunde\n").getBytes(), StandardOpenOption.APPEND);



    }

}
