/*
Copyright (c) 2005-2010, Regents of the University of California
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
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import com.hp.hpl.jena.rdf.model.*;

import org.cdlib.mrt.utility.URLEncoder;
import java.util.Vector;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.dataone.content.ResourceContent;
import org.cdlib.mrt.dataone.utility.DataONEUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;

/**
 * A resource content list constructed from the Resource Manifest is used to build the ORE
 * Resource Map used by DataONE. The ORE Resource Map is now the mechanism used to show the
 * relationships between different content submitted to DataONE
 *
 * JENA is used for construction of the semantic model. Once the model is constructed then the
 * model (Resource Map) can be output in a variety of different semantic formats: rdf, turtle...
 *
 * The basic model is initially based on DataONE documentation as it exists 9/2/2011.
 * This should be viewed as unstable at the time of this code creation.
 *
 * The resources used in the the Map use a DataONE getObject URL to reference the objects.
 * A similar approach is used in Merritt.
 *
 * @author dloy
 */
public class ResourceMap
{
    protected static final String NAME = "ResourceMap";
    protected static final String MESSAGE = NAME + ":";
    protected final static String NL = System.getProperty("line.separator");
    private static final String PRODUCER = "producer/";
    private static final boolean DEBUG = false;

    protected LoggerInf logger = null;
    protected Vector<ResourceContent> table = null;
    protected String dataoneURLS = null;
    protected String objectURLS = null;
    protected String resourcePid = null;
    protected Integer versionID = null;
    protected Identifier objectID = null;
    protected DateState date = null;

    protected String cito="http://purl.org/spar/cito/";
    protected String dc="http://purl.org/dc/elements/1.1/";
    protected String dcterms="http://purl.org/dc/terms/";
    protected String foaf="http://xmlns.com/foaf/0.1/";
    protected String ore="http://www.openarchives.org/ore/terms/";
    protected String rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#";
    protected String rdfs1="http://www.w3.org/2001/01/rdf-schema#";

    protected Model model = ModelFactory.createDefaultModel();

    protected Property citoDocuments = ResourceFactory.createProperty(cito + "documents");
    protected Property citoIsDocumentedBy = ResourceFactory.createProperty(cito + "isDocumentedBy");
    protected Property dcFormat = ResourceFactory.createProperty(dc + "format");
    protected Property dctermsCreated = ResourceFactory.createProperty(dcterms + "created");
    protected Property dctermsCreator = ResourceFactory.createProperty(dcterms + "creator");
    protected Property dctermsDescription = ResourceFactory.createProperty(dcterms + "description");
    protected Property dctermsIdentifier = ResourceFactory.createProperty(dcterms + "identifier");
    protected Property dctermsModified = ResourceFactory.createProperty(dcterms + "modified");
    protected Property dctermsTitle = ResourceFactory.createProperty(dcterms + "title");
    protected Property oreDescribes = ResourceFactory.createProperty(ore + "describes");
    protected Property oreAggregates = ResourceFactory.createProperty(ore + "aggregates");
    protected Property rdfs1IsDefinedBy = ResourceFactory.createProperty(rdfs1 + "isDefinedBy");
    protected Property rdfs1Label = ResourceFactory.createProperty(rdfs1 + "label");
    protected Property rdfType = ResourceFactory.createProperty(rdf + "type");

    public static ResourceMap getResourceMap(
            String dataoneURLS,
            String objectURLS,
            Identifier objectID,
            Integer versionID,
            String resourcePid,
            Vector<ResourceContent> table,
            LoggerInf logger)
        throws TException
    {
        return new ResourceMap(dataoneURLS, objectURLS, objectID, versionID, resourcePid, table, logger);
    }

    /**
     *
     * @param dataoneURLS DataONE service URL
     * @param objectURLS Merritt Storage service URL
     * @param objectID Merritt Object identifier
     * @param versionID Merritt version identifier
     * @param resourcePid DataONE pid for the ORE Resource Map being created here
     * @param table Constructed resource manifest
     * @param logger Merritt logger
     * @throws TException
     */
    protected ResourceMap(
            String dataoneURLS,
            String objectURLS,
            Identifier objectID,
            Integer versionID,
            String resourcePid,
            Vector<ResourceContent> table,
            LoggerInf logger)
        throws TException
    {
        try {
            this.dataoneURLS = dataoneURLS;
            this.objectURLS = objectURLS;
            this.versionID = versionID;
            this.objectID = objectID;
            this.resourcePid = resourcePid;
            this.table = table;
            this.logger = logger;
            validate();
            buildModel();
        } catch (Exception ex) {
            String msg = MESSAGE + "Exception:" + ex;
            System.out.println(msg);
            ex.printStackTrace();
            logger.logError(msg, 1);
            logger.logError(StringUtil.stackTrace(ex), 2);
        }
    }

    protected void validate()
        throws TException
    {
        notEmpty("dataoneURLS", dataoneURLS);
        notNull("objectID", objectID);
        notNull("versionID", versionID);
        notNull("table", table);
        notNull("logger", logger);
        if (versionID < 0) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "versionID < 1");
        }
        model.setNsPrefix("cito", cito);
        model.setNsPrefix("dc", dc);
        model.setNsPrefix("dcterms", dcterms);
        model.setNsPrefix("foaf", foaf);
        model.setNsPrefix("ore", ore);
        model.setNsPrefix("rdf", rdf);
        model.setNsPrefix("rdfs1", rdfs1);

        date = new DateState();
    }

    protected void notNull(String header, Object object)
        throws TException
    {
        DataONEUtil.notNull(header, object);
    }

    protected void notEmpty(String header, String object)
        throws TException
    {
        DataONEUtil.notEmpty(header, object);
    }


    public void buildModel()
        throws TException
    {
        addMap();
        for (ResourceContent entry : table) {
            addEntry(entry);
        }
    }

    protected void addMap()
        throws TException
    {
        //String resourcePid = getPid(resourceFileID);
        Resource resourceID = getDataONEResource(resourcePid);
        model.add(resourceID, dctermsIdentifier, resourcePid);
        model.add(resourceID, dctermsModified, date.getIsoDate());
        Resource type = model.createResource("http://www.openarchives.org/ore/terms/ResourceMap");
        Resource objectType = model.createResource(objectURLS);
        model.add(resourceID, rdfType, type);
        model.add(resourceID, dcFormat, "application/rdf+xml");
        model.add(resourceID, oreDescribes, objectType);
        model.add(resourceID, dctermsCreated, date.getIsoDate());
        model.add(resourceID, dctermsCreator, "Merritt");


        Resource aggregationResource = model.createResource("http://www.openarchives.org/ore/terms/Aggregation");
        Resource isDefinedByResource = model.createResource("http://www.openarchives.org/ore/terms/");
        model.add(aggregationResource, rdfs1IsDefinedBy, isDefinedByResource);
        model.add(aggregationResource, rdfs1Label, "Aggregation");

        Resource aggregationResourceID = model.createResource(objectURLS);
        model.add(aggregationResourceID, rdfs1IsDefinedBy, isDefinedByResource);
        model.add(aggregationResourceID, dctermsTitle, "Simple aggregation of science metadata and data");
        Properties prop = new Properties();
        for (ResourceContent entry : table) {
            addMapORE(aggregationResourceID, prop, entry.scienceMetadataID);
            addMapORE(aggregationResourceID, prop, entry.scienceDataID);
        }
    }

    protected void addMapORE(Resource aggregationResourceID, Properties prop, String id)
        throws TException
    {
        String identifier = getPid(id);
        String found = prop.getProperty(identifier);
        if (found != null) return;
        Resource oreID = getDataONEResource(identifier);
        model.add(aggregationResourceID, oreAggregates, oreID);
        prop.setProperty(identifier, "found");
    }

    protected void addEntry(
            ResourceContent entry)
        throws TException
    {
        Resource metadataResource = getDataONEResource(entry.scienceMetadataPid);
        Resource dataResource = getDataONEResource(entry.scienceDataPid);

        model.add(metadataResource, citoDocuments, dataResource);
        model.add(metadataResource, dctermsIdentifier, entry.scienceMetadataPid);
        model.add(metadataResource, dctermsDescription,
                "A reference to a science metadata document using a DataONE identifier.");

        model.add(dataResource, citoIsDocumentedBy, metadataResource);
        model.add(dataResource, dctermsIdentifier, entry.scienceDataPid);
        model.add(metadataResource, dctermsDescription,
                "A reference to a science data object using a DataONE identifier.");
    }


    protected String getPid(String fileID)
    {
        return DataONEUtil.getPid(objectID, versionID, fileID);
    }

    protected Resource getDataONEResource(String pid)
        throws TException
    {
        try {
            String resource =  dataoneURLS  + URLEncoder.encode(pid, "UTF-8");
            return model.createResource(resource);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new TException(ex);
        }
    }



    public String formatModel(String format)
        throws TException
    {
        String [] formats = {
            "RDF/XML",
            "RDF/XML-ABBREV",
            "N-TRIPLE",
            "TURTLE",
            "TTL",
            "N3"};


         try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(5000);
            model.write(baos, format);
            baos.close();
            byte[] bytes = baos.toByteArray();
            return new String(bytes, "utf-8");

        } catch(Exception ex) {
            String err = MESSAGE + "Could not complete version file output - Exception:" + ex;
            logger.logError(err ,  LoggerInf.LogLevel.UPDATE_EXCEPTION);
            logger.logError(StringUtil.stackTrace(ex),  LoggerInf.LogLevel.DEBUG);
            throw new TException.GENERAL_EXCEPTION( err);
        }

    }

    public String [] getAggregates(
            Vector<String> fileNames,
            String fileURLS,
            int nodeID,
            Identifier objectID,
            int versionID)
        throws TException
    {

        try {
            if (StringUtil.isEmpty(fileURLS)) {
                String msg = MESSAGE
                    + "getPOSTManifest - base URL not provided";
                throw new TException.INVALID_OR_MISSING_PARM( msg);
            }

            String [] arr = new String[fileNames.size()];
            for (int i=0; i < fileNames.size(); i++) {
                String fileName = fileNames.get(i);
                String fileURL = fileURLS
                        + "/content/" + nodeID
                        + "/" + URLEncoder.encode(objectID.getValue(), "utf-8")
                        + "/" + versionID
                        + "/" + URLEncoder.encode(fileName, "utf-8");
                URL testURL = new URL(fileURL);
                arr[i] = testURL.toString();
            }
            return arr;

        } catch (TException fe) {
            throw fe;

        } catch(Exception ex) {
            String err = MESSAGE + "Could not complete version file output - Exception:" + ex;
            logger.logError(err ,  LoggerInf.LogLevel.UPDATE_EXCEPTION);
            logger.logError(StringUtil.stackTrace(ex),  LoggerInf.LogLevel.DEBUG);
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }


    /**
     * Build a POST manifest
     * @param fileURLS base URL for deriving manifest fileURLs
     * @param sourceDir directory file containing files for manifest generation
     * @param postManifest output file to contain POST manifest
     * @return accumulated size of files referenced by manifest
     * @throws TException process exception
     */
    public String getStoreResource(
            String dataoneURLS,
            int nodeID,
            Identifier objectID,
            int versionID)
        throws TException
    {
        try {
                String resourceURL = dataoneURLS
                        + "/object/" + nodeID
                        + "/" + URLEncoder.encode(objectID.getValue(), "utf-8")
                        + "." + versionID;
                URL testURL = new URL(resourceURL);
            return testURL.toString();

        } catch(Exception ex) {
            String err = MESSAGE + "Could not complete version file output - Exception:" + ex;
            logger.logError(err ,  LoggerInf.LogLevel.UPDATE_EXCEPTION);
            logger.logError(StringUtil.stackTrace(ex),  LoggerInf.LogLevel.DEBUG);
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }


    /**
     * Get this Object Resource
     * @param dataoneURLS base URL
     * @param nodeID node identifier for resource
     * @param objectID object identifier for resource
     * @return String form URL for object resource
     * @throws TException process exception
     */
    public String getObjectResource(
            String dataoneURLS,
            int nodeID,
            Identifier objectID)
        throws TException
    {
        try {
                String resourceURL = dataoneURLS
                        + "/object/" + nodeID 
                        + "/" + URLEncoder.encode(objectID.getValue(), "utf-8");
                URL testURL = new URL(resourceURL);
            return testURL.toString();

        } catch(Exception ex) {
            String err = MESSAGE + "Could not complete version file output - Exception:" + ex;
            logger.logError(err ,  LoggerInf.LogLevel.UPDATE_EXCEPTION);
            logger.logError(StringUtil.stackTrace(ex),  LoggerInf.LogLevel.DEBUG);
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }

    protected String getFileName(File sourceDir, File file)
        throws Exception
    {
            String fileName = file.getCanonicalPath();
            String sourceName = sourceDir.getCanonicalPath();
            fileName = fileName.substring(sourceName.length() + 1);
            fileName = fileName.replace('\\', '/');
            return fileName;
    }

    public String getResourcePid() {
        return resourcePid;
    }


    protected void log(String msg)
    {
        System.out.println(msg);
    }
}
