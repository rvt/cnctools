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
package com.rvantwisk.gcodeparser;


import com.rvantwisk.gcodeparser.gcodes.GCodeGroups;
import com.rvantwisk.gcodeparser.gcodes.ModalGrouping;

import java.util.*;

/**
 * Holds the machine status and handles machine modifications
 * <p/>
 * User: rvt
 * Date: 12/3/13
 * Time: 9:03 AM
 * TODO: See if we can make the enum's a tad smarter so instead if string's they are mapped to integers. This could potentially speed up the hashmap in GCodeParser
 */
public class MachineStatus {



    // Modal vars are variables that stay the same after each block, examples are A,X,Y,Z etc
    private Map<String, Double> modalVars = new HashMap<>();

    // Non modal var's are variable's that will get removed after each block
    private Set<String> modals = new HashSet<>();

    private Map<Axis, Double> machineCoordinates = new HashMap<>();
    private Map<Axis, Double> machineOffsets = new HashMap<>();
    private Map<Axis, Double> coordinatesCache = new HashMap<>();
    private boolean coordinatesCachesDirty = true;

    // We should consider asking this values from the MachineControoler or MachineRules ?
    public enum ModalVars {
        F, S
    }

    public static enum NonModals {
        G53
    }

    // These variables can be set during the block, but will get cleared out when a new block starts
    public enum NonModalsVars {
        P, I, K, J, L, R
    }

    // Axis words to be copied in the coordinates
    public enum Axis {
        A, B, C, U, V, W, X, Y, Z
    }

    public MachineStatus() {
        for (Axis word : Axis.values()) {
            machineCoordinates.put(word, 0.0);
            machineOffsets.put(word, 0.0);
            coordinatesCache.put(word, 0.0);
        }
        modalVars.put("F", 0.0);
        modalVars.put("S", 0.0);

        modals.add("G80");
        modals.add("G17");
        modals.add("G40");
        modals.add("G20");
        modals.add("G90");
        modals.add("G94");
        modals.add("G54");
        modals.add("G49");
        modals.add("G99");
        modals.add("G64");
        modals.add("G97");
        modals.add("G91_1");
        modals.add("G8");
        modals.add("M5");
        modals.add("M9");
        modals.add("M48");
        modals.add("M53");
        modals.add("M0");
    }


    public void setBlock(final Map<String, ParsedWord> block) {
        setModals(block);
        setVars(block);
        coordinatesCachesDirty = true;
        if (block.containsKey("G92")) {
            setCoordinateOffset(block);
        } else {
            setAxis(block);
        }
    }

    private void setCoordinateOffset(Map<String, ParsedWord> block) {
        for (Axis axis : Axis.values()) {
            if (block.containsKey(axis.toString())) {
                ParsedWord word = block.get(axis.toString());
                machineOffsets.put(axis, word.value);
            }
        }
    }

    private void setAxis(final Map<String, ParsedWord> block) {
        for (Axis axis : Axis.values()) {
            if (block.containsKey(axis.toString())) {
                ParsedWord word = block.get(axis.toString());
                // Handle absolute cordinate position (G0 and G1 only)
                if (modals.contains("G53")) {
                    machineCoordinates.put(axis, word.value);
                } else {
                    // Handle relative distance mode
                    if (modals.contains("G90")) {
                        machineCoordinates.put(axis, machineOffsets.get(axis) + word.value);
                    } else {
                        machineCoordinates.put(axis, machineCoordinates.get(axis) + word.value);
                    }
                }
            }
        }
    }

    private void setModals(final Map<String, ParsedWord> block) {
        for (ParsedWord word : block.values()) {
            if (word.word.equals("G") || word.word.equals("M")) {

                // Find the modal group and remove all modals if found
                GCodeGroups thisGroup = ModalGrouping.whatGroup(word.parsed);
                if (thisGroup != null) {
                    modals.removeAll(ModalGrouping.groupToModals.get(thisGroup));
                }

                modals.add(word.parsed);
            }
        }
    }

    private void setVars(final Map<String, ParsedWord> block) {
        for (ModalVars modalvar : ModalVars.values()) {
            ParsedWord word = block.get(modalvar.toString());
            if (word!=null) {
                modalVars.put(word.word, word.value);
            }
        }
    }

    public void copyFrom(final MachineStatus cpFrom) {
        this.modalVars = cpFrom.modalVars;
        this.modals = cpFrom.modals;
        this.machineCoordinates = cpFrom.machineCoordinates;
        this.machineOffsets = cpFrom.machineOffsets;
    }

    public void startBlock() {
        for (NonModalsVars item : NonModalsVars.values()) {
            modalVars.remove(item.toString());
        }
        for (NonModals item : NonModals.values()) {
            modals.remove(item.toString());
        }
    }

    public Map<String, Double> getModalVars() {
        return Collections.unmodifiableMap(modalVars);
    }

    public Set<String> getModals() {
        return Collections.unmodifiableSet(modals);
    }


    public void endBlock() {
    }


    /**
     * Needs to move this to a general static class? sa : MachineValidator
     * @param value
     * @param enumClass
     * @param <T>
     * @return
     */
    private <T extends Enum<T>> boolean hasAny(final String value, Class<T> enumClass) {
        for (T c : enumClass.getEnumConstants()) {
            if (c.name().equals(value)) {
                return true;
            }
        }
        return false;
    }

    // Actual machine's absolute position
    public Map<Axis, Double> getMachineCoordinates() {
        return Collections.unmodifiableMap(machineCoordinates);
    }

    // Get relative position
    public Map<Axis, Double> getCoordinates() {

        if (coordinatesCachesDirty) {
            for (Axis axis : Axis.values()) {
                coordinatesCache.put(axis, machineCoordinates.get(axis) - machineOffsets.get(axis));
            }
        }

        return Collections.unmodifiableMap(coordinatesCache);
    }

    // Machine offsets
    public Map<Axis, Double> getMachineOffsets() {
        return Collections.unmodifiableMap(machineOffsets);
    }
}
