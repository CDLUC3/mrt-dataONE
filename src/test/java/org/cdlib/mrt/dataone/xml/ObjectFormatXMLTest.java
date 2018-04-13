/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.dataone.xml;
import org.cdlib.mrt.dataone.create.*;
import java.io.FileInputStream;
import java.util.Vector;
import java.util.Hashtable;

import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.Manifest;
import org.cdlib.mrt.core.ManifestRowAbs;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.dataone.content.*;
import org.cdlib.mrt.core.FileComponent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;


import java.io.File;

/**
 *
 * @author dloy
 */
public class ObjectFormatXMLTest {
    public ObjectFormatXMLTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }



    @Test
    public void testObjectFormatXML()
    {
        try {
            ObjectFormatXML objectFormat = new ObjectFormatXML();
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);

        } 
    }
}