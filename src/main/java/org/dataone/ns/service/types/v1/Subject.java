
package org.dataone.ns.service.types.v1;

/** 
 * An identifier for a Person (user), Group,
 Organization, or System.The :term:`Subject` is a string that provides a formal
 name to identify a user or group in the DataONE Identity Management
 Service. The *subject* is represented by a unique, persistent,
 non-reassignable identifier string that follows the same constraints as
 :class:`Types.Identifier`. Subjects are immutable and can not be 
 deleted.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="Subject">
 *   &lt;xs:simpleContent>
 *     &lt;xs:extension base="xs:string"/>
 *   &lt;/xs:simpleContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class Subject
{
    private String string;

    /** 
     * Get the 'Subject' complexType value.
     * 
     * @return value
     */
    public String getString() {
        return string;
    }

    /** 
     * Set the 'Subject' complexType value.
     * 
     * @param string
     */
    public void setString(String string) {
        this.string = string;
    }
}
