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
import org.cdlib.mrt.core.DateState;
import org.cdlib.mrt.core.FileComponent;
import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.utility.TException;
/**
 * This container class includes all of the needed objects for the creation of the
 * SystemMetadata file created by SystemMetadataXML.
 *
 * @author dloy
 */
public class SystemMetadataContent
{

    private static final String NL = System.getProperty("line.separator");
/*
 *            <xs:element name="identifier" type="d1:Identifier" minOccurs="1" maxOccurs="1"/>
            <xs:element name="objectFormat" type="d1:ObjectFormat"/>
            <xs:element name="size" type="xs:long"/>
            <xs:element name="checksum" type="d1:Checksum"
                minOccurs="1" maxOccurs="1" />

            <xs:element name="submitter" type="d1:Subject"/>
            <xs:element name="rightsHolder" type="d1:Subject"/>
            <xs:element name="accessPolicy" type="d1:AccessPolicy"
                minOccurs="0" maxOccurs="1" />
            <xs:element name="replicationPolicy" type="d1:ReplicationPolicy"
                minOccurs="0" maxOccurs="1" />

            <xs:element name="obsoletes" type="d1:Identifier" minOccurs="0"
                maxOccurs="unbounded"/>
            <xs:element name="obsoletedBy" type="d1:Identifier" minOccurs="0"
                maxOccurs="unbounded"/>
            <xs:element name="resourceMap" type="d1:Identifier" minOccurs="0"
                maxOccurs="unbounded"/>

            <xs:element name="dateUploaded" type="xs:dateTime"/>
            <xs:element name="dateSysMetadataModified" type="xs:dateTime"/>
            <xs:element name="originMemberNode" type="d1:NodeReference"/>
            <xs:element name="authoritativeMemberNode" type="d1:NodeReference"/>
            <xs:element name="replica" maxOccurs="unbounded" minOccurs="0" type="d1:Replica" />
 *
 *
 */

    protected String identifier = null;
    protected ObjectFormatContent objectFormat = null;
    protected Long size = null;
    protected MessageDigest digest = null;
    protected String submitter = null;
    protected String rightsHolder = null;
    protected DateState dateUploaded = null;
    protected DateState dateSysMetadataModified = null;
    protected String owner = null;
    protected String authoritativeMemberNode = null;
    protected String originMemberNode = null;

    /**
     * Constructor
     * @param identifier DataONE pid
     * @param owner in MRT this is the owner ARK
     * @param memberNode DataONE memberNode name
     * @param objectFormat Object format based on format list - note that this object
     * will be deprecated in the SystemMetadata and only the fmtid of this will be used
     * @param component file component information - used for extraction of fixity information
     * @throws TException
     */
    public SystemMetadataContent(
            String identifier,
            String owner,
            String memberNode,
            ObjectFormatContent objectFormat,
            FileComponent component)
        throws TException
    {
        this.identifier = identifier;
        this.owner = owner;
        this.authoritativeMemberNode = memberNode;
        this.originMemberNode = memberNode;
        this.objectFormat = objectFormat;
        validate(component);
        saveComponent(component);
    }

    protected void validate(FileComponent component)
        throws TException
    {
        notNull("identifier", identifier);
        notNull("owner", owner);
        notNull("originMemberNode", originMemberNode);
        notNull("authoritativeMemberNode", authoritativeMemberNode);
        notNull("component", component);
    }

    protected void saveComponent(FileComponent component)
    {
        setDateUploaded(component.getCreated());
        setDateSysMetadataModified(component.getCreated());
        setSize(component.getSize());
        setDigest(component.getMessageDigest());
        setRightsHolder(owner);
        setSubmitter(owner);
        setOriginMemberNode(originMemberNode);
        setAuthoritativeMemberNode(authoritativeMemberNode);
    }

    public String getAuthoritativeMemberNode() {
        return authoritativeMemberNode;
    }

    public void setAuthoritativeMemberNode(String authoritativeMemberNode) {
        this.authoritativeMemberNode = authoritativeMemberNode;
    }

    public DateState getDateSysMetadataModified() {
        return dateSysMetadataModified;
    }

    public void setDateSysMetadataModified(DateState dateSysMetadataModified) {
        this.dateSysMetadataModified = dateSysMetadataModified;
    }

    public DateState getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(DateState dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public MessageDigest getDigest() {
        return digest;
    }

    public void setDigest(MessageDigest digest) {
        this.digest = digest;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ObjectFormatContent getObjectFormat() {
        return objectFormat;
    }

    public void setObjectFormat(ObjectFormatContent objectFormat) {
        this.objectFormat = objectFormat;
    }

    public String getOriginMemberNode() {
        return originMemberNode;
    }

    public void setOriginMemberNode(String originMemberNode) {
        this.originMemberNode = originMemberNode;
    }

    public String getRightsHolder() {
        return rightsHolder;
    }

    public void setRightsHolder(String rightsHolder) {
        this.rightsHolder = rightsHolder;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    private void notNull(String name, Object object)
        throws TException
    {
        if (object == null) {
            throw new TException.INVALID_OR_MISSING_PARM("Required value not supplied:" + name);
        }
    }

    public String dump(String header)
    {
        String out = "SystemMetadataContent[" + header + "]:" + NL
            + " - identifier=" + identifier + NL
            + " - size=" + size + NL
            + " - digest=" + digest.toString() + NL
            + " - submitter=" + submitter + NL
            + " - rightsHolder=" + rightsHolder + NL
            + " - dateUploaded=" + dateUploaded.toString() + NL
            + " - dateSysMetadataModified=" + dateSysMetadataModified.toString() + NL
            + " - owner=" + owner + NL
            + " - originMemberNode=" + originMemberNode + NL
            + " - authoritativeMemberNode=" + authoritativeMemberNode + NL
            + " - objectFormatContent" + NL + "***>" + NL + objectFormat.dump("") + "<***" + NL;
        return out;
    }

}
