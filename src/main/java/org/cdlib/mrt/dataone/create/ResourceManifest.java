/*
Copyright (c) 2005-2012, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 *
- Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
- Neither the name of the University of California nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
**********************************************************/
package org.cdlib.mrt.dataone.create;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.Vector;

import org.cdlib.mrt.dataone.utility.DataONEUtil;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
/**
 * This Class is instantiated for each object to be processed for DataONE create.
 * This object provides the needed content for issuing dataONE create requests.
 *
 * The processing is based on a ResourceManifest of the form:
 * science metadata fileid | science metadata dataONE format | science data fileid | science data dataONE format
 * ...
 * science metadata fileid - Merritt fileid of metadata as seen by submitter - no producer/xxx
 * science metadata dataONE format - metadata format
 * science data -  Merritt fileid of science data as seen by submitter - no producer/xxx
 * science data dataONE format - typically the mime type
 *
 * This resource manifest collectively is used for performing a DataONE create for each component and for construction of the
 * dataONE ORE resource map
 * @author dloy
 */
public class ResourceManifest
{
    private static final String NAME = "ResourceManifest";
    private static final String MESSAGE = NAME + ": ";
    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = false;
    
    
    //protected String PRODUCER = null;
    protected File resourceManifestFile = null;
    protected File baseDir = null;
    protected String defaultManifestName = null;
    protected String defaultExtractDir = null;
    protected String resourceManifestName = null;
    protected String returnManifestName = null;
    protected LoggerInf logger = null;
    
    protected Properties extList = null;
    
    
    public static ResourceManifest getResourceManifest(
            File baseDir,
            String resourceManifestName,
            String defaultExtractDir,
            String defaultManifestName,
            LoggerInf logger
            )
        throws TException
    {
        return new ResourceManifest (
            baseDir,
            resourceManifestName,
            defaultExtractDir,
            defaultManifestName,
            logger
            );
    }
    
    public File getLocalResourceManifest(
            String resourceManifestName)
    {
        try {
            File resourceManifest = new File(baseDir, resourceManifestName);
            if (DEBUG) System.out.println("***search resourceManifest=" + resourceManifest.getCanonicalPath());
            if (!resourceManifest.exists()) return null;
            return resourceManifest;
        } catch (Exception ex) {
            return null;
        }
    }
    
    /**
     * Handle the processing of a dataONE object
     * @param baseDir directory file that is used as the base for resolving a fileID to FILE
     * in the Resource Manifest
     * @param resourceManifestName fileID of the ResourceManifest
     * @param logger
     * @throws TException process exception
     */
    protected ResourceManifest(
            File baseDir,
            String resourceManifestName,
            String defaultExtractDir,
            String defaultManifestName,
            LoggerInf logger
            )
        throws TException
    {
        this.logger = logger;
        this.baseDir = baseDir;
        this.resourceManifestName = resourceManifestName;
        this.defaultExtractDir = defaultExtractDir;
        this.defaultManifestName = defaultManifestName;
        validate();
        initialize();
        buildDefaultResourceManifest();
    }
    
    protected void validate()
        throws TException
    {
        try {
            notNull("baseDir", baseDir);
            notEmpty("resourceManifestName", resourceManifestName);
            notNull("logger", logger);
            if (!baseDir.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE
                        + "baseDir does not exist:" + baseDir.getCanonicalPath());
            }

        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            throw new TException(ex);
        }
    }

   protected void initialize()
        throws TException
    {
        try {
            File localBase = new File(baseDir, resourceManifestName);
            if (DEBUG) System.out.println("***initialize:" + localBase.getCanonicalPath());
            
            resourceManifestFile = getLocalResourceManifest(resourceManifestName);
            if (resourceManifestFile != null) {
                returnManifestName = resourceManifestName;
                return;
            }
            if (StringUtil.isEmpty(defaultExtractDir)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE 
                        + "user resource manifest not found and default extract directory name not supplied"
                        + " - user resource manifest:" + resourceManifestFile.getCanonicalPath());
            }
            if (StringUtil.isEmpty(defaultManifestName)) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE 
                        + "user resource manifest not found and default manifest name not supplied"
                        + " - user resource manifest:" + resourceManifestFile.getCanonicalPath());
            }
            extList = getExtensionList();
            
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

    /**
     * Input the formatTypes.xml and build a Document
     * @throws TException
     */
    protected Properties getExtensionList()
        throws TException
    {
        InputStream inStream = null;
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            inStream = classLoader.getResourceAsStream("resources/extToMime.properties");
            Properties prop = new Properties();
            prop.load(inStream);
            return prop;

        } catch (Exception ex) {
            System.out.println("ObjectFormatXML: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (Exception e) { }
            }
        }
    }
    
   protected void buildDefaultResourceManifest()
        throws TException
    {
        
        String header = "#%dataonem_0.1" + NL
            + "#%profile | http://uc3.cdlib.org/registry/ingest/manifest/mrt-dataone-manifest" + NL
            + "#%prefix  | dom: | http://uc3.cdlib.org/ontology/dataonem#" + NL
            + "#%prefix  | mrt: | http://uc3.cdlib.org/ontology/mom#" + NL
            + "#%fields  | dom:scienceMetadataFile  | dom:scienceMetadatFormat | mrt:mimeType" + NL;

                    
        if (resourceManifestFile != null) return;
        OutputStreamWriter osw = null;
        OutputStream outputStream = null;
        try {
            File outFile = new File(baseDir, defaultManifestName);
            if (DEBUG) System.out.println("***builDefaultResourcManifest file=" + outFile.getCanonicalPath());
            returnManifestName = defaultManifestName;
            outputStream = new FileOutputStream(outFile);
            osw = new OutputStreamWriter(outputStream, "utf-8");
            String defLine = "DEFAULT | ERC | ";
            String defExt = "unknown";
            File dataDir = new File(baseDir, defaultExtractDir);
            if (!dataDir.exists()) {
                throw new TException.REQUESTED_ITEM_NOT_FOUND(MESSAGE + "Directory not found:" + dataDir.getCanonicalPath());
            }
            Vector<File> files = new Vector<File>(1000);
            FileUtil.getDirectoryFiles(dataDir, files);
            if (files.size() == 0) {
                throw new TException.REQUESTED_ITEM_NOT_FOUND(MESSAGE + "No items found for:"
                        + dataDir.getCanonicalPath());
            }
            osw.write(header);
            for (File file : files) {
                String dataFileID = DataONEUtil.getFileID(dataDir, file);
                if (DEBUG) System.out.println("dataFileID=" + dataFileID);
                String ext = DataONEUtil.getFileExt(dataFileID);
                if (DEBUG) System.out.println("ext=" + ext);
                if (ext == null) ext = defExt;
                String mime = extList.getProperty(ext);
                if (DEBUG) System.out.println("mime=" + mime);
                String line = defLine + dataFileID + " | " + mime + NL;
                osw.write(line);
            }
            String eof = "#%eof" + NL;
            osw.write(eof);
            resourceManifestFile = outFile;
            
        } catch (TException tex) {
            System.out.println("Exception:" + tex);
            tex.printStackTrace();
            throw tex;

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
            
        } finally {
            try {
                osw.close();
                
            } catch (Exception ex) { }
            
            try {
                outputStream.close();
                
            } catch (Exception ex) { }
        }
    }


    public String dump(String header)
        throws TException
    {
        try {
            
            StringBuffer buf = new StringBuffer(header + NL);
            String msg = ""
                    + " - baseDir=" + baseDir.getCanonicalPath()
                    + " - resourceManifestName=" + resourceManifestName
                    + " - resourceManifestFile=" + resourceManifestFile.getCanonicalPath()
                    + NL;
            buf.append(msg);
            String dumpFile = "";
            if (resourceManifestFile != null) {
                dumpFile = "ResourceManifest:" + NL
                    + FileUtil.file2String(resourceManifestFile) + NL;
            }
            buf.append(dumpFile);
            return buf.toString();
            
        } catch (Exception ex) {
            throw new TException(ex);
        }
    }

    private void notNull(String name, Object object)
        throws TException
    {
        DataONEUtil.notNull(name, object);
    }

    private void notEmpty(String name, String object)
        throws TException
    {
        DataONEUtil.notEmpty(name, object);
    }

    public File getResourceManifestFile() {
        return resourceManifestFile;
    }

    public String getReturnManifestName() {
        return returnManifestName;
    }
    
    
}
