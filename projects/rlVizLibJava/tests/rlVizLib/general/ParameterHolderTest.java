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

package rlVizLib.general;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author btanner
 */
public class ParameterHolderTest {

    public ParameterHolderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testMain() {
    }

    @Test
    public void testIsParamSet() {
    }

    @Test
    public void testIsNull() {
    }

    @Test
    public void testSetAlias() {
    }

    @Test
    public void testAddStringParam_String_String() {
    }

    @Test
    public void testSetStringParam() {
        ParameterHolder P=new ParameterHolder();
        assertFalse(P.isParamSet("testString"));
        P.addStringParam("testString");
        assertTrue(P.isParamSet("testString"));
        
        String theValue=P.getStringParam("testString");
        assertEquals(theValue,null);
    }

    @Test
    public void testAddStringParam_String() {
    }

    @Test
    public void testAddIntegerParam_String() {
    }

    @Test
    public void testAddDoubleParam_String() {
    }

    @Test
    public void testAddDoubleParam_String_Double() {
    }

    @Test
    public void testSetDoubleParam() {
    }

    @Test
    public void testAddBooleanParam_String() {
    }

    @Test
    public void testAddBooleanParam_String_Boolean() {
    }

    @Test
    public void testSetBooleanParam() {
    }

    @Test
    public void testAddIntegerParam_String_Integer() {
    }

    @Test
    public void testSetIntegerParam() {
    }

    @Test
    public void testStringSerialize() {
    }

    @Test
    public void testGetStringParam() {
    }

    @Test
    public void testGetDoubleParam() {
    }

    @Test
    public void testGetBooleanParam() {
    }

    @Test
    public void testGetIntegerParam() {
    }

    @Test
    public void testSetParamByMagicFromString() {
    }

    @Test
    public void testGetParamAsString() {
    }

    @Test
    public void testGetParamTypeByName() {
    }

    @Test
    public void testToString() {
    }

    @Test
    public void testMakeTestParameterHolder() {
    }

    @Test
    public void testGetParamCount() {
    }

    @Test
    public void testGetParamType() {
    }

    @Test
    public void testGetParamName() {
    }

}