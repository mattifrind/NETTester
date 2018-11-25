/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        return bbox.stream().filter(b -> clasz.equalsIgnoreCase(b.getClasz())).count();
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
        if (tempValues[8].equalsIgnoreCase("foot")) {
            BoundingBox[] boxes = BBoxParser.readBBox2(tempValues[4], tempValues[5], tempValues[6], tempValues[7]);
            return new Image(tempValues[0].substring(0, line.indexOf(".png")), Arrays.asList(boxes));
        }
        BoundingBox bb = new BoundingBox(tempValues[4], tempValues[5], tempValues[6], tempValues[7], "1", tempValues[8]);
        return new Image(tempValues[0].substring(0, line.indexOf(".png")), bb);
    }
    
    private boolean equals(List<BoundingBox> ls1, List<BoundingBox> ls2) {
        if (ls1 == ls2) {
            return true;
        }
        if (ls1 == null || ls2 == null) {
            return false;
        }
        if (ls1.size() != ls2.size()) {
            return false;
        }
        for (BoundingBox boundingBox : ls1) {
            if (!ls2.contains(boundingBox)) return false;
        }
        for (BoundingBox boundingBox : ls2) {
            if (!ls1.contains(boundingBox)) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Image other = (Image) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!equals(this.bbox, other.bbox)) {
            return false;
        }
        return true;
    }

}
