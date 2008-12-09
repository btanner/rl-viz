/*
 * Copyright 2008 Brian Tanner
 * http://bt-recordbook.googlecode.com/
 * brian@tannerpages.com
 * http://brian.tannerpages.com
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package rlVizLib.messaging.environmentShell;

import java.io.DataOutputStream;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import rlVizLib.messaging.BinaryPayload;
import static org.junit.Assert.*;

/**
 *
 * @author btanner
 */
public class TaskSpecPayloadTest {

    public TaskSpecPayloadTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Test of getTaskSpec method, of class TaskSpecPayload.
     */
    @Test
    public void testGetTaskSpec() {
    }

    /**
     * Test of getErrorStatus method, of class TaskSpecPayload.
     */
    @Test
    public void testGetErrorStatus() {
    }

    /**
     * Test of getErrorMessage method, of class TaskSpecPayload.
     */
    @Test
    public void testGetErrorMessage() {
    }

    /**
     * Test of toString method, of class TaskSpecPayload.
     */
    @Test
    public void testToString() {
    }
    
    @Test
    public void checkEncoding() throws IOException{
                BinaryPayload P = new BinaryPayload();
        DataOutputStream DOS = P.getOutputStream();
        P.writeRawString("sample task spec");
        P.writeRawString("");
            DOS.writeBoolean(false);
        
        String encodedPayload = P.getAsEncodedString();
        System.out.println("Returning encoded payload of length:"+encodedPayload.length());
        System.out.println(encodedPayload);

    }

}