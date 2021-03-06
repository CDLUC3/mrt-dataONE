
package org.dataone.ns.service.types.v1;

import java.util.ArrayList;
import java.util.List;

/** 
 * *Person* represents metadata about a :term:`Principal`
 that is a person and that can be used by clients and nodes for
 :class:`Types.AccessPolicy` information. The mutable properties of a
 *Person* instance can only be changed by itself (i.e., the Subject
 identifying the Person instance) and by the Coordinating Node identity,
 but can be read by any identity in the DataONE system.

 * 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://ns.dataone.org/service/types/v1" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="Person">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="ns:Subject" name="subject" minOccurs="1" maxOccurs="1"/>
 *     &lt;xs:element type="xs:string" name="givenName" minOccurs="1" maxOccurs="unbounded"/>
 *     &lt;xs:element type="xs:string" name="familyName" minOccurs="1" maxOccurs="1"/>
 *     &lt;xs:element type="xs:string" name="email" minOccurs="0" maxOccurs="unbounded"/>
 *     &lt;xs:element type="ns:Subject" name="isMemberOf" minOccurs="0" maxOccurs="unbounded"/>
 *     &lt;xs:element type="ns:Subject" name="equivalentIdentity" minOccurs="0" maxOccurs="unbounded"/>
 *     &lt;xs:element type="xs:boolean" name="verified" minOccurs="0" maxOccurs="1"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class Person
{
    private Subject subject;
    private List<String> givenNameList = new ArrayList<String>();
    private String familyName;
    private List<String> emailList = new ArrayList<String>();
    private List<Subject> isMemberOfList = new ArrayList<Subject>();
    private List<Subject> equivalentIdentityList = new ArrayList<Subject>();
    private Boolean verified;

    /** 
     * Get the 'subject' element value. The unique, immutable identifier for the
     *Person*.
     * 
     * @return value
     */
    public Subject getSubject() {
        return subject;
    }

    /** 
     * Set the 'subject' element value. The unique, immutable identifier for the
     *Person*.
     * 
     * @param subject
     */
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    /** 
     * Get the list of 'givenName' element items. The given name of the *Person*, repeatable if they
            have more than one given name.
     * 
     * @return list
     */
    public List<String> getGivenNameList() {
        return givenNameList;
    }

    /** 
     * Set the list of 'givenName' element items. The given name of the *Person*, repeatable if they
            have more than one given name.
     * 
     * @param list
     */
    public void setGivenNameList(List<String> list) {
        givenNameList = list;
    }

    /** 
     * Get the 'familyName' element value. The family name of the *Person*.
     * 
     * @return value
     */
    public String getFamilyName() {
        return familyName;
    }

    /** 
     * Set the 'familyName' element value. The family name of the *Person*.
     * 
     * @param familyName
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    /** 
     * Get the list of 'email' element items. The email address of the *Person*, repeatable if
            they have more than one email address. 
     * 
     * @return list
     */
    public List<String> getEmailList() {
        return emailList;
    }

    /** 
     * Set the list of 'email' element items. The email address of the *Person*, repeatable if
            they have more than one email address. 
     * 
     * @param list
     */
    public void setEmailList(List<String> list) {
        emailList = list;
    }

    /** 
     * Get the list of 'isMemberOf' element items. A *group* or role in which the *Person* is a member,
            expressed using the unique :class:`Types.Subject` identifier for
            that :class:`Types.Group`, and repeatable if they are a member of
            more than one group. 
     * 
     * @return list
     */
    public List<Subject> getIsMemberOfList() {
        return isMemberOfList;
    }

    /** 
     * Set the list of 'isMemberOf' element items. A *group* or role in which the *Person* is a member,
            expressed using the unique :class:`Types.Subject` identifier for
            that :class:`Types.Group`, and repeatable if they are a member of
            more than one group. 
     * 
     * @param list
     */
    public void setIsMemberOfList(List<Subject> list) {
        isMemberOfList = list;
    }

    /** 
     * Get the list of 'equivalentIdentity' element items. An alternative but equivalent identity for the
            :term:`principal` that has been used in alternate identity systems,
            repeatable if more than one equivalent identity applies.
            
     * 
     * @return list
     */
    public List<Subject> getEquivalentIdentityList() {
        return equivalentIdentityList;
    }

    /** 
     * Set the list of 'equivalentIdentity' element items. An alternative but equivalent identity for the
            :term:`principal` that has been used in alternate identity systems,
            repeatable if more than one equivalent identity applies.
            
     * 
     * @param list
     */
    public void setEquivalentIdentityList(List<Subject> list) {
        equivalentIdentityList = list;
    }

    /** 
     * Get the 'verified' element value. *true* if the name and email address of the
     *Person* have been :term:`verified` to ensure that the *givenName*
            and *familyName* represent the real person's legal name, and that
            the email address is correct for that person and is in the control
            of the indicated individual. Verification occurs through an
            established procedure within DataONE as part of the Identity
            Management system. A Person can not change their own *verified*
            field, but rather must be verified and changed through this DataONE
            established process. 
     * 
     * @return value
     */
    public Boolean getVerified() {
        return verified;
    }

    /** 
     * Set the 'verified' element value. *true* if the name and email address of the
     *Person* have been :term:`verified` to ensure that the *givenName*
            and *familyName* represent the real person's legal name, and that
            the email address is correct for that person and is in the control
            of the indicated individual. Verification occurs through an
            established procedure within DataONE as part of the Identity
            Management system. A Person can not change their own *verified*
            field, but rather must be verified and changed through this DataONE
            established process. 
     * 
     * @param verified
     */
    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
