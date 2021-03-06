
package org.dataone.ns.service.types.v1;

import java.util.Date;

/** 
 * Metadata about an object, representing a subset of the
 metadata found in :class:`Types.SystemMetadata`.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="ObjectInfo">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:Identifier" name="identifier" minOccurs="1" maxOccurs="1"/>
 *     &lt;xs:element type="xs:string" name="formatId"/>
 *     &lt;xs:element type="ns:Checksum" name="checksum" minOccurs="1" maxOccurs="1"/>
 *     &lt;xs:element type="xs:dateTime" name="dateSysMetadataModified"/>
 *     &lt;xs:element type="xs:long" name="size"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class ObjectInfo
{
    private Identifier identifier;
    private String formatId;
    private Checksum checksum;
    private Date dateSysMetadataModified;
    private long size;

    /** 
     * Get the 'identifier' element value.
     * 
     * @return value
     */
    public Identifier getIdentifier() {
        return identifier;
    }

    /** 
     * Set the 'identifier' element value.
     * 
     * @param identifier
     */
    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    /** 
     * Get the 'formatId' element value.
     * 
     * @return value
     */
    public String getFormatId() {
        return formatId;
    }

    /** 
     * Set the 'formatId' element value.
     * 
     * @param formatId
     */
    public void setFormatId(String formatId) {
        this.formatId = formatId;
    }

    /** 
     * Get the 'checksum' element value.
     * 
     * @return value
     */
    public Checksum getChecksum() {
        return checksum;
    }

    /** 
     * Set the 'checksum' element value.
     * 
     * @param checksum
     */
    public void setChecksum(Checksum checksum) {
        this.checksum = checksum;
    }

    /** 
     * Get the 'dateSysMetadataModified' element value.
     * 
     * @return value
     */
    public Date getDateSysMetadataModified() {
        return dateSysMetadataModified;
    }

    /** 
     * Set the 'dateSysMetadataModified' element value.
     * 
     * @param dateSysMetadataModified
     */
    public void setDateSysMetadataModified(Date dateSysMetadataModified) {
        this.dateSysMetadataModified = dateSysMetadataModified;
    }

    /** 
     * Get the 'size' element value.
     * 
     * @return value
     */
    public long getSize() {
        return size;
    }

    /** 
     * Set the 'size' element value.
     * 
     * @param size
     */
    public void setSize(long size) {
        this.size = size;
    }
}
