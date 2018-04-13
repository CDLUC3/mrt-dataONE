
package org.dataone.ns.service.types.v1;

import java.util.ArrayList;
import java.util.List;

/** 
 * Represents a collection of :class:`Types.LogEntry`
 elements, used to transfer log information between DataONE
 components.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="Log">
 *   &lt;xs:complexContent>
 *     &lt;xs:extension base="ns:Slice">
 *       &lt;xs:sequence>
 *         &lt;xs:element type="ns:LogEntry" name="logEntry" minOccurs="0" maxOccurs="unbounded"/>
 *       &lt;/xs:sequence>
 *     &lt;/xs:extension>
 *   &lt;/xs:complexContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class Log extends Slice
{
    private List<LogEntry> logEntryList = new ArrayList<LogEntry>();

    /** 
     * Get the list of 'logEntry' element items.
     * 
     * @return list
     */
    public List<LogEntry> getLogEntryList() {
        return logEntryList;
    }

    /** 
     * Set the list of 'logEntry' element items.
     * 
     * @param list
     */
    public void setLogEntryList(List<LogEntry> list) {
        logEntryList = list;
    }
}
