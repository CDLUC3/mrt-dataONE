
package org.dataone.ns.service.types.v1;

import java.util.ArrayList;
import java.util.List;

/** 
 *  A list of :term:`Subjects` used for identity/group
 management
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="SubjectList">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:Subject" name="subject" minOccurs="0" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class SubjectList
{
    private List<Subject> subjectList = new ArrayList<Subject>();

    /** 
     * Get the list of 'subject' element items.
     * 
     * @return list
     */
    public List<Subject> getSubjectList() {
        return subjectList;
    }

    /** 
     * Set the list of 'subject' element items.
     * 
     * @param list
     */
    public void setSubjectList(List<Subject> list) {
        subjectList = list;
    }
}
