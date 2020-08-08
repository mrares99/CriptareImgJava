package good;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageOperations {

    public BufferedImage readImage(File path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(path);
        } catch (IOException e) {
            System.out.println("Calea spre imagine si/sau numele imaginii este gresit!");
        }
        return image;
    }


//    public List<BufferedImage> extractColorChannels(BufferedImage bufferedImage) {
//        List<BufferedImage> bufferedImageList=new ArrayList<>();
//        int width=bufferedImage.getWidth(),height=bufferedImage.getHeight();
//        //imaginile vor fi stocate in ordine red-green-blue
//        BufferedImage redChannel=new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
//        BufferedImage greenChannel=new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
//        BufferedImage blueChannel=new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
//        for(int i=-1;++i<height;){
//            for(int j=-1;++j<width;){
//                //rgb = bufferedImage.getRGB(j,i);
//                redChannel.setRGB(j,i,(bufferedImage.getRGB(j,i) & 0x00ff0000) ); //red
//                greenChannel.setRGB(j,i,bufferedImage.getRGB(j,i) & 0x0000ff00); //green
//                blueChannel.setRGB(j,i,bufferedImage.getRGB(j,i) & 0x000000ff); //blue
//            }
//        }
//        bufferedImageList.add(redChannel);
//        bufferedImageList.add(greenChannel);
//        bufferedImageList.add(blueChannel);
//        return bufferedImageList;
//    }


    public List<BufferedImage> extractColorChannels(BufferedImage bufferedImage) {
        List<BufferedImage> bufferedImageList=new ArrayList<>();
        int width=bufferedImage.getWidth(),height=bufferedImage.getHeight();
        //imaginile vor fi stocate in ordine red-green-blue
        BufferedImage redChannel=new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        BufferedImage greenChannel=new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        BufferedImage blueChannel=new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);

        int[] pixels = new int[width * height];
        PixelGrabber pg = new PixelGrabber(bufferedImage, 0, 0, width, height, pixels, 0, width);

        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
            System.err.println("interrupted waiting for pixels!");
        }
        if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
            System.err.println("image fetch aborted or errored");
        }

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                redChannel.setRGB(i,j,pixels[j * width + i] & 0x00ff0000  ); //red
                greenChannel.setRGB(i,j,pixels[j * width + i] & 0x0000ff00); //green
                blueChannel.setRGB(i,j,pixels[j * width + i] & 0x000000ff ); //blue
            }
        }
        bufferedImageList.add(redChannel);
        bufferedImageList.add(greenChannel);
        bufferedImageList.add(blueChannel);
        return bufferedImageList;
    }


    public BufferedImage constructImageFromRGBChannels(BufferedImage firstImage, BufferedImage secondImage, BufferedImage thirdImage){
        int width=firstImage.getWidth(),height=firstImage.getHeight();
        BufferedImage outputImage=new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        for(int i=-1;++i<height;){
            for(int j=-1;++j<width;){
                outputImage.setRGB(j,i,firstImage.getRGB(j,i) | secondImage.getRGB(j,i) | thirdImage.getRGB(j,i));
            }
        }
        return outputImage;
    }


}