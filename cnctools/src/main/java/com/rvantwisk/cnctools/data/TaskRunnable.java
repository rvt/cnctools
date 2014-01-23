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

import com.rvantwisk.cnctools.data.interfaces.Copyable;
import com.rvantwisk.cnctools.data.interfaces.TaskModel;
import com.rvantwisk.cnctools.gcode.CncToolsGCodegenerator;
import com.rvantwisk.cnctools.misc.ToolDBManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/8/13
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class TaskRunnable extends AbstractTask implements Copyable<TaskRunnable> {

    private String id;
    private BooleanProperty enabled = new SimpleBooleanProperty();
    private ObjectProperty<TaskModel> milltaskModel = new SimpleObjectProperty<TaskModel>();

    public TaskRunnable(String name, String description, String className, String fxmlFileName) {
        super(name, description, className, fxmlFileName);
        this.enabled.set(Boolean.TRUE);
        id = UUID.randomUUID().toString();
    }


    public boolean getEnabled() {
        return enabled.get();
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    public <T extends TaskModel> T getMilltaskModel() {
        return (T) milltaskModel.get();
    }

    public ObjectProperty<TaskModel> milltaskModelProperty() {
        return milltaskModel;
    }

    public void setMilltaskModel(TaskModel milltaskModel) {
        this.milltaskModel.set(milltaskModel);
    }

    public void generateGCode(final ToolDBManager toolDBManager, final CncToolsGCodegenerator gCodeGenerator) {
        if (enabled.get() == true) {
            gCodeGenerator.newSet(true, id, null);
            gCodeGenerator.comment(StringUtils.rightPad("--- Program: " + getName(), 50, "-"));
            milltaskModel.get().generateGCode(toolDBManager, gCodeGenerator, getId());
        } else {
            gCodeGenerator.newSet(true, id, null);
            gCodeGenerator.comment(StringUtils.rightPad("--- Program: " + getName() + " (disabled)", 50, "-"));
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public TaskRunnable copy() {
        TaskRunnable t = new TaskRunnable(this.getName(), this.getDescription(), this.getClassName(), this.getFxmlFileName());
        t.setId(this.getId());
        this.enabled.set(this.getEnabled());
        if (milltaskModel.get()!=null) {
            t.milltaskModel.set(milltaskModel.get().copy());
        }
        return t;
    }


}
