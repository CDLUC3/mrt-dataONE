
package org.dataone.ns.service.types.v1;

/** 
 * An :term:`identifier` (:term:`PID`) in the DataONE
 system that is used to uniquely and globally identify an object.
 Identifiers can not be reused once assigned. Identifiers can not be 
 deleted from the DataONE system.Identifiers are represented by a Unicode 
 string of printable characters, excluding :term:`whitespace`. All 
 representations of identifiers must be encoded in 7-bit ASCII or 
 UTF-8.Identifiers have a maximum length of 800 characters,
 and a variety of other properties designed for preservation and
 longevity. Some discussion on this is described in the `PID
 documentation`_ and in decision `ticket 577`_. .. _ticket 577: https://redmine.dataone.org/issues/577
 .. _PID documentation: http://mule1.dataone.org/ArchitectureDocs-current/design/PIDs.html

 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="Identifier">
 *   &lt;xs:simpleContent>
 *     &lt;xs:extension base="xs:string"/>
 *   &lt;/xs:simpleContent>
 * &lt;/xs:complexType>
 * </pre>
 */
public class Identifier
{
    private String string;

    /** 
     * Get the 'Identifier' complexType value.
     * 
     * @return value
     */
    public String getString() {
        return string;
    }

    /** 
     * Set the 'Identifier' complexType value.
     * 
     * @param string
     */
    public void setString(String string) {
        this.string = string;
    }
}
