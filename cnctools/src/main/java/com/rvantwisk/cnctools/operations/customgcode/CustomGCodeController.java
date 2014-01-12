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
import com.rvantwisk.cnctools.controls.opengl.GCodeActor;
import com.rvantwisk.cnctools.controls.opengl.PlatformActor;
import com.rvantwisk.cnctools.data.interfaces.TaskModel;
import com.rvantwisk.cnctools.misc.ProjectModel;
import com.rvantwisk.cnctools.misc.ToolDBManager;
import com.rvantwisk.cnctools.operations.interfaces.MillTaskController;
import com.rvantwisk.gcodeparser.GCodeParser;
import com.rvantwisk.gcodeparser.MachineStatus;
import com.rvantwisk.gcodeparser.exceptions.SimException;
import com.rvantwisk.gcodeparser.exceptions.UnsupportedSimException;
import com.rvantwisk.gcodeparser.machines.StatisticLimitsController;
import com.rvantwisk.gcodeparser.validators.LinuxCNCValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import jfxtras.labs.dialogs.MonologFX;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@Scope("prototype")
public class CustomGCodeController implements MillTaskController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private GCodeTaskModel model;

    @Autowired
    ToolDBManager toolDBManager;
    @FXML
    private GCodeViewerControl gCodeViewerControl;
    @FXML
    private TextArea gCodeText;
    @FXML
    private TextField iFileName;
    @FXML
    private TextField errors;
    @FXML
    private CheckBox iKeepReference;


    @Override
    public void setModel(TaskModel model) {
        this.model = (GCodeTaskModel) model;
    }

    @Override
    public <T extends TaskModel> T createNewModel() {
        return (T) new GCodeTaskModel();
    }

    @Override
    public void destroy() {
        gCodeViewerControl.destroy();
    }

    @FXML
    public TaskModel getModel() {
        formToModel();
        return model;
    }

    private void formToModel() {
        model.gcodeProperty().setValue(gCodeText.getText());
        model.referencedFileProperty().setValue(iKeepReference.selectedProperty().get());
        // Remove the G-Code data during save operation if it's a referenced file
        if (model.referencedFileProperty().get()) {
            model.setGcode("");
        }
    }

    private void modelToForm() {
        reLoadReferencedFile();
        iFileName.setText(model.getgCodeFile());
        gCodeText.setText(model.getGcode());
        iKeepReference.setSelected(model.referencedFileProperty().get());
    }

    @FXML
    void initialize() {
        errors.setTooltip(new Tooltip());
        modelToForm();
        gCodeText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                reRenderModel();
            }
        });
        reRenderModel();
    }

    private void reRenderModel() {
        try {
            InputStream str = null;

            if (model.referencedFileProperty().get() && !StringUtils.isEmpty(model.getgCodeFile())) {
                str = new ByteArrayInputStream(model.getGcode().getBytes(StandardCharsets.UTF_8));
            } else if (gCodeText.getText() != null) {
                str = new ByteArrayInputStream(gCodeText.getText().getBytes(StandardCharsets.UTF_8));
            }

            if (str != null) {
                GCodeActor machine = new GCodeActor("gcode");
                StatisticLimitsController stats = new StatisticLimitsController(machine);
                LinuxCNCValidator validator = new LinuxCNCValidator();
                GCodeParser parser = new GCodeParser(stats, validator, str);

                gCodeViewerControl.addActor(machine);

                // create a platform
                gCodeViewerControl.addActor(new PlatformActor(
                        stats.getMaxValues().get(MachineStatus.Axis.X).floatValue()*1.2f,
                        stats.getMaxValues().get(MachineStatus.Axis.Y).floatValue()*1.2f
                ));

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

    public void selectFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("NC File", "*.tap", "*.ngc"));
        fileChooser.setTitle("Load GCode");
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                model.setGcode(ProjectModel.readFileIntoString(file));
                model.setgCodeFile(file.getAbsolutePath());
                iFileName.setText(model.getgCodeFile());
                gCodeText.setText(model.getGcode());
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void onKeepReference(ActionEvent actionEvent) {
        model.referencedFileProperty().setValue(iKeepReference.selectedProperty().get());
        if (model.referencedFileProperty().get() == true) {
            reLoadReferencedFile();
        }
    }

    private void reLoadReferencedFile() {
        // Load the referenced model back in
        if (model.referencedFileProperty().get() && !StringUtils.isEmpty(model.getgCodeFile())) {
            try {
                model.setGcode(ProjectModel.readFileIntoString(new File(model.getgCodeFile())));
                reRenderModel();
            } catch (IOException e) {
                MonologFX dialog = new MonologFX(MonologFX.Type.QUESTION);
                dialog.setTitleText("Referenced file is gone!");
                dialog.setMessage("Referenced file is not found on the system, please re-reference.");
                dialog.show();
                gCodeText.setText("");
            }
        }
    }
}
