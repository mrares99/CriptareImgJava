package good;

import org.jtransforms.dct.DoubleDCT_2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Decryption {

    public BufferedImage doDecryption(BufferedImage inputBufferedImage,List<Integer> secretKeyForBakerMap,int arnoldParameterA,
                                      int arnoldParameterB){
        int lenghtOfImage=inputBufferedImage.getWidth();
        BufferedImage outputBufferedImage=arnoldDecryptionTransform(inputBufferedImage,arnoldParameterA,arnoldParameterB);
        outputBufferedImage=decryptBakerMapVertical(outputBufferedImage,lenghtOfImage,secretKeyForBakerMap);
        outputBufferedImage=decryptBakerMapOrizontal(outputBufferedImage,lenghtOfImage,secretKeyForBakerMap);
        return outputBufferedImage;
    }

    public double[][] imageToDouble(BufferedImage bufferedImage){
        int height=bufferedImage.getHeight(),width=bufferedImage.getWidth();
        double[][] result=new double[height][width];
        for(int i=-1;++i<height;){
            for(int j=-1;++j<width;){
                result[i][j]=bufferedImage.getRGB(i,j);//elimin componenta alfa
            }
        }
        return result;
    }

    public double[][] createIDCTofImage(double[][] image,int height,int width){
        new DoubleDCT_2D(height,width).inverse(image,false);
        return image;
    }

    public double[][] generateDiffusionImage(long key,double mean,double variance,int height,int width) throws NoSuchAlgorithmException {
        double[][] diffusionImage=new double[height][width];
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG") ;
        random.setSeed(key);
        for(int i=-1;++i<height;){
            for(int j=-1;++j<width;){
                diffusionImage[i][j]=random.nextGaussian()*variance+mean;
            }
        }
        return diffusionImage;
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

    public BufferedImage generateBufferedImageFromDoubleValues(double[][] inputImage,int height,int width){
        BufferedImage bufferedImage=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for(int i=-1;++i<height;){
            for(int j=-1;++j<width;){
                bufferedImage.setRGB(i,j, (int) inputImage[i][j]);
            }
        }
        return bufferedImage;
    }

    public double[][] generateBakerMap(double[][] inputImage,int widthOfInputImage,int heightOfInputImage,List<Integer> secretKey) {
        double[][] outputBakerMap=new double[heightOfInputImage][widthOfInputImage];
        int height,offset=widthOfInputImage,row=0,col;
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
        for(int i=-1;++i<n1;){
            for(int row=-1;++row<height;){
                for(int col=-1;++col<width;){
                    firstInputImage[row][col]=Double.longBitsToDouble(
                            Double.doubleToRawLongBits(firstInputImage[row][col])^
                                    Double.doubleToRawLongBits(secondInputImage[row][col]));
                    outputImage[row][col]=firstInputImage[row][col];
                }
            }
        }
        return outputImage;
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
