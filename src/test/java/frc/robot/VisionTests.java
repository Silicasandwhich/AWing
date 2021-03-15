package frc.robot;

import frc.robot.commands.GalacticSearch;

class VisionTests {
    @Test
    @DisplayName("Blue A")
    void bluePathATest() {
        String path = GalacticSearch.selectPathFromRects(new Rect[] {});
        assertEquals("", path);
    }
}