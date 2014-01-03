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

import com.rvantwisk.cnctools.data.CNCToolsPostProcessConfig;
import com.rvantwisk.cnctools.data.tools.EndMill;
import com.rvantwisk.cnctools.data.StockToolParameter;
import com.rvantwisk.cnctools.data.ToolParameter;
import com.rvantwisk.cnctools.gcode.CncToolsGCodegenerator;
import com.rvantwisk.cnctools.gcode.CncToolsRS274;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 11/29/13
 * Time: 8:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class Factory {

    private Factory() {
    }

    public static StockToolParameter newStockTool() {
        final StockToolParameter nt = new StockToolParameter();
        nt.setName("New");
        //nt.setToolNumber(0);
        //nt.setNumberOfFlutes(null);
        //nt.setCoolant(Boolean.FALSE);

        nt.setSpindleDirection(ToolParameter.SpindleDirection.CW);

        nt.spindleSpeedProperty().set(new DimensionProperty(3000.0, Dimensions.Dim.RPM));
        nt.feedRateProperty().set(new DimensionProperty(750.0, Dimensions.Dim.MM_MINUTE));
        nt.plungeRateProperty().set(new DimensionProperty(350.0, Dimensions.Dim.MM_MINUTE));
        nt.axialDepthProperty().set(new DimensionProperty(3.0, Dimensions.Dim.MM));
        nt.radialDepthProperty().set(new DimensionProperty(3.0, Dimensions.Dim.MM));
        nt.setToolType(new EndMill(new DimensionProperty(6.0, Dimensions.Dim.MM)));
        return nt;
    }

    public static ToolParameter newTool() {
        final ToolParameter nt = new ToolParameter();
        nt.setName("New");
        //nt.setToolNumber(0);
        //nt.setNumberOfFlutes(null);
        //nt.setCoolant(Boolean.FALSE);

        nt.setSpindleDirection(ToolParameter.SpindleDirection.CW);
        nt.spindleSpeedProperty().set(new DimensionProperty(3000.0, Dimensions.Dim.RPM));
        nt.feedRateProperty().set(new DimensionProperty(750.0, Dimensions.Dim.MM_MINUTE));
        nt.plungeRateProperty().set(new DimensionProperty(350.0, Dimensions.Dim.MM_MINUTE));
        nt.axialDepthProperty().set(new DimensionProperty(3.0, Dimensions.Dim.MM));
        nt.radialDepthProperty().set(new DimensionProperty(3.0, Dimensions.Dim.MM));
        nt.setToolType(new EndMill(new DimensionProperty(6.0, Dimensions.Dim.MM)));
        return nt;
    }


    public static CNCToolsPostProcessConfig newPostProcessor() {
        final CNCToolsPostProcessConfig nt = new CNCToolsPostProcessConfig();
        nt.setName("New");
        nt.setSpaceBetweenWords(false);
        nt.decimalsFProperty().set(2);
        nt.decimalsSProperty().set(0);
        nt.decimalsOthersProperty().set(2);
        nt.hasToolChangerProperty().setValue(false);
        nt.axisDecimalsProperty().put("A", 2);
        nt.axisDecimalsProperty().put("B", 2);
        nt.axisDecimalsProperty().put("C", 2);
        nt.axisDecimalsProperty().put("X", 2);
        nt.axisDecimalsProperty().put("Y", 2);
        nt.axisDecimalsProperty().put("Z", 2);
        nt.axisDecimalsProperty().put("U", 2);
        nt.axisDecimalsProperty().put("V", 2);
        nt.axisDecimalsProperty().put("W", 2);
        nt.axisMappingProperty().put("A", "A");
        nt.axisMappingProperty().put("B", "B");
        nt.axisMappingProperty().put("C", "C");
        nt.axisMappingProperty().put("X", "X");
        nt.axisMappingProperty().put("Y", "Y");
        nt.axisMappingProperty().put("Z", "Z");
        nt.axisMappingProperty().put("U", "U");
        nt.axisMappingProperty().put("V", "V");
        nt.axisMappingProperty().put("W", "W");
        nt.preabmleProperty().set("%\n" +
                "G17 G21 G40 G49\n" +
                "G64 P0.01");
        nt.postambleProperty().set("M30\n" +
                "%");
        nt.setDialect("RS274");
        return nt;
    }

    /**
     * Retrieve a postprocessor dialect from a post processor configuration
     * @param pc    PostProcessorConfiguration
     * @return
     *
     * TODO: Need to change this to use enum's or class name, for now we just have one dialect
     */
    public static CncToolsGCodegenerator getProcessorDialect(final CNCToolsPostProcessConfig pc) {
        return new CncToolsRS274(pc);
    }
}
