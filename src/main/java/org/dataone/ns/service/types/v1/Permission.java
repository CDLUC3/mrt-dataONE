
package org.dataone.ns.service.types.v1;

/** 
 * A string value indicating the set of actions that can
 be performed on a resource as specified in an access policy. The set of
 permissions include the ability to read a resource (*read*), modify a
 resource (*write*), and to change the set of access control policies for
 a resource (*changePermission*). Permission levels are cumulative, in
 that write permission implicitly grants read access, and
 changePermission permission implicitly grants write access (and
 therefore read as well). If a subject is granted multiple permissions,
 the highest level of access applies.
 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:simpleType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="Permission">
 *   &lt;xs:restriction base="xs:string">
 *     &lt;xs:enumeration value="read"/>
 *     &lt;xs:enumeration value="write"/>
 *     &lt;xs:enumeration value="changePermission"/>
 *   &lt;/xs:restriction>
 * &lt;/xs:simpleType>
 * </pre>
 */
public enum Permission {
    READ("read"), WRITE("write"), CHANGE_PERMISSION("changePermission");
    private final String value;

    private Permission(String value) {
        this.value = value;
    }

    public String xmlValue() {
        return value;
    }

    public static Permission convert(String value) {
        for (Permission inst : values()) {
            if (inst.xmlValue().equals(value)) {
                return inst;
            }
        }
        return null;
    }
}
