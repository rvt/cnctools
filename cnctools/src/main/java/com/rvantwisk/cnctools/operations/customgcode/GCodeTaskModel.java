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

package com.rvantwisk.cnctools.operations.customgcode;

import com.rvantwisk.cnctools.data.interfaces.TaskModel;
import com.rvantwisk.cnctools.gcode.CncToolsGCodegenerator;
import com.rvantwisk.cnctools.misc.ProjectModel;
import com.rvantwisk.cnctools.misc.ToolDBManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by rvt on 12/13/13.
 */
public class GCodeTaskModel implements TaskModel {

    private final StringProperty gcode = new SimpleStringProperty("");
    private StringProperty gCodeFile = new SimpleStringProperty("");
    private BooleanProperty referencedFile = new SimpleBooleanProperty(false);

    @Override
    public void generateGCode(final ToolDBManager toolDBManager, CncToolsGCodegenerator gCodeGenerator) {

        // Load the file if it was referenced
        if (referencedFile.get() == true && !StringUtils.isEmpty(gCodeFile.get())) {
            try {
                gCodeGenerator.addRaw(ProjectModel.readFileIntoString(new File(this.getgCodeFile())));
            } catch (IOException e) {
                // What to do here???
            }
        } else {
            gCodeGenerator.addRaw(gcode.get());
        }
    }

    public Object readResolve() {
        if (gCodeFile == null) {
            gCodeFile = new SimpleStringProperty();
        }
        if (referencedFile == null) {
            referencedFile = new SimpleBooleanProperty();
        }
        return this;
    }


    public String getGcode() {
        return gcode.get();
    }

    public StringProperty gcodeProperty() {
        return gcode;
    }

    public void setGcode(String gcode) {
        this.gcode.set(gcode);
    }

    public String getgCodeFile() {
        return gCodeFile.get();
    }

    public StringProperty gCodeFileProperty() {
        return gCodeFile;
    }

    public void setgCodeFile(String gCodeFile) {
        this.gCodeFile.set(gCodeFile);
    }

    public boolean getReferencedFile() {
        return referencedFile.get();
    }

    public BooleanProperty referencedFileProperty() {
        return referencedFile;
    }

    public void setReferencedFile(boolean referencedFile) {
        this.referencedFile.set(referencedFile);
    }

    @Override
    public TaskModel copy() {
        GCodeTaskModel n = new GCodeTaskModel();
        n.referencedFileProperty().setValue(this.referencedFileProperty().get());
        n.gCodeFileProperty().setValue(this.gCodeFileProperty().get());
        n.gcodeProperty().setValue(this.getGcode());
        return n;
    }
}
