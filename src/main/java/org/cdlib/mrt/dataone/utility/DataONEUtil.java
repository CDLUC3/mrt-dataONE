/*
Copyright (c) 2005-2010, Regents of the University of California
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
package org.cdlib.mrt.dataone.utility;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.cdlib.mrt.core.Identifier;
import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.StringUtil;
/**
 * General validation methods command line info
 * @author dloy
 */
public class DataONEUtil
{
    private static final String NAME = "DataONEUtil";
    private static final String MESSAGE = NAME + ": ";
    private static final String PRODUCER = "producer/";

    public static String getPid(Identifier objectID, int versionID, String fileID)
    {
        if (fileID.startsWith(PRODUCER))
            fileID = fileID.substring(PRODUCER.length());
        String normFileID = objectID.getValue() + "/" + versionID + "/" + fileID;
        return normFileID.replaceAll(" ", "__");
    }
    
    public static void notNull(String name, Object object)
        throws TException
    {
        if (object == null) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "Required value not supplied:" + name);
        }
    }

    public static void notEmpty(String name, String object)
        throws TException
    {
        if (StringUtil.isEmpty(object)) {
            throw new TException.INVALID_OR_MISSING_PARM(MESSAGE + "Required value is Empty:" + name);
        }
    }


    public static String getFileID(File sourceLocation, File contentFile)
        throws TException
    {
        try {
            String nameSource = sourceLocation.getCanonicalPath();
            String nameContent = contentFile.getCanonicalPath();
            System.out.println("nameSource=" + nameSource);
            System.out.println("nameContent=" + nameContent);
            if (!nameContent.startsWith(nameSource)) {
                String err = MESSAGE + "getFileID sourceDirectory not subelement of contentFile:"
                        + " - nameSource=" + nameSource
                        + " - nameContent=" + nameContent;
                throw new TException.INVALID_DATA_FORMAT(err);
            }
            return nameContent.substring(nameSource.length() + 1);

        } catch(Exception ex) {
            String err = MESSAGE + "getFileID - Exception:" + ex;
            throw new TException.GENERAL_EXCEPTION( err);
        }
    }
    
    public static String getFileExt(String fileID)
    {
        if (StringUtil.isEmpty(fileID)) return null;
        int pos = fileID.lastIndexOf('.');
        if (pos < 0) return null;
        String ext = fileID.substring(pos + 1);
        if (ext.length() > 6) return null;
        return ext;
    }
    
}
