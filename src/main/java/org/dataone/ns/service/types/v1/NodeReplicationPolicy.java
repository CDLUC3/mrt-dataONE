
package org.dataone.ns.service.types.v1;

import java.util.ArrayList;
import java.util.List;

/** 
 * The overall replication policy for the node that
 expresses constraints on object size, total objects, source nodes, and
 object format types. A node may choose to restrict replication from only
 certain peer nodes, may have file size limits, total allocated size
 limits, or may want to focus on being a :term:`replication target` for
 domain-specific object formats.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="NodeReplicationPolicy">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="xs:long" name="maxObjectSize" minOccurs="0" maxOccurs="1"/>
 *     &lt;xs:element type="xs:long" name="spaceAllocated" minOccurs="0" maxOccurs="1"/>
 *     &lt;xs:element type="ns:NodeReference" name="allowedNode" minOccurs="0" maxOccurs="unbounded"/>
 *     &lt;xs:element type="xs:string" name="allowedObjectFormat" minOccurs="0" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class NodeReplicationPolicy
{
    private Long maxObjectSize;
    private Long spaceAllocated;
    private List<NodeReference> allowedNodeList = new ArrayList<NodeReference>();
    private List<String> allowedObjectFormatList = new ArrayList<String>();

    /** 
     * Get the 'maxObjectSize' element value. An optional statement of the maximum size in octets 
            (8-bit bytes) of objects this node is willing to accept for
            replication.
     * 
     * @return value
     */
    public Long getMaxObjectSize() {
        return maxObjectSize;
    }

    /** 
     * Set the 'maxObjectSize' element value. An optional statement of the maximum size in octets 
            (8-bit bytes) of objects this node is willing to accept for
            replication.
     * 
     * @param maxObjectSize
     */
    public void setMaxObjectSize(Long maxObjectSize) {
        this.maxObjectSize = maxObjectSize;
    }

    /** 
     * Get the 'spaceAllocated' element value. An optional statement of the total space in bytes
            allocated for replication object storage on this
            node.
     * 
     * @return value
     */
    public Long getSpaceAllocated() {
        return spaceAllocated;
    }

    /** 
     * Set the 'spaceAllocated' element value. An optional statement of the total space in bytes
            allocated for replication object storage on this
            node.
     * 
     * @param spaceAllocated
     */
    public void setSpaceAllocated(Long spaceAllocated) {
        this.spaceAllocated = spaceAllocated;
    }

    /** 
     * Get the list of 'allowedNode' element items. An optional, repeatable statement of a peer source
            node from which this node is willing to replicate content, expressed
            as a :class:`Types.NodeReference`.
     * 
     * @return list
     */
    public List<NodeReference> getAllowedNodeList() {
        return allowedNodeList;
    }

    /** 
     * Set the list of 'allowedNode' element items. An optional, repeatable statement of a peer source
            node from which this node is willing to replicate content, expressed
            as a :class:`Types.NodeReference`.
     * 
     * @param list
     */
    public void setAllowedNodeList(List<NodeReference> list) {
        allowedNodeList = list;
    }

    /** 
     * Get the list of 'allowedObjectFormat' element items. An optional, repeatable statement of an object
            format that this node is willing to replicate, expressed as a
            :class:`Types.ObjectFormatIdentifier`.
     * 
     * @return list
     */
    public List<String> getAllowedObjectFormatList() {
        return allowedObjectFormatList;
    }

    /** 
     * Set the list of 'allowedObjectFormat' element items. An optional, repeatable statement of an object
            format that this node is willing to replicate, expressed as a
            :class:`Types.ObjectFormatIdentifier`.
     * 
     * @param list
     */
    public void setAllowedObjectFormatList(List<String> list) {
        allowedObjectFormatList = list;
    }
}
