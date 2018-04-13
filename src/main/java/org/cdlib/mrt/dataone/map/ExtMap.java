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
package org.cdlib.mrt.dataone.map;

import java.util.Hashtable;

import org.cdlib.mrt.utility.TException;
import org.cdlib.mrt.utility.StringUtil;

/**
 * this class is used for the basic processing of the Resource Manifest.
 *
 * There are two major functions provided by the Resource manifest:
 * 1. Content information for constructing the ORE Resource Map used by DataONE
 * mapList provides this content
 * 2. Identification of each file to be sent on to DataONE with identification of
 * the DataONE format for that content.
 * resourceTable provides this concontent.
 *
 * @author dloy
 */
public class ExtMap
{
    private static final String NAME = "ExtMap";
    private static final String MESSAGE = NAME + ": ";

    protected Hashtable<String,String> extMap = new Hashtable(20);

    public ExtMap()
    {
        setExtMap();
    }
    
    protected void setExtMap()
    {
        setExt("csv", "text/csv");
        setExt("xls", "application/vnd.ms-excel");
        setExt("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }
    
    protected void setExt(String ext, String mime)
    {
        extMap.put(ext, mime);
    }
    
    public String getExt(String name, String passedMime)
    {
        if (StringUtil.isEmpty(name)) return null;
        if (StringUtil.isEmpty(passedMime)) return null;
        int pos = name.lastIndexOf(".");
        if (pos < 0) return passedMime;
        String ext = name.substring(pos+1);
        String map = extMap.get(ext);
        if (map == null) return passedMime;
        return map;
    }

}
