package frc.robot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.opencv.core.Rect;

import frc.robot.commands.GalacticSearch;

public class TriangleTests {
     //Path Blue A
     Rect[] refBA = {
        new Rect(1456, 428, 63, 63),
        new Rect(969, 377, 37, 37),
        new Rect(762, 414, 55, 55)
    };
    //Path Blue B
    Rect[] refBB = {
        new Rect(1172, 364,34,34),
        new Rect(843, 389,49,49),
        new Rect(1293,428,63,63)
    };
    //Path Red A
    Rect[] refRA = {
        new Rect(912,633,124,124),
        new Rect(464,452,62,62),
        new Rect(1242,466,77,77)
    };
    //Path Red B
    Rect[] refRB = {
        new Rect(537,642,102,102),
        new Rect(884,416,49,49),
        new Rect(1362,470,68,68)
    };
    @Test
    public void triangleWithinTest() {
        Rect[] pathBASwitch = {new Rect(969, 377, 37, 37), new Rect(762, 414, 55, 55), new Rect(1456, 428, 63, 63)};
        Rect[] pathBA = {new Rect(1456, 428, 63, 63), new Rect(969, 377, 37, 37), new Rect(762, 414, 55, 55)};
        assertTrue("Its the same triangle. BA", GalacticSearch.triangleWithinTolerance(
            pathBA, pathBASwitch, 1.0));

        assertTrue("Its the same triangle. BB", GalacticSearch.triangleWithinTolerance(refBB, refBB, 1.10));
        assertTrue("Its the same triangle. RA", GalacticSearch.triangleWithinTolerance(refRA, refRA, 1.10));
        assertTrue("Its the same triangle. RB", GalacticSearch.triangleWithinTolerance(refRB, refRB, 1.10));
    }
    @Test
    public void traingleNotWithinTest() {
       
        
        assertFalse("Its a different triangle: Path Blue B ", GalacticSearch.triangleWithinTolerance(
            refBA, refBB, 1.10));
        assertFalse("Its a different triangle: Path Red A ", GalacticSearch.triangleWithinTolerance(
            refBA, refRA, 1.10));
        assertFalse("Its a different triangle. Path Red B and BA", GalacticSearch.triangleWithinTolerance(
            refBA, refRB, 1.10));
    }
}
