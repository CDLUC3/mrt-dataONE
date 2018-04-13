/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.dataone.create;
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
public class DataOneResourcetTest {
    private static final String OBJECTIDS = "ark:/20775/bb7031834m";
    private static final int VERSIONID = 3;
    private static final String MANIFESTFILE = "C:/Documents and Settings/dloy/My Documents/CDL6/tomcat_base/webapps/test/d1/add-manifest.txt";
    private static final String BASEDIR = "C:/Documents and Settings/dloy/My Documents/CDL6/tomcat_base/webapps/test/d1/full";
    public DataOneResourcetTest() {
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
    public void defaultRun()
    {
       assertTrue(true);
    }

    //@Test
    public void testDataOneResource()
    {
        try {
            LoggerInf logger = new TFileLogger("testDataOneResource", 10, 10);
            Identifier objectID = new Identifier(OBJECTIDS);
            File baseDir = new File(BASEDIR);
            DataOneResource resource = getResource(objectID, VERSIONID, baseDir, logger);
            dump(resource);
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);

        } 
    }

    protected DataOneResource getResource(
            Identifier objectID,
            int versionID,
            File baseDir,
            LoggerInf logger)
        throws TException
    {
        try {
            VersionContent versionContent = VersionContent.getVersionContent(
                logger,
                objectID,
                versionID,
                baseDir);
            dumpVersion(versionContent);
            FileComponent component = versionContent.retrieveComponent("producer/mrt-d1rmm.txt");
            File resourceManifestFile = null;
            if (component != null) {
                resourceManifestFile = component.getComponentFile();
            }
            DataOneResource resource = new DataOneResource(
                resourceManifestFile,
                versionContent,
                null,
                logger);
            return resource;

        } catch (TException tex) {
            System.out.println("Exception:" + tex);
            tex.printStackTrace();
            throw tex;

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }


    public static void dumpVersion(VersionContent versionContent)
    {
        System.out.println("DUMP VersionContent entered");
        System.out.println(versionContent.dumpVersion("DUMP VersionContent"));
    }

    public static void dump(DataOneResource resource)
    {
        System.out.println("DUMP entered");
        Vector<ResourceContent> mapList = null;
        try {

            mapList = resource.getMapList();
            System.out.println("mapList size=" + mapList.size());
            for (ResourceContent entry: mapList) {
                System.out.println(entry.dump("test"));
            }

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
        }
    }
}