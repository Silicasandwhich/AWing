package frc.robot;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.opencv.core.Rect;

import frc.robot.commands.GalacticSearch;

public class VisionTests {

    public VisionTests() {}

    @Test
    public void testNoPath() throws Exception {
        Rect[] lemons = {};
        String path = GalacticSearch.selectPathFromRects(lemons);
        assertEquals("No Path Should Be Expected","", path);

    }

    @Test
    public void testBluePathA() throws Exception {
        Rect[] lemons = {};
        String path = GalacticSearch.selectPathFromRects(lemons);
        assertEquals("Blue Path A Expected","blue_a.wpilib.json", path);
    }

    @Test
    public void testRedPathA() throws Exception {
        Rect[] lemons = {};
        String path = GalacticSearch.selectPathFromRects(lemons);
        assertEquals("Red Path A Expected","blue_a.wpilib.json", path);
    }

    @Test
    public void testBluePathB() throws Exception {
        Rect[] lemons = {};
        String path = GalacticSearch.selectPathFromRects(lemons);
        assertEquals("Blue Path B Expected","blue_a.wpilib.json", path);
    }

    @Test
    public void testRedPathB() throws Exception {
        Rect[] lemons = {};
        String path = GalacticSearch.selectPathFromRects(lemons);
        assertEquals("Red Path B Expected","blue_a.wpilib.json", path);
    }
}