/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rlVizLib;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author btanner
 */
public class RLVizCoreTest {

    @Test
    public void testRLVizCoreVersion(){
        assertEquals(rlVizCore.getSpecVersion(),rlVizCore.class.getPackage().getSpecificationVersion());
    }
}
