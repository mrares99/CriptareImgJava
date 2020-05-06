import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ImageEncryption {
    private int n11;

    public int getN11() {
        return n11;
    }

    public void setN11(int n11) {
        this.n11 = n11;
    }

    public BufferedImage readImage(File path) {//trebuie verificare pt nullPointer(in caz ca nu gaseste imaginea)
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
            imageObjectList.add(new ImageObject(0,0,height,width,bufferedImage,height,width));
            return imageObjectList;
        }
        if(height>width){
            while(coordY+width<=height){
                imageObjectList.add(new ImageObject(coordX,coordY,width,width,bufferedImage.getSubimage(0,coordY,width,width),height,width));
                coordY+=width;
            }
            imageObjectList.add(new ImageObject(0,height-width,width,width,bufferedImage.getSubimage(0,height-width,width,width),height,width));
            return imageObjectList;
        }
        else if(width>height){
            while(coordX+height<=width){
                imageObjectList.add(new ImageObject(coordX,coordY,height,height,bufferedImage.getSubimage(coordX,0,height,height),height,width));
                coordX+=height;
            }
            imageObjectList.add(new ImageObject(width-height,0,height,height,bufferedImage.getSubimage(width-height,0,height,height),height,width));
            return imageObjectList;
        }
        return imageObjectList;
    }

    public int[] getARGBPixelArrayFromImage(BufferedImage bufferedImage){
        int[] argb=bufferedImage.getRGB(0,0,bufferedImage.getWidth(),
                bufferedImage.getHeight(),null,0,bufferedImage.getWidth());
        return argb;
    }

    public int[] getAlphaPixelArrayFromImage(int [] argb){
        int[] alphaPixel=new int[argb.length];
        for(int i=0;i<argb.length;i++){
            alphaPixel[i]=argb[i] >> 24 & 0xff;
        }
        return alphaPixel;
    }

    public int[] getRedPixelArrayFromImage(int [] argb){
        int[] redPixel=new int[argb.length];
        for(int i=0;i<argb.length;i++){
            redPixel[i]= (argb[i] >> 16 & 0xff);
        }
        return redPixel;
    }

    public int[] getGreenPixelArrayFromImage(int[] argb){
        int[] greenPixel=new int[argb.length];
        for(int i=0;i<argb.length;i++){
            greenPixel[i]=  (argb[i] >> 8 & 0xff);
        }
        return greenPixel;
    }

    public int[] getBluePixelArrayFromImage(int [] argb){
        int[] bluePixel=new int[argb.length];
        for(int i=0;i<argb.length;i++){
            bluePixel[i]=  (argb[i] & 0xff);
        }
        return bluePixel;
    }

    public double[][] generateDCTForImage(BufferedImage inputBufferedImage) throws IOException {
        double [][] outputDCTBufferedImage=new double[inputBufferedImage.getHeight()][inputBufferedImage.getWidth()];
        double alphaP=0,alphaQ=0,sum=0,dct=0;
        double firstSquare=1/Math.sqrt(inputBufferedImage.getWidth()),secondSquare=Math.sqrt(2)/Math.sqrt(inputBufferedImage.getWidth());
        int result=2*inputBufferedImage.getWidth();
        for(int i=0;i<inputBufferedImage.getHeight();i++){
            for(int j=0;j<inputBufferedImage.getWidth();j++){
                if(i==0) alphaP=firstSquare;//alphaP= 1/Math.sqrt(inputBufferedImage.getWidth());
                else alphaP=secondSquare;//alphaP=  Math.sqrt(2)/Math.sqrt(inputBufferedImage.getWidth());
                if(j==0) alphaQ=firstSquare;//alphaQ=  1/Math.sqrt(inputBufferedImage.getWidth());
                else alphaQ=secondSquare;//alphaQ=  Math.sqrt(2)/Math.sqrt(inputBufferedImage.getWidth());
                sum=0;
                for(int k=0;k<inputBufferedImage.getHeight();k++){
                    for(int w=0;w<inputBufferedImage.getWidth();w++) {
                        int rgb=inputBufferedImage.getRGB(k,w);
                        int alpha = 0;
                        int red = rgb >>16 & 0xff;
                        int green = rgb >>8 & 0xff;
                        int blue = rgb & 0xff;
                        rgb=alpha<<24 | red<<16 | green<<8 | blue;
//                        dct=rgb*
//                                Math.cos((Math.PI*(2*k+1)*i)/(result))*
//                                Math.cos((Math.PI*(2*w+1)*j)/(result));
                        int valK=k<<1,valW=w<<1;
                        dct=rgb*
                                ((Math.cos((Math.PI*((valK+1)*i+(valW+1)*j))/result)+
                                Math.cos((Math.PI*((valK+1)*i-(valW+1)*j))/result))/2);

                        sum+=dct;
                    }
                }
                outputDCTBufferedImage[i][j]=sum*alphaP*alphaQ;
            }
        }
        return outputDCTBufferedImage;
    }

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

    public double[][] generateBakerMap(double[][] inputImage,int widthOfInputImage,int heightOfInputImage,List<Integer> secretKey) throws IOException {//se imparte in dreptunghiuri,la numar fiind lungimea laturii imaginii
        double[][] outputBakerMap=new double[heightOfInputImage][widthOfInputImage];
        int height=0,offset=widthOfInputImage,row=0,col=0;//offset=widthOfInputImage-secretKey[secretKey.length-1]
        int sum=0;
        for(int i=0;i<secretKey.size();i++){
            sum+=secretKey.get(i);
        }
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
