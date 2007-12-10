package btViz;
import org.junit.Test;
import static org.junit.Assert.*;
import rlVizLib.rlVizCore;
import btViz.GraphicalDriver;
/**
 *
 * @author btanner
 */
public class btVizTest {

    @Test
    public void testGraphicalDriverVersion(){
       assertEquals(rlVizCore.getSpecVersion(),rlVizLib.rlVizCore.getRLVizLinkVersionOfClass(GraphicalDriver.class));
    }
}
