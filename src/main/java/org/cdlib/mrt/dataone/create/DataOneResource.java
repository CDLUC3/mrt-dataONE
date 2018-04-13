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
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.dataone.content.FileMapContent;
import org.cdlib.mrt.dataone.content.ObjectFormatContent;
import org.cdlib.mrt.dataone.content.VersionContent;
import org.cdlib.mrt.dataone.content.ResourceContent;
import org.cdlib.mrt.dataone.map.ExtMap;
import org.cdlib.mrt.dataone.utility.DataONEUtil;
import org.cdlib.mrt.dataone.xml.ObjectFormatXML;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.StringUtil;

/**
 * this class is used for the basic processing of the Resource Manifest.
 *
 * There are two major functions provided by the Resource manifest:
 * 1. Content information for constructing the ORE Resource Map used by DataONE
 * mapList provides this content
 * 2. Identification of each file to be sent on to DataONE with identification of
 * the DataONE format for that content.
 * resourceTable provides this concontent.
 *
 * @author dloy
 */
public class DataOneResource
{
    private static final String NAME = "DataOneResource";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = false;
    private static final String PREFIX = "producer/";

    protected File resourceManifestFile = null;
    protected BufferedReader resourceBR = null;
    protected VersionContent versionContent = null;
    protected Vector<ResourceContent> mapList = new Vector<ResourceContent>();
    protected Hashtable<String, FileMapContent> resourceTable = new Hashtable<String, FileMapContent>(100);
    protected ObjectFormatXML objectFormatXML = null;
    protected String defaultERC = null;
    protected LoggerInf logger = null;
    protected ExtMap extMap = new ExtMap();

    public DataOneResource(
            File resourceManifestFile,
            VersionContent versionContent,
            String defaultERC,
            LoggerInf logger)
        throws TException
    {
        this.resourceManifestFile = resourceManifestFile;
        this.versionContent = versionContent;
        this.defaultERC = defaultERC;
        this.logger = logger;
        validate();
        buildResourceTable();
        addResourceTable();
        if (DEBUG) System.out.println("****MapList size=" + mapList.size());
        if (DEBUG) System.out.println("****resourceTable size=" + resourceTable.size());
    }
    
    protected void validate()
        throws TException
    {
        try {
            if (resourceManifestFile == null) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "resourceManifestFile not provided");
            }
            if (!resourceManifestFile.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "resourceManifestFile does not exist:"
                        + resourceManifestFile.getCanonicalPath());
            }
            if (StringUtil.isEmpty(defaultERC)) {
                defaultERC = "system/mrt-erc.txt";
            }
            buildReaderFile();
            objectFormatXML = new ObjectFormatXML();

        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
        }
    }

    protected void buildResourceTable()
        throws TException
    {
        try {
            while(true) {
                String line = resourceBR.readLine();
                if (line == null) break;
                addLine(line);
                if (DEBUG) System.out.println("add line:" + line);

            }

        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
        }
    }

    protected void addLine(String line)
        throws TException
    {
        try {
            if (StringUtil.isEmpty(line)) return;
            if (line.startsWith("#")) return;
            String [] parts = line.split("\\s*\\|\\s*");
            if (DEBUG) {
                log("addLine:" + line + " - parts.length=" + parts.length);
                for (int i=0; i<parts.length; i++) {
                    log("part[" + i + "]:" + parts[i]);
                }
            }
            if (parts.length < 2) return;
            ResourceContent entry = new ResourceContent();
            entry.scienceMetadataID = parts[0];
            entry.scienceMetadataType = parts[1];
            if (parts.length == 4) {
                entry.scienceDataID = parts[2];
                entry.scienceDataType = parts[3];
                entry.scienceDataType = extMap.getExt(entry.scienceDataID, entry.scienceDataType);
            }
            fillEntry(entry);
            if (addEntry(entry)) mapList.add(entry);
            if (DEBUG) System.out.println(entry.dump("***ENTRY***")); //!!!!

        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException(ex);
        }
    }
    
    protected boolean addEntry(ResourceContent entry)
        throws TException
    {
        VersionContent.ResourceFormatAction action = versionContent.getResourceFormatAction();
        if (DEBUG) System.out.println(MESSAGE + "addEntry action=" + action.toString());
        if (action == VersionContent.ResourceFormatAction.keep) return true;
        ObjectFormatContent scienceFormat = entry.scienceDataFormat;
        ObjectFormatContent metadataFormat = entry.scienceMetadataFormat;
        boolean specified = true;
        if (scienceFormat != null) specified = specified & scienceFormat.isSpecified();
        if (metadataFormat != null) specified = specified & metadataFormat.isSpecified();
        if (specified) return true;
        if (action == VersionContent.ResourceFormatAction.ignore) return false;
        if (action == VersionContent.ResourceFormatAction.exception) {
            StringBuffer buf = new StringBuffer();
            if ((scienceFormat != null) && !scienceFormat.isSpecified()){
                buf.append(" - scienceFormat=" + scienceFormat.getFmtid());
            }
            if ((metadataFormat != null) && !metadataFormat.isSpecified()){
                buf.append(" - metadataFormat=" + metadataFormat.getFmtid());
            }
            throw new TException.INVALID_DATA_FORMAT(MESSAGE + "addEntry - undefined format:" + buf.toString());
        }
        return true;
    }

    protected void fillEntry(ResourceContent entry)
        throws TException
    {

        try {
            entry.scienceMetadataComponent = getFileComponent(entry.scienceMetadataID);
            entry.scienceMetadataPid = getPid(entry.scienceMetadataID);
            entry.scienceDataComponent = getFileComponent(entry.scienceDataID);
            entry.scienceDataPid = getPid(entry.scienceDataID);
            setFormatContent(entry);

        } catch (TException tex) {
            throw tex;

        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException(ex);
        }
    }

    protected FileComponent getFileComponent(String fileID)
        throws TException
    {
        try {
            if (StringUtil.isEmpty(fileID)) return null;
            if (fileID.equals("DEFAULT")) fileID = defaultERC;
            else fileID = PREFIX + fileID;
            //add logic to build empty component
            return versionContent.retrieveComponent(fileID);

        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException(ex);
        }
    }

    protected void setFormatContent(ResourceContent entry)
        throws TException
    {
        try {
            if (DEBUG) System.out.println("***entry.scienceMetadataType=" + entry.scienceMetadataType);
            entry.scienceMetadataFormat = objectFormatXML.getFormatDefault("METADATA", entry.scienceMetadataType);
            if (DEBUG) System.out.println(entry.scienceMetadataFormat.dump("scienceMetadataFormat"));

            if (entry.scienceDataID != null) {
               if (DEBUG)  System.out.println("***entry.scienceDataType=" + entry.scienceDataType);
                entry.scienceDataFormat = objectFormatXML.getFormatDefault("DATA", entry.scienceDataType);
                if (DEBUG) System.out.println(entry.scienceDataFormat.dump("scienceDataFormat"));
            }
            
            
        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException(ex);
        }
    }

    public void addResourceTable()
        throws TException
    {
        try {
            for (ResourceContent entry : mapList) {

                FileMapContent content = resourceTable.get(entry.scienceMetadataID);
                if (content == null) {
                    content = new FileMapContent(
                            entry.scienceMetadataID,
                            entry.scienceMetadataPid,
                            entry.scienceMetadataFormat,
                            entry.scienceMetadataComponent);
                    resourceTable.put(entry.scienceMetadataID, content);
                }
            }
            for (ResourceContent entry : mapList) {

                FileMapContent content = resourceTable.get(entry.scienceDataID);
                if (content == null) {
                    content = new FileMapContent(
                            entry.scienceDataID,
                            entry.scienceDataPid,
                            entry.scienceDataFormat,
                            entry.scienceDataComponent);
                    resourceTable.put(entry.scienceDataID, content);
                }
            }

        } catch (Exception ex) {
            logger.logMessage(MESSAGE + "Exception:" + ex, 4);
            logger.logError(MESSAGE + "Trace:" + StringUtil.stackTrace(ex), 10);
            throw new TException(ex);
        }
    }

    protected void buildReaderFile()
        throws TException
    {
        try {
            if ((resourceManifestFile == null) || !resourceManifestFile.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "resourceManifestFile not found");
            }
            FileInputStream fstream = new FileInputStream(resourceManifestFile);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            resourceBR = new BufferedReader(new InputStreamReader(in));

        } catch (Exception ex) {
            throw new TException.GENERAL_EXCEPTION(ex.toString());
        }
    }

    protected void log(String msg)
    {
        if (!DEBUG) return;
        System.out.println(MESSAGE + msg);
    }

    public String dump(String header)
        throws TException
    {
        StringBuffer buf = new StringBuffer();
        buf.append(header + NL);
        try {

            mapList = getMapList();
            buf.append("mapList size=" + mapList.size() + NL);
            for (ResourceContent entry: mapList) {
                buf.append(entry.dump("test") + NL);
            }
            return buf.toString();

        } catch (Exception ex) {
            System.out.println("Exception:" + ex);
            ex.printStackTrace();
            throw new TException (ex);
        }
    }

    public Vector<ResourceContent> getMapList() {
        return mapList;
    }

    public Hashtable<String, FileMapContent> getResourceTable() {
        return resourceTable;
    }

    public Vector<FileMapContent> getEntryList()
        throws TException
    {
        //System.out.println(MESSAGE + "getEntryList size=" + resourceTable.size());
        Vector<FileMapContent> entryList = new Vector<FileMapContent>(resourceTable.size());
        Set<String> keys = resourceTable.keySet();
        for (String key : keys) {
            entryList.add(resourceTable.get(key));
        }
        return entryList;
    }

    public VersionContent getVersionContent() {
        return versionContent;
    }


    protected String getPid(String fileID)
    {
        if (fileID.equals("DEFAULT")) fileID = defaultERC;
        return DataONEUtil.getPid(versionContent.getObjectID(), versionContent.getVersionID(), fileID);
    }

    public ObjectFormatXML getObjectFormatXML() {
        return objectFormatXML;
    }

}
