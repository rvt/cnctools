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

package com.rvantwisk.cnctools.data;

import com.rvantwisk.cnctools.data.interfaces.Toolbase;
import com.rvantwisk.cnctools.misc.DimensionProperty;
import javafx.beans.property.*;

import java.util.UUID;

public class ToolParameter {

    private final StringProperty id = new SimpleStringProperty();

    // General
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty toolNumber = new SimpleIntegerProperty();
    private final IntegerProperty numberOfFlutes = new SimpleIntegerProperty();

    // Cut Data
    private final DimensionProperty spindleSpeed = new DimensionProperty();
    private final DimensionProperty feedRate = new DimensionProperty();
    private final DimensionProperty plungeRate = new DimensionProperty();
    private final DimensionProperty axialDepth = new DimensionProperty();
    private final DimensionProperty radialDepth = new DimensionProperty();

    // Misc Data
    private final BooleanProperty coolant = new SimpleBooleanProperty();
    private final StringProperty spindleDirection = new SimpleStringProperty();
    private final ObjectProperty<Toolbase> toolType = new SimpleObjectProperty<>();

    public ToolParameter(final String name, final Integer toolNumber, final Integer numberOfFlutes, final DimensionProperty spindleSpeed, final double feedRate, final Double plungeRate, final double axialDepth, final double radialDepth, final Boolean coolant, final SpindleDirection spindleDirection, final Toolbase toolType) {
        this.id.set(UUID.randomUUID().toString());
        this.name.setValue(name);
        this.toolNumber.setValue(toolNumber);
        this.numberOfFlutes.setValue(numberOfFlutes);
        this.spindleSpeed.set(spindleSpeed);
        this.feedRate.setValue(feedRate);
        this.plungeRate.setValue(plungeRate);
        this.axialDepth.setValue(axialDepth);
        this.radialDepth.setValue(radialDepth);
        this.coolant.setValue(coolant);
        this.spindleDirection.setValue(spindleDirection.toString());
        this.toolType.setValue(toolType);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public enum SpindleDirection {
        CW, CCW
    }

    public ToolParameter() {
        this.id.set(UUID.randomUUID().toString());
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public Integer getToolNumber() {
        return toolNumber.get();
    }

    public IntegerProperty toolNumberProperty() {
        return toolNumber;
    }

    public void setToolNumber(Integer toolNumber) {
        this.toolNumber.set(toolNumber);
    }

    public Integer getNumberOfFlutes() {
        return numberOfFlutes.get();
    }

    public IntegerProperty numberOfFlutesProperty() {
        return numberOfFlutes;
    }

    public void setNumberOfFlutes(Integer numberOfFlutes) {
        this.numberOfFlutes.setValue(numberOfFlutes);
    }

    public DimensionProperty spindleSpeedProperty() {
        return spindleSpeed;
    }

    public Boolean getCoolant() {
        return coolant.get();
    }

    public BooleanProperty coolantProperty() {
        return coolant;
    }

    public void setCoolant(Boolean coolant) {
        this.coolant.set(coolant);
    }

    public SpindleDirection getSpindleDirection() {
        return SpindleDirection.valueOf(spindleDirectionProperty().get());
    }

    public StringProperty spindleDirectionProperty() {
        return spindleDirection;
    }

    public void setSpindleDirection(SpindleDirection spindleDirection) {
        this.spindleDirection.set(spindleDirection.toString());
    }

    public <T extends Toolbase> T getToolType() {
        return (T) toolType.get();
    }

    public ObjectProperty<Toolbase> toolTypeProperty() {
        return toolType;
    }

    public void setToolType(Toolbase toolType) {
        this.toolType.set(toolType);
    }

    public DimensionProperty feedRateProperty() {
        return feedRate;
    }

    public DimensionProperty plungeRateProperty() {
        return plungeRate;
    }

    public DimensionProperty axialDepthProperty() {
        return axialDepth;
    }

    public DimensionProperty radialDepthProperty() {
        return radialDepth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ToolParameter)) return false;

        ToolParameter that = (ToolParameter) o;

        if (!id.get().equals(that.id.get())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
