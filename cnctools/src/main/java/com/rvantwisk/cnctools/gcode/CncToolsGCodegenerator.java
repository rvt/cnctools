package com.rvantwisk.cnctools.gcode;

import com.rvantwisk.cnctools.data.ToolParameter;
import com.rvantwisk.cnctools.misc.DimensionProperty;
import com.rvantwisk.cnctools.misc.Dimensions;
import com.rvantwisk.gcodegenerator.interfaces.GCodeGenerator;

/**
 * Created by rvt on 12/31/13.
 */
public interface CncToolsGCodegenerator extends GCodeGenerator{

    DimensionProperty convert(final DimensionProperty dimensionProperty);

    Dimensions.Dim getLengthDimension();
    Dimensions.Dim getVelocityDimension();

    void addTool(final ToolParameter tool);

}
