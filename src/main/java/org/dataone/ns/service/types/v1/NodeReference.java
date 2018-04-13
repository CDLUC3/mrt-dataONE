
package org.dataone.ns.service.types.v1;

/** 
 * A unique identifier for a DataONE Node. The
 *NodeReference* must be unique across nodes, and must always be
 assigned to one Member or Coordinating Node instance even in the event of 
 the *BaseURL* or other characteristics changing.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="NodeReference">
 *   &lt;xs:simpleContent>
 *     &lt;xs:extension base="xs:string"/>
 *   &lt;/xs:simpleContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class NodeReference
{
    private String string;

    /** 
     * Get the 'NodeReference' complexType value.
     * 
     * @return value
     */
    public String getString() {
        return string;
    }

    /** 
     * Set the 'NodeReference' complexType value.
     * 
     * @param string
     */
    public void setString(String string) {
        this.string = string;
    }
}
