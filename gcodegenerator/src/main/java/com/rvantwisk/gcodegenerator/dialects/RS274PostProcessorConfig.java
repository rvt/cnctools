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

package com.rvantwisk.gcodegenerator.dialects;

import com.rvantwisk.gcodegenerator.interfaces.PostProcessorConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Standard RS274 post processor confoigurations
 */
public class RS274PostProcessorConfig implements PostProcessorConfig {
    private boolean hasToolChanger=false;
    private String preamble="";
    private String postamble="";
    private int decimalsF=3;
    private int decimalsS=3;
    private int decimalsOthers=3;
    Map<String, String> axisMapping = new HashMap<>();
    Map<String, Integer> axisDecimals = new HashMap<>();
    
    public RS274PostProcessorConfig() {
        axisDecimals.put("A", 4);
        axisDecimals.put("B", 4);
        axisDecimals.put("C", 4);
        axisDecimals.put("X", 4);
        axisDecimals.put("Y", 4);
        axisDecimals.put("Z", 4);
        axisDecimals.put("U", 4);
        axisDecimals.put("V", 4);
        axisDecimals.put("W", 4);

        axisMapping.put("A", "A");
        axisMapping.put("B", "B");
        axisMapping.put("C", "C");
        axisMapping.put("X", "X");
        axisMapping.put("Y", "Y");
        axisMapping.put("Z", "Z");
        axisMapping.put("U", "U");
        axisMapping.put("V", "V");
        axisMapping.put("W", "W");

    }

    public boolean isHasToolChanger() {
        return hasToolChanger;
    }

    public void setHasToolChanger(boolean hasToolChanger) {
        this.hasToolChanger = hasToolChanger;
    }

    public String getPreamble() {
        return preamble;
    }

    public void setPreamble(String preamble) {
        this.preamble = preamble;
    }

    public String getPostamble() {
        return postamble;
    }

    public void setPostamble(String postamble) {
        this.postamble = postamble;
    }

    public int getDecimalsF() {
        return decimalsF;
    }

    public void setDecimalsF(int decimalsF) {
        this.decimalsF = decimalsF;
    }

    public int getDecimalsS() {
        return decimalsS;
    }

    public void setDecimalsS(int decimalsS) {
        this.decimalsS = decimalsS;
    }

    public int getDecimalsOthers() {
        return decimalsOthers;
    }

    public void setDecimalsOthers(int decimalsOthers) {
        this.decimalsOthers = decimalsOthers;
    }

    public Map<String, String> getAxisMapping() {
        return axisMapping;
    }

    public void setAxisMapping(Map<String, String> axisMapping) {
        this.axisMapping = axisMapping;
    }

    public Map<String, Integer> getAxisDecimals() {
        return axisDecimals;
    }

    public void setAxisDecimals(Map<String, Integer> axisDecimals) {
        this.axisDecimals = axisDecimals;
    }
}
