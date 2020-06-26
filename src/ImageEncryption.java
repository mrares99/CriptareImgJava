import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.spark.ml.feature.PolynomialExpansion;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.jtransforms.dct.DoubleDCT_2D;

public class ImageEncryption {

    public BufferedImage readImage(File path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(path);
        } catch (IOException e) {
            System.out.println("Calea spre imagine si/sau numele imaginii este gresit!");
        }
        return image;
    }

    public List<ImageObject> createSquareImagesWithMaximumEdgeLength(BufferedImage bufferedImage) {
        List<ImageObject> imageObjectList = new ArrayList<ImageObject>();
        int height=bufferedImage.getHeight(),width=bufferedImage.getWidth();
        int coordX=0,coordY=0;
        if(height==width){
            imageObjectList.add(new ImageObject(0,0,height,width,bufferedImage));
            return imageObjectList;
        }
        if(height>width){
            while(coordY+width<=height){
                imageObjectList.add(new ImageObject(coordX,coordY,width,width,bufferedImage.getSubimage(0,coordY,width,width)));
                coordY+=width;
            }
            imageObjectList.add(new ImageObject(0,height-width,width,width,bufferedImage.getSubimage(0,height-width,width,width)));
            return imageObjectList;
        }
        else if(width>height){
            while(coordX+height<=width){
                imageObjectList.add(new ImageObject(coordX,coordY,height,height,bufferedImage.getSubimage(coordX,0,height,height)));
                coordX+=height;
            }
            imageObjectList.add(new ImageObject(width-height,0,height,height,bufferedImage.getSubimage(width-height,0,height,height)));
            return imageObjectList;
        }
        return imageObjectList;
    }

    public double[][] generateDCTForImage(BufferedImage bufferedImage){
        final int width = bufferedImage.getWidth();
        final int height = bufferedImage.getHeight();
        DoubleDCT_2D doubleDCT_2D=new DoubleDCT_2D(height, height);
        double[][] doubleBufferedImage=new double[height][height];
        final boolean hasAlphaChannel=bufferedImage.getAlphaRaster()!=null;
        final byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        if (hasAlphaChannel) {//daca imaginea contine parametru pentru transparenta,il elimin;nu lucrez cu imagini cu paratreu de transparenta
            final int pixelLength = 4;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; //  alpha
                argb += ((int) pixels[pixel + 1] & 0xff); // albastru
                argb += (((int) pixels[pixel + 2] & 0xff) << 8); // verde
                argb += (((int) pixels[pixel + 3] & 0xff) << 16); // rosu
                doubleBufferedImage[row][col] = (double)argb;
                col++;
                if (col == width) {
                    col = 0;
                    if(row!=(width-1)) {
                        row++;
                    }
                }
            }
        } else {//daca nu are transparenta
            final int pixelLength = 3;
            for (int pixel = 0, row = 0, col = 0; pixel < pixels.length; pixel += pixelLength) {
                int argb = 0;
                argb += -16777216; //  alpha
                argb += ((int) pixels[pixel] & 0xff); // albastru
                argb += (((int) pixels[pixel + 1] & 0xff) << 8); // verde
                argb += (((int) pixels[pixel + 2] & 0xff) << 16); // rosu
                doubleBufferedImage[row][col] = (double)argb;
                col++;
                if (col == width) {
                    col = 0;
                    if(row!=(width-1)) {
                        row++;
                    }
                }
            }
        }
        doubleDCT_2D.forward(doubleBufferedImage,false);
        return doubleBufferedImage;
    }

//    public double[][] generateDCTForImage(BufferedImage inputBufferedImage) throws IOException {
//        double [][] outputDCTBufferedImage=new double[inputBufferedImage.getHeight()][inputBufferedImage.getWidth()];
//        double alphaP=0,alphaQ=0,sum=0,dct=0;
//        double firstSquare=1/Math.sqrt(inputBufferedImage.getWidth()),secondSquare=Math.sqrt(2)/Math.sqrt(inputBufferedImage.getWidth());
//        int lengthOfImage=inputBufferedImage.getWidth();
//        int result=lengthOfImage<<1;
//        for(int i=0;i<lengthOfImage;i++){
//            for(int j=0;j<lengthOfImage;j++){
//                if(i==0) alphaP=firstSquare;
//                else alphaP=secondSquare;
//                if(j==0) alphaQ=firstSquare;
//                else alphaQ=secondSquare;
//                sum=0;
//                for(int k=0;k<lengthOfImage;k++){
//                    for(int w=0;w<lengthOfImage;w++) {
//                        int rgb=inputBufferedImage.getRGB(k,w);
//                        int alpha = 0;
//                        int red = rgb >>16 & 0xff;
//                        int green = rgb >>8 & 0xff;
//                        int blue = rgb & 0xff;
//                        rgb=alpha<<24 | red<<16 | green<<8 | blue;
//                        int valK=k<<1,valW=w<<1;
//                        dct=rgb*
//                                ((Math.cos((Math.PI*((valK+1)*i+(valW+1)*j))/result)+
//                                Math.cos((Math.PI*((valK+1)*i-(valW+1)*j))/result))/2);
//                        sum+=dct;
//                    }
//                }
//                outputDCTBufferedImage[i][j]=sum*alphaP*alphaQ;
//            }
//        }
//        return outputDCTBufferedImage;
//    }

    public long generateKey(int k,int x0,int y0,int n1,int k1,int x10,int y10,int n11){
        long key=0;
        key=((((((k*10+x0)*10+y0)*10+n1)*10+k1)*10+x10)*10+y10)*10+n11;
        return key;
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

    public List<Integer> generateSecretKey(int inputNumber){
        List<Integer> outputSecretKey=new ArrayList<Integer>();
        List<Integer> sequenceGenerated=new ArrayList<Integer>();
        for(int i=2;i<=inputNumber/2+1;i++){
            if(inputNumber%i==0){
                outputSecretKey.add(i);
            }
        }
        Random random=new Random();
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
                        outputBakerMap[row][col++] =inputImage[coordX][coordY];
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

    public BufferedImage generateBufferedImageFromDoubleValues(double[][] inputImage,int height,int width){
        BufferedImage bufferedImage=new BufferedImage(width,height,2);
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                bufferedImage.setRGB(i,j, (int) inputImage[i][j]);
            }
        }
        return bufferedImage;
    }



}
