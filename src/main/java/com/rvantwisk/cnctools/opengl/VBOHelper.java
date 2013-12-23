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

package com.rvantwisk.cnctools.opengl;

import org.lwjgl.opengl.GL15;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by rvt on 12/10/13.
 */
public class VBOHelper {

    public void add(VBOInfo vbo) {
        this.vboList.add(vbo);
    }

    public static class VBOInfo {
        public int vbID = -1;
        public int vbRows = 0;
        public float colorR = 1.0f;
        public float colorG = 1.0f;
        public float colorB = 1.0f;
        public float colorA = 1.0f;
        public boolean draw = true;
        public boolean hasOwnColor = false;
        public float[] data;
    }

    private final List<VBOInfo> vboList = new ArrayList<>();


    public List<VBOInfo> getVboList() {
        return Collections.unmodifiableList(vboList);
    }

    public void destroy() {
        removeAll();
    }


    public static VBOInfo createVBO(final float[] data, int i, boolean b) {
        VBOInfo vbo = new VBOInfo();
        vbo.vbRows = i;
        vbo.hasOwnColor = b;
        vbo.data = data;

        return vbo;
    }

    public void removeVBO(final VBOInfo vboInfo) {
        vboList.remove(vboInfo);
        GL15.glDeleteBuffers(vboInfo.vbID);
    }

    public void removeAll() {
        Iterator<VBOInfo> vboInfo = vboList.iterator();
        while (vboInfo.hasNext()) {
            GL15.glDeleteBuffers(vboInfo.next().vbID);
            vboInfo.remove();
        }
    }
}
