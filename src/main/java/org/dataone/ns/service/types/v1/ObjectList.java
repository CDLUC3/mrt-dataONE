
package org.dataone.ns.service.types.v1;

import java.util.ArrayList;
import java.util.List;

/** 
 * A list of object locations (nodes) from which the
 object can be retrieved. 
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="ObjectList">
 *   &lt;xs:complexContent>
 *     &lt;xs:extension base="ns:Slice">
 *       &lt;xs:sequence>
 *         &lt;xs:element type="ns:ObjectInfo" name="objectInfo" minOccurs="0" maxOccurs="unbounded"/>
 *       &lt;/xs:sequence>
 *     &lt;/xs:extension>
 *   &lt;/xs:complexContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class ObjectList extends Slice
{
    private List<ObjectInfo> objectInfoList = new ArrayList<ObjectInfo>();

    /** 
     * Get the list of 'objectInfo' element items.
     * 
     * @return list
     */
    public List<ObjectInfo> getObjectInfoList() {
        return objectInfoList;
    }

    /** 
     * Set the list of 'objectInfo' element items.
     * 
     * @param list
     */
    public void setObjectInfoList(List<ObjectInfo> list) {
        objectInfoList = list;
    }
}
