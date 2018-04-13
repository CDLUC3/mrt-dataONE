/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.dataone.content;

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
public class SystemMetadataContentTest {

    private static final String NL = System.getProperty("line.separator");
    public SystemMetadataContentTest() {
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
        //https://confluence.ucop.edu/download/attachments/20250670/testG23.xls | sha256 | be18adbda20306f18bc2c725e3c3e9efe6fb3d5da933ba7c62184a4f3c373820 | 21504 | - | fptr105.xls
        File tmpFile = null;
        try {
            tmpFile = FileUtil.getTempFile("tmp", "txt");
            FileComponent component = new FileComponent();
            component.addMessageDigest("be18adbda20306f18bc2c725e3c3e9efe6fb3d5da933ba7c62184a4f3c373820", "SHA-256");
            component.setSize("21504");
            component.setCreated();
            ObjectFormatContent content = new ObjectFormatContent("thisIsFMTID","thisIsFMTName","METADATA");
            SystemMetadataContent systemMetadataContent
                    = new SystemMetadataContent(
                        "ark:/13030/something",
                        "OwnerIsMe",
                        "memberNodeIsUs",
                        content,
                        component);
            System.out.println(systemMetadataContent.dump("TestSystemMetadataBuild"));

            SystemMetadataXML xml = new SystemMetadataXML(systemMetadataContent, tmpFile);
            xml.buildXML();
            System.out.println("SystemMetadataContentTest XML=" + NL + FileUtil.file2String(tmpFile));
            assertTrue(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);

        } finally {
            if (tmpFile != null) {
                try {
                    tmpFile.delete();
                } catch (Exception ex) {}
            }
        }
    }

}