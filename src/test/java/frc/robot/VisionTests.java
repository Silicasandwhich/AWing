package frc.robot;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.CascadeClassifier;

import frc.robot.commands.GalacticSearch;
import frc.robot.subsystems.camera.ExMachina;

public class VisionTests {

    /**
     *
     */

    public VisionTests() {}

    @Test
    public void testNoPath() throws Exception {
        Rect[] lemons = {};
        String traj = GalacticSearch.selectPathFromRects(lemons);
        assertEquals("No Path Should Be Expected","", traj);
    }

    @Test
    public void testBluePathA() throws Exception {
        Rect[] lemons = getLemons(0);
        String path = GalacticSearch.selectPathFromRects(lemons);
        assertEquals("Blue Path A Expected","blue_a.wpilib.json", path);
    }

    @Test
    public void testRedPathA() throws Exception {
        Rect[] lemons = getLemons(2);
        String path = GalacticSearch.selectPathFromRects(lemons);
        assertEquals("Red Path A Expected","blue_a.wpilib.json", path);
    }

    @Test
    public void testBluePathB() throws Exception {
        Rect[] lemons = getLemons(1);
        String path = GalacticSearch.selectPathFromRects(lemons);
        assertEquals("Blue Path B Expected","blue_a.wpilib.json", path);
    }

    @Test
    public void testRedPathB() throws Exception {
        Rect[] lemons = getLemons(3);
        String path = GalacticSearch.selectPathFromRects(lemons);
        assertEquals("Red Path B Expected","blue_a.wpilib.json", path);
    }

    private Rect[] getLemons(int traj) {
        String path = "src/main/deploy/ExMachina.xml";
        ExMachina ml = new ExMachina();
        
        File cXML = new File(path);
        String absolutePath = cXML.getAbsolutePath();
        System.out.println("Ex Machina: "+absolutePath);

        Mat image;
        String imagePath = "src/test/resources/";
        File file = new File(imagePath);
        imagePath = file.getAbsolutePath();
        // 0:  Blue A
        // 1: Blue B
        // 2: Red A
        // 3: Red B
        if(traj == 0) {
            image = Imgcodecs.imread(imagePath+"\\Blue A\\WIN_20210313_09_33_09_Pro.jpg");
        } else if (traj == 1) {
            image = Imgcodecs.imread(imagePath+"\\Blue B\\WIN_20210313_09_35_09_Pro.jpg");
        } else if (traj == 2) {
            image = Imgcodecs.imread(imagePath+"\\Red A\\WIN_20210313_09_32_04_Pro.jpg");
        } else if (traj == 3) {
            image = Imgcodecs.imread(imagePath+"\\Red B\\WIN_20210313_09_34_23_Pro.jpg");
        } else {
            image = new Mat();
        }
        
        CascadeClassifier classifier = new CascadeClassifier(cXML.getAbsolutePath());

        ml.process(image, classifier);
        Rect[] lemons = ml.cascadeClassifierOutput().toArray();
        for (int i = 0; i < lemons.length; i++) {
            System.out.println("Path: "+ traj + "; Rect: "+i+"; x:"+lemons[i].x + " y: "+lemons[i].y+ " size:"+lemons[i].size());
        }
        return lemons;
    }
}