
package org.dataone.ns.service.types.v1;

import java.util.ArrayList;
import java.util.List;

/** 
 * A list of :term:`Subjects`, including both
 :class:`Types.Person` and :class:`Types.Group` entries returned from
 the :func:`CNIdentity.getSubjectInfo` service and
 :func:`CNIdentity.listSubjects` services.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="SubjectInfo">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:Person" name="person" minOccurs="0" maxOccurs="unbounded"/>
 *     &lt;xs:element type="ns:Group" name="group" minOccurs="0" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class SubjectInfo
{
    private List<Person> personList = new ArrayList<Person>();
    private List<Group> groupList = new ArrayList<Group>();

    /** 
     * Get the list of 'person' element items.
     * 
     * @return list
     */
    public List<Person> getPersonList() {
        return personList;
    }

    /** 
     * Set the list of 'person' element items.
     * 
     * @param list
     */
    public void setPersonList(List<Person> list) {
        personList = list;
    }

    /** 
     * Get the list of 'group' element items.
     * 
     * @return list
     */
    public List<Group> getGroupList() {
        return groupList;
    }

    /** 
     * Set the list of 'group' element items.
     * 
     * @param list
     */
    public void setGroupList(List<Group> list) {
        groupList = list;
    }
}
