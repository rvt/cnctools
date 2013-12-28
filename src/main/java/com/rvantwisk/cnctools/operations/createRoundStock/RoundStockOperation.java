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
import com.rvantwisk.cnctools.gcodegenerator.interfaces.GCodeGenerator;
import com.rvantwisk.cnctools.misc.DimensionProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Created with IntelliJ IDEA.
 * User: rvtgenerateGCodegenerateGCodegenerateGCode
 * Date: 10/11/13
 * Time: 3:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoundStockOperation extends AbstractOperation {

    private final ObjectProperty<ToolParameter> toolParameters = new SimpleObjectProperty<>();

    private final DimensionProperty stockSize= new DimensionProperty();
    private final DimensionProperty finalLength = new DimensionProperty();
    private final DimensionProperty finalSize = new DimensionProperty();

    private final DoubleProperty rapidClearance= new SimpleDoubleProperty();
    private final DoubleProperty stockClearance = new SimpleDoubleProperty();

    public RoundStockOperation() {
    }

    public RoundStockOperation(ToolParameter tool, DimensionProperty stockSizeProperty, DimensionProperty finalSizeProperty, DimensionProperty finalLengthProperty) {
        this.toolParameters.set(tool);
        this.finalLength.set(finalLengthProperty);
        this.stockSize.set(stockSizeProperty);
        this.finalSize.set(finalSizeProperty);
        this.rapidClearance.set(((EndMill) tool.getToolType()).diameterProperty().getValue() / 2.0);
        this.stockClearance.set(((EndMill) tool.getToolType()).diameterProperty().getValue() / 2.0);
    }

    public ToolParameter getToolParameters() {
        return toolParameters.get();
    }

    public ObjectProperty<ToolParameter> toolParametersProperty() {
        return toolParameters;
    }

    public void setToolParameters(ToolParameter toolParameters) {
        this.toolParameters.set(toolParameters);
    }

    public DimensionProperty stockSizeProperty() {
        return stockSize;
    }

    public DimensionProperty finalLengthProperty() {
        return finalLength;
    }

    public double getRapidClearance() {
        return rapidClearance.get();
    }

    public DoubleProperty rapidClearanceProperty() {
        return rapidClearance;
    }

    public void setRapidClearance(double rapidClearance) {
        this.rapidClearance.set(rapidClearance);
    }

    public double getStockClearance() {
        return stockClearance.get();
    }

    public DoubleProperty stockClearanceProperty() {
        return stockClearance;
    }

    public void setStockClearance(double stockClearance) {
        this.stockClearance.set(stockClearance);
    }

    public DimensionProperty finalSizeProperty() {
        return finalSize;
    }


    @Override
    public void generateGCode(final GCodeGenerator gCodeGenerator) {
        final RoundStockHelper helper = new RoundStockHelper(gCodeGenerator);

//        helper.setClearancebeforeFinal(2.0);
        helper.setFinalSize(finalSize.getValue());  // Diameter
        helper.setStockSize(21.0); // Diameter
        helper.setStockLength(100.0);
        helper.setMillSize(8.0);
        helper.setRadialDepth(toolParameters.get().radialDepthProperty().getValue());
        helper.setAxialDepth(toolParameters.get().axialDepthProperty().getValue());
        helper.setFeedRate(toolParameters.get().feedRateProperty().getValue());

//        graph.getChildren().clear();
//        graph.getChildren().add(canvas);

        helper.calculate();
    }
}
