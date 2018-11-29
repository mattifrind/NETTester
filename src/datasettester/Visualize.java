
package datasettester;

import static datasettester.DatasetTester.IMAGE_DIRECTORY;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import static datasettester.DatasetTester.VIS_DIRECTORY;
import java.awt.BasicStroke;
import java.awt.Font;
import java.util.List;

/**
 * FROM ANNOTATION CONVERTER
 * @author matti
 */
public class Visualize {
    
    public void visualizeBBoxes(String image, List<BoundingBox> gt, List<BoundingBox> dets, ImageStatistic is, String selectedClass, double thresh, double iou_thresh) {
        BufferedImage bimage = new BufferedImage(640, 480, BufferedImage.TYPE_BYTE_INDEXED);
        try {
            bimage = ImageIO.read(new File(IMAGE_DIRECTORY + image + ".jpg"));
        } catch (IOException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        }

        Graphics2D g2d = bimage.createGraphics();
        //g2d.setColor(Color.white);
        //g2d.fillRect(0, 0, 640, 480);
        g2d.setColor(Color.red);
        g2d.setStroke(new BasicStroke(2));
        g2d.setFont(new Font("Roboto Light", 0, 15));
        int drawings = 0;
        for (BoundingBox bb : gt) {
            if (!bb.getClasz().equalsIgnoreCase(selectedClass)) continue;
            drawings++;
            g2d.drawRect((int) bb.getX(), (int) bb.getY(), (int) bb.getWidth(), (int) bb.getHeight());
        }
        g2d.setColor(Color.blue);
        for (BoundingBox bb : dets) {
            if (bb.getProb() < 0.1) continue;
            if (!bb.getClasz().equalsIgnoreCase(selectedClass)) continue;
            drawings++;
            g2d.drawRect((int) bb.getX(), (int) bb.getY(), (int) bb.getWidth(), (int) bb.getHeight());
            g2d.drawString(bb.getRoundedProbString(), bb.getCenter().getKey().intValue() - 8, bb.getCenter().getValue().intValue());
        }
        if(drawings == 0) return;
        g2d.drawString("thresh=" + thresh, 10, bimage.getHeight()-130);
        g2d.drawString("iou_thresh=" + iou_thresh, 10, bimage.getHeight()-100);
        g2d.drawString("tp=" + is.getTrue_positive(), 10, bimage.getHeight()-70);
        g2d.drawString("fp=" + is.getFalse_positive(), 10, bimage.getHeight()-40);
        g2d.drawString("fn=" + is.getFalse_negative(), 10, bimage.getHeight()-10);
        
        g2d.dispose();
        
        File outputfile = new File(VIS_DIRECTORY + image + ".jpg");
        try {
            ImageIO.write(bimage, "jpg", outputfile);
        } catch (IOException ex) {
            Logger.getLogger(Visualize.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
