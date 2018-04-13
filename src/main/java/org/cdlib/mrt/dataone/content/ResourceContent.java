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


import org.cdlib.mrt.core.FileComponent;

/**
 * This object/table reflects an entry (i.e. line) from the Resource Manifest table.
 *
 * These entries are the basic table used for constructing the ORE map AND for building the individual
 * Create Content table used for the DataONE create
 * @author dloy
 */
public class ResourceContent
{
    private static final String NAME = "ResourceContent";
    private static final String MESSAGE = NAME + ": ";

    private static final String NL = System.getProperty("line.separator");

    // scienceMetadataID - fileID of the science metadata
    public String scienceMetadataID = null;

    // scienceMetadataPid - DataONE pid for this metadata
    public String scienceMetadataPid = null;

    // scienceMetadataComponent - component information for this file (e.g. digest,date, size...)
    public FileComponent scienceMetadataComponent = null;

    // scienceMetadataType - DataONE formatid
    public String scienceMetadataType = null;

    // scienceMetadataFormat - FormatObject content for this file type
    public ObjectFormatContent scienceMetadataFormat = null;

    // scienceDataID - fileID of the science data
    public String scienceDataID = null;

    // scienceDataPid - DataONE pid for this science data
    public String scienceDataPid = null;

    // scienceDataComponent - component information for this file (e.g. digest,date, size...)
    public FileComponent scienceDataComponent = null;

    // scienceDataType - DataONE formatid
    public String scienceDataType = null;

    // scienceDataFormat - FormatObject content for this file type
    public ObjectFormatContent scienceDataFormat = null;

    /**
     * dump content
     * @param header display header
     * @return String containing dump information
     */
    public String dump(String header) {
        StringBuffer buf = new StringBuffer();
        buf.append(header + NL
                + " - scienceMetadataID=" + scienceMetadataID + NL
                + " - scienceMetadataPid=" + scienceMetadataPid + NL
                + " - scienceMetadataType=" + scienceMetadataType + NL
                + " - scienceDataID=" + scienceDataID + NL
                + " - scienceDataPid=" + scienceDataPid + NL
                + " - scienceDataType=" + scienceDataType + NL
                );
        if (scienceMetadataComponent != null) {
            buf.append(scienceMetadataComponent.dump("ScienceMetadata FileComponent") + NL);
        }
        if (scienceMetadataFormat != null) {
            buf.append(scienceMetadataFormat.dump("ScienceMetadata Format") + NL);
        }
        if (scienceDataComponent != null) {
            buf.append(scienceDataComponent.dump("ScienceData FileComponent") + NL);
        }
        if (scienceDataFormat != null) {
            buf.append(scienceDataFormat.dump("ScienceData Format") + NL);
        }
        return buf.toString();
    }
}
