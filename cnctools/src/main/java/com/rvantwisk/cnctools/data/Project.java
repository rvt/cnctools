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

package com.rvantwisk.cnctools.data;

import com.rvantwisk.cnctools.gcode.CncToolsGCodegenerator;
import com.rvantwisk.cnctools.misc.Factory;
import com.rvantwisk.cnctools.misc.ToolDBManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Project {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObservableList<Task> milltasks = FXCollections.observableArrayList();
    private  ObjectProperty<CNCToolsPostProcessConfig> postProcessor = new SimpleObjectProperty<>();

    public Project() {
    }

    public Project(String projectName, String description) {
        this.name.set(projectName);
        this.description.set(description);
    }

    public Object readResolve() {
        if (postProcessor==null) {
            postProcessor = new SimpleObjectProperty<>();
            postProcessorProperty().set(Factory.newPostProcessor());
        }
        return this;
    }

    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", has Operations='" + milltasks.size() + '\'' +
                '}';
    }
    public ObservableList<Task> millTasksProperty() {
        return milltasks;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public CNCToolsPostProcessConfig getPostProcessor() {
        return postProcessor.get();
    }

    public ObjectProperty<CNCToolsPostProcessConfig> postProcessorProperty() {
        return postProcessor;
    }

    public void setPostProcessor(CNCToolsPostProcessConfig postProcessor) {
        this.postProcessor.set(postProcessor);
    }

    public StringBuilder getGCode(ToolDBManager toolDBManager) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final PrintStream printStream = new PrintStream(os);

        final CncToolsGCodegenerator gCodeGenerator = Factory.getProcessorDialect(postProcessor.get());
        gCodeGenerator.setOutput(printStream);

        gCodeGenerator.startProgram();
        for (Task t : milltasks) {
            t.generateGCode(toolDBManager, gCodeGenerator);
        }
        gCodeGenerator.endProgram();

        StringBuilder sb = new StringBuilder();
        sb.append(os.toString());
        return sb;
    }

}
