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
import com.rvantwisk.cnctools.misc.ToolDBManager;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/8/13
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class Task extends AbstractTask  {

    private BooleanProperty enabled = new SimpleBooleanProperty();
    private ObjectProperty<AbstractOperation> milltaskModel = new SimpleObjectProperty<AbstractOperation>();

    public Task(String name, String description, String className, String fxmlFileName) {
        super(name,description,className,fxmlFileName);
        this.enabled.set(Boolean.TRUE);
    }

    public Task() {
    }

    public BooleanProperty enabledProperty() {
        return enabled;
    }

    public <T extends AbstractOperation> T getMilltaskModel() {
        return (T) milltaskModel.get();
    }

    public ObjectProperty<AbstractOperation> milltaskModelProperty() {
        return milltaskModel;
    }

    public void setMilltaskModel(AbstractOperation milltaskModel) {
        this.milltaskModel.set(milltaskModel);
    }

    public void generateGCode(final ToolDBManager toolDBManager, final CncToolsGCodegenerator gCodeGenerator) {
        if (enabled.get()==true) {
            milltaskModel.get().generateGCode(toolDBManager, gCodeGenerator);
        }
    };
}
