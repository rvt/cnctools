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

package com.rvantwisk.cnctools.operations.facing;

import com.rvantwisk.cnctools.data.ToolParameter;
import com.rvantwisk.cnctools.data.interfaces.TaskModel;
import com.rvantwisk.cnctools.data.tools.EndMill;
import com.rvantwisk.cnctools.gcode.CncToolsGCodegenerator;
import com.rvantwisk.cnctools.misc.DimensionProperty;
import com.rvantwisk.cnctools.misc.ToolDBManager;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.circulinear.CirculinearContourArray2D;
import math.geom2d.circulinear.CirculinearCurve2D;
import math.geom2d.domain.ContourArray2D;

/**
 * Created by rvt on 12/30/13.
 */
public class FacingOperation implements TaskModel {

    public static class Configuration {
        public final String name;
        public final FacingHelper.CutStrategy cutStrategy;
        public final Double angle;

        public Configuration(String name, FacingHelper.CutStrategy cutStrategy) {
            this.name = name;
            this.cutStrategy = cutStrategy;
            this.angle = null;
        }

        public Configuration(String name, FacingHelper.CutStrategy cutStrategy, Double angle) {
            this.name = name;
            this.cutStrategy = cutStrategy;
            this.angle = angle;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static final ObservableList<Configuration> CONFIGLIST = FXCollections.observableArrayList(
            new Configuration("Vertical Zig Zag", FacingHelper.CutStrategy.ZIGZAG, 0.0),
            new Configuration("Horizontal Zig Zag", FacingHelper.CutStrategy.ZIGZAG, 90.0),
            new Configuration("Vertical Linear", FacingHelper.CutStrategy.LINEAR, 0.0),
            new Configuration("Horizontal Linear", FacingHelper.CutStrategy.LINEAR, 90.0)
    );


    private final StringProperty toolID = new SimpleStringProperty();
    private final DimensionProperty zTop = new DimensionProperty(DimensionProperty.DimMM(0.0));
    private final DimensionProperty zFinal = new DimensionProperty(DimensionProperty.DimMM(1.0));
    private final DimensionProperty zSafe = new DimensionProperty(DimensionProperty.DimMM(10.0));
    private final BooleanProperty edgeCleanup = new SimpleBooleanProperty(true);
    private final BooleanProperty edgeCleanupClimb = new SimpleBooleanProperty(false);
    private final BooleanProperty climbCutting = new SimpleBooleanProperty(false);
    private final StringProperty partReference = new SimpleStringProperty("bottomleft");
    private final SimpleObjectProperty<Configuration> cutStrategy = new SimpleObjectProperty<Configuration>();
    private final DimensionProperty width = new DimensionProperty(DimensionProperty.DimMM(100.0));
    private final DimensionProperty height = new DimensionProperty(DimensionProperty.DimMM(100.0));

    public String getToolID() {
        return toolID.get();
    }

    public StringProperty toolIDProperty() {
        return toolID;
    }

    public void setToolID(String toolID) {
        this.toolID.set(toolID);
    }

    @Override
    public void generateGCode(ToolDBManager toolDBManager, CncToolsGCodegenerator gCodeGenerator) {

        final FacingHelper helper = new FacingHelper(gCodeGenerator);
        ToolParameter tp = toolDBManager.getByID(getToolID());
        gCodeGenerator.addTool(tp);

        EndMill em = tp.getToolType();
        helper.setzFinal(gCodeGenerator.convert(zFinal).getValue());
        helper.setzSafe(gCodeGenerator.convert(zSafe).getValue());
        helper.setzTop(gCodeGenerator.convert(zTop).getValue());
        helper.setMillSize(gCodeGenerator.convert(em.diameterProperty()).getValue());
        helper.setRapidClearance(gCodeGenerator.convert(em.diameterProperty()).getValue());
        helper.setStockClearance(gCodeGenerator.convert(em.diameterProperty()).getValue());
        helper.setRadialDepth(gCodeGenerator.convert(tp.radialDepthProperty()).getValue());
        helper.setAxialDepth(gCodeGenerator.convert(tp.axialDepthProperty()).getValue());
        helper.setSpindleCW(tp.getSpindleDirection() == ToolParameter.SpindleDirection.CW ? true : false);
        helper.setEdgeCleanup(edgeCleanup.get());
        helper.setEdgeCleanupClimb(edgeCleanupClimb.get());

        // If edge cleanup is selected, setup a edge clearance and use this for final pass
        if (helper.isEdgeCleanup()) {
            helper.setEdgeClearance(gCodeGenerator.convert(tp.axialDepthProperty()).getValue() / 5.0);
        }

// Get the shape and apply transformation
//        CirculinearCurve2D curve = FacingHelper.getCircleDomain(gCodeGenerator.convert(width).getValue());
//        CirculinearCurve2D curve = FacingHelper.getEllipseDomain(gCodeGenerator.convert(width).getValue(), gCodeGenerator.convert(height).getValue(), 50);

        CirculinearCurve2D curve = FacingHelper.getRectangularDomain(gCodeGenerator.convert(width).getValue(), gCodeGenerator.convert(height).getValue());
        Box2D bBox = curve.boundingBox();
        AffineTransform2D transform;
        switch (partReference.get().toLowerCase()) {
            case "center":
                transform = AffineTransform2D.createTranslation(-bBox.getWidth() / 2.0, -bBox.getHeight() / 2.0);
                break;
            case "top left":
                transform = AffineTransform2D.createTranslation(0, -bBox.getHeight());
                break;
            case "top right":
                transform = AffineTransform2D.createTranslation(-bBox.getWidth(), -bBox.getHeight());
                break;
            case "bottom right":
                transform = AffineTransform2D.createTranslation(-bBox.getWidth(), 0.0);
                break;
            default:
                transform = AffineTransform2D.createTranslation(0.0, 0.0);
        }

        ContourArray2D transformedCurve = (ContourArray2D) curve.transform(transform);
        CirculinearContourArray2D foo = CirculinearContourArray2D.create(transformedCurve.continuousCurves());

        helper.setDomain(foo);


        if (this.cutStrategy.get().angle != null) {
            helper.setAngle(this.cutStrategy.get().angle);
        }
        helper.setCutStrategy(this.cutStrategy.get().cutStrategy);

        helper.calculate();


    }

    @Override
    public TaskModel copy() {
        FacingOperation f = new FacingOperation();
        f.setCutStrategy(this.getCutStrategy());
        f.setEdgeCleanup(this.getEdgeCleanup());
        f.setEdgeCleanupClimb(this.getEdgeCleanupClimb());
        f.setPartReference(this.getPartReference());
        f.setToolID(this.getToolID());
        f.setCutStrategy(this.getCutStrategy());
        f.setClimbCutting(this.getClimbCutting());
        f.width.set(this.width);
        f.height.set(this.height);
        f.zTop.set(this.zTop);
        f.zSafe.set(this.zSafe);
        f.zFinal.set(this.zFinal);
        return f;
    }

    public DimensionProperty getzTop() {
        return zTop;
    }

    public DimensionProperty getzFinal() {
        return zFinal;
    }

    public DimensionProperty getzSafe() {
        return zSafe;
    }

    public boolean getEdgeCleanup() {
        return edgeCleanup.get();
    }

    public BooleanProperty edgeCleanupProperty() {
        return edgeCleanup;
    }

    public void setEdgeCleanup(boolean edgeCleanup) {
        this.edgeCleanup.set(edgeCleanup);
    }

    public boolean getEdgeCleanupClimb() {
        return edgeCleanupClimb.get();
    }

    public BooleanProperty edgeCleanupClimbProperty() {
        return edgeCleanupClimb;
    }

    public void setEdgeCleanupClimb(boolean edgeCleanupClimb) {
        this.edgeCleanupClimb.set(edgeCleanupClimb);
    }

    public String getPartReference() {
        return partReference.get();
    }

    public StringProperty partReferenceProperty() {
        return partReference;
    }

    public void setPartReference(String partReference) {
        this.partReference.set(partReference);
    }

    public Configuration getCutStrategy() {
        return cutStrategy.get();
    }

    public SimpleObjectProperty<Configuration> cutStrategyProperty() {
        return cutStrategy;
    }

    public void setCutStrategy(Configuration cutStrategy) {
        this.cutStrategy.set(cutStrategy);
    }

    public DimensionProperty getWidth() {
        return width;
    }

    public DimensionProperty getHeight() {
        return height;
    }

    public boolean getClimbCutting() {
        return climbCutting.get();
    }

    public BooleanProperty climbCuttingProperty() {
        return climbCutting;
    }

    public void setClimbCutting(boolean climbCutting) {
        this.climbCutting.set(climbCutting);
    }
}
