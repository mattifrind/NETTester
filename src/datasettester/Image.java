/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author matti
 */
public class Image {
   private String name = "";
   private List<BoundingBox> bbox = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<BoundingBox> getBbox() {
        return bbox;
    }

    public void setBbox(ArrayList<BoundingBox> bbox) {
        this.bbox = bbox;
    }
    
    public void addBBox(BoundingBox bb) {
        if (bb.getClasz().equals("foot") || bb.getClasz().equals("ball")) bbox.add(bb);
    }
    
    public void addBBox(List<BoundingBox> bb) {
        bb.forEach(box -> {
            if (box.getClasz().equalsIgnoreCase("foot") || box.getClasz().equalsIgnoreCase("ball")) {
                bbox.add(box);
            }
        });
    }
    
    public void merge(Image image) {
        addBBox(image.getBbox());
    }
    
    public long countBoxes(String clasz) {
        return bbox.stream().filter(b -> clasz.equals(b.getClasz())).count();
    }
    

    public Image(String name, List<BoundingBox> bbox) {
        this(name);
        addBBox(bbox);
    }
    
    public Image(String name, BoundingBox bbox) {
        this(name, Arrays.asList(bbox));
    }
    
    public Image(String name) {
        setName(name);
    }
    
    /**
     * Parse these values:
     * 2017_GermanOpenG1E_Gargamel_00229820_U.png;1493924792341;tkalbitz;Ball;280;293;318;244;Ball
     * @param line to be parsed
     * @return
     */
    public static Image of(String line) {
        String[] tempValues = line.split(";");
        if (tempValues.length != 9) return null;
        BoundingBox bb = new BoundingBox(tempValues[4], tempValues[5], tempValues[6], tempValues[7], "1", tempValues[8]);
        return new Image(tempValues[0].substring(0, line.indexOf(".png")), bb);
    }
}
