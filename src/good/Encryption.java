package good;

import org.jtransforms.dct.DoubleDCT_2D;

import java.awt.image.BufferedImage;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Encryption {

    public double[][] imageToDouble(BufferedImage bufferedImage){
        int height=bufferedImage.getHeight(),width=bufferedImage.getWidth();
        double[][] result=new double[height][width];
        for(int i=-1;++i<height;){
            for(int j=-1;++j<width;){
                result[i][j]=bufferedImage.getRGB(j,i);
            }
        }
        return result;
    }

    public double[][] createDCTofImage(double[][] image,int height,int width){
        new DoubleDCT_2D(height,width).forward(image,false);
        return image;
    }

    public long generateKey(int k,int x0,int y0,int n1,int k1,int x10,int y10,int n11){
        return ((((((k*10+x0)*10+y0)*10+n1)*10+k1)*10+x10)*10+y10)*10+n11;
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

}
