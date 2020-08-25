package good;

import org.jtransforms.dct.DoubleDCT_2D;

import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Encryption {

    public int[] generateRandomSequenceForArnoldTransform(int key) throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG") ;
        secureRandom.setSeed(key);
        int[] sequence=new int[6];
        for(int i=0;i<6;i++){
            sequence[i]=secureRandom.nextInt(800);
        }
        return sequence;
    }

    public BufferedImage doEncryption(BufferedImage inputBufferedImage,List<Integer> secretKeyForBakerMap,int arnoldParameterA,
                                      int arnoldParameterB){
        int lenghtOfImage=inputBufferedImage.getWidth();
        BufferedImage outputBufferedImage=generateBakerMapOrizontal(inputBufferedImage,lenghtOfImage,secretKeyForBakerMap);
        outputBufferedImage=generateBakerMapVertical(outputBufferedImage,lenghtOfImage,secretKeyForBakerMap);
        outputBufferedImage=arnoldEncryptionTransform(outputBufferedImage,arnoldParameterA,arnoldParameterB);

        return outputBufferedImage;
    }

//
//    public double[][] imageToDouble(BufferedImage bufferedImage){
//        int height=bufferedImage.getHeight(),width=bufferedImage.getWidth();
//        double[][] result=new double[height][width];
//        for(int i=-1;++i<height;){
//            for(int j=-1;++j<width;){
//                result[i][j]=bufferedImage.getRGB(i,j);
//            }
//        }
//        return result;
//    }
//
//    public double[][] createDCTofImage(double[][] image,int height,int width){
//        new DoubleDCT_2D(height,width).forward(image,false);
//        return image;
//    }
//
//    public double[][] createIDCTofImage(double[][] image,int height,int width){
//        new DoubleDCT_2D(height,width).inverse(image,false);
//        return image;
//    }
//
//    public long generateKey(int k,int x0,int y0,int n1,int k1,int x10,int y10,int n11){
//        return ((((((k*10+x0)*10+y0)*10+n1)*10+k1)*10+x10)*10+y10)*10+n11;
//    }
//
//    public double[][] generateDiffusionImage(long key,double mean,double variance,int height,int width) throws NoSuchAlgorithmException {
//        double[][] diffusionImage=new double[height][width];
//        SecureRandom random = SecureRandom.getInstance("SHA1PRNG") ;
//        random.setSeed(key);
//        for(int i=-1;++i<height;){
//            for(int j=-1;++j<width;){
//                diffusionImage[i][j]=random.nextGaussian()*variance+mean;
//            }
//        }
//        return diffusionImage;
//    }



    public BufferedImage generateBakerMapOrizontal(BufferedImage inputImage,int widthOfInputImage,List<Integer> secretKey) {
        BufferedImage outputBakerMap=new BufferedImage(widthOfInputImage,widthOfInputImage,BufferedImage.TYPE_INT_RGB);
        int height,offset=widthOfInputImage,row=0,col;
        for(int i=secretKey.size()-1;i>=0;i--){
            int valForSecretKey=secretKey.get(i);
            height=widthOfInputImage/valForSecretKey-1;
            offset-=valForSecretKey;
            while(height<widthOfInputImage){
                col=0;
                for(int coordY=offset;coordY<=offset+valForSecretKey-1;coordY++){
                    for(int coordX=height;coordX>=height-widthOfInputImage/valForSecretKey+1;coordX--){
                        outputBakerMap.setRGB(col++,row,inputImage.getRGB(coordY,coordX));
                    }
                }
                row++;
                height+=widthOfInputImage/valForSecretKey;
            }
        }
        return outputBakerMap;
    }





    public BufferedImage generateBakerMapVertical(BufferedImage inputImage,int widthOfInputImage,List<Integer> secretKey) {
        BufferedImage outputBakerMap=new BufferedImage(widthOfInputImage,widthOfInputImage,BufferedImage.TYPE_INT_RGB);
        int height,offset=widthOfInputImage,row=0,col;
        for(int i=secretKey.size()-1;i>=0;i--){
            int valForSecretKey=secretKey.get(i);
            height=widthOfInputImage/valForSecretKey-1;
            offset-=valForSecretKey;
            while(height<widthOfInputImage){
                col=0;
                for(int coordY=offset;coordY<=offset+valForSecretKey-1;coordY++){
                    for(int coordX=height;coordX>=height-widthOfInputImage/valForSecretKey+1;coordX--){
                        outputBakerMap.setRGB(col++,row,inputImage.getRGB(coordX,coordY));
                    }
                }
                row++;
                height+=widthOfInputImage/valForSecretKey;
            }
        }
        return outputBakerMap;
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
//
//    public BufferedImage generateBufferedImageFromDoubleValues(double[][] inputImage,int height,int width){
//        BufferedImage bufferedImage=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
//        for(int i=-1;++i<height;){
//            for(int j=-1;++j<width;){
//                if(i==0 && j==1) System.out.println("val din img double(0,1)="+inputImage[0][1]+
//                        " dupa convertire="+(int)Math.round(inputImage[0][1]));
//                int val= (int) Math.round(inputImage[i][j]);
//                if(i==0 && j==1) System.out.println("in fct(0,1)="+val);
//                bufferedImage.setRGB(i,j, val);
//            }
//        }
//        System.out.println("in fct; img(0,1)="+bufferedImage.getRGB(0,1)+" img(1,0)="+bufferedImage.getRGB(1,0));
//        return bufferedImage;
//    }
//

//    public BufferedImage generateBufferedImageFromDoubleValues(double[][] inputImage,int height,int width){
//        BufferedImage bufferedImage=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
//        for(int i=-1;++i<height;){
//            for(int j=-1;++j<width;){
//                bufferedImage.setRGB(i,j, (int) inputImage[i][j]);
//            }
//        }
//        return bufferedImage;
//    }
//
//    public double[][] XORTwoImages(double[][] firstInputImage,double[][] secondInputImage,int height,int width,long n1){
//        double[][] outputImage=new double[height][width];
//        for(int i=-1;++i<n1;){
//            for(int row=-1;++row<height;){
//                for(int col=-1;++col<width;){
//                    firstInputImage[row][col]=Double.longBitsToDouble(
//                            Double.doubleToRawLongBits(firstInputImage[row][col])^
//                                    Double.doubleToRawLongBits(secondInputImage[row][col]));
//                    outputImage[row][col]=firstInputImage[row][col];
//                }
//            }
//        }
//        return outputImage;
//    }











    public BufferedImage arnoldEncryptionTransform(BufferedImage inputBufferedImage,int a,int b){
        int width=inputBufferedImage.getWidth();
        BufferedImage outputBufferedImage=new BufferedImage(width,width,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<width;i++){
            for(int j=0;j<width;j++){
                outputBufferedImage.setRGB(((a*b+1)*i+a*j)%width,(b*i+j)%width,inputBufferedImage.getRGB(i,j));
            }
        }
        return outputBufferedImage;
    }

}
