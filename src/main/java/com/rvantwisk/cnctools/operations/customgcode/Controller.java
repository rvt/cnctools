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

import com.rvantwisk.cnctools.controls.GCodeViewerControl;
import com.rvantwisk.cnctools.data.Project;
import com.rvantwisk.cnctools.data.Task;
import com.rvantwisk.cnctools.data.ToolParameter;
import com.rvantwisk.cnctools.gcodeparser.exceptions.SimException;
import com.rvantwisk.cnctools.gcodeparser.exceptions.UnsupportedSimException;
import com.rvantwisk.cnctools.misc.FXMLDialog;
import com.rvantwisk.cnctools.misc.ToolDBManager;
import com.rvantwisk.cnctools.operations.interfaces.MillTaskController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

public class Controller extends MillTaskController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Project project;
    final private ObservableList<ToolParameter> toolDB = FXCollections.observableArrayList();

    @FXML
    private GCodeViewerControl gCodeViewerControl;
    @FXML
    private TextArea gCodeText;
    @FXML
    private Label errors;

    private FXMLDialog dialog;

    private GCodeOperation model;
    private Task task;

    private ToolDBManager toolDBManager;

    public void setProject(Project project) {
        this.project = project;
    }

    @Override
    public void setToolDBManager(ToolDBManager toolDBManager) {
        this.toolDBManager = toolDBManager;
    }


    @Override
    public void setTask(Task task) {
        this.task = task;
        model = task.getMilltaskModel();
        if (model == null) {
            model = new GCodeOperation();
            task.setMilltaskModel(model);
        }
    }

    @FXML
    public void onCancel() {
        dialog.close();
    }

    @FXML
    public void onSave() throws ParseException {
        model.gcodeProperty().setValue(gCodeText.getText());
        dialog.close();
    }

    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }


    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

        errors.setTooltip(new Tooltip());

        gCodeText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {

                try {
                    if (gCodeText.getText()!=null) {
                        InputStream str = new ByteArrayInputStream(gCodeText.getText().getBytes(StandardCharsets.UTF_8));
                        gCodeViewerControl.load(str);
                    }
                    errors.textProperty().set("");
                    errors.getTooltip().setText("");
                } catch (UnsupportedSimException e) {
                    errors.textProperty().set(e.getMessage());
                    errors.getTooltip().setText(e.getMessage());
                } catch (SimException e) {
                    errors.textProperty().set(e.getMessage());
                    errors.getTooltip().setText(e.getMessage());
                } catch (Exception e) {
                    errors.textProperty().set(e.getMessage());
                    errors.getTooltip().setText(e.getMessage());
                }
            }
        });

        // toolSelectOrEditController.setTool();
        gCodeText.setText(model.getGcode());


        /*
        ApplicapableMillTask myBean = new ApplicapableMillTask("name", "description", "classname"," fxmlfilename"); // get an instance of the bean to be edited
        Node fxForm = new FXForm(myBean);  // create the FXForm node for your bean
        mypane.getChildren().add(fxForm); */

    }

}
