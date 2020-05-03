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
    private int k;
    private int x0;
    private int y0;
    private int n1;
    private int k1;
    private int x10;
    private int y10;
    private int n11;

    public BufferedImage readImage(File path) {//trebuie verificare pt nullPointer(in caz ca nu gaseste imaginea)
        BufferedImage image = null;
        try {
            image = ImageIO.read(path);
        } catch (IOException e) {
            System.out.println("Calea spre imagine si/sau numele imaginii este gresit!");
        }
        return image;
    }

//    public List<Image> splitImageIntoSquares(BufferedImage bufferedImage) {
//        List<Image> imageList = new ArrayList<Image>();
//        BufferedImage auxiliaryFirst = bufferedImage;
//        int initialHeight = auxiliaryFirst.getHeight(),initialWidth = auxiliaryFirst.getWidth(),coordX = 0, coordY = 0;
//        boolean finish = false;
//        while (!finish) {
//            if (auxiliaryFirst.getWidth() == auxiliaryFirst.getHeight()) {
//                if (coordX + auxiliaryFirst.getHeight() == initialHeight && coordY + auxiliaryFirst.getWidth() == initialWidth) {
//                    finish = true;
//                    //coordX += auxiliaryFirst.getHeight();
//                    //coordY += auxiliaryFirst.getWidth();
//                } else if (coordX + auxiliaryFirst.getHeight() == initialHeight && coordY + auxiliaryFirst.getWidth() != initialWidth) {
//                    finish = true;
//                    //coordX += auxiliaryFirst.getHeight();
//                } else if (coordX + auxiliaryFirst.getHeight() != initialHeight && coordY + auxiliaryFirst.getWidth() == initialWidth) {
//                    finish = true;
//                   // coordY += auxiliaryFirst.getWidth();
//                }
//                imageList.add(new Image(coordX, coordY, auxiliaryFirst.getHeight(), auxiliaryFirst.getWidth(), auxiliaryFirst));
//            }
//            if (auxiliaryFirst.getHeight() > auxiliaryFirst.getWidth()) {
//                imageList.add(new Image(coordX, coordY, auxiliaryFirst.getWidth(), auxiliaryFirst.getWidth(), auxiliaryFirst.getSubimage(0, 0, auxiliaryFirst.getWidth(), auxiliaryFirst.getWidth())));
//                coordX = coordX + auxiliaryFirst.getWidth();
//                auxiliaryFirst = auxiliaryFirst.getSubimage(0, auxiliaryFirst.getWidth(), auxiliaryFirst.getWidth(), auxiliaryFirst.getHeight() - auxiliaryFirst.getWidth());
//            }
//            if (auxiliaryFirst.getWidth() > auxiliaryFirst.getHeight()) {
//                imageList.add(new Image(coordX, coordY, auxiliaryFirst.getHeight(), auxiliaryFirst.getHeight(), auxiliaryFirst.getSubimage(0, 0, auxiliaryFirst.getHeight(), auxiliaryFirst.getHeight())));
//                coordY += auxiliaryFirst.getHeight();
//                auxiliaryFirst = auxiliaryFirst.getSubimage(auxiliaryFirst.getHeight(), 0, auxiliaryFirst.getWidth() - auxiliaryFirst.getHeight(), auxiliaryFirst.getHeight());
//            }
//        }
//        return imageList;
//    }

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
        for(int i=0;i<inputBufferedImage.getHeight();i++){
            for(int j=0;j<inputBufferedImage.getWidth();j++){
                if(i==0) alphaP= 1/Math.sqrt(inputBufferedImage.getWidth());
                else alphaP=  Math.sqrt(2)/Math.sqrt(inputBufferedImage.getWidth());
                if(j==0) alphaQ=  1/Math.sqrt(inputBufferedImage.getWidth());
                else alphaQ=  Math.sqrt(2)/Math.sqrt(inputBufferedImage.getWidth());
                sum=0;
                for(int k=0;k<inputBufferedImage.getHeight();k++){
                    for(int w=0;w<inputBufferedImage.getWidth();w++) {
                        int rgb=inputBufferedImage.getRGB(k,w);
                        int alpha = 0;
                        int red = rgb >>16 & 0xff;
                        int green = rgb >>8 & 0xff;
                        int blue = rgb & 0xff;
                        rgb=alpha<<24 | red<<16 | green<<8 | blue;
                        dct=rgb*
                                Math.cos((Math.PI*(2*k+1)*i)/(2*inputBufferedImage.getWidth()))*
                                Math.cos((Math.PI*(2*w+1)*j)/(2*inputBufferedImage.getWidth()));
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

    public int[] generateSecretKeyForBakerMap(int inputNumber){
        int[] secretKey=new int[inputNumber/2+1];
        int step=0;
        for(int i=2;i<=inputNumber/2;i++){
            if(inputNumber%i==0) {
                secretKey[step++]=i;
            }
        }
        int[] outputSecretKey=new int[step];
        for(int i=0;i<step;i++){
            outputSecretKey[i]=secretKey[i];
        }
        int[] sequenceGenerated=new int[inputNumber];
        int altStep=0;
        Random random=new Random();
        int pas=0;
        int sum=0;
        while(true){
            int value=outputSecretKey[random.nextInt(step)];
            if(sum+value==inputNumber){
                sequenceGenerated[altStep++]=value;
                break;
            }
            else if(sum+value<inputNumber){
                if(value%2==0){
                    sequenceGenerated[altStep++]=value;
                    sum+=value;
                }
                if(value%2==1){
                    if(sum+2*value<inputNumber){
                        sequenceGenerated[altStep++]=value;
                        sequenceGenerated[altStep++]=value;
                        sum+=2*value;
                    }
                }
            }
            else if(sum+value>inputNumber){
                continue;
            }
        }
        int[] outputSequenceGenerated=new int[altStep];
        for(int i=0;i<altStep;i++){
            outputSequenceGenerated[i]=sequenceGenerated[i];
        }
        return outputSequenceGenerated;
    }



    public double[][] generateBakerMap(double[][] inputImage,int widthOfInputImage,int heightOfInputImage,List<Integer> secretKey) throws IOException {//se imparte in dreptunghiuri,la numar fiind lungimea laturii imaginii
        double[][] outputBakerMap=new double[heightOfInputImage][widthOfInputImage];
        //FileWriter fileWriter=new FileWriter("Rezultat.txt");
//        int pas=0,inwhileinfor=0;
        int height=0,offset=widthOfInputImage,row=0,col=0;//offset=widthOfInputImage-secretKey[secretKey.length-1]
        //fileWriter.write("Imagine noua=>widthOfInputImage= "+widthOfInputImage+" heightOfInputImage= "+heightOfInputImage+"\n");
        //fileWriter.write("inainte de for=>height= "+height+" offset= "+offset+"\n");

        //fileWriter.write("secretKey=");
        int sum=0;
        for(int i=0;i<secretKey.size();i++){
//            fileWriter.write(secretKey[i]+" ");
            sum+=secretKey.get(i);
        }
//        fileWriter.write(" sum="+sum+"\n");
        for(int i=secretKey.size()-1;i>=0;i--){
            height=heightOfInputImage/secretKey.get(i)-1;
            offset-=secretKey.get(i);
//            fileWriter.write("\n"+"in for la pasul= "+i+" =>height="+height+" offset= "+offset);
            while(height<heightOfInputImage){
                col=0;
//                inwhileinfor=0;
//                for(int coordX=height;coordX>=height-heightOfInputImage/secretKey[i]+1;coordX--){
//                    for(int coordY=offset;coordY<offset+secretKey[i]-1;coordY++){
//                        outputBakerMap[row][col++]=inputImage[coordX][coordY];
//                    }
//                }
//                fileWriter.write("\ninainte de for-ul din while=>inwhileinfor="+inwhileinfor+" coordY="+offset+" coordYLimita="+(offset+secretKey[i]-1)+" coordX="+height
//                +" coordXLimita="+(height-heightOfInputImage/secretKey[i]+1));
                for(int coordY=offset;coordY<=offset+secretKey.get(i)-1;coordY++){//for(int coordY=offset;coordY<offset+secretKey[i]-1;coordY++){
                    for(int coordX=height;coordX>=height-heightOfInputImage/secretKey.get(i)+1;coordX--){
                        outputBakerMap[row][col++] =inputImage[coordX][coordY];
//                        fileWriter.write(" ");
//                        inwhileinfor++;
                    }
                }
//                fileWriter.write("\ndupa forul din while=>inwhileinfor="+inwhileinfor+" ");
//                fileWriter.write("\n"+"in while pas="+(pas++));
                row++;
                height+=heightOfInputImage/secretKey.get(i);
//                fileWriter.write("\n"+"row= "+row+" col="+col);
//                fileWriter.write("\n"+"in while=>height="+height);
            }
//            fileWriter.write("\nafara din while===========================================\n");
        }
//        fileWriter.close();
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
