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

package com.rvantwisk.cnctools.operations.createRoundStock;

import com.rvantwisk.cnctools.controls.DimensionControl;
import com.rvantwisk.cnctools.controls.GCodeViewerControl;
import com.rvantwisk.cnctools.controls.SelectOrEditToolControl;
import com.rvantwisk.cnctools.data.CNCToolsPostProcessConfig;
import com.rvantwisk.cnctools.data.interfaces.TaskModel;
import com.rvantwisk.cnctools.gcode.CncToolsRS274;
import com.rvantwisk.cnctools.misc.Factory;
import com.rvantwisk.cnctools.misc.ToolDBManager;
import com.rvantwisk.cnctools.operations.interfaces.MillTaskController;
import com.rvantwisk.events.ToolChangedEvent;
import com.rvantwisk.gcodeparser.exceptions.SimException;
import com.rvantwisk.gcodeparser.exceptions.UnsupportedSimException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

@Component
@Scope("prototype")
public class CreateRoundStockController implements MillTaskController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private RoundStockTaskModel model;
    @Autowired
    private ToolDBManager toolDBManager;
    @FXML
    private SelectOrEditToolControl selectOrEditTool;
    @FXML
    private AnchorPane toolSelectOrEdit;
    @FXML
    private GCodeViewerControl gCodeViewerControl;
    @FXML
    private DimensionControl iFinalLength;
    @FXML
    private DimensionControl iFinalSize;
    @FXML
    private DimensionControl iStockSize;
    @FXML
    private TextField iName;


    @Override
    public void setModel(TaskModel model) {
        this.model = (RoundStockTaskModel) model;
    }

    @Override
    public <T extends TaskModel> T createNewModel() {
        return (T) new RoundStockTaskModel();
    }

    @FXML
    public TaskModel getModel() {
        formToModel();
        return model;
    }

    private void formToModel() {
        model.setToolID(this.selectOrEditTool.getTool().getId());
        model.finalSizeProperty().set(iFinalSize.dimensionProperty());
        model.finalLengthProperty().set(iFinalLength.dimensionProperty());
        model.stockSizeProperty().set(iStockSize.dimensionProperty());
    }

    private void modelToForm() {
        selectOrEditTool.setTool(toolDBManager.getByID(model.getToolID()));
        iFinalLength.dimensionProperty().set(model.finalLengthProperty());
        iFinalSize.dimensionProperty().set(model.finalSizeProperty());
        iStockSize.dimensionProperty().set(model.stockSizeProperty());
    }

    @FXML
    void initialize() {

       // iName.textProperty().setValue(task.getName());
        modelToForm();

        selectOrEditTool.addEventHandler(ToolChangedEvent.TOOL_CHANGED_EVENT, new EventHandler<ToolChangedEvent>() {
            @Override
            public void handle(ToolChangedEvent toolChangedEvent) {
                generateGCode();
            }
        });


        iFinalLength.dimensionProperty().valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                generateGCode();
            }
        });
        iFinalSize.dimensionProperty().valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                generateGCode();
            }
        });
        iStockSize.dimensionProperty().valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                generateGCode();
            }
        });

        generateGCode();
    }

    private void generateGCode() {
        String error = "";
        try {

            formToModel();

            CNCToolsPostProcessConfig ppc;
//            PostProcessorConfig ppc = project.getPostProcessor();
//            if (ppc==null) {
            ppc = Factory.newPostProcessor();
//            }

            final ByteArrayOutputStream os = new ByteArrayOutputStream();
            final PrintStream printStream = new PrintStream(os);

            CncToolsRS274 gCodeGenerator = new CncToolsRS274(ppc);
            gCodeGenerator.setOutput(printStream);

            model.generateGCode(toolDBManager, gCodeGenerator);

            InputStream in = new ByteArrayInputStream(os.toByteArray());
            gCodeViewerControl.load(in);

        } catch (UnsupportedSimException e) {
            error = e.getMessage();
        } catch (SimException e) {
            error = e.getMessage();
        } catch (Exception e) {
            error = e.getMessage();
        }
    }

}
