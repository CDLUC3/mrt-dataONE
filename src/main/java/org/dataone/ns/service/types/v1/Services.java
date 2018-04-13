
package org.dataone.ns.service.types.v1;

import java.util.ArrayList;
import java.util.List;

/** 
 * A list of services that are provided by a node. Used
 in Node descriptions so that Nodes can provide metadata about each
 service they implement and support. 
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="Services">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:Service" name="service" minOccurs="1" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class Services
{
    private List<Service> serviceList = new ArrayList<Service>();

    /** 
     * Get the list of 'service' element items.
     * 
     * @return list
     */
    public List<Service> getServiceList() {
        return serviceList;
    }

    /** 
     * Set the list of 'service' element items.
     * 
     * @param list
     */
    public void setServiceList(List<Service> list) {
        serviceList = list;
    }
}
