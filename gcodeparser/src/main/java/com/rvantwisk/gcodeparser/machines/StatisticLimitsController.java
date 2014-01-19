/*
 * Copyright (c) 2014, R. van Twisk
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

package com.rvantwisk.gcodeparser.machines;

import com.rvantwisk.gcodeparser.GCodeParser;
import com.rvantwisk.gcodeparser.MachineController;
import com.rvantwisk.gcodeparser.MachineStatus;
import com.rvantwisk.gcodeparser.ParsedWord;
import com.rvantwisk.gcodeparser.exceptions.SimException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Machine controller that gathers statistics on the axis limits
 * Created by rvt on 1/12/14.
 */
public class StatisticLimitsController implements MachineController {

    private static String[] AXIS = {"X","Y","Z","A","B","C","U","V","W"};
    private boolean metric=true;

    private final Map<MachineStatus.Axis, Double> maxValues = new HashMap<>();
    private final Map<MachineStatus.Axis, Double> minValues = new HashMap<>();

    public StatisticLimitsController() {
        for (MachineStatus.Axis axis : MachineStatus.Axis.values()) {
            maxValues.put(axis, Double.NEGATIVE_INFINITY);
            minValues.put(axis, Double.POSITIVE_INFINITY );
        }
    }

    @Override
    public void startBlock(GCodeParser parser, MachineStatus machineStatus, Map<String, ParsedWord> block) {
    }

    @Override
    public void endBlock(GCodeParser parser, MachineStatus machineStatus, Map<String, ParsedWord> block) throws SimException {
        final Map<MachineStatus.Axis, Double> coords = machineStatus.getCoordinates();
        for (MachineStatus.Axis axis : MachineStatus.Axis.values()) {
            if (coords.get(axis)!=null) {
                maxValues.put(axis,Math.max(coords.get(axis), maxValues.get(axis)));
                minValues.put(axis,Math.min(coords.get(axis), minValues.get(axis)));
            }
        }
    }

    @Override
    public void end(GCodeParser parser, MachineStatus machineStatus) throws SimException {
        for (MachineStatus.Axis axis : MachineStatus.Axis.values()) {
            if (maxValues.get(axis)==Double.NEGATIVE_INFINITY) {
                maxValues.put(axis, null);
            }
            if (minValues.get(axis)==Double.POSITIVE_INFINITY) {
                minValues.put(axis, null);
            }
        }
        metric = machineStatus.getModals().contains("G21");
    }

    /**
     * Get the map with gathered maximum statistics over each axis
     * @return
     */
    public Map<MachineStatus.Axis, Double> getMaxValues() {
        return Collections.unmodifiableMap(maxValues);
    }
    /**
     * Get the map with gathered maximum statistics over each axis
     * @return
     */
    public Map<MachineStatus.Axis, Double> getMinValues() {
        return Collections.unmodifiableMap(minValues);
    }

    public boolean isMetric() {
        return metric;
    }
}
