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

package com.rvantwisk.cnctools.gcodeparser.gcodes;

import java.util.*;

public final class ModalGrouping {

    public static final Map<GCodeGroups, Set<String>> groupToModals;
    public static final Map<String, GCodeGroups> modalToGroup;

    static {
        HashMap<GCodeGroups, Set<String>> gGroups = new HashMap<>();
        HashMap<String, GCodeGroups> mGroups = new HashMap<>();

        // Add all model sets
        gGroups.put(GCodeGroups.ActivePlane,  toStringSet(ActivePlane.class.getEnumConstants()));
        gGroups.put(GCodeGroups.AxisOffset, toStringSet(AxisOffset.class.getEnumConstants()));
        gGroups.put(GCodeGroups.CollantMode,  toStringSet(CollantMode.class.getEnumConstants()));
        gGroups.put(GCodeGroups.CoordinateSystemMode, toStringSet(CoordinateSystemMode.class.getEnumConstants()));
        gGroups.put(GCodeGroups.CutterLengthCompMode,  toStringSet(CutterLengthCompMode.class.getEnumConstants()));
        gGroups.put(GCodeGroups.CutterRadiusCompMode, toStringSet(CutterRadiusCompMode.class.getEnumConstants()));
        gGroups.put(GCodeGroups.DistanceMode,  toStringSet(DistanceMode.class.getEnumConstants()));
        gGroups.put(GCodeGroups.FeedRateMode, toStringSet(FeedRateMode.class.getEnumConstants()));
        gGroups.put(GCodeGroups.MotionsModes,  toStringSet(MotionMode.class.getEnumConstants()));
        gGroups.put(GCodeGroups.PathControleMode, toStringSet(PathControleMode.class.getEnumConstants()));
        gGroups.put(GCodeGroups.PredefinedPosition,  toStringSet(PredefinedPosition.class.getEnumConstants()));
        gGroups.put(GCodeGroups.ReferenceLocation, toStringSet(ReferenceLocation.class.getEnumConstants()));
        gGroups.put(GCodeGroups.RetrackMode, toStringSet(RetrackMode.class.getEnumConstants()));
        gGroups.put(GCodeGroups.SFOverrideMode, toStringSet(SFOverrideMode.class.getEnumConstants()));
        gGroups.put(GCodeGroups.SpindleMode, toStringSet(SpindleMode.class.getEnumConstants()));
        gGroups.put(GCodeGroups.StopModes, toStringSet(StopModes.class.getEnumConstants()));
        gGroups.put(GCodeGroups.Units, toStringSet(Units.class.getEnumConstants()));

        // Create a modal to group map
        for (Map.Entry<GCodeGroups, Set<String>> group : gGroups.entrySet()) {
            for (String modals : group.getValue()) {
                mGroups.put(modals, group.getKey());
            }
        }

        //
        groupToModals = Collections.unmodifiableMap(gGroups);
        modalToGroup = Collections.unmodifiableMap(mGroups);
    }

    /**
     * Return the group belonging to a modal word
     * @param word
     * @return
     */
    static public GCodeGroups whatGroup(final String word) {
        return modalToGroup.get(word);
    }

    private static <T> Set<String> toStringSet(T[] enumClass) {
        final Set<String> stringSet = new HashSet<>();
        for (T code : enumClass) {
            stringSet.add(code.toString());
        }
        return stringSet;
    }

}
