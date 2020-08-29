package good;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ParallelDecryption extends Thread{
    private BufferedImage colorChannel;
    private final Decryption decryption;
    private static List<BufferedImage> outputEncryptedImageList;
    private int arnoldParameterA;
    private int arnoldParameterB;

    public ParallelDecryption(){
        this.decryption=new Decryption();
        outputEncryptedImageList=new ArrayList<>();
    }

    public void run(){
        try{
            addEncryptedImageInList(decryption.doDecryption(colorChannel,arnoldParameterA,arnoldParameterB));
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public  List<BufferedImage> getOutputEncryptedImageList() {
        return outputEncryptedImageList;
    }

    public synchronized void addEncryptedImageInList(BufferedImage bufferedImage){
        outputEncryptedImageList.add(bufferedImage);
    }

    public void setColorChannel(BufferedImage colorChannel) {
        this.colorChannel = colorChannel;
    }

    public void setArnoldParameterA(int arnoldParameterA) {
        this.arnoldParameterA = arnoldParameterA;
    }

    public void setArnoldParameterB(int arnoldParameterB) {
        this.arnoldParameterB = arnoldParameterB;
    }
}
