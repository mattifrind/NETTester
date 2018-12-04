package nettester;

import nettester.Image;
import nettester.parsing.Parser;
import nettester.parsing.BBoxParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Matti J. Frind
 */
public class ParserTester {
    
    @Test
    public void test() throws IOException {
        String inputString = "2016WMLeipzigG4A_GLaDOS_00274245_U.png;1486075843552;flopp;foot;148;60;164;60;foot\n" +
                "2016WMLeipzigG4C_MisterBurns_00023111_U.png;1483187932780;tkalbitz;Ball;381;184;368;179;Ball\n"
                + "2016WMLeipzigG4C_GLaDOS_00148664_U.png;1483187339934;tkalbitz;Ball;617;72;623;91;Ball";
        BufferedReader bf = new BufferedReader(new StringReader(inputString));
        Parser ps = new Parser();
        Map<String, Image> result = ps.parseTestData(bf);
        Image img = result.get("2016WMLeipzigG4C_GLaDOS_00148664_U");
        Image ref = new Image("2016WMLeipzigG4C_GLaDOS_00148664_U", Arrays.asList(BBoxParser.readBBox1("617", "72", "623", "91", "Ball")));
        System.out.println(img.getBbox());
        assertEquals("Test 1", ref, img);
        
        img = result.get("2016WMLeipzigG4C_MisterBurns_00023111_U");
        ref = new Image("2016WMLeipzigG4C_MisterBurns_00023111_U", Arrays.asList(BBoxParser.readBBox1("381", "184", "368", "179", "Ball")));
        System.out.println(img.getBbox());
        assertEquals("Test 2", ref, img);
    }
}
