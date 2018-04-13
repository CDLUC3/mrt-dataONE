/*
Copyright (c) 2005-2012, Regents of the University of California
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:
 *
- Redistributions of source code must retain the above copyright notice,
  this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.
- Neither the name of the University of California nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
OF THE POSSIBILITY OF SUCH DAMAGE.
**********************************************************/
package org.cdlib.mrt.dataone.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

import org.cdlib.mrt.core.MessageDigest;
import org.cdlib.mrt.utility.FileUtil;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.dataone.content.ObjectFormatContent;
import org.cdlib.mrt.dataone.content.SystemMetadataContent;

import org.dataone.ns.service.types.v1.*;

/**
 * This class is used to construct SystemMetadata. The input is the SystemMetadataContent object that
 * contains the necessary variables for build the SystemMetadataXML.
 *
 * This routine uses the codegen source expanded from jibx/dataoneTypes.xsd. Maven is used for the
 * needed jibx binding to create the output SystemMetadata.xml.
 *
 * Note, with changes to ObjectFormat several elements in dataoneTypes (primarily FormatObject) will
 * be dropped.
 * @author dloy
 */
public class SystemMetadataXML
{
    protected final static boolean DEBUG = false;
    protected SystemMetadataContent content = null;
    protected File outFile = null;
    protected SystemMetadata systemMetadata = new SystemMetadata();

    public SystemMetadataXML(SystemMetadataContent content, File outFile)
        throws TException
    {
        this.content = content;
        this.outFile = outFile;
        buildSystemMetadata();
    }

    public void buildSystemMetadata()
        throws TException
    {
        systemMetadata.setIdentifier(getIdentifier(content.getIdentifier()));
        systemMetadata.setDateUploaded(content.getDateUploaded().getDate());
        systemMetadata.setDateSysMetadataModified(content.getDateSysMetadataModified().getDate());
        systemMetadata.setOriginMemberNode(getNodeReference(content.getOriginMemberNode()));
        systemMetadata.setAuthoritativeMemberNode(getNodeReference(content.getAuthoritativeMemberNode()));
        systemMetadata.setSubmitter(getSubject(content.getSubmitter()));
        systemMetadata.setRightsHolder(getSubject(content.getRightsHolder()));
        systemMetadata.setFormatId(getFmtid(content.getObjectFormat()));
        systemMetadata.setSize(content.getSize());
        systemMetadata.setChecksum(getChecksum(content.getDigest()));
        systemMetadata.setSerialVersion(new Long(1));
        Subject subject = new Subject();
        subject.setString("public");
        AccessRule publicAllowRule = new AccessRule();
        ArrayList<Subject> subjectList = new ArrayList();
        subjectList.add(subject);
        ArrayList<Permission> permissionList = new ArrayList();
        permissionList.add(Permission.READ);
        publicAllowRule.setSubjectList(subjectList);
        publicAllowRule.setPermissionList(permissionList);
        ArrayList<AccessRule> accessRules = new ArrayList();
        accessRules.add(publicAllowRule);
        AccessPolicy policy = new AccessPolicy();
        policy.setAllowList(accessRules);
        systemMetadata.setAccessPolicy(policy);

    }

    private Identifier getIdentifier(String value)
    {
        Identifier identifier = new Identifier();
        identifier.setString(value);
        return identifier;
    }

    private Subject getSubject(String value)
    {
        Subject subject = new Subject();
        subject.setString(value);
        return subject;
    }

    private NodeReference getNodeReference(String value)
    {
        NodeReference reference = new NodeReference();
        reference.setString(value);
        return reference;
    }

    private ObjectFormat getObjectFormat(ObjectFormatContent objectFormatContent)
    {
        ObjectFormat objectFormat = new ObjectFormat();
        objectFormat.setFormatId(objectFormatContent.getFmtid());
        objectFormat.setFormatName(objectFormatContent.getFormatName());
        objectFormat.setFormatType(objectFormatContent.getFormatType());
        return objectFormat;
    }



    private String getFmtid(ObjectFormatContent objectFormatContent)
    {
        return objectFormatContent.getFmtid();
    }

    private Checksum getChecksum(MessageDigest digest)
    {
        Checksum checksum = new Checksum();
        String algrithm = digest.getJavaAlgorithm();
        String value = digest.getValue();
        checksum.setAlgorithm(algrithm);
        checksum.setString(value);
        return checksum;
    }

    public void buildXML()
        throws TException
    {
         try {

            // unmarshal customer information from file
            IBindingFactory bfact = BindingDirectory.getFactory(SystemMetadata.class);
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(2);
            FileOutputStream out = new FileOutputStream(outFile);
            mctx.setOutput(out, null);
            mctx.marshalDocument(systemMetadata);
            if (DEBUG) {
                String xml = FileUtil.file2String(outFile);
                System.out.println("SystemMetadataXML - xml=" + xml);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (JiBXException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}