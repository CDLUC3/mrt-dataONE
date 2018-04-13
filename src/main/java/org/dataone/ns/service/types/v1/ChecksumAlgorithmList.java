
package org.dataone.ns.service.types.v1;

import java.util.ArrayList;
import java.util.List;

/** 
 * Represents a list of :term:`checksum`
 algorithms.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="ChecksumAlgorithmList">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="xs:string" name="algorithm" minOccurs="1" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class ChecksumAlgorithmList
{
    private List<String> algorithmList = new ArrayList<String>();

    /** 
     * Get the list of 'algorithm' element items.
     * 
     * @return list
     */
    public List<String> getAlgorithmList() {
        return algorithmList;
    }

    /** 
     * Set the list of 'algorithm' element items.
     * 
     * @param list
     */
    public void setAlgorithmList(List<String> list) {
        algorithmList = list;
    }
}
