package good;

import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Decryption {

    public BufferedImage doDecryption(BufferedImage inputBufferedImage,int arnoldParameterA, int arnoldParameterB){
        return arnoldDecryptionTransform(inputBufferedImage,arnoldParameterA,arnoldParameterB);
    }

    public int[] generateRandomSequenceForArnoldTransform(int seed) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG") ;
        secureRandom.setSeed(seed);
        int[] sequence=new int[6];
        for(int i=0;i<6;i++){
            sequence[i]=secureRandom.nextInt(200);
        }
        return sequence;
    }

    public BufferedImage decryptBakerMapOrizontal(BufferedImage inputBakerMap,int widthOfInputImage,List<Integer> secretKey) {
        BufferedImage outputDecryptedBakerMap=new BufferedImage(widthOfInputImage,widthOfInputImage,BufferedImage.TYPE_INT_RGB);
        int row=widthOfInputImage-1,col,height,offset=0;
        for(int i=-1;++i<secretKey.size();){
            int valForSecretKey=secretKey.get(i);
            height=widthOfInputImage/valForSecretKey;
            int auxiliaryHeightOfImage=widthOfInputImage-1;
            while(auxiliaryHeightOfImage>0){
                col=0;
                for(int coordY=offset;coordY<=offset+valForSecretKey-1;coordY++){
                    for(int coordX=auxiliaryHeightOfImage;coordX>=auxiliaryHeightOfImage-height+1;coordX--){
                        outputDecryptedBakerMap.setRGB(coordY,coordX,inputBakerMap.getRGB(col++,row));
                    }
                }
                row--;
                auxiliaryHeightOfImage=auxiliaryHeightOfImage-height;
            }
            offset+=valForSecretKey;
        }
        return  outputDecryptedBakerMap;
    }

    public BufferedImage decryptBakerMapVertical(BufferedImage inputBakerMap,int widthOfInputImage,List<Integer> secretKey) {
        BufferedImage outputDecryptedBakerMap=new BufferedImage(widthOfInputImage,widthOfInputImage,BufferedImage.TYPE_INT_RGB);
        int row=widthOfInputImage-1,col,height,offset=0;
        for(int i=-1;++i<secretKey.size();){
            int valForSecretKey=secretKey.get(i);
            height=widthOfInputImage/valForSecretKey;
            int auxiliaryHeightOfImage=widthOfInputImage-1;
            while(auxiliaryHeightOfImage>0){
                col=0;
                for(int coordY=offset;coordY<=offset+valForSecretKey-1;coordY++){
                    for(int coordX=auxiliaryHeightOfImage;coordX>=auxiliaryHeightOfImage-height+1;coordX--){
                        outputDecryptedBakerMap.setRGB(coordX,coordY,inputBakerMap.getRGB(col++,row));
                    }
                }
                row--;
                auxiliaryHeightOfImage=auxiliaryHeightOfImage-height;
            }
            offset+=valForSecretKey;
        }
        return  outputDecryptedBakerMap;
    }

    public List<Integer> generateSecretKey(int inputNumber) throws NoSuchAlgorithmException {
        List<Integer> outputSecretKey=new ArrayList<>();
        List<Integer> sequenceGenerated=new ArrayList<>();
        for(int i=2;i<=inputNumber/2+1;i++){
            if(inputNumber%i==0){
                outputSecretKey.add(i);
            }
        }
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG") ;
        random.setSeed(inputNumber);
        int sum=0;
        while(true){
            int value=outputSecretKey.get(random.nextInt(outputSecretKey.size()));
            if(sum+value==inputNumber){
                sequenceGenerated.add(value);
                break;
            }
            else if(sum+value<inputNumber){
                if(value%2==0){
                    sequenceGenerated.add(value);
                    sum+=value;
                }
                if(value%2==1){
                    if(sum+2*value<inputNumber){
                        sequenceGenerated.add(value);
                        sequenceGenerated.add(value);
                        sum+=2*value;
                    }
                }
            }
        }
        return sequenceGenerated;
    }

    public BufferedImage arnoldDecryptionTransform(BufferedImage inputBufferedImage,int a,int b){
        int width=inputBufferedImage.getWidth();
        BufferedImage outputBufferedImage=new BufferedImage(width,width,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<width;i++){
            for(int j=0;j<width;j++){
                int aux1=(i-a*j)%width;
                int aux2=(-b*i+j*(a*b+1))%width;
                outputBufferedImage.setRGB((aux1%width + width)%width,(aux2%width + width)%width,inputBufferedImage.getRGB(i,j));
            }
        }
        return outputBufferedImage;
    }

}
