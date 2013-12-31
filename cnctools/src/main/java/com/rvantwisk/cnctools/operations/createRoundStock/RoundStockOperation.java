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

package com.rvantwisk.cnctools.operations.createRoundStock;

import com.rvantwisk.cnctools.data.AbstractOperation;
import com.rvantwisk.cnctools.data.EndMill;
import com.rvantwisk.cnctools.data.ToolParameter;
import com.rvantwisk.cnctools.gcode.CncToolsGCodegenerator;
import com.rvantwisk.cnctools.misc.DimensionProperty;
import com.rvantwisk.cnctools.misc.ToolDBManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created with IntelliJ IDEA.
 * User: rvtgenerateGCodegenerateGCodegenerateGCode
 * Date: 10/11/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoundStockOperation extends AbstractOperation {


    private final DimensionProperty stockSize= new DimensionProperty();
    private final DimensionProperty finalLength = new DimensionProperty();
    private final DimensionProperty finalSize = new DimensionProperty();
    private final StringProperty toolID = new SimpleStringProperty();

    public RoundStockOperation() {
    }

    public RoundStockOperation(StringProperty toolID, DimensionProperty stockSizeProperty, DimensionProperty finalSizeProperty, DimensionProperty finalLengthProperty) {
        this.toolID.set(toolID.get());
        this.finalLength.set(finalLengthProperty);
        this.stockSize.set(stockSizeProperty);
        this.finalSize.set(finalSizeProperty);
    }

    public String getToolID() {
        return toolID.get();
    }

    public StringProperty toolIDProperty() {
        return toolID;
    }

    public void setToolID(String toolID) {
        this.toolID.set(toolID);
    }

    public DimensionProperty stockSizeProperty() {
        return stockSize;
    }

    public DimensionProperty finalLengthProperty() {
        return finalLength;
    }

    public DimensionProperty finalSizeProperty() {
        return finalSize;
    }


    @Override
    public void generateGCode(final ToolDBManager toolDBManager, final CncToolsGCodegenerator gCodeGenerator) {
        final RoundStockHelper helper = new RoundStockHelper(gCodeGenerator);

        ToolParameter tp = toolDBManager.getByID(getToolID());

        EndMill em = tp.getToolType();

        helper.setFinalSize(gCodeGenerator.convert(finalSize).getValue());
        helper.setStockSize(gCodeGenerator.convert(stockSize).getValue());
        helper.setStockLength(gCodeGenerator.convert(finalLength).getValue());
        helper.setMillSize(gCodeGenerator.convert(em.diameterProperty()).getValue());
        helper.setRadialDepth(gCodeGenerator.convert(tp.radialDepthProperty()).getValue());
        helper.setAxialDepth(gCodeGenerator.convert(tp.axialDepthProperty()).getValue());
        helper.setFeedRate(gCodeGenerator.convert(tp.feedRateProperty()).getValue());
        helper.setRapidClearance(gCodeGenerator.convert(em.diameterProperty()).getValue());
        helper.setStockClearance(gCodeGenerator.convert(em.diameterProperty()).getValue());

        helper.calculate();
    }


}
