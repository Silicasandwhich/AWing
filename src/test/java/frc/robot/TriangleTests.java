package frc.robot;

import static org.junit.Assert.assertTrue;

import org.opencv.core.Rect;

import frc.robot.commands.GalacticSearch;

public class TriangleTests {
    public void triangleWithinTest() {
        Rect[] ref = {new Rect(969, 377, 37, 37), new Rect(762, 414, 55, 55), new Rect(1456, 428, 63, 63)};
        Rect[] fixed = {new Rect(1456, 428, 63, 63), new Rect(969, 377, 37, 37), new Rect(762, 414, 55, 55)};
        assertTrue("Its the same triangle. ", GalacticSearch.triangleWithinTolerance(
            fixed, ref, 1.5));
    }
}
