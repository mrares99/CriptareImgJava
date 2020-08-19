package good;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageOperations {

    public BufferedImage readImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("choosertitle");
        chooser.setAcceptAllFileFilterUsed(false);
        File path;
        BufferedImage image = null;
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            path=chooser.getSelectedFile();
            try {
                image = ImageIO.read(path);
            } catch (IOException e) {
                System.out.println("Calea spre imagine si/sau numele imaginii este gresit!");
            }
        } else {
            path= new File("Wrong path");
        }


        return image;
    }

}