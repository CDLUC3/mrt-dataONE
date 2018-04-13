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
*********************************************************************/
package org.cdlib.mrt.dataone.content;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Set;

//import org.cdlib.mrt.core.*;
import org.cdlib.mrt.core.ComponentContent;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.Manifest;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.FixityTests;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StateInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
/**
 * This class contains a list of all component information used by the DataONE interface
 * @author dloy
 */
public class VersionContent
        //extends ComponentContent
        implements StateInf, Serializable
{

    private static final String NAME = "VersionContent";
    private static final String MESSAGE = NAME + ": ";
    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = false;

    public enum ResourceFormatAction {exception, ignore, keep};
    
    protected ResourceFormatAction resourceFormatAction = ResourceFormatAction.keep;
    protected LinkedHashMap<String, FileComponent> componentTable = new LinkedHashMap<String, FileComponent>(200);
    protected int versionID = -1;
    protected Identifier objectID = null;
    protected ComponentContent componentContent = null;
    protected LoggerInf logger = null;
    protected File baseDir = null;

    /**
     * From a post manifest InputStream
     * Return a VersionContent object
     * @param logger process logger
     * @param manifest post type manifest
     * @param manifestInputStream InputStream to manifest
     * @return VersionContent object defining a file content data
     * @throws TException process excepton
     */
    public static VersionContent getVersionContent(
            LoggerInf logger,
            Identifier objectID,
            int versionID,
            File baseDir,
            Manifest manifest,
            InputStream manifestInputStream)
        throws TException
    {
        VersionContent retVersionContent = new VersionContent(logger, objectID, versionID, baseDir, manifest, manifestInputStream);
        return retVersionContent;
    }

    /**
     * This form of the VersionContent does not build a component list from a manifest. This version
     * assumes that retrieveComponent will be used to input all component information
     * into the object.
     *
     * @param logger - Merritt log
     * @param objectID object identifier
     * @param versionID version identifier
     * @param baseDir base directory. This File shold be at the level that the fileIDs in the
     * components can properly resolve.
     * @return a new empty VersionContent
     * @throws TException
     */
    public static VersionContent getVersionContent(
            LoggerInf logger,
            Identifier objectID,
            int versionID,
            File baseDir)
        throws TException
    {
        VersionContent retVersionContent = new VersionContent(logger, objectID, versionID, baseDir);
        return retVersionContent;
    }


    /**
     * This form of the VersionContent uses a storage add manifest to add component content.
     * The componentFile for the FileComponents is not set untuil an addComponentFiles() call
     * is made.
     * @param logger - Merritt log
     * @param objectID object identifier
     * @param versionID version identifier
     * @param baseDir base directory. This File shold be at the level that the fileIDs in the
     * components can properly resolve.
     * @param manifest A manifest object set up for processing a storage add
     * @param manifestInputStream file input stream for manifest file
     * @return a partial VersionContent - component Files not set
     * @throws TException
     */
    public VersionContent(
            LoggerInf logger,
            Identifier objectID,
            int versionID,
            File baseDir,
            Manifest manifest,
            InputStream manifestInputStream)
        throws TException
    {
        this.logger = logger;
        this.objectID = objectID;
        this.versionID = versionID;
        this.baseDir = baseDir;
        componentContent = new ComponentContent(logger, manifest, manifestInputStream);
        componentTable = componentContent.getFileComponentTable();
        addComponentFiles();
    }

    public VersionContent(
            LoggerInf logger,
            Identifier objectID,
            int versionID,
            File baseDir)
        throws TException
    {
        this.logger = logger;
        this.objectID = objectID;
        this.versionID = versionID;
        this.baseDir = baseDir;
    }

    public LinkedHashMap<String, FileComponent> getVersionTable()
    {
        return componentTable;
    }

    public Identifier getObjectID() {
        return objectID;
    }

    public void setObjectID(Identifier objectID) {
        this.objectID = objectID;
    }

    public int getVersionID() {
        return versionID;
    }

    public void setVersionID(int versionID) {
        this.versionID = versionID;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    public void addComponentFiles()
        throws TException
    {
        try {
            if (baseDir == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "setFile - missing baseDir");
            }
            if (!baseDir.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "setFile - baseDir does not exists:" + baseDir.getCanonicalPath());
            }
            for (String key : componentTable.keySet()) {
                FileComponent component = componentTable.get(key);
                String id = component.getIdentifier();
                File compFile = new File(baseDir, id);
                if (!compFile.exists()) {
                    FileUtil.url2File(logger, component.getURL().toString(), compFile);
                }
                component.setComponentFile(compFile);
            }

        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException(ex);
        }
    }

    /**
     * This method may be used to load the VersionContent one component at a time.
     * The method uses the baseDir to build the component information - primarily fixity and date
     * @param fileID file identifier (relative to baseDir) to be added
     * @return the extracted FileComponent
     * @throws TException
     */
    public FileComponent retrieveComponent(String fileID)
        throws TException
    {
        try {
            FileComponent fileComponent = componentTable.get(fileID);
            if (fileComponent != null) return fileComponent;
            File componentFile = new File(baseDir, fileID);
            if (!componentFile.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "component file not found:"
                    + " - componentFile=" + componentFile.getCanonicalPath()
                    );
            }
            fileComponent = addFile(fileID, componentFile, "SHA-1");
            componentTable.put(fileID, fileComponent);
            return fileComponent;

        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException(ex);
        }

    }


    /**
     * build the fixity info for a specific file
     * @param fileID file identifier (relative to baseDir) to be added
     * @param file existing file to have fixity extracted
     * @param checksumType digest method used for creation of fixity info
     * @return Constructed file component information
     * @throws TException
     */
    public FileComponent addFile(String fileID, File file, String checksumType)
        throws TException
    {
        try {
            FileComponent fileComponent = new FileComponent();
            fileComponent.setIdentifier(fileID);
            FixityTests fixity = new FixityTests(file, checksumType, logger);
            fileComponent.addMessageDigest(fixity.getChecksum(), fixity.getChecksumType().toString());
            fileComponent.setSize(file.length());
            fileComponent.setComponentFile(file);
            fileComponent.setCreated();
            if (DEBUG) {
                System.out.println(fileComponent.dump("AddFile"));
            }
            return fileComponent;

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION("FileContent - addFile - Exception:" + ex);
        }

    }

    public ResourceFormatAction getResourceFormatAction() {
        return resourceFormatAction;
    }

    public void setResourceFormatAction(ResourceFormatAction resourceFormatAction) {
        this.resourceFormatAction = resourceFormatAction;
    }

    public void setResourceFormatAction(String resourceFormatActionS) 
    {
        if (StringUtil.isEmpty(resourceFormatActionS)) return;
        resourceFormatActionS = resourceFormatActionS.toLowerCase();
        ResourceFormatAction formatActionLocal = ResourceFormatAction.valueOf(resourceFormatActionS);
        this.resourceFormatAction = formatActionLocal;
    }

    public String dumpVersion(String header)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(MESSAGE + header + NL);

        try {
            Set<String> keys = componentTable.keySet();
            buf.append("componentList size=" + keys.size() + NL);
            for (String key : keys) {
                FileComponent entry = componentTable.get(key);
                buf.append(entry.dump(entry.getIdentifier()) + NL);
            }
            return buf.toString();

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            return null;
        }
    }
}
