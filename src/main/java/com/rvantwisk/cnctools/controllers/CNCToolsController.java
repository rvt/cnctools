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

package com.rvantwisk.cnctools.controllers;

import com.rvantwisk.cnctools.ScreensConfiguration;
import com.rvantwisk.cnctools.controllers.interfaces.DialogController;
import com.rvantwisk.cnctools.data.*;
import com.rvantwisk.cnctools.misc.DimensionProperty;
import com.rvantwisk.cnctools.misc.Dimensions;
import com.rvantwisk.cnctools.misc.Factory;
import com.rvantwisk.cnctools.misc.ProjectModel;
import com.rvantwisk.cnctools.operations.createRoundStock.RoundStockOperation;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import jfxtras.labs.dialogs.MonologFX;
import jfxtras.labs.dialogs.MonologFXButton;
import jfxtras.labs.scene.control.BeanPathAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;


/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/5/13
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class CNCToolsController implements DialogController {

    @Autowired
    @Qualifier("projectModel")
    private ProjectModel projectModel;

    @FXML
    TextArea descriptionValue;
    @FXML
    TableView tbl_millTasks;
    @FXML
    ListView<Project> v_projectList;
    @FXML
    TableColumn<Task, Boolean> milltaskEnabled;
    @FXML
    Button addMillTask;
    @FXML
    Button removeMilltask;
    @FXML
    Button editMilltask;
    @FXML
    Button deleteProject;
    @FXML
    Button generateGCode;


    BeanPathAdapter<Project> currentProjectBinding;

    private FXMLDialog dialog;
    private ScreensConfiguration screens;


    public CNCToolsController(ScreensConfiguration screens) {
        this.screens = screens;
    }

    @FXML
    public void addProject(ActionEvent event) throws Exception {
        screens.projectDialog().showAndWait();
    }

    @FXML
    public void showAbout(ActionEvent event) throws Exception {
        screens.aboutDialog().showAndWait();
    }

    @FXML
    public void generateGCode(ActionEvent event) throws Exception {
        if (v_projectList.getSelectionModel().selectedItemProperty().get() != null) {
            final Project p = v_projectList.getSelectionModel().selectedItemProperty().get();
            final StringBuilder gCode = p.getGCode();

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("NC File", "*.tap", "*.ngc"));
            fileChooser.setTitle("Save GCode");
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {
                try {
                    file.delete();
                    BufferedWriter br = Files.newBufferedWriter(file.toPath(),
                            Charset.forName("UTF-8"),
                            new OpenOption[]{StandardOpenOption.CREATE_NEW});
                    br.write(gCode.toString());
                    br.write("\n");

                    br.flush();
                    br.close();

                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }


    @FXML
    public void deleteProject(ActionEvent event) throws Exception {
        if (v_projectList.getSelectionModel().selectedItemProperty().get() != null) {
            MonologFX dialog = new MonologFX(MonologFX.Type.QUESTION);
            dialog.setTitleText("Deleting a project");
            dialog.setMessage("Are you sure you want to delete this project?");
            if (dialog.showDialog() == MonologFXButton.Type.YES) {
                Project p = v_projectList.getSelectionModel().getSelectedItem();
                descriptionValue.setText("");
                projectModel.projectsProperty().remove(v_projectList.getSelectionModel().getSelectedItem());
            }
        }
    }

    @FXML
    public void machinesConfiguration(ActionEvent event) throws Exception {
    }

    @FXML
    public void toolsConfiguration(ActionEvent event) throws Exception {
        screens.toolConfigurationsDialog().showAndWait();
    }

    @FXML
    public void addMillTask(ActionEvent event) throws Exception {
        FXMLDialog mt = screens.millTaskDialog();
        mt.showAndWait();
        AddMillTaskController mtc = (AddMillTaskController) mt.getController();
        mtc.setCurrentProject(v_projectList.getSelectionModel().getSelectedItem());
    }

    @FXML
    public void removeMillTask(ActionEvent event) throws Exception {
        if (tbl_millTasks.getSelectionModel().getSelectedCells().size() > 0) {
            MonologFX dialog = new MonologFX(MonologFX.Type.QUESTION);
            dialog.setTitleText("Deleting a milltask");
            dialog.setMessage("Are you sure you want to delete this task?");
            if (dialog.showDialog() == MonologFXButton.Type.YES) {
                Project project = projectModel.projectsProperty().get(v_projectList.getSelectionModel().getSelectedIndex());
                project.millTasksProperty().remove(tbl_millTasks.getSelectionModel().getSelectedIndex());
            }
        }
    }

    @FXML
    public void editMillTask(ActionEvent event) throws Exception {
        String error = "";
        try {
            Task task = (Task) tbl_millTasks.getSelectionModel().selectedItemProperty().get();


            FXMLDialog dialog = screens.milltaskFactory().getOperationDialog(
                    v_projectList.getSelectionModel().getSelectedItem(),
                    projectModel.toolDBProperty(),
                    task);

            dialog.showAndWait();
            return;
        } catch (ClassNotFoundException e) {
            error = "This Milltask cannot be edited because the controller for this task wasn't found.";
        } catch (NoSuchMethodException e) {
            error = "This Milltask cannot be edited because the method for this task wasn't found.";
        } catch (IllegalAccessException e) {
            error = "This Milltask cannot be edited because it was illegally accessed.";
        } catch (InvocationTargetException e) {
            error = "This Milltask cannot be edited because it was incorrectly invoked.";
        } catch (InstantiationException e) {
            error = "This Milltask cannot be edited because it was incorrectly instanciated.";
        }

        MonologFX dialog = new MonologFX(MonologFX.Type.ERROR);
        dialog.setTitleText("Error creating Mill Task dialog");
        dialog.setMessage(error);
        dialog.showDialog();

    }


    @Override
    public void setDialog(FXMLDialog dialog) {
        this.dialog = dialog;
    }

    private void addProjectDefaults() {
        Project p = new Project("Round stock 30mm", "Creates a round stock of 100mx30mm. Feedrate 2400");
        projectModel.addProject(p);
        Task mt = new Task("Make Round", "Make square stock round (invalid milltask)", "com.rvantwisk.cnctools.operations.createRoundStock.Controller", "CreateRoundStock.fxml");

        ToolParameter nt = Factory.newTool();
        nt.setName("6MM end Mill");
        mt.setMilltaskModel(new RoundStockOperation(nt, DimensionProperty.DimMM(30.0), DimensionProperty.DimMM(20.0), DimensionProperty.DimMM(100.0)));
        p.millTasksProperty().add(mt);

        mt = new Task("Make Square", "Make round stock square", "com.rvantwisk.cnctools.operations.createRoundStock.Controller", "CreateRoundStock.fxml");
        nt = Factory.newTool();
        nt.setName("8MM end Mill");
        nt.radialDepthProperty().set(DimensionProperty.DimMM(4.0));
        nt.axialDepthProperty().set(DimensionProperty.DimMM(4.0));
        nt.setToolType(new EndMill(new DimensionProperty(8.0, Dimensions.Dim.MM)));
        mt.setMilltaskModel(new RoundStockOperation(nt, DimensionProperty.DimMM(30.0), DimensionProperty.DimMM(20.0), DimensionProperty.DimMM(100.0)));
        p.millTasksProperty().add(mt);


        p = new Project("Facing 30mmx30mm", "Facing of wood. Feedrate 2400");
        mt = new Task("Make Round Form Square", "Make feed edge", "com.rvantwisk.cnctools.operations.createRoundStock.Controller", "CreateRoundStock.fxml");
        nt = Factory.newTool();
        nt.setName("10mm end Mill");
        nt.radialDepthProperty().set(DimensionProperty.DimMM(5.0));
        nt.axialDepthProperty().set(DimensionProperty.DimMM(5.0));
        nt.setToolType(new EndMill(new DimensionProperty(10.0, Dimensions.Dim.MM)));
        mt.setMilltaskModel(new RoundStockOperation(nt, DimensionProperty.DimMM(30.0), DimensionProperty.DimMM(20.0), DimensionProperty.DimMM(100.0)));
        p.millTasksProperty().add(mt);
        projectModel.addProject(p);

    }

    private void addDefaultToolSet() {
        StockToolParameter stp = Factory.newStockTool();
        projectModel.toolDBProperty().add(stp);

        stp = Factory.newStockTool();
        stp.radialDepthProperty().set(DimensionProperty.DimMM(4.0));
        stp.axialDepthProperty().set(DimensionProperty.DimMM(5.0));
        stp.setToolType(new EndMill(new DimensionProperty(10.0, Dimensions.Dim.MM)));
        stp.setName("End Mill 10mm (Spindle)");
        projectModel.toolDBProperty().add(stp);

        stp = Factory.newStockTool();
        stp.radialDepthProperty().set(DimensionProperty.DimMM(3.0));
        stp.axialDepthProperty().set(DimensionProperty.DimMM(4.0));
        stp.setToolType(new EndMill(new DimensionProperty(8.0, Dimensions.Dim.MM)));
        stp.setName("End Mill 8mm");
        projectModel.toolDBProperty().add(stp);

        stp = Factory.newStockTool();
        stp.radialDepthProperty().set(DimensionProperty.DimMM(2.5));
        stp.axialDepthProperty().set(DimensionProperty.DimMM(3.0));
        stp.setToolType(new EndMill(new DimensionProperty(6.0, Dimensions.Dim.MM)));
        stp.setName("End Mill 6mm");
        projectModel.toolDBProperty().add(stp);

        stp = Factory.newStockTool();
        stp.radialDepthProperty().set(DimensionProperty.DimMM(4.0));
        stp.axialDepthProperty().set(DimensionProperty.DimMM(5.0));
        stp.setToolType(new BallMill(new DimensionProperty(10.0, Dimensions.Dim.MM)));
        stp.setName("Ball Mill 10mm");
        projectModel.toolDBProperty().add(stp);

        stp = Factory.newStockTool();
        stp.radialDepthProperty().set(DimensionProperty.DimMM(3.0));
        stp.axialDepthProperty().set(DimensionProperty.DimMM(4.0));
        stp.setToolType(new BallMill(new DimensionProperty(8.0, Dimensions.Dim.MM)));
        stp.setName("Ball Mill 8mm");
        projectModel.toolDBProperty().add(stp);

        stp = Factory.newStockTool();
        stp.radialDepthProperty().set(DimensionProperty.DimMM(2.5));
        stp.axialDepthProperty().set(DimensionProperty.DimMM(5.0));
        stp.setToolType(new BallMill(new DimensionProperty(6.0, Dimensions.Dim.MM)));
        stp.setName("Ball Mill 6mm");
        projectModel.toolDBProperty().add(stp);

    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        v_projectList.setItems(projectModel.projectsProperty());

        projectModel.loadToolsFromDB();
        Integer s = projectModel.toolDBProperty().size();
        if (projectModel.toolDBProperty().size() == 0) {
            addDefaultToolSet();
            projectModel.saveToolDB();
            MonologFX dialog = new MonologFX(MonologFX.Type.INFO);
            dialog.setTitleText("Tools defaults loaded");
            dialog.setMessage("No tools where found, a new a new toolset has been created.");
            dialog.showDialog();
        }

        projectModel.loadProjectsFromDB();
        if (projectModel.projectsProperty().size() == 0) {
            addProjectDefaults();
            projectModel.saveProjects();
            MonologFX dialog = new MonologFX(MonologFX.Type.INFO);
            dialog.setTitleText("Project defaults loaded");
            dialog.setMessage("No project was found, a new template project was created. Feel free to delete or modify");
            dialog.showDialog();
        }


        deleteProject.disableProperty().bind(v_projectList.getSelectionModel().selectedItemProperty().isNull());
        removeMilltask.disableProperty().bind(tbl_millTasks.getSelectionModel().selectedItemProperty().isNull());
        addMillTask.disableProperty().bind(v_projectList.getSelectionModel().selectedItemProperty().isNull());
        editMilltask.disableProperty().bind(tbl_millTasks.getSelectionModel().selectedItemProperty().isNull());
        generateGCode.disableProperty().bind(v_projectList.getSelectionModel().selectedItemProperty().isNull());

        // Update description when project changes
        v_projectList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Project>() {
            @Override
            public void changed(ObservableValue<? extends Project> observable, Project oldValue, Project newValue) {
                if (oldValue != null) {
                    descriptionValue.textProperty().unbindBidirectional(oldValue.descriptionProperty());
                }
                if (projectModel.projectsProperty().size() == 0) {
                    tbl_millTasks.getItems().clear();
                } else {
                    tbl_millTasks.setItems(newValue.millTasksProperty());
                    descriptionValue.textProperty().bindBidirectional(newValue.descriptionProperty());
                }
            }
        });

        // Set text in ListView
        v_projectList.setCellFactory(new Callback<ListView<Project>, ListCell<Project>>() {
            @Override
            public ListCell<Project> call(ListView<Project> p) {
                ListCell<Project> cell = new ListCell<Project>() {
                    @Override
                    protected void updateItem(Project t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.nameProperty().getValue());
                        } else {
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });

        // checkbox renderer for milltasks
        milltaskEnabled.setCellFactory(new Callback<TableColumn<Task, Boolean>, TableCell<Task, Boolean>>() {
            @Override
            public TableCell<Task, Boolean> call(TableColumn<Task, Boolean> p) {
                CheckBoxTableCell<Task, Boolean> cell = new CheckBoxTableCell<>();
                cell.setEditable(true);
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        tbl_millTasks.setEditable(true);


        // Save DB on exit
        dialog.setOnHidden(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                projectModel.saveProjects();
                projectModel.saveToolDB();
            }
        });
    }

    public void save(ActionEvent actionEvent) {
        projectModel.saveProjects();
        projectModel.saveToolDB();
    }

}

