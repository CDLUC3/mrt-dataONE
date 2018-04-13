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
package org.cdlib.mrt.dataone.content;

import org.cdlib.mrt.utility.StringUtil;
import org.cdlib.mrt.utility.TException;

/**
 * ObjectFormat contains 3 elements;
 * fmtid - System Metadata format id
 * formatName - descriptive name for this format
 * scienceMetadata - flag - true=this a metadata file.
 *
 * Note that with v1 this object may be discontinued. With the introduction of the ORE Resource
 * Map the content is redundant. Only the fmtid will remain but as a element of the SystemMetadata
 * object.
 * 
 * @author dloy
 */
public class ObjectFormatContent
{

    private static final String NAME = "ObjectFormatContent";
    private static final String MESSAGE = NAME + ": ";
    private static final String NL = System.getProperty("line.separator");
    
    public enum ObjectType {DATA, METADATA, RESOURCE};
    protected ObjectType type = null;
    private String fmtid;
    private String formatName;
    private boolean specified = true;

    public ObjectFormatContent() { }

    public ObjectFormatContent(String fmtid, String formatName, String formatType)
        throws TException
    {
        this.fmtid = fmtid;
        this.formatName = formatName;
        validate(formatType);
        setFormatType(formatType);
    }
    
    private void validate(String formatType)
        throws TException
    {
        if (StringUtil.isEmpty(fmtid)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "fmtid not supplied");
        }
        if (StringUtil.isEmpty(formatName)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "formatName not supplied");
        }
        if (StringUtil.isEmpty(formatType)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "formatType not supplied");
        }
    }

    public String getFmtid() {
        return fmtid;
    }

    public void setFmtid(String fmtid) {
        this.fmtid = fmtid;
    }

    public String getFormatName() {
        return formatName;
    }

    public void setFormatName(String formatName) {
        this.formatName = formatName;
    }

    public String getFormatType() {
        return type.toString();
    }

    public void setFormatType(String formatType)
        throws TException
    {
        if (StringUtil.isEmpty(formatType)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "formatType required");
        }
        try {
            formatType = formatType.toUpperCase();
            type = ObjectType.valueOf(formatType);
        } catch (Exception ex) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "type not supported:" + formatType);
        }
    }
    

    public ObjectType getType() {
        return type;
    }

    public boolean isSpecified() {
        return specified;
    }

    public void setSpecified(boolean specified) {
        this.specified = specified;
    }
    

    public String dump(String header)
    {
        String out = "ObjectFormatContent[" + header + "]:" + NL
            + " - fmtid=" + fmtid + NL
            + " - formatName=" + formatName + NL
            + " - type=" + getFormatType() + NL
            + " - specified=" + isSpecified() + NL;
        return out;
    }
}
