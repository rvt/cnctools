package com.rvantwisk.cnctools.gcode;

import com.rvantwisk.cnctools.misc.DimensionProperty;
import com.rvantwisk.cnctools.misc.Dimensions;
import com.rvantwisk.gcodegenerator.dialects.RS274;
import com.rvantwisk.gcodegenerator.interfaces.PostProcessorConfig;

/**
 * Created by rvt on 12/31/13.
 */
public class CncToolsRS274 extends RS274 implements CncToolsGCodegenerator {
    public CncToolsRS274(PostProcessorConfig pc) {
        super(pc);
    }

    @Override
    public DimensionProperty convert(final DimensionProperty dimensionProperty) {
        if (dimensionProperty.getDimension().getType() == Dimensions.Type.RPM) {
            return new DimensionProperty(dimensionProperty);
        } else if (dimensionProperty.getDimension().getType() == Dimensions.Type.VELOCITY) {
            return dimensionProperty.convert(getVelocityDimension());
        } else if (dimensionProperty.getDimension().getType() == Dimensions.Type.LENGTH) {
            return dimensionProperty.convert(getLengthDimension());
        } else {
            throw new IllegalArgumentException("Dimension [" + dimensionProperty.getDimension() + "] not supported");
        }
    }


    @Override
    public Dimensions.Dim getLengthDimension() {
        return Dimensions.Dim.MM;
    }

    @Override
    public Dimensions.Dim getVelocityDimension() {
        if (getLengthDimension() == Dimensions.Dim.MM) {
            return Dimensions.Dim.MM_MINUTE;
        } else if (getLengthDimension() == Dimensions.Dim.INCH) {
            return Dimensions.Dim.INCH_MINUTE;
        } else {
            throw new RuntimeException("Invalid dimension for length found, no suitable velocity is available.");
        }
    }
}
