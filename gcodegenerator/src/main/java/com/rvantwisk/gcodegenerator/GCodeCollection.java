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

package com.rvantwisk.gcodegenerator;

import java.util.ArrayList;

/**
 * Created by rvt on 1/23/14.
 */
public class GCodeCollection extends ArrayList<GCodeCollection.GeneratedGCode> {
    private static final String SEPERATOR = System.getProperty("line.separator");

    /**
     * Build's a string builder object with all GCode concated
     *
     * @return
     */
    public StringBuilder concate() {
        StringBuilder sb = new StringBuilder();
        for (GeneratedGCode code : this) {
            sb.append(SEPERATOR).append(code.gCode);
        }
        return sb;
    }

    /**
     * Create a deep copy of this object
     *
     * @return
     */
    public GCodeCollection deepCopy() {
        GCodeCollection col = new GCodeCollection();
        for (GeneratedGCode code : this) {
            col.add(new GeneratedGCode(new StringBuilder(code.getGCode()), code.isMergeable(), code.getId(), code.getToolId()));
        }
        return col;
    }

    /**
     * Create a merged version of this GCode
     */
    public void merge() {
        // Merge all set's with a mergable flag
        GCodeCollection col = new GCodeCollection();
        col.addAll(this);
        this.clear();
        StringBuilder mergeBuilder = new StringBuilder();
        for (GeneratedGCode code : col) {
            // When code can be merged, add it to the mergeBuilder
            if (code.isMergeable()) {
                mergeBuilder.append(SEPERATOR).append(code.getGCode());
            } else {
                // Merge the previous set with the non mergeable version
                if (mergeBuilder.length() > 0) {
                    mergeBuilder.append(SEPERATOR).append(code.getGCode());
                    this.add(new GeneratedGCode(mergeBuilder, false, code.getId(), code.getToolId()));
                    mergeBuilder = new StringBuilder();
                } else {
                    this.add(code);
                }
            }
        }

        // merge with same toolID
        col = new GCodeCollection();
        col.addAll(this);
        this.clear();

        // Add the first one (Preamble)
        this.add(col.get(0));
        GeneratedGCode lastCode = null;
        for (int i = 1; i < (col.size() - 1); i++) {
            GeneratedGCode code = col.get(i);

            // If this toolID is null, add it to the list directly
            if (code.getToolId() == null) {
                if (lastCode != null) {
                    this.add(lastCode);
                    lastCode = null;
                }
                this.add(code);
            } else {
                // if lastCode was null, simply buffer this one, we might have a next one with the same code id
                if (lastCode == null) {
                    lastCode = code;
                } else {
                    // If the last tool ID and this toolID are the same, merge the GCOde
                    if (lastCode.getToolId().equals(code.getToolId())) {
                        lastCode = new GeneratedGCode(lastCode.getGCode().append(SEPERATOR).append(code.getGCode()), false, "merged", code.getToolId());
                    } else {
                        // IF they are not the same, add the lastCode and buffer this one
                        this.add(lastCode);
                        lastCode = code;
                    }
                }
            }
        }

        if (lastCode != null) {
            this.add(lastCode);
        }

        // Add last one postamble
        this.add(col.get(col.size() - 1));
    }

    public static final class GeneratedGCode {
        private final StringBuilder gCode;  // String builder object with GCode
        private final String id;            // ID of this GCode
        private final String toolId;        // ToolID used for this GCode
        private final boolean mergeable;          // When true, it allows to be merged with other GCode, if mergable is set to true the code shouldn't have any effect where it's merged into, usually used for comment only

        public GeneratedGCode(final StringBuilder gCode, final boolean mergeable, final String id, final String toolId) {
            if (id == null) {
                throw new IllegalArgumentException("Id most not be null");
            }
            if (gCode == null) {
                throw new IllegalArgumentException("GCode most not be null");
            }
            if (mergeable == true && toolId != null) {
                throw new IllegalArgumentException("Mergable cannot be true with a toolId");
            }
            this.mergeable = mergeable;
            this.gCode = gCode;
            this.id = id;
            this.toolId = toolId;

        }

        public GeneratedGCode(final StringBuilder gCode, final boolean mergeable, final String id) {
            this(gCode, mergeable, id, null);
        }

        public StringBuilder getGCode() {
            return gCode;
        }

        public String getId() {
            return id;
        }

        public String getToolId() {
            return toolId;
        }

        public boolean isMergeable() {
            return mergeable;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GeneratedGCode)) return false;

            GeneratedGCode that = (GeneratedGCode) o;

            if (id != null ? !id.equals(that.id) : that.id != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id != null ? id.hashCode() : 0;
        }
    }

}
