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

import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * A property that stores both the value and it's dimension in one property
 * If the dimension changes, the value wil automatically be changed aswell
 */
public class DimensionProperty {


    private final DoubleProperty value = new SimpleDoubleProperty();
    private final ObjectProperty<Dimensions.Dim> dimension = new SimpleObjectProperty<>();
    boolean block=false;

    public DimensionProperty() {
        listeners();
    }

    public DimensionProperty(DimensionProperty dimensionProperty) {
        value.set(dimensionProperty.getValue());
        dimension.set(dimensionProperty.getDimension());
        listeners();
    }

    private void listeners() {

        value.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
//                fireValueChangedEvent();
            }
        });
        dimension.addListener(new ChangeListener<Dimensions.Dim>() {
            @Override
            public void changed(ObservableValue<? extends Dimensions.Dim> observableValue, Dimensions.Dim dim, Dimensions.Dim dim2) {
                if (dim != null && block==false) {
                    value.set(Dimensions.convert(value.get(), dim, dim2));
                }
            }
        });
    }

    public DimensionProperty(final Double value, final Dimensions.Dim dimension) {
        this.value.setValue(value);
        this.dimension.set(dimension);
        listeners();
    }


    public static DimensionProperty DimMM(final Double value) {
        return new DimensionProperty(value, Dimensions.Dim.MM);
    }

    public static DimensionProperty DimInch(final Double value) {
        return new DimensionProperty(value, Dimensions.Dim.INCH);
    }

    public static DimensionProperty DimRPM(final Double value) {
        return new DimensionProperty(value, Dimensions.Dim.RPM);
    }

    public double getValue() {
        return value.get();
    }

    /**
     * Get the value's property, be carefull setting the values as under some conditions you might want to prevent change even't beeing fired
     * @return
     */
    public DoubleProperty valueProperty() {
        return value;
    }

    public void setValue(double value) {
        //     block=true;
        this.value.set(value);
        //     block=false;
    }

    public Dimensions.Dim getDimension() {
        return dimension.get();
    }

    /**
     * get the dimension's property be carefull setting the values as under some conditions you might want to prevent change even't beeing fired
     *
     * @return
     */
    public ObjectProperty<Dimensions.Dim> dimensionProperty() {
        return dimension;
    }

    public void setDimension(Dimensions.Dim dimension) {
   //     block=true;
        this.dimension.set(dimension);
        //     block=false;
    }

    public DimensionProperty convert(final Dimensions.Dim toDimention) {
        return new DimensionProperty(Dimensions.convert(this.getValue(), this.getDimension(), toDimention), toDimention);
    }

    public void set(final DimensionProperty dimensionProperty) {
        block=true;
        value.setValue(dimensionProperty.value.get());
        dimension.setValue(dimensionProperty.dimension.get());
        block=false;
    }
}
