/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datasettester;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author matti
 */
public class ParserTester {
    
    //DEPRECATED: AdditionalBallSamples werden rausgefiltert.
    
    
    @Test
    public void test() throws IOException {
        String inputString = "2017_GermanOpenG1A_AddionalBallSamples_00077143_U.png;1493846696230;tkalbitz;foot;609;203;595;202;foot\n" +
                "2017_GermanOpenG1A_AddionalBallSamples_00077767_L.png;1493846705735;tkalbitz;foot;28;78;163;74;foot\n"
                + "2016WMLeipzigG4C_GLaDOS_00148664_U.png;1483187339934;tkalbitz;Ball;617;72;623;91;Ball";
        BufferedReader bf = new BufferedReader(new StringReader(inputString));
        Parser ps = new Parser();
        Map<String, Image> result = ps.parseTestData(bf);
        Image img = result.get("2017_GermanOpenG1A_AddionalBallSamples_00077143_U");
        Image ref = new Image("2017_GermanOpenG1A_AddionalBallSamples_00077143_U", Arrays.asList(BBoxParser.readBBox2("595", "202", "609", "203", "foot")));
        assertEquals(ref, img);
        
        img = result.get("2017_GermanOpenG1A_AddionalBallSamples_00077767_L");
        ref = new Image("2017_GermanOpenG1A_AddionalBallSamples_00077767_L", Arrays.asList(BBoxParser.readBBox2("28", "78", "163", "74", "foot")));
        assertEquals(ref, img);
    }
}
