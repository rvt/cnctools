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

package com.rvantwisk.cnctools.operations.facing;

import com.rvantwisk.cnctools.controls.DimensionControl;
import com.rvantwisk.cnctools.controls.GCodeViewerControl;
import com.rvantwisk.cnctools.controls.SelectOrEditToolControl;
import com.rvantwisk.cnctools.controls.opengl.ArrowsActor;
import com.rvantwisk.cnctools.controls.opengl.GCodeActor;
import com.rvantwisk.cnctools.controls.opengl.PlatformActor;
import com.rvantwisk.cnctools.data.CNCToolsPostProcessConfig;
import com.rvantwisk.cnctools.data.Project;
import com.rvantwisk.cnctools.data.interfaces.TaskModel;
import com.rvantwisk.cnctools.gcode.CncToolsRS274;
import com.rvantwisk.cnctools.misc.Factory;
import com.rvantwisk.cnctools.misc.ToolDBManager;
import com.rvantwisk.cnctools.operations.interfaces.MillTaskController;
import com.rvantwisk.events.ToolChangedEvent;
import com.rvantwisk.gcodeparser.GCodeParser;
import com.rvantwisk.gcodeparser.MachineStatus;
import com.rvantwisk.gcodeparser.exceptions.SimException;
import com.rvantwisk.gcodeparser.exceptions.UnsupportedSimException;
import com.rvantwisk.gcodeparser.machines.StatisticLimitsController;
import com.rvantwisk.gcodeparser.validators.LinuxCNCValidator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by rvt on 12/30/13.
 */
@Component
public class FacingController implements MillTaskController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Project project;
    @Autowired
    private ToolDBManager toolDBManager;
    private FacingModel model;
    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private ToggleGroup edgeDirection;
    @FXML
    private ToggleGroup cutDirection;
    @FXML
    private GCodeViewerControl gCodeViewerControl;
    @FXML
    private ComboBox<FacingModel.Configuration> iCutStrategy;
    @FXML
    private CheckBox iEdgeCleanup;
    @FXML
    private RadioButton iEdgeClimb;
    @FXML
    private RadioButton iEdgeConv;
    @FXML
    private RadioButton iConv;
    @FXML
    private RadioButton iClimb;
    @FXML
    private ChoiceBox<String> iPartReference;
    @FXML
    private DimensionControl iXLength;
    @FXML
    private DimensionControl iYLength;
    @FXML
    private DimensionControl iZFinal;
    @FXML
    private DimensionControl iZSafe;
    @FXML
    private DimensionControl iZTop;
    @FXML
    private SelectOrEditToolControl selectOrEditTool;

    @Override
    public void setProject(Project project) {
        this.project = project;
    }

    @FXML
    void initialize() {
        assert cutDirection != null : "fx:id=\"cutDirection\" was not injected: check your FXML file 'Facing.fxml'.";
        assert edgeDirection != null : "fx:id=\"edgeDirection\" was not injected: check your FXML file 'Facing.fxml'.";
        assert gCodeViewerControl != null : "fx:id=\"gCodeViewerControl\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iClimb != null : "fx:id=\"iClimb\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iConv != null : "fx:id=\"iConv\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iCutStrategy != null : "fx:id=\"iCutStrategy\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iEdgeCleanup != null : "fx:id=\"iEdgeCleanup\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iEdgeClimb != null : "fx:id=\"iEdgeClimb\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iEdgeConv != null : "fx:id=\"iEdgeConv\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iPartReference != null : "fx:id=\"iPartReference\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iXLength != null : "fx:id=\"iXLength\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iYLength != null : "fx:id=\"iYLength\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iZFinal != null : "fx:id=\"iZFinal\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iZSafe != null : "fx:id=\"iZSafe\" was not injected: check your FXML file 'Facing.fxml'.";
        assert iZTop != null : "fx:id=\"iZTop\" was not injected: check your FXML file 'Facing.fxml'.";
        assert selectOrEditTool != null : "fx:id=\"selectOrEditTool\" was not injected: check your FXML file 'Facing.fxml'.";


        iCutStrategy.getItems().clear();
        iCutStrategy.getItems().addAll(FacingModel.CONFIGLIST);

        modelToForm();

        selectOrEditTool.addEventHandler(ToolChangedEvent.TOOL_CHANGED_EVENT, new EventHandler<ToolChangedEvent>() {
            @Override
            public void handle(ToolChangedEvent toolChangedEvent) {
                generateGCode();
            }
        });

        iXLength.dimensionProperty().valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                generateGCode();
            }
        });
        iYLength.dimensionProperty().valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                generateGCode();
            }
        });
        iZFinal.dimensionProperty().valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                generateGCode();
            }
        });
        iZSafe.dimensionProperty().valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                generateGCode();
            }
        });
        iZTop.dimensionProperty().valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                generateGCode();
            }
        });


        iEdgeCleanup.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                generateGCode();
            }
        });

        edgeDirection.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle toggle2) {
                generateGCode();
            }
        });
        cutDirection.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observableValue, Toggle toggle, Toggle toggle2) {
                generateGCode();
            }
        });

        iCutStrategy.valueProperty().addListener(new ChangeListener<FacingModel.Configuration>() {

            @Override
            public void changed(ObservableValue<? extends FacingModel.Configuration> observableValue, FacingModel.Configuration configuration, FacingModel.Configuration configuration2) {
                generateGCode();
            }
        });

        iPartReference.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                generateGCode();
            }
        });


        generateGCode();
    }

    @Override
    public TaskModel getModel() {
        formToModel();
        return model;
    }

    @Override
    public void setModel(final TaskModel model) {
        this.model = (FacingModel) model;
    }

    @Override
    public <T extends TaskModel> T createNewModel() {
        return (T) new FacingModel();
    }

    @Override
    public void destroy() {
        gCodeViewerControl.destroy();
    }

    private void formToModel() {

        model.setToolID(this.selectOrEditTool.getTool().getId());
        model.setCutStrategy(this.iCutStrategy.selectionModelProperty().getValue().getSelectedItem());
        model.setEdgeCleanup(this.iEdgeCleanup.selectedProperty().get());
        model.setEdgeCleanupClimb(this.iEdgeClimb.selectedProperty().get());
        model.setPartReference(this.iPartReference.selectionModelProperty().getValue().getSelectedItem());

        model.getzFinal().set(this.iZFinal.dimensionProperty());
        model.getzSafe().set(this.iZSafe.dimensionProperty());
        model.getzTop().set(this.iZTop.dimensionProperty());
        model.getWidth().set(this.iXLength.dimensionProperty());
        model.getHeight().set(this.iYLength.dimensionProperty());

        model.setClimbCutting(this.iClimb.selectedProperty().get());
    }

    private void modelToForm() {
        selectOrEditTool.setTool(toolDBManager.getByID(model.getToolID()));

        int i = iCutStrategy.getItems().indexOf(model.cutStrategyProperty().get());
        iCutStrategy.getSelectionModel().select(i < 0 ? i = 0 : i);

        iEdgeCleanup.selectedProperty().set(model.edgeCleanupProperty().get());
        iEdgeClimb.selectedProperty().set(model.getEdgeCleanupClimb());
        iEdgeConv.selectedProperty().set(!model.getEdgeCleanupClimb());

        i = iPartReference.getItems().indexOf(model.partReferenceProperty().get());
        iPartReference.getSelectionModel().select(i < 0 ? i = 0 : i);

        iZFinal.dimensionProperty().set(model.getzFinal());
        iZSafe.dimensionProperty().set(model.getzSafe());
        iZTop.dimensionProperty().set(model.getzTop());
        iXLength.dimensionProperty().set(model.getWidth());
        iYLength.dimensionProperty().set(model.getHeight());

        iClimb.selectedProperty().set(model.getClimbCutting());
        iConv.selectedProperty().set(!model.getClimbCutting());
    }

    private void generateGCode() {
        String error = "";
        try {

            formToModel();

            CNCToolsPostProcessConfig ppc;
            ppc = project.getPostProcessor();
            if (ppc == null) {
                ppc = Factory.newPostProcessor();
            }

            CncToolsRS274 gCodeGenerator = new CncToolsRS274(ppc);
            gCodeGenerator.startProgram();
            gCodeGenerator.newSet("");
            model.generateGCode(toolDBManager, gCodeGenerator);
            gCodeGenerator.endProgram();

            GCodeActor machine = new GCodeActor("gcode");
            ArrowsActor arrows = new ArrowsActor("arrows");
            StatisticLimitsController stats = new StatisticLimitsController();
            LinuxCNCValidator validator = new LinuxCNCValidator();

            GCodeParser parser = new GCodeParser(validator, gCodeGenerator.getGCode().concate(), machine, arrows, stats);

            gCodeViewerControl.addActor(machine);
            gCodeViewerControl.addActor(arrows);

            // create a platform
            gCodeViewerControl.addActor(new PlatformActor(
                    stats.getMinValues().get(MachineStatus.Axis.X).floatValue() - 20.0f,
                    stats.getMinValues().get(MachineStatus.Axis.Y).floatValue() - 20.0f,
                    stats.getMaxValues().get(MachineStatus.Axis.X).floatValue() + 20.0f,
                    stats.getMaxValues().get(MachineStatus.Axis.Y).floatValue() + 20.0f,
                    stats.isMetric()
            ));

        } catch (UnsupportedSimException e) {
            error = e.getMessage();
            logger.error("UnsupportedSimException", e);
        } catch (SimException e) {
            error = e.getMessage();
            logger.error("SimException", e);
        } catch (Exception e) {
            logger.error("Exception", e);
            error = e.getMessage();
        }
    }
}
