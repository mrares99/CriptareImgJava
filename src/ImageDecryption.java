import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ImageDecryption {


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

    public double[][] generateBakerMap(double[][] inputImage, int widthOfInputImage, int heightOfInputImage, List<Integer> secretKey) throws IOException {//se imparte in dreptunghiuri,la numar fiind lungimea laturii imaginii
        double[][] outputBakerMap=new double[heightOfInputImage][widthOfInputImage];
        int height=0,offset=widthOfInputImage,row=0,col=0;//offset=widthOfInputImage-secretKey[secretKey.length-1]
        for(int i=secretKey.size()-1;i>=0;i--){
            height=heightOfInputImage/secretKey.get(i)-1;
            offset-=secretKey.get(i);
            while(height<heightOfInputImage){
                col=0;
                for(int coordY=offset;coordY<=offset+secretKey.get(i)-1;coordY++){
                    for(int coordX=height;coordX>=height-heightOfInputImage/secretKey.get(i)+1;coordX--){
                        outputBakerMap[row][col++] =inputImage[coordX][coordY];
                    }
                }
                row++;
                height+=heightOfInputImage/secretKey.get(i);
            }
        }
        return outputBakerMap;
    }

    public double[][] decryptBakerMap(double[][] inputBakerMap,int widthOfInputImage,int heightOfInputImage,List<Integer> secretKey) throws IOException {
        double[][] outputDecryptedBakerMap=new double[heightOfInputImage][widthOfInputImage];
        int row=heightOfInputImage-1,col=0,height=0,offset=0;
        for(int i=0;i<secretKey.size();i++){
            height=heightOfInputImage/secretKey.get(i);
            int auxiliaryHeightOfImage=heightOfInputImage-1;
            while(auxiliaryHeightOfImage>0){
                col=0;
                for(int coordY=offset;coordY<=offset+secretKey.get(i)-1;coordY++){
                    for(int coordX=auxiliaryHeightOfImage;coordX>=auxiliaryHeightOfImage-height+1;coordX--){
                        outputDecryptedBakerMap[coordX][coordY]=inputBakerMap[row][col++];
                    }
                }
                row--;
                auxiliaryHeightOfImage=auxiliaryHeightOfImage-height;
            }
            offset+=secretKey.get(i);
        }
        return  outputDecryptedBakerMap;
    }

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

    public double[][] generateIDCTForImage(double[][] inputBufferedImage,int inputHeight,int inputWeight) throws IOException {
        double [][] outputDCTBufferedImage=new double[inputHeight][inputWeight];
        double sum=0,dct=0;
        double alphaP=0,alphaQ=0;
        double firstSquare=1/Math.sqrt(inputHeight);
        double seconSquare=Math.sqrt(2)/Math.sqrt(inputHeight);
        int result=2*inputHeight;
        for(int i=0;i<inputHeight;i++){
            for(int j=0;j<inputWeight;j++){
                sum=0;
                for(int k=0;k<inputHeight;k++){
                    for(int w=0;w<inputWeight;w++) {
                        if(k==0) alphaP=firstSquare;
                        else alphaP=seconSquare;
                        if(w==0) alphaQ=firstSquare;
                        else alphaQ=seconSquare;

                        sum=sum+alphaP*alphaQ*inputBufferedImage[k][w]*
                                Math.cos(((2*i+1)*k*Math.PI)/result)*
                                Math.cos(((2*j+1)*w*Math.PI)/result);
                    }
                }
                outputDCTBufferedImage[i][j]=sum;
            }
        }
        return outputDCTBufferedImage;
    }

    public BufferedImage reconstructImage(List<ImageObject> imageObjectList, int height, int width){
        BufferedImage bufferedImage=new BufferedImage(width, height,1);
        Graphics2D graphics2D=bufferedImage.createGraphics();
        for(int i = 0; i< imageObjectList.size(); i++) {
            graphics2D.drawImage(imageObjectList.get(i).getBufferedImage(),
                    imageObjectList.get(i).getXAxis(), imageObjectList.get(i).getYAxis(),//am schimbat axisX cu Y
                    imageObjectList.get(i).getWidth(), imageObjectList.get(i).getWidth(),
                    null);
        }
        return bufferedImage;
    }

    public BufferedImage generateBufferedImageFromDoubleValues(double[][] inputImage,int height,int width){
        BufferedImage bufferedImage=new BufferedImage(width,height,1);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                bufferedImage.setRGB(i,j, (int) inputImage[i][j]);
            }
        }
        return bufferedImage;
    }

}
