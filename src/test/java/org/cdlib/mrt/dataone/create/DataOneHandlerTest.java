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
public class DataOneHandlerTest {
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

    public DataOneHandlerTest() {
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
    public void testDataOneHandlerDefault()
    {
        try {
            logger = new TFileLogger("testDataOneHandler", 10, 10);
            Identifier objectID = new Identifier(OBJECTIDS);
            File baseDir = new File(BASEDIR);
            URL dataoneURL = new URL(DATAONEURL);
            URL objectURL = new URL(OBJECTURL);
            
            dumpResourceManifest("testDataOneHandlerDefault", baseDir, RESOURCEMANIFESTNAMEBAD);
            handler = DataOneHandler.getDataOneHandler(
                baseDir,
                RESOURCEMANIFESTNAME,
                MEMBERNODE,
                OWNER,
                objectID,
                VERSIONID,
                OUTPUTRESOURCENAME,
                dataoneURL,
                objectURL,
                logger
                );
            
            ResourceMap resourceMap = handler.getResourceMap();
            String output = resourceMap.formatModel(OUTFORMAT);
            if (LVL >= 10) System.out.println("RESOURCE=" + NL + output);
            if (LVL >= 10) handler.dumpList();

            if (LVL >= 5) dumpCreateContent(handler);
            CreateContent oreMap = handler.getCreateContentResourceMap(OUTFORMAT, "system/mrt-dataone-map.rdf");
            if (LVL >= 5) System.out.println("*********CreateContentResourceMap*******");
            if (LVL >= 5) System.out.println(oreMap.dump("TEST"));
            String map = FileUtil.file2String(oreMap.getCreateFile());
            if (LVL >= 5) System.out.println("CONTENT RESOURCE=" + NL + map);


            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);

        } 
    }

    //@Test
    public void testDataOneHandlerMissing()
    {
        try {
            logger = new TFileLogger("testDataOneHandlerIgnore", 10, 10);
            Identifier objectID = new Identifier(OBJECTIDS);
            File baseDir = new File(BASEDIR);
            URL dataoneURL = new URL(DATAONEURL);
            URL objectURL = new URL(OBJECTURL);

            dumpResourceManifest("testDataOneHandlerDefault input", baseDir, "xxx-missing.txt");            
            handler = DataOneHandler.getDataOneHandler(
                baseDir,
                "xxx-missing.txt",
                MEMBERNODE,
                OWNER,
                objectID,
                VERSIONID,
                OUTPUTRESOURCENAME,
                dataoneURL,
                objectURL,
                "keep",
                "producer",
                "system/myrm.txt",
                "system/mrt-erc.txt",
                logger
                );
            File manifest = handler.getResourceManifestFile();
            dumpResourceManifest("testDataOneHandlerDefault default", manifest);      
            
            ResourceMap resourceMap = handler.getResourceMap();
            String output = resourceMap.formatModel(OUTFORMAT);
            if (LVL >= 10) System.out.println("RESOURCE=" + NL + output);
            if (LVL >= 10) handler.dumpList();

            if (LVL >= 5) dumpCreateContent(handler);
            CreateContent oreMap = handler.getCreateContentResourceMap(OUTFORMAT, "system/mrt-dataone-map.rdf");
            if (LVL >= 5) System.out.println("*********CreateContentResourceMap*******");
            if (LVL >= 5) System.out.println(oreMap.dump("TEST"));
            String map = FileUtil.file2String(oreMap.getCreateFile());
            if (LVL >= 5) System.out.println("CONTENT RESOURCE=" + NL + map);


            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);

        } 
    }

    //@Test
    public void testDataOneHandlerKeep()
    {
        try {
            logger = new TFileLogger("testDataOneHandlerKeep", 10, 10);
            Identifier objectID = new Identifier(OBJECTIDS);
            File baseDir = new File(BASEDIR);
            URL dataoneURL = new URL(DATAONEURL);
            URL objectURL = new URL(OBJECTURL);
            dumpResourceManifest("testDataOneHandlerKeep", baseDir, RESOURCEMANIFESTNAMEBAD);
            handler = DataOneHandler.getDataOneHandler(
                baseDir,
                RESOURCEMANIFESTNAMEBAD,
                MEMBERNODE,
                OWNER,
                objectID,
                VERSIONID,
                OUTPUTRESOURCENAME,
                dataoneURL,
                objectURL,
                "keep",
                null,
                null,
                "system/mrt-erc.txt",
                logger
                );
            
            ResourceMap resourceMap = handler.getResourceMap();
            String output = resourceMap.formatModel(OUTFORMAT);
            if (LVL >= 10) System.out.println("RESOURCE=" + NL + output);
            if (LVL >= 10) handler.dumpList();

            if (LVL >= 5) dumpCreateContent(handler);
            CreateContent oreMap = handler.getCreateContentResourceMap(OUTFORMAT, "system/mrt-dataone-map.rdf");
            if (LVL >= 5) System.out.println("*********CreateContentResourceMap*******");
            if (LVL >= 5) System.out.println(oreMap.dump("TEST"));
            String map = FileUtil.file2String(oreMap.getCreateFile());
            if (LVL >= 5) System.out.println("CONTENT RESOURCE=" + NL + map);


            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);

        } 
    }

    //@Test
    public void testDataOneHandlerIgnore()
    {
        try {
            logger = new TFileLogger("testDataOneHandlerIgnore", 10, 10);
            Identifier objectID = new Identifier(OBJECTIDS);
            File baseDir = new File(BASEDIR);
            URL dataoneURL = new URL(DATAONEURL);
            URL objectURL = new URL(OBJECTURL);
            
            dumpResourceManifest("testDataOneHandlerKeep", baseDir, RESOURCEMANIFESTNAMEBAD);
            handler = DataOneHandler.getDataOneHandler(
                baseDir,
                RESOURCEMANIFESTNAMEBAD,
                MEMBERNODE,
                OWNER,
                objectID,
                VERSIONID,
                OUTPUTRESOURCENAME,
                dataoneURL,
                objectURL,
                "ignore",
                null,
                null,
                "system/mrt-erc.txt",
                logger
                );
            
            ResourceMap resourceMap = handler.getResourceMap();
            String output = resourceMap.formatModel(OUTFORMAT);
            if (LVL >= 10) System.out.println("RESOURCE=" + NL + output);
            if (LVL >= 10) handler.dumpList();

            if (LVL >= 5) dumpCreateContent(handler);
            CreateContent oreMap = handler.getCreateContentResourceMap(OUTFORMAT, "system/mrt-dataone-map.rdf");
            if (LVL >= 5) System.out.println("*********CreateContentResourceMap*******");
            if (LVL >= 5) System.out.println(oreMap.dump("TEST"));
            String map = FileUtil.file2String(oreMap.getCreateFile());
            if (LVL >= 5) System.out.println("CONTENT RESOURCE=" + NL + map);


            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);

        } 
    }

    //@Test
    public void testDataOneHandlerException()
    {
        try {
            logger = new TFileLogger("testDataOneHandlerIgnore", 10, 10);
            Identifier objectID = new Identifier(OBJECTIDS);
            File baseDir = new File(BASEDIR);
            URL dataoneURL = new URL(DATAONEURL);
            URL objectURL = new URL(OBJECTURL);
            
            handler = DataOneHandler.getDataOneHandler(
                baseDir,
                RESOURCEMANIFESTNAME,
                MEMBERNODE,
                OWNER,
                objectID,
                VERSIONID,
                OUTPUTRESOURCENAME,
                dataoneURL,
                objectURL,
                "ignore",
                null,
                null,
                "system/mrt-erc.txt",
                logger
                );
            
            ResourceMap resourceMap = handler.getResourceMap();
            String output = resourceMap.formatModel(OUTFORMAT);
            if (LVL >= 10) System.out.println("RESOURCE=" + NL + output);
            if (LVL >= 10) handler.dumpList();

            if (LVL >= 5) dumpCreateContent(handler);
            CreateContent oreMap = handler.getCreateContentResourceMap(OUTFORMAT, "system/mrt-dataone-map.rdf");
            if (LVL >= 5) System.out.println("*********CreateContentResourceMap*******");
            if (LVL >= 5) System.out.println(oreMap.dump("TEST"));
            String map = FileUtil.file2String(oreMap.getCreateFile());
            if (LVL >= 5) System.out.println("CONTENT RESOURCE=" + NL + map);


            assertTrue(true);

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            assertFalse("Exception:" + ex, true);

        } 
    }


    public static void dumpCreateContent(DataOneHandler handler)
        throws TException
    {
        System.out.println("dumpCreateContent*************" + NL);
        for (int i=0; true; i++) {
            CreateContent createContent = handler.getCreateContent(i);
            if (createContent == null) break;
            System.out.println("*********CreateContent[" + i + "]*******");
            System.out.println(createContent.dump("TEST"));
        }
    }

    public static void dumpVersion(VersionContent versionContent)
    {
        System.out.println("DUMP VersionContent entered");
        System.out.println(versionContent.dumpVersion("DUMP VersionContent"));
    }

    private static void dumpResourceManifest(String header, File baseID, String fileID)
    {
        try {
            File manifestFile = new File(baseID, fileID);
            dumpResourceManifest(header, manifestFile);
            
        } catch (Exception ex) {
            
        }
    }

    private static void dumpResourceManifest(String header, File manifestFile)
    {
        System.out.println("##### Resource Manifest dump:" + header);
        try {
            if (!manifestFile.exists()) {
                System.out.println("Manifest doe not exist:" + manifestFile.getCanonicalPath());
            } else {
                String dispManifest = FileUtil.file2String(manifestFile);
                System.out.println("# Manifest:" + NL + dispManifest);
            }
        } catch (Exception ex) {
            System.out.println("# Exception:" + ex);
        } finally {
            System.out.println("##### End Resource Manifest dump");
        }
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