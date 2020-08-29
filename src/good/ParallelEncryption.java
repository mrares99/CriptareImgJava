package good;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ParallelEncryption extends Thread{
    private BufferedImage colorChannel;
    private final Encryption encryption;
    private static List<BufferedImage> outputEncryptedImageList;
    private int arnoldParameterA;
    private int arnoldParameterB;

    public ParallelEncryption(){
        this.encryption=new Encryption();
        outputEncryptedImageList=new ArrayList<>();
    }

    public void run(){
        try{
            addEncryptedImageInList(encryption.doEncryption(colorChannel,arnoldParameterA,arnoldParameterB));
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
