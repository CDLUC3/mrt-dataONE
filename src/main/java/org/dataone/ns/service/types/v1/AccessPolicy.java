
package org.dataone.ns.service.types.v1;

import java.util.ArrayList;
import java.util.List;

/** 
 * A set of rules that specifies as a whole the allowable
 permissions that a given user, group, or system has for accessing a
 resource, including data, metadata, resource map, and service resources.
 An access policy consists of a sequence of allow rules that grant
 permissions to principals, which can be individual users, groups of
 users, symbolic users, or systems and services.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="AccessPolicy">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:AccessRule" name="allow" minOccurs="1" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class AccessPolicy
{
    private List<AccessRule> allowList = new ArrayList<AccessRule>();

    /** 
     * Get the list of 'allow' element items.
     * 
     * @return list
     */
    public List<AccessRule> getAllowList() {
        return allowList;
    }

    /** 
     * Set the list of 'allow' element items.
     * 
     * @param list
     */
    public void setAllowList(List<AccessRule> list) {
        allowList = list;
    }
}
