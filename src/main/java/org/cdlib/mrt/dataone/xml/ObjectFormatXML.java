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
package org.cdlib.mrt.dataone.xml;

import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.dataone.content.ObjectFormatContent;

/**
 * This object imports the formatTypes.xml and builds a local table of supported format types.
 * Note, that the ObjectFormat is being deprecated and replaced by a single format id (fmtid).
 * This change is happening because formatName is strictly a description and has no functional
 * use. The scienceMetadata flag is being dropped because the ORE Resource Map is more flexible
 * and allows for a broader set of data type.
 * 
 * @author dloy
 */
public class ObjectFormatXML
{
    private static final String NAME = "ObjectFormatXML";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");
    private static final boolean DEBUG = false;
    protected Hashtable<String, ObjectFormatContent> formatList = new Hashtable<String, ObjectFormatContent>();
    protected Document doc = null;

    public ObjectFormatXML()
        throws TException
    {
        getDocuments();
        extractObjectFormat();
    }

    /**
     * Input the formatTypes.xml and build a Document
     * @throws TException
     */
    protected void getDocuments()
        throws TException
    {
        InputStream inStream = null;
        ClassLoader classLoader = this.getClass().getClassLoader();
        try {
            inStream = classLoader.getResourceAsStream("resources/objectFormatList.xml");
            SAXBuilder builder = new SAXBuilder();
            doc = builder.build(inStream);
            if (DEBUG) System.out.println("Doc built");

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
    public void extractObjectFormat()
        throws TException
    {
        try {
            XPath xpath = XPath.newInstance("//objectFormat");
            List list = xpath.selectNodes(doc);
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                Element elem = (Element) iter.next();
                Element fmtidE = elem.getChild("fmtid");
                Element formatNameE = elem.getChild("formatName");
                Element formatTypeE = elem.getChild("formatType");

                String fmtid = fmtidE.getText();
                String formatName = formatNameE.getText();
                String formatType = formatTypeE.getText();

                ObjectFormatContent formatContent = new ObjectFormatContent(fmtid, formatName, formatType);

                if (DEBUG) System.out.println(formatContent.dump("put"));
                String fmtidKey = formatContent.getFmtid().toLowerCase();
                formatList.put(fmtidKey, formatContent);
            }

        } catch (Exception ex) {
            System.out.println(MESSAGE + "Exception:" + ex);
            ex.printStackTrace();
            throw new TException(ex);
        }
    }

    /**
     * Search for format id (fmtid) in table and return an ObjectFormatContent matching id.
     * In future dataone releases only fmtid is supported.
     * @param fmtid
     * @return
     */
    public ObjectFormatContent getFormat(String fmtid)
    {
        String fmtidKey = fmtid.toLowerCase();
        return formatList.get(fmtidKey);
    }

    public ObjectFormatContent getFormatDefault(String formatType, String fmtid)
        throws TException
    {
        String fmtidKey = fmtid.toLowerCase();
        ObjectFormatContent formatContent = formatList.get(fmtidKey);
        if (formatContent == null) {
            formatContent = new ObjectFormatContent();
            formatContent.setFmtid(fmtid);
            formatContent.setFormatName("Unspecified:" + fmtid);
            formatContent.setFormatType(formatType);
            formatContent.setSpecified(false);
        }
        return formatContent;
    }
}
