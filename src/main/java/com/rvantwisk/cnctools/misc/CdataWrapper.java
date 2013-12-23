/*
 * Copyright (c) 2013, R. van Twisk
 * All rights reserved.
 * Licensed under the The BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://opensource.org/licenses/BSD-3-Clause
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of the aic-util nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.rvantwisk.cnctools.misc;

import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;

import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * Wrapper of CData to waro saome string's into CDATA tags
 * use this with xstream example:
 *
 * XStream xStream = new XStream(new XppDomDriver() {
 *  public HierarchicalStreamWriter createWriter(Writer out) {
 *  CdataWrapper cdw = new CdataWrapper(out);
 *  cdw.setToWrap(new String[]{"gcode"});
 *  return cdw;
 *  }
 *  }
 * );
 */
public class CdataWrapper extends PrettyPrintWriter
{
    private boolean toBeWrapped = false;
    private List<String> toWrap;

    /**
     * Fired everytime a node is about to be emitted.
     * If the node's name is 'comment', we flag toBeWrapped to true
     * @param s The name of the node.
     */
    public void startNode(String s)
    {
        if(toWrap.contains(s) || toWrap.contains("CDATA"))
        {
            toBeWrapped = true;
        }
        super.startNode(s);
    }

    /**
     * Fired everytime a node is closed.
     * We always reset toBeWrapped to false.
     */
    public void endNode()
    {
        toBeWrapped = false;
        super.endNode();
    }

    /**
     * If toBeWrapped is true, then we wrap up the content with
     * the correct CDATA start/end markers. Also, we escape the CDATA end tag,
     * if present in the data.
     * @param quickWriter
     * @param s
     */
    protected void writeText(QuickWriter quickWriter, String s)
    {
        if(toBeWrapped && s != null)
        {
            quickWriter.write("<![CDATA[");
            // XStream doesn't escape the > in ]]>.
            // Let's do it for them!
            s = s.replaceAll("]]>", "]]&gt;");
            quickWriter.write(s);
            quickWriter.write("]]>");
        }
        else
        {
            super.writeText(quickWriter, s);
        }
    }

    // --------- CONSTRUCTORS ---------==

    public CdataWrapper(Writer writer, char[] chars, String s, XmlFriendlyReplacer xmlFriendlyReplacer)
    {
        super(writer, chars, s, xmlFriendlyReplacer);
    }

    public CdataWrapper(Writer writer, char[] chars, String s)
    {
        super(writer, chars, s);
    }

    public CdataWrapper(Writer writer, char[] chars)
    {
        super(writer, chars);
    }

    public CdataWrapper(Writer writer, String s, String s1)
    {
        super(writer, s, s1);
    }

    public CdataWrapper(Writer writer, String s)
    {
        super(writer, s);
    }

    public CdataWrapper(Writer writer, XmlFriendlyReplacer xmlFriendlyReplacer)
    {
        super(writer, xmlFriendlyReplacer);
    }

    public CdataWrapper(Writer writer)
    {
        super(writer);
    }

    public void setToWrap(String[] toWrap) {
        this.toWrap = (List<String>) Arrays.asList(toWrap);
    }

}