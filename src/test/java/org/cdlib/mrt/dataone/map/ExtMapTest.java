/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.dataone.map;

import org.cdlib.mrt.dataone.content.*;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.dataone.xml.SystemMetadataXML;
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
public class ExtMapTest {

    private static final String NL = System.getProperty("line.separator");
    public ExtMapTest() {
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
    public void testSystemMetadataBuild()
    {
       ExtMap map = new ExtMap();
       test(map, "abcef.csv", "text/xml", "text/csv");
       test(map, "abcef.xml", "text/xml", "text/xml");
       test(map, "abcef.xls", "text/xml", "application/vnd.ms-excel");
       test(map, "abcef.xlsx", "text/xml", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
       test(map, null, null, null);
    }
    
    public void test(ExtMap map, String name, String mime, String match)
    {
        String newMime = map.getExt(name, mime);
        System.out.println("test "
                + " - name=" + name
                + " - mime=" + mime
                + " - newMime=" + "\"" + newMime + "\""
                + " - match=" + "\"" + match + "\""
                );
        if ((name == null) || (mime == null)) {
            assertTrue(newMime == match);
            return;
        }
        assertTrue(newMime.equals(match));
        assertTrue(true);
    }

}