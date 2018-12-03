package datasettester.parsing;

import datasettester.BoundingBox;
import datasettester.DatasetTester;
import datasettester.Image;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains all functions for parsing the input data in the used data format
 * @author Matti J. Frind
 */
public class Parser {

    public Map<String, Image> parseTestData(BufferedReader data) throws IOException {
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

    /**
     * Parses the test data in a Map of the name of the image and the image itself.
     * @return Map of the names of the images and the images themselves
     * @throws java.io.FileNotFoundException
     * @throws IOException
     */
    public Map<String, Image> readTest() throws FileNotFoundException, IOException {
        try (final BufferedReader traindata = new BufferedReader(new FileReader("data//" + DatasetTester.TEST_FILE))) {
            return parseTestData(traindata);
        }
    }

    /**
     * Parses the detections data in a Map of the name of the image and the image itself.
     * @return Map of the names of the images and the images themselves
     * @throws IOException
     */
    public Map<String, Image> readDetections() throws IOException {
        try (final BufferedReader traindata = new BufferedReader(new FileReader("data//" + DatasetTester.DET_FILE))) {
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
