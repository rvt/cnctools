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

package com.rvantwisk.cnctools.controls;

import com.rvantwisk.cnctools.ScreensConfiguration;
import com.rvantwisk.cnctools.controllers.ToolConfigurationsController;
import com.rvantwisk.cnctools.controllers.ToolEditController;
import com.rvantwisk.cnctools.data.ToolParameter;
import com.rvantwisk.cnctools.misc.AbstractController;
import com.rvantwisk.cnctools.misc.FXMLDialog;
import com.rvantwisk.cnctools.misc.ProjectModel;
import com.rvantwisk.events.ToolChangedEvent;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/11/13
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class SelectOrEditToolControl extends VBox {
    @FXML
    private Label iName;
    @FXML
    private Button btnEdit;

    private final ObjectProperty<ToolParameter> tool = new SimpleObjectProperty<>();

    public SelectOrEditToolControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SelectedOrEditTool.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @FXML
    void initialize() {
        btnEdit.disableProperty().bind(tool.isNull());
    }


    @FXML
    void onSelect(ActionEvent event) {
        final FXMLDialog toolConfigurationsDialog= ScreensConfiguration.getInstance().toolConfigurationsDialog();
        ToolConfigurationsController tcc = toolConfigurationsDialog.getController();
        tcc.setMode(ToolConfigurationsController.Mode.SELECT);
        tcc.setToolParameter(this.getTool());
        toolConfigurationsDialog.showAndWait();
        if (tcc.getReturned() != AbstractController.Result.DISMISS) {
            setTool(tcc.getTool());
            fireEvent(new ToolChangedEvent(ToolChangedEvent.TOOL_CHANGED_EVENT));
        }
    }

    @FXML
    void onEdit(ActionEvent event) {
        final FXMLDialog toolEditDialog = ScreensConfiguration.getInstance().toolEditDialog();
        ToolEditController tec = toolEditDialog.getController();
        tec.setTool(ProjectModel.<ToolParameter>deepCopy((tool.get())));
        ToolEditController tcc = toolEditDialog.getController();
        if (tcc.getReturned() != AbstractController.Result.DISMISS) {
            setTool(tcc.getTool());
            fireEvent(new ToolChangedEvent(ToolChangedEvent.TOOL_CHANGED_EVENT));
        }
        toolEditDialog.showAndWait();
    }

    public  ToolParameter getTool() {
        return tool.get();
    }

    public ObjectProperty<ToolParameter> toolProperty() {
        return tool;
    }

    public void setTool(ToolParameter tool) {
        if (tool!=null) {
            iName.setText(tool.getName());
        }
        this.tool.set(tool);
    }

}
