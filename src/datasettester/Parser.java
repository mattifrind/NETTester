/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import static datasettester.DatasetTester.DET_FILE;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author matti
 */
public class Parser {

    Map<String, Image> parseTestData(BufferedReader data) throws IOException {
        Map<String, Image> images = new HashMap<>();
        String line = "";
        while ((line = data.readLine()) != null) {
            Image img = Image.of(line);
            if (img == null) {
                continue;
            }
            Image found = images.get(img.getName());
            if (found == null) {
                images.put(img.getName(), img);
            } else {
                found.merge(img);
            }
        }
        return images;
    }

    public Map<String, Image> readTest() throws FileNotFoundException, IOException {
        try (final BufferedReader traindata = new BufferedReader(new FileReader("data//large_robot.csv"))) {
            return parseTestData(traindata);
        }
    }

    public Map<String, Image> readDetections() throws IOException {
        try (final BufferedReader traindata = new BufferedReader(new FileReader("data//" + DET_FILE))) {
            return parseDetections(traindata);
        }
    }

    protected Map<String, Image> parseDetections(BufferedReader detections) throws IOException {
        HashMap<String, Image> images = new HashMap<>();
        String line;
        ParseState state = ParseState.OTHER;
        Image img = null;
        int pos;
        StringBuffer arrayContent = null;
        String probability = null;
        lineLoop:
        while ((line = detections.readLine()) != null) {
            String tempLine = line.trim();
            pos = 0;
            while (pos < tempLine.length()) {
                switch (state) {
                    case BOUNDINGBOX:
                        char currChar = tempLine.charAt(pos);
                        if (currChar == '[') {
                            arrayContent = new StringBuffer();
                        } else if (currChar == ']') {
                            state = ParseState.PROBABILITY;
                        } else if (currChar == '|') {
                            state = ParseState.OTHER;
                            if (img != null) {
                                images.put(img.getName(), img);
                                img = null;
                            }
                        } else if (arrayContent != null) {
                            arrayContent.append(currChar);
                        }
                        break;
                    case PROBABILITY:
                        probability = tempLine;
                        state = ParseState.CLASZ;
                        continue lineLoop;
                    case CLASZ:
                        String[] values = tempLine.split("\\s+");
                        img.addBBox(BoundingBox.of(arrayContent.toString(), probability, values[0]));
                        state = ParseState.BOUNDINGBOX;
                        continue lineLoop;
                    case OTHER:
                        if (tempLine.contains("/")) {
                            continue lineLoop;
                        }
                        if (img == null) {
                            img = new Image(tempLine);
                            state = ParseState.BOUNDINGBOX;
                            continue lineLoop;
                        }
                        break;
                }
                pos++;
            }
        }
        return images;
    }
    
}
