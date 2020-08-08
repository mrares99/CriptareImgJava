package good;

import org.jtransforms.dct.DoubleDCT_2D;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Decryption {

    public double[][] imageToDouble(BufferedImage bufferedImage){
        int height=bufferedImage.getHeight(),width=bufferedImage.getWidth();
        double[][] result=new double[height][width];
        for(int i=-1;++i<height;){
            for(int j=-1;++j<width;){
                result[i][j]=bufferedImage.getRGB(j,i) & 0x00ffffff;//elimin componenta alfa
            }
        }
        return result;
    }

    public double[][] createIDCTofImage(double[][] image,int height,int width){
        new DoubleDCT_2D(height,width).inverse(image,false);
        return image;
    }

//    public double[][] decryptBakerMap(double[][] inputBakerMap, int widthOfInputImage, int heightOfInputImage, List<Integer> secretKey) throws IOException {
//        double[][] outputDecryptedBakerMap=new double[heightOfInputImage][widthOfInputImage];
//        int row=heightOfInputImage-1,col=0,height=0,offset=0;
//        for(int i=0;i<secretKey.size();i++){
//            int valForSecretKey=secretKey.get(i);
//            height=heightOfInputImage/valForSecretKey;
//            int auxiliaryHeightOfImage=heightOfInputImage-1;
//            while(auxiliaryHeightOfImage>0){
//                col=0;
//                for(int coordY=offset;coordY<=offset+valForSecretKey-1;coordY++){
//                    for(int coordX=auxiliaryHeightOfImage;coordX>=auxiliaryHeightOfImage-height+1;coordX--){
//                        outputDecryptedBakerMap[coordX][coordY]=inputBakerMap[row][col++];
//                    }
//                }
//                row--;
//                auxiliaryHeightOfImage=auxiliaryHeightOfImage-height;
//            }
//            offset+=valForSecretKey;
//        }
//        return  outputDecryptedBakerMap;
//    }

    public double[][] generateDiffusionImage(long key,double mean,double variance,int height,int width){
        double[][] diffusionImage=new double[height][width];
        Random random=new Random();
        random.setSeed(key);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                diffusionImage[i][j]=random.nextGaussian()*variance+mean;
            }
        }
        return diffusionImage;
    }

    public double[][] decryptBakerMap(double[][] inputBakerMap,int widthOfInputImage,int heightOfInputImage,List<Integer> secretKey) throws IOException {
        double[][] outputDecryptedBakerMap=new double[heightOfInputImage][widthOfInputImage];
        int row=heightOfInputImage-1,col=0,height=0,offset=0;
        for(int i=0;i<secretKey.size();i++){
            int valForSecretKey=secretKey.get(i);
            height=heightOfInputImage/valForSecretKey;
            int auxiliaryHeightOfImage=heightOfInputImage-1;
            while(auxiliaryHeightOfImage>0){
                col=0;
                for(int coordY=offset;coordY<=offset+valForSecretKey-1;coordY++){
                    for(int coordX=auxiliaryHeightOfImage;coordX>=auxiliaryHeightOfImage-height+1;coordX--){
                        //outputDecryptedBakerMap[coordX][coordY]=inputBakerMap.getRGB(row,col++);
                        outputDecryptedBakerMap[coordX][coordY]=inputBakerMap[col++][row];
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
        List<Integer> outputSecretKey=new ArrayList<Integer>();
        List<Integer> sequenceGenerated=new ArrayList<Integer>();
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
            else if(sum+value>inputNumber){
                continue;
            }
        }
        return sequenceGenerated;
    }

    public BufferedImage generateBufferedImageFromDoubleValues(double[][] inputImage,int height,int width){
        BufferedImage bufferedImage=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                bufferedImage.setRGB(i,j, (int) inputImage[i][j]);
            }
        }
        return bufferedImage;
    }

    public double[][] generateBakerMap(double[][] inputImage,int widthOfInputImage,int heightOfInputImage,List<Integer> secretKey) throws IOException {
        double[][] outputBakerMap=new double[heightOfInputImage][widthOfInputImage];
        int height=0,offset=widthOfInputImage,row=0,col=0;
        for(int i=secretKey.size()-1;i>=0;i--){
            int valForSecretKey=secretKey.get(i);
            height=heightOfInputImage/valForSecretKey-1;
            offset-=valForSecretKey;
            while(height<heightOfInputImage){
                col=0;
                for(int coordY=offset;coordY<=offset+valForSecretKey-1;coordY++){
                    for(int coordX=height;coordX>=height-heightOfInputImage/valForSecretKey+1;coordX--){
                        //outputBakerMap[row][col++] =inputImage.getRGB(coordX,coordY);
                        outputBakerMap[row][col++] =inputImage[coordY][coordX];
                    }
                }
                row++;
                height+=heightOfInputImage/valForSecretKey;
            }
        }
        return outputBakerMap;
    }

    public double[][] XORTwoImages(double[][] firstInputImage,double[][] secondInputImage,int height,int width,long n1){
        double[][] outputImage=new double[height][width];
        for(int i=0;i<n1;i++){
            for(int row=0;row<height;row++){
                for(int col=0;col<width;col++){
                    firstInputImage[row][col]=Double.longBitsToDouble(
                            Double.doubleToRawLongBits(firstInputImage[row][col])^
                                    Double.doubleToRawLongBits(secondInputImage[row][col]));
                    outputImage[row][col]=firstInputImage[row][col];
                }
            }
        }
        return outputImage;
    }

}
