/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cdlib.mrt.dataone.create;
import java.net.URL;
import java.util.Vector;

import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.cdlib.mrt.dataone.content.*;
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
public class ResourceManifestTest {
    private static final String MEMBERNODE = "Merritt";
    private static final String OBJECTIDS = "ark:/20775/bb7031834m";
    private static final int VERSIONID = 0;

    private static final String OWNER = "TheOwner";
    private static final String MANIFESTFILE = "C:/Documents and Settings/dloy/My Documents/CDL6/tomcat_base/webapps/test/d1/add-manifest.txt";
    private static final String BASEDIR = "C:/Documents and Settings/dloy/My Documents/CDL6/tomcat_base/webapps/test/d1/full";
    private static final String DATAONEURL = "https://cn.dataone.org/object/";
    private static final String OBJECTURL = "http://store.cdlib.org/content/1/someark";
    private static final String OUTFORMAT = "RDF/XML";
    private static final String RESOURCEMANIFESTNAME = "producer/mrt-d1rmm.txt";
    private static final String RESOURCEMANIFESTNAMEBAD = "producer/mrt-d1bad.txt";
    private static final String OUTPUTRESOURCENAME = "system/mrt-dataone-map.rdf";
    private static final String NL = System.getProperty("line.separator");
    private static final int LVL = 5;

    protected DataOneHandler handler = null;
    protected LoggerInf logger = null;

    public ResourceManifestTest() {
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
    public void testResourceExists()
    {
        try {
            logger = new TFileLogger("testDataOneHandler", 10, 10);
            File baseDir = new File(BASEDIR);
            

            ResourceManifest resourceManifest = ResourceManifest.getResourceManifest(
                baseDir,
                RESOURCEMANIFESTNAME,
                null,
                null,
                logger
                );
            System.out.println(resourceManifest.dump("USER EXISTS"));
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);

        } 
    }

    //@Test
    public void testResourceBuild()
    {
        try {
            logger = new TFileLogger("testDataOneHandler", 10, 10);
            File baseDir = new File(BASEDIR);
            

            ResourceManifest resourceManifest = ResourceManifest.getResourceManifest(
                baseDir,
                "producer/xxx.txt",
                //File resourceManifestFile,
                "producer",
                "system/newman.txt",
                logger
                );
            System.out.println(resourceManifest.dump("USER EXISTS"));
            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);

        } 
    }

}