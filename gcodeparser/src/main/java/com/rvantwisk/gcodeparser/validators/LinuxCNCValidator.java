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

package com.rvantwisk.gcodeparser.validators;

import com.rvantwisk.gcodeparser.AbstractMachineValidator;
import com.rvantwisk.gcodeparser.MachineStatus;
import com.rvantwisk.gcodeparser.MachineStatusHelper;
import com.rvantwisk.gcodeparser.ParsedWord;
import com.rvantwisk.gcodeparser.exceptions.SimException;
import com.rvantwisk.gcodeparser.exceptions.SimValidationException;
import com.rvantwisk.gcodeparser.gcodes.*;

import java.util.Map;

/**
 * A G-Code validator that tries to mimic the validation schema of LinuxCNC
 *
 */
public class LinuxCNCValidator extends AbstractMachineValidator {


    @Override
    public void preVerify(Map<String, ParsedWord> block) throws SimException {

        // test for modals within the same group
        if (
                hasMultipleWords(block, MotionMode.class) ||
                        hasMultipleWords(block, SpindleMode.class) ||
                        hasMultipleWords(block, FeedRateMode.class) ||
                        hasMultipleWords(block, SFOverrideMode.class) ||
                        hasMultipleWords(block, ActivePlane.class) ||
                        hasMultipleWords(block, Units.class) ||
                        hasMultipleWords(block, CutterRadiusCompMode.class) ||
                        hasMultipleWords(block, CutterLengthCompMode.class) ||
                        hasMultipleWords(block, CoordinateSystemMode.class) ||
                        hasMultipleWords(block, PathControleMode.class) ||
                        hasMultipleWords(block, DistanceMode.class) ||
                        hasMultipleWords(block, RetrackMode.class) ||
                        hasMultipleWords(block, ReferenceLocation.class) ||
                        hasMultipleWords(block, PredefinedPosition.class) ||
                        hasMultipleWords(block, AxisOffset.class)

                ) {
            throw new SimValidationException("Two M or G codes used in the same model group [" + block.toString() + "]");
        }

        // Validate modal codes that uses axis values
        if (wordCount(block, MotionMode.class) > 0 &&
                (wordCount(block, PredefinedPosition.class) > 0 ||
                        wordCount(block, ReferenceLocation.class) > 0 ||
                        wordCount(block, AxisOffset.class) > 0 ||
                        block.containsKey("G43_1") ||
                        block.containsKey("G92")
                )) {
            throw new SimValidationException("Cannot use two g codes that both use axis values");
        }

        if (block.containsKey("G43")) {
            Double H = block.containsKey("H") ? block.get("H").value : null;
            if (H != null && (H < 0 || H.intValue() != H)) {
                throw new SimValidationException("G43 has invalid H value.");
            }
        }

        if (block.containsKey("F") && block.get("F").value < 0) {
            throw new SimValidationException("Negative F word used");
        }

        if (block.containsKey("G64")) {
            Double P = block.containsKey("P") ? block.get("P").value : null;
            Double Q = block.containsKey("Q") ? block.get("Q").value : null;
            if (Q != null && P == null) {
                throw new SimValidationException("Q word given without P word on G64");
            }
        }
    }

    @Override
    public void postVerify(MachineStatus machineStatus) throws SimValidationException {
        if (machineStatus.getModalVars().get("F") < 0.00001 && hasAny(machineStatus.getModals(), new String[]{"G1", "G2", "G3"})) {
            throw new SimValidationException("Cannot do motion with zero feedrate");
        }

        if (hasAny(machineStatus.getModals(), new String[]{"G2", "G3"}) && machineStatus.getModalVars().containsKey("P")) {
            double P = machineStatus.getModalVars().get("P") == null ? 1.0f : machineStatus.getModalVars().get("P");
            if (P != (int) P) {
                throw new SimValidationException("P value not a integer with G2 or g3");
            }
        }

        if (hasAny(machineStatus.getModals(), new String[]{"G53"}) && !hasAny(machineStatus.getModals(), new String[]{"G0", "G1"})) {
            throw new SimValidationException("Must use g0 or g1 with G53");
        }

        if (hasAny(machineStatus.getModals(), new String[]{"G53"}) && hasAny(machineStatus.getModals(), new String[]{"G41", "G42", "G41_1", "G42_1"})) {
            throw new SimValidationException("Cannot use G53 with cutter compensation.");
        }

        MachineStatusHelper machine=new MachineStatusHelper();
        machine.setMachineStatus(machineStatus);

        if (machine.getActivePlane() != ActivePlane.G17 && hasAny(machineStatus.getModals(), new String[]{"G2", "G3"})) {
            throw new SimValidationException("Arc currently only support G17, please report and we will update!");
        }


    }
}
