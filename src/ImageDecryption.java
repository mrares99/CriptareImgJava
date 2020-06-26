import org.jtransforms.dct.DoubleDCT_2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class ImageDecryption {

//    public double[][] XORTwoImages(double[][] firstInputImage,double[][] secondInputImage,int height,int width,long n1){
//        double[][] outputImage=new double[height][width];
//        for(int i=0;i<n1;i++){
//            for(int row=0;row<height;row++){
//                for(int col=0;col<width;col++){
//                    firstInputImage[row][col]=Double.longBitsToDouble(
//                            Double.doubleToRawLongBits(firstInputImage[row][col])^
//                                    Double.doubleToRawLongBits(secondInputImage[row][col]));
//                    outputImage[row][col]=firstInputImage[row][col];
//                }
//            }
//        }
//        return outputImage;
//    }

    public double[][] XORTwoImages(BufferedImage firstInputImage,double[][] secondInputImage,int height,int width,long n1){
        double[][] outputImage=new double[height][width];
        for(int i=0;i<n1;i++){
            for(int row=0;row<height;row++){
                for(int col=0;col<width;col++){
                    firstInputImage.setRGB(row,col,(int) (firstInputImage.getRGB(row,col)^
                            Double.doubleToRawLongBits(secondInputImage[row][col])));
                    outputImage[row][col]=firstInputImage.getRGB(row,col);
                }
            }
        }
        return outputImage;
    }

    public double[][] generateBakerMap(double[][] inputImage, int widthOfInputImage, int heightOfInputImage, List<Integer> secretKey) throws IOException {
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
                        outputBakerMap[row][col++] =inputImage[coordX][coordY];
                    }
                }
                row++;
                height+=heightOfInputImage/valForSecretKey;
            }
        }
        return outputBakerMap;
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
                        outputDecryptedBakerMap[coordX][coordY]=inputBakerMap[row][col++];
                    }
                }
                row--;
                auxiliaryHeightOfImage=auxiliaryHeightOfImage-height;
            }
            offset+=valForSecretKey;
        }
        return  outputDecryptedBakerMap;
    }

    public double[][] generateIDCTForImage(double[][] inputBufferedImage,int inputHeight,int inputWidth) throws IOException {
        DoubleDCT_2D doubleDCT_2D=new DoubleDCT_2D(inputHeight, inputWidth);
        doubleDCT_2D.inverse(inputBufferedImage,false);
        return  inputBufferedImage;
    }


//    public double[][] generateIDCTForImage(double[][] inputBufferedImage,int inputHeight,int inputWidth) throws IOException {
//        double [][] outputDCTBufferedImage=new double[inputHeight][inputWidth];
//        double sum=0,dct=0,alphaP=0,alphaQ=0,firstSquare=1/Math.sqrt(inputHeight),secondSquare=Math.sqrt(2)/Math.sqrt(inputHeight);
//        int result=inputHeight<<1;
//        for(int i=0;i<inputHeight;i++){
//            for(int j=0;j<inputWidth;j++){
//                sum=0;
//                for(int k=0;k<inputHeight;k++){
//                    for(int w=0;w<inputWidth;w++) {
//                        if(k==0) alphaP=firstSquare;
//                        else alphaP=secondSquare;
//                        if(w==0) alphaQ=firstSquare;
//                        else alphaQ=secondSquare;
//                        int valI=i<<1,valJ=j<<1;
//                        sum+=alphaP*alphaQ*inputBufferedImage[k][w]*
//                                ((Math.cos((Math.PI*((valI+1)*k+(valJ+1)*w))/result)+
//                                Math.cos((Math.PI*((valI+1)*k-(valJ+1)*w)))/result)/2);
//                    }
//                }
//                outputDCTBufferedImage[i][j]=sum;
//            }
//        }
//        return outputDCTBufferedImage;
//    }

    public BufferedImage reconstructImage(List<ImageObject> imageObjectList, int height, int width){
        BufferedImage bufferedImage=new BufferedImage(width, height,1);
        Graphics2D graphics2D=bufferedImage.createGraphics();
        for(int i = 0; i< imageObjectList.size(); i++) {
            graphics2D.drawImage(imageObjectList.get(i).getBufferedImage(),
                    imageObjectList.get(i).getXAxis(), imageObjectList.get(i).getYAxis(),
                    imageObjectList.get(i).getWidth(), imageObjectList.get(i).getWidth(), null);
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
