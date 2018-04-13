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
import java.net.URL;
import java.io.File;
import java.util.Vector;

import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.dataone.content.FileMapContent;
import org.cdlib.mrt.dataone.content.CreateContent;
import org.cdlib.mrt.dataone.content.ObjectFormatContent;
import org.cdlib.mrt.dataone.content.ResourceContent;
import org.cdlib.mrt.dataone.content.VersionContent;
import org.cdlib.mrt.dataone.content.SystemMetadataContent;
import org.cdlib.mrt.dataone.xml.ObjectFormatXML;
import org.cdlib.mrt.dataone.xml.SystemMetadataXML;
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
public class DataOneHandler
{
    private static final String NAME = "DataOneHandler";
    private static final String MESSAGE = NAME + ": ";
    private static final String PRODUCER = "producer/";
    protected VersionContent versionContent = null;
    protected LoggerInf logger = null;
    protected String memberNode = null;
    protected URL dataoneURL = null;
    protected URL objectURL = null;
    protected int versionID = -1;
    protected Identifier objectID = null;
    protected String defaultExtractDir = null;
    protected String defaultManifestName = null;
    protected String defaultERC = null;
    protected String owner = null;
    protected DataOneResource resource = null;
    protected ResourceManifest resourceManifest = null;
    protected File baseDir = null;
    protected ResourceMap resourceMap = null;
    protected String resourceManifestName = null;
    protected String outputResourceName = null;
    protected String outputResourcePid = null;
    
    public static DataOneHandler getDataOneHandler(
            File baseDir,
            String resourceManifestName,
            String memberNode,
            String owner,
            Identifier objectID,
            int versionID,
            String outputResourceName,
            URL dataoneURL,
            URL objectURL,
            LoggerInf logger
            )
        throws TException
    {
        return new DataOneHandler (
            baseDir,
            resourceManifestName,
            memberNode,
            owner,
            objectID,
            versionID,
            outputResourceName,
            dataoneURL,
            objectURL,
            "keep",
            null,
            null,
            "system/mrt-erc.txt",
            logger
            );
    }
  
    /**
     * 
     * @param baseDir directory file that is used as the base for resolving a fileID to FILE
     * in the Resource Manifest
     * @param resourceManifestName fileID of the ResourceManifest
     * @param memberNode "Merritt"
     * @param owner Ingest owner ARK
     * @param objectID ark assigned to object
     * @param versionID version id for specific entry
     * @param outputResourceName output fileID name within baseDir
     * @param dataoneURL DataONE server url used in creation of ORE resource map
     * @param objectURL Resource Map URL - get content URL for object
     * @param resourceAction action to take if ObjectFormat list does not match entry format:
     * keep-use as is, ignore-drop line from resource manifest, exception-throw exception
     * @param defaultExtractDir directory name within baseDir for automatic resource manifest creation
     * @param defaultManifestName name of default to be constructed within baseDir (i.e. system/mrt-man.txt)
     * @param defaultERC name of file if DEFAULT is used within Resource Manifest
     * @param logger debug logger
     * @throws TException process exception
     */   
    public static DataOneHandler getDataOneHandler(
            File baseDir,
            String resourceManifestName,
            String memberNode,
            String owner,
            Identifier objectID,
            int versionID,
            String outputResourceName,
            URL dataoneURL,
            URL objectURL,
            String resourceAction,
            String defaultExtractDir,
            String defaultManifestName,
            String defaultERC,
            LoggerInf logger
            )
        throws TException
    {
        return new DataOneHandler (
            baseDir,
            resourceManifestName,
            memberNode,
            owner,
            objectID,
            versionID,
            outputResourceName,
            dataoneURL,
            objectURL,
            resourceAction,
            defaultExtractDir,
            defaultManifestName,
            defaultERC,
            logger
            );
    }

 
    /**
     * 
     * @param baseDir directory file that is used as the base for resolving a fileID to FILE
     * in the Resource Manifest
     * @param resourceManifestName fileID of the ResourceManifest
     * @param memberNode "Merritt"
     * @param owner Ingest owner ARK
     * @param objectID ark assigned to object
     * @param versionID version id for specific entry
     * @param outputResourceName output fileID name within baseDir
     * @param dataoneURL DataONE server url used in creation of ORE resource map
     * @param objectURL Resource Map URL - get content URL for object
     * @param resourceAction action to take if ObjectFormat list does not match entry format:
     * keep-use as is, ignore-drop line from resource manifest, exception-throw exception
     * @param defaultExtractDir directory name within baseDir for automatic resource manifest creation
     * @param defaultManifestName name of default to be constructed within baseDir (i.e. system/mrt-man.txt)
     * @param defaultERC name of file if DEFAULT is used within Resource Manifest
     * @param logger debug logger
     * @throws TException process exception
     */
    protected DataOneHandler(
            File baseDir,
            String resourceManifestName,
            String memberNode,
            String owner,
            Identifier objectID,
            int versionID,
            String outputResourceName,
            URL dataoneURL,
            URL objectURL,
            String resourceAction,
            String defaultExtractDir,
            String defaultManifestName,
            String defaultERC,
            LoggerInf logger
            )
        throws TException
    {
        this.logger = logger;
        this.baseDir = baseDir;
        this.resourceManifestName = resourceManifestName;
        this.memberNode = memberNode;
        this.dataoneURL = dataoneURL;
        this.objectURL = objectURL;
        this.owner = owner;
        this.objectID = objectID;
        this.versionID = versionID;
        this.outputResourceName = outputResourceName;
        this.defaultExtractDir = defaultExtractDir;
        this.defaultManifestName = defaultManifestName;
        this.defaultERC = defaultERC;
        validate();
        initialize(resourceAction);
        addResource();
    }


    /**
     * The CreateContent objects contain the necessary content to issue a DataONE create.
     *
     * This content includes the pid, the systemmetadata and the data as a file reference
     * This routine should be sequentially called (inx) and processed until a null pointer is returned
     * @param inx sequential index of create content
     * @return not null - files to be created; null = end of list
     * @throws TException
     */
    public CreateContent getCreateContent(int inx)
        throws TException
    {
        String componentPid = null;
        File createFile = null;
        String createSystemMetadata = null;

        FileMapContent fileMap = getFileMap(inx);
        if (fileMap == null) return null;
        componentPid = fileMap.getComponentPid();
        createFile = fileMap.getFileComponent().getComponentFile();
        createSystemMetadata = getSystemMetadata(fileMap);
        return new CreateContent(componentPid, createFile, createSystemMetadata);
    }

    protected void validate()
        throws TException
    {
        try {
            notNull("owner", owner);
            notEmpty("resourceManifestName", resourceManifestName);
            notEmpty("outputResourceName", outputResourceName);
            notEmpty("defaultERC", defaultERC);
            notNull("logger", logger);
            notNull("baseDir", baseDir);
            notNull("dataoneURL", dataoneURL);
            notNull("objectID", objectID);
            if (!baseDir.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE
                        + "baseDir does not exist:" + baseDir.getCanonicalPath());
            }
            if (versionID < 0) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "Invalid versionID:" + versionID);
            }

        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            throw new TException(ex);
        }
    }

   protected void initialize(String resourceAction)
        throws TException
    {
        try {
            versionContent = VersionContent.getVersionContent(
                logger,
                objectID,
                versionID,
                baseDir);
            if (StringUtil.isNotEmpty(resourceAction)) {
                versionContent.setResourceFormatAction(resourceAction);
            }
            //dumpVersion(versionContent);
            resourceManifest = ResourceManifest.getResourceManifest(baseDir,
                    resourceManifestName, defaultExtractDir, defaultManifestName, logger);
            String returnManifestName = resourceManifest.getReturnManifestName();
            FileComponent component = versionContent.retrieveComponent(returnManifestName);
            File resourceManifestFile = null;
            if (component != null) {
                resourceManifestFile = component.getComponentFile();
            }
            resource = new DataOneResource(
                resourceManifestFile,
                versionContent,
                defaultERC,
                logger);
            String idName = outputResourceName;
            if (idName.startsWith("system/")) idName = idName.substring(7);
            outputResourcePid = DataONEUtil.getPid(objectID, versionID, idName);
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

    protected void addResource ()
        throws TException
    {
        Vector<ResourceContent> mapList = resource.getMapList();
        String dataoneURLS = dataoneURL.toString();
        String objectURLS = objectURL.toString();
        resourceMap  = ResourceMap.getResourceMap(
            dataoneURLS,
            objectURLS,
            objectID,
            versionID,
            outputResourcePid,
            mapList,
            logger);
    }

    public FileMapContent getFileMap(int inx)
        throws TException
    {
        if (inx < 0) return null;
        Vector<FileMapContent> entryList = resource.getEntryList();
        if (inx >= entryList.size()) return null;
        return entryList.get(inx);
    }

    public CreateContent getCreateContentResourceMap(
            String resourceFormat,
            String outputResourceName)
        throws TException
    {
        String componentPid = resourceMap.getResourcePid();
        File createFile = null;
        String createSystemMetadata = null;
        ObjectFormatContent objectFormat = null;
        try {
            ObjectFormatXML objectFormatXML = resource.getObjectFormatXML();
            objectFormat = objectFormatXML.getFormatDefault("RESOURCE", "http://www.openarchives.org/ore/terms");
            //FileMapContent fileMap = new FileMapContent();
            File outputResourceFile = new File(baseDir, outputResourceName);
            String resourceS = resourceMap.formatModel(resourceFormat);
            FileUtil.string2File(outputResourceFile, resourceS);
            FileComponent fileComponent = versionContent.retrieveComponent(outputResourceName);
            FileMapContent fileMap = new FileMapContent(outputResourceName, componentPid, objectFormat, fileComponent);

            componentPid = fileMap.getComponentPid();
            createFile = outputResourceFile;
            createSystemMetadata = getSystemMetadata(fileMap);
            return new CreateContent(componentPid, createFile, createSystemMetadata);

        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    protected String getSystemMetadata(FileMapContent fileMap)
        throws TException
    {
        File tmpFile = FileUtil.getTempFile("tmp", "tmp");
        try {
            SystemMetadataContent systemMetadataContent = new SystemMetadataContent(
                fileMap.getComponentPid(),
                owner,
                memberNode,
                fileMap.getObjectFormat(),
                fileMap.getFileComponent());


            SystemMetadataXML xml = new SystemMetadataXML(systemMetadataContent, tmpFile);
            xml.buildXML();
            return FileUtil.file2String(tmpFile);
            
        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);

        } finally {
            if (tmpFile != null) {
                try {
                    tmpFile.delete();
                } catch (Exception ex) { }
            }
        }
    }
    public String dumpEntry(int inx)
        throws TException
    {
        FileMapContent fileMap = getFileMap(inx);
        if (fileMap == null) return null;
        return fileMap.dump("FileMap dump[" + inx + "]");
    }


    public void dumpList()
        throws TException
    {
        System.out.println(MESSAGE + "List Entries ************************");
        for (int i=0; true; i++) {
            String dump = dumpEntry(i);
            if (dump == null) return;
            System.out.println("Entry[" + i + "]**************");
            System.out.println(dump);
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

    public ResourceMap getResourceMap() {
        return resourceMap;
    }
    
    public File getResourceManifestFile()
    {
        return resourceManifest.getResourceManifestFile();
    }
}
