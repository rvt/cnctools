package com.rvantwisk.cnctools.gcode;

import com.rvantwisk.cnctools.data.ToolParameter;
import com.rvantwisk.cnctools.misc.DimensionProperty;
import com.rvantwisk.cnctools.misc.Dimensions;
import com.rvantwisk.gcodegenerator.interfaces.GCodeGenerator;

/**
 * Created by rvt on 12/31/13.
 */
public interface CncToolsGCodegenerator extends GCodeGenerator {

    /**
     * Get the dimension currently use by the machine
     *
     * @param dimensionProperty
     * @return
     */
    DimensionProperty convert(final DimensionProperty dimensionProperty);

    /**
     * Get the length dimension currently set
     *
     * @return
     */
    Dimensions.Dim getLengthDimension();

    /**
     * Get the velocity dimension currently set
     *
     * @return
     */
    Dimensions.Dim getVelocityDimension();

    /**
     * Set a new tool for the next set of operations
     * This will generate the needed g-code for this machine
     *
     * @param tool
     */
    void setTool(final ToolParameter tool);

}
