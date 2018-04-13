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
package org.cdlib.mrt.dataone.map;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;


import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;

import org.cdlib.mrt.cloud.ManInfo;
import org.cdlib.mrt.cloud.VersionMap;
import org.cdlib.mrt.core.ComponentContent;
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.LoggerInf;
import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.TFileLogger;
import org.jdom.Namespace;
import org.jdom.output.Format;

/**
Recursively delete all empty elements.

If there ISN’T a <dataset><contact> element, then insert the <contact> 
with a surname value duplicated from <creator><surName>:

            <contact>
              <individualName>
                <surName>Create surname</surname>
              </individualName>
            </contact>

 
If there IS a <project> element but no <project><title> element, then add the <title>:

	<project>
              <title>Missing required element</title.
          ...
            </project>

 
If there IS a <dataTable> element and there IS a <dataTable><attributeList> 
but NO <dataTable><entityName>, then add <entityName>:

            <dataTable>
              <entityName>Missing required element</entityName>
              <entityDescription>…</entityDescription>
              <attributeList>
                …
              </attributeList>
            </dataTable>

If there is a <dataTable> element and there IS a <dataTable><entityName> 
but NO <dataTable><attributeList>, then add <attributeList>:


            <dataTable>
 * 
 * @author dloy
 */
public class FixEML
{
    private static final String NAME = "FixEML";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = false;
    private static boolean CHANGE = false;
    private static boolean DUMPIO = false;


    public static String[] TESTFILES = {
        "C:/Documents and Settings/dloy/My Documents/dataup/schema-2.1.1/dataone/tests/min-1.xml",
        "C:/Documents and Settings/dloy/My Documents/dataup/schema-2.1.1/dataone/tests/min-2.xml",
        "C:/Documents and Settings/dloy/My Documents/dataup/schema-2.1.1/dataone/tests/Maximal-EML.xml",
        "C:/Documents and Settings/dloy/My Documents/dataup/schema-2.1.1/dataone/tests/projtitle.xml",
        "C:/Documents and Settings/dloy/My Documents/dataup/schema-2.1.1/dataone/tests/entityname.xml",
        "C:/Documents and Settings/dloy/My Documents/dataup/schema-2.1.1/dataone/tests/missboundary.xml",
        "C:/Documents and Settings/dloy/My Documents/dataup/schema-2.1.1/dataone/tests/noboundary.xml",
        "C:/Documents and Settings/dloy/My Documents/dataup/schema-2.1.1/dataone/tests/missgeo.xml",
        "C:/Documents and Settings/dloy/My Documents/dataup/schema-2.1.1/dataone/tests/fail1.xml"
    };
    public static String FIXDIR = "C:/Documents and Settings/dloy/My Documents/dataup/schema-2.1.1/dataone/fix/";

    /**
     * Normalize Microsoft EML
     * @param inEML UTF-8 xml input string
     * @return normalzed xml as string
     * @throws TException 
     */
    public static String fix(String inEML)
           throws TException
    {
        
        try {
            if (StringUtil.isEmpty(inEML)) return null;
            InputStream inStream = StringUtil.stringToStream(inEML, "utf-8");
            Document normEMLD = fix(inStream);
            return xml2String(normEMLD);

        } catch (Exception ex) {
            System.out.println("ObjectFormatXML: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }

    public static Document fix(InputStream xmlStream)
           throws TException
    {
        
        logChange("fix entered");
        try {
            Document doc =  getDocument(xmlStream);
            stripEmpty(doc);
            Element root = doc.getRootElement();
            Element datasetE = root.getChild("dataset");
            fixContact(datasetE);
            fixProject(datasetE);
            fixDataTable(datasetE);
            fixGeographicCoverage(datasetE);
            fixTemporal(root, datasetE);
            
            return doc;

        } catch (Exception ex) {
            System.out.println("ObjectFormatXML: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    protected static void fixContact(Element datasetE)
        throws TException
    {
        logChange("fixContact entered");
        try {
            Element contactE = datasetE.getChild("contact");;
            if (contactE == null) throw new TException.INVALID_OR_MISSING_PARM("contact missing");
            List children = contactE.getChildren();
            if ((children == null) || (children.size() == 0)) {
                Element cloneCreatorE = getCreator(datasetE, "contact");
                int posContact = datasetE.indexOf(contactE);
                contactE.detach();
                datasetE.addContent(posContact, cloneCreatorE);
                logChange("-->fixContact: Creator copied to contact");
                
            }
            return;

        } catch (Exception ex) {
            System.out.println("fixProject: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    protected static Element getCreator(Element datasetE, String setName)
        throws TException
    {
        logChange("getCreator entered");
        try {
            Element creatorE = datasetE.getChild("creator");
            if (creatorE == null) throw new TException.INVALID_OR_MISSING_PARM("creator missing");
            Element cloneCreatorE = (Element)creatorE.clone();
            cloneCreatorE.setName(setName);
            return cloneCreatorE;

        } catch (Exception ex) {
            System.out.println("fixProject: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    protected static Element getTitle(Element datasetE, String setName)
        throws TException
    {
        logChange("getTitle entered");
        try {
            Element titleE = datasetE.getChild("title");
            if (titleE == null) throw new TException.INVALID_OR_MISSING_PARM("title missing");
            Element cloneTitleE = (Element)titleE.clone();
            cloneTitleE.setName(setName);
            return cloneTitleE;

        } catch (Exception ex) {
            System.out.println("fixProject: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    protected static void fixProject(Element datasetE)
        throws TException
    {
        logChange("fixProject entered");
        try {
            Element projectE = datasetE.getChild("project");
            if (projectE == null) return;
            Element titleE = projectE.getChild("title");
            if (titleE == null) {
                titleE = new Element("title");
                titleE.setText("Missing required element");
                projectE.addContent(0, titleE);
                logChange("-->fixProject: project title added");
            }
            Element personnelE = projectE.getChild("personnel");
            if (personnelE == null) {
                Element cloneCreatorE = getCreator(datasetE, "personnel");
                Element roleE = new Element("role");
                roleE.setText("creator");
                cloneCreatorE.addContent(roleE);
                int pos = projectE.indexOf(titleE);
                projectE.addContent(pos + 1, cloneCreatorE);
                logChange("-->fixProject: personnel added");
            }

        } catch (Exception ex) {
            System.out.println("fixProject: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    protected static void fixDataTable(Element datasetE)
        throws TException
    {
        logChange("fixDataTable entered");
        try {
            Element dataTableE = datasetE.getChild("dataTable");
            if (dataTableE == null) return;
            Element entityNameE = dataTableE.getChild("entityName");
            if (entityNameE == null) {
                entityNameE = new Element("entityName");
                entityNameE.setText("Missing required element");
                dataTableE.addContent(0, entityNameE);
                logChange("-->fixDataTable: dataTable add entityName");
            }
            Element attributeListE = dataTableE.getChild("attributeList");
            if (attributeListE == null) {
                attributeListE = getDummyAttributeList();
                dataTableE.addContent(attributeListE);
                logChange("-->fixDataTable: add default attribute list");
            }

        } catch (Exception ex) {
            System.out.println("fixProject: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    protected static void fixGeographicCoverage(Element datasetE)
        throws TException
    {
        logChange("fixGeographicCoverage entered");
        try {
            Element coverageE = datasetE.getChild("coverage");
            if (coverageE == null) return;
            Element geographicCoverageE = coverageE.getChild("geographicCoverage");
            if (geographicCoverageE == null) return;
            Element geographicDescriptionE = geographicCoverageE.getChild("geographicDescription");
            if (geographicDescriptionE == null) {
                geographicDescriptionE = new Element("geographicDescription");
                geographicDescriptionE.setText("Missing required element");
                geographicCoverageE.addContent(0, geographicDescriptionE);
                logChange("-->fixGeographicCoverage: add missing geographicDescription");
            }
            Element boundingCoordinatesE = geographicCoverageE.getChild("boundingCoordinates");
            if (boundingCoordinatesE == null) {
                boundingCoordinatesE = new Element("boundingCoordinates");
                geographicCoverageE.addContent(boundingCoordinatesE);
            }
            setMissingCoordinates(boundingCoordinatesE);
            
        } catch (Exception ex) {
            System.out.println("fixProject: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    protected static void setMissingCoordinates(Element boundingCoordinatesE)
        throws TException
    {
            try {
                Element westBoundingCoordinateE = boundingCoordinatesE.getChild("westBoundingCoordinate");
                Element eastBoundingCoordinateE = boundingCoordinatesE.getChild("eastBoundingCoordinate");
                Element northBoundingCoordinateE = boundingCoordinatesE.getChild("northBoundingCoordinate");
                Element southBoundingCoordinateE = boundingCoordinatesE.getChild("southBoundingCoordinate");
                if ((westBoundingCoordinateE != null)
                        && (eastBoundingCoordinateE != null)
                        && (northBoundingCoordinateE != null)
                        && (southBoundingCoordinateE != null)) {
                    return;           
                }
                logChange("-->fixGeographicCoverage: set default because of bad content");
                StringBuffer suppliedRange = new StringBuffer();
                appendCoordinate(suppliedRange, westBoundingCoordinateE);
                appendCoordinate(suppliedRange, eastBoundingCoordinateE);
                appendCoordinate(suppliedRange, northBoundingCoordinateE);
                appendCoordinate(suppliedRange, southBoundingCoordinateE);
                
                removeCoordinate(boundingCoordinatesE, westBoundingCoordinateE);
                removeCoordinate(boundingCoordinatesE, eastBoundingCoordinateE);
                removeCoordinate(boundingCoordinatesE, northBoundingCoordinateE);
                removeCoordinate(boundingCoordinatesE, southBoundingCoordinateE);
            
                addDefaultCoordinate(boundingCoordinatesE, "westBoundingCoordinate", null);
                addDefaultCoordinate(boundingCoordinatesE, "eastBoundingCoordinate", "westBoundingCoordinate");
                addDefaultCoordinate(boundingCoordinatesE, "northBoundingCoordinate", "eastBoundingCoordinate");
                addDefaultCoordinate(boundingCoordinatesE, "southBoundingCoordinate", "northBoundingCoordinate");
  
                Comment comment1 = new Comment(
                        "ERROR: The “0.0” values above indicate that all four coordinates were not supplied"
                        );
                boundingCoordinatesE.addContent(comment1);
                Comment comment2 = new Comment(
                        "The supplied coordinates were: west,east,north,south = "
                        + suppliedRange.toString());
                boundingCoordinatesE.addContent(comment2);
            
        } catch (Exception ex) {
            System.out.println("fixProject: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    protected static void removeCoordinate(Element parent, Element child)
        throws TException
    {
        if (child == null) return;
        parent.removeContent(child);
    }
    
    protected static void appendCoordinate(StringBuffer buf, Element coordinateE)
        throws TException
    {
        if (buf.length() > 0) buf.append(',');
        if (coordinateE == null) {
            buf.append('*');
        } else {
            String value = coordinateE.getText();
            buf.append(value);
        }
    }
    
    protected static void addDefaultCoordinate(Element boundingCoordinatesE, String boundingName, String afterBoundingName)
        throws TException
    {
        try {
            Element boundingNameE = new Element(boundingName);
            boundingNameE.setText("0.0");
            int pos = -1;
            if (afterBoundingName == null) {
                boundingCoordinatesE.addContent(boundingNameE);
            } else {
                Element afterBoundingE = boundingCoordinatesE.getChild(afterBoundingName);
                pos = boundingCoordinatesE.indexOf(afterBoundingE);
                boundingCoordinatesE.addContent(pos + 1, boundingNameE);
            }
            
        } catch (Exception ex) {
            System.out.println("fixProject: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    protected static void fixTemporal(Element rootE, Element datasetE)
        throws TException
    {
        logChange("fixTemporal entered");
        try {
            List<Element> addMetaList = rootE.getChildren("additionalMetadata");
            if ((addMetaList == null) || (addMetaList.size() == 0)) return;
            LinkedList<Element> copyMeta = new LinkedList();
            for (Element addE : addMetaList) {
                if (DEBUG) System.out.println("-->*addE Tag:" + addE.getName());
                copyMeta.add(addE);
            }
            for (Element addE : copyMeta) {
                Element describesE = addE.getChild("describes");
                if (describesE != null) {
                    String ref = describesE.getTextTrim();
                    if (DEBUG) System.out.println("-->*Description found:" + ref);
                    if (StringUtil.isNotEmpty(ref)) {
                        if (!matchTemporal(datasetE, ref)) {
                            addE.detach();
                            logChange("-->fixTemporal: detach additionalMetadata");
                        }
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("fixProject: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    protected static boolean matchTemporal(Element datasetE, String refId)
        throws TException
    {
        try {
            if (StringUtil.isEmpty(refId)) return true;
            Element dataTableE = datasetE.getChild("coverage");
            if (dataTableE == null) return false;
            Element temporalE = dataTableE.getChild("temporalCoverage");
            if (temporalE == null) return false;
            Attribute temporalIdA = temporalE.getAttribute("id");
            if (temporalIdA == null) return false;
            String temporalId = temporalIdA.getValue();
            if (StringUtil.isEmpty(temporalId)) return false;
            if (!temporalId.equals(refId)) return false;
            return true;

        } catch (Exception ex) {
            System.out.println("fixProject: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    public static Element getDummyAttributeList()
        throws TException
    {
        try {
            Element attributeListE = new Element("attributeList");
            Element attributeE = new Element("attribute");
            attributeListE.addContent(attributeE);
            Element attributeNameE = new Element("attributeName");
            attributeNameE.setText("Missing required element");
            attributeE.addContent(attributeNameE);
            Element attributeDefinitionE = new Element("attributeDefinition");
            attributeDefinitionE.setText("Missing required element");
            attributeE.addContent(attributeDefinitionE);
            Element measurementScaleE = new Element("measurementScale");
            attributeE.addContent(measurementScaleE);
            Element nominalE = new Element("nominal");
            
            measurementScaleE.addContent(nominalE);
            Element nonNumericDomainE = new Element("nonNumericDomain");
            nominalE.addContent(nonNumericDomainE);
            Element textDomainE = new Element("textDomain");
            nonNumericDomainE.addContent(textDomainE);
            
            Element definitionE = new Element("definition");
            definitionE.setText("Missing required element");
            textDomainE.addContent(definitionE);
            return attributeListE;

        } catch (Exception ex) {
            System.out.println("fixProject: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
        
    }
    
    protected static void dumpTags(String header, Element inEle)
        throws TException
    {
        System.out.println("-->**" + header + "-->**");
        dumpElementTags("", inEle);
    }
    
    protected static void dumpElementTags(String fill, Element inEle)
        throws TException
    {
        try {
            System.out.print(fill + "<" + inEle.getName() + ">");
            //System.out.println("DUMP FOR " + inEle.getName());
            List<Element> eles = inEle.getChildren();
            if ((eles != null) && (eles.size() > 0)) {
                System.out.println("");
                fill += "  ";
                for (Element ele : eles) {
                    dumpElementTags(fill, ele);
                }
            } else {
                System.out.println(inEle.getText());
            }
            

        } catch (Exception ex) {
            System.out.println("ObjectFormatXML: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    /**
     * Input the formatTypes.xml and build a Document
     * @throws TException
     */
    protected static Document getDocument(InputStream inStream)
        throws TException
    {
        try {
            SAXBuilder builder = new SAXBuilder();
            if (DEBUG) System.out.println("Doc built");
            return builder.build(inStream);

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
        
    /**
     * Extract the 3 elements of the FormatObject from this xml and build table
     * @throws TException
     */
    protected static void stripEmpty(Document doc)
        throws TException
    {
        try {
            Element root = doc.getRootElement();
            Element dataset = root.getChild("dataset");
            /*
            XPath xpath = XPath.newInstance("//x:dataset");
            xpath.addNamespace("x", emlNameBad.getURI());
            List list = xpath.selectNodes(doc);
             * 
             */
            removeEmpty(root, dataset);
            
        } catch (TException tex) {
            System.out.println(MESSAGE + "Exception:" + tex);
            tex.printStackTrace();
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
        
        
    /**
     * Extract the 3 elements of the FormatObject from this xml and build table
     * @throws TException
     */
    protected static void removeEmpty(Element parent, Element ele)
        throws TException
    {
        try {
            String name = ele.getName();
            String uri = ele.getNamespace().getURI();
            boolean removed = remove(parent, ele);
            
            if (removed) {
                parent.removeContent(ele);
                if (DEBUG) System.out.println("Removed Start(" + uri + "):" + name);
                return;
            }
            if (DEBUG) System.out.println("Saved Start(" + uri + "):" + name);
            
            LinkedList<Element> eleList = new LinkedList<Element>();
            List list = ele.getChildren();
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                Element child = (Element) iter.next();
                eleList.add(child);
            }
            for (Element child : eleList) {
                removeEmpty(ele, child);
            }
            removed = remove(parent, ele);
            if (removed) {
                if (skipRemove(name)) return;
                parent.removeContent(ele);
                if (DEBUG) System.out.println("Removed End(" + uri + "):" + name);
                return;
            }
            if (DEBUG) System.out.println("Saved End(" + uri + "):" + name);
            
        } catch (TException tex) {
            System.out.println(MESSAGE + "Exception:" + tex);
            tex.printStackTrace();
            throw tex;

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    protected static boolean skipRemove(String name)
    {
        String [] skipList =
        {
            "contact"
        };
        for (String skip : skipList) {
            if (skip.equals(name)) return true;
        }
        return false;
    }
    
    protected static boolean remove(Element parent, Element ele)
        throws TException
    {
        try {
            if (parent == null) return false;
            
            List list = ele.getChildren();
            if (list.isEmpty()) {
                String text = ele.getText();
                if (StringUtil.isAllBlank(text)) {
                    //System.out.println("Remove:" + ele.getName());
                    return true;
                }
            }
            return false;
            
        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }
    
    public static String xml2String(Document document)
        throws TException
    {
        try {
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            String xmlString = outputter.outputString(document);
            if (DEBUG) System.out.println(xmlString);
            return xmlString;
            

        } catch (Exception ex) {
            System.out.println("ObjectFormatXML: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    public static void dumpDoc(String header, Document document)
        throws TException
    {
        try {
            System.out.println("-->****>>>>" + header + "<<<<********");
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            String xmlString = outputter.outputString(document);
            System.out.println(xmlString);
            

        } catch (Exception ex) {
            System.out.println("ObjectFormatXML: Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);

        }
    }
    
    protected static void logChange(String msg)
    {
        if (!CHANGE) return;
        System.out.println(MESSAGE + msg);
    }
    
    public static void main(String[] args) throws TException {
        /*
         * Important: Be sure to fill in your AWS access credentials in the
         *            AwsCredentials.properties file before you try to run this
         *            sample.
         * http://aws.amazon.com/security-credentials
         */
        try {
            SAXBuilder builder = new SAXBuilder(true); 
            builder.setFeature("http://apache.org/xml/features/validation/schema", true);
            Document myDocument = null;
            CHANGE = true;
            DUMPIO = false;
            File fixDir = new File(FIXDIR);
            if (!fixDir.exists()) {
                throw new TException.INVALID_OR_MISSING_PARM("fixDir not foumnd:" + fixDir.getCanonicalPath());
            }
            for (String test : TESTFILES) {
                System.out.println("\n******************************\n");
                System.out.println(test);
                System.out.println("\n******************************\n");
                File recFile = new File(test);
                String inEML = FileUtil.file2String(recFile, "utf-8");
                String outEML = fix(inEML);
                if (DUMPIO) System.out.println("-->*IN :" + recFile.getCanonicalPath() + "\n" + inEML + "\n\n");
                if (DUMPIO) System.out.println("-->*OUT:" + recFile.getCanonicalPath() + "\n" + outEML + "\n\n");
                String name = recFile.getName();
                File outFile = new File(fixDir, name);
                FileUtil.string2File(outFile, outEML);
                myDocument = builder.build(outFile);
                System.out.println("-->VALIDATED:" + outFile.getCanonicalPath());
            }
            
        } catch (TException tex) {
            throw tex;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("NAME=" + ex.getClass().getName());
            System.out.println("Exception:" + ex);
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            
        }
        
    }
}
