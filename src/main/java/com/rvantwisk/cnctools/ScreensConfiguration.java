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

package com.rvantwisk.cnctools;

import com.rvantwisk.cnctools.controllers.*;
import com.rvantwisk.cnctools.misc.FXMLDialog;
import com.rvantwisk.cnctools.misc.DialogBuilder;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

@Configuration
@Lazy
public class ScreensConfiguration {
    private Stage primaryStage;
    private ApplicationContext context;
    private static ScreensConfiguration screensConfiguration;

    public ScreensConfiguration() {
        screensConfiguration = this;
    }

    public static ScreensConfiguration getInstance() {
        return screensConfiguration;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    public void setContext(ApplicationContext context) {
        this.context = context;
    }

    @Bean
    @Scope("prototype")
    public DialogBuilder milltaskFactory() {
        return new DialogBuilder(primaryStage, context);
    }

    /**
     * ***************************** cncTools *******************************
     */
    @Bean
    @Scope("prototype")
    public FXMLDialog cncTools() {
        return new FXMLDialog(indexerController(), getClass().getResource("CNCTools.fxml"), primaryStage);
    }

    @Bean
    @Scope("prototype")
    public CNCToolsController indexerController() {
        return new CNCToolsController(this);
    }

    /**
     * ***************************** errorDialog *******************************
     */
    @Bean
    @Scope("prototype")
    public FXMLDialog errorDialog() {
        return new FXMLDialog(errorController(), getClass().getResource("Error.fxml"), primaryStage, StageStyle.UNDECORATED);
    }

    @Bean
    @Scope("prototype")
    public ErrorController errorController() {
        return new ErrorController();
    }


    /**
     * ***************************** projectDialog *******************************
     */
    @Bean
    @Scope("prototype")
    public FXMLDialog projectDialog() {
        return new FXMLDialog(projectController(), getClass().getResource("AddProject.fxml"), primaryStage);
    }

    @Bean
    @Scope("prototype")
    public AddProjectController projectController() {
        return new AddProjectController();
    }

    /**
     * ***************************** millTaskDialog *******************************
     */
    @Bean
    @Scope("prototype")
    public FXMLDialog millTaskDialog() {
        return new FXMLDialog(millTaskController(), getClass().getResource("AddMillTask.fxml"), primaryStage);
    }

    @Bean
    @Scope("prototype")
    public AddMillTaskController millTaskController() {
        return new AddMillTaskController();
    }


    /**
     * ***************************** Tools configuration *******************************
     */
    @Bean
    @Scope("prototype")
    @Qualifier("toolConfigurationsDialog")
    public FXMLDialog toolConfigurationsDialog() {
        ToolConfigurationsController tcc = new ToolConfigurationsController();
        tcc.setMode(ToolConfigurationsController.Mode.EDIT);
        return new FXMLDialog(toolConfigurationsController(), getClass().getResource("ToolConfigurations.fxml"), primaryStage);
    }

    @Bean
    @Scope("prototype")
    public ToolConfigurationsController toolConfigurationsController() {
        return new ToolConfigurationsController();
    }

    /**
     * ***************************** Edit Tool *******************************
     */
    @Bean
    @Scope("prototype")
    @Qualifier("toolEditDialog")
    public FXMLDialog toolEditDialog() {
        return new FXMLDialog(toolEditController(), getClass().getResource("ToolEdit.fxml"), primaryStage);
    }

    @Bean
    @Scope("prototype")
    @Qualifier("toolEditController")
    public ToolEditController toolEditController() {
        return new ToolEditController();
    }

    /**
     * ***************************** About Tool *******************************
     */
    @Bean
    @Scope("prototype")
    @Qualifier("aboutDialog")
    public FXMLDialog aboutDialog() {
        return new FXMLDialog(aboutController(), getClass().getResource("About.fxml"), primaryStage, StageStyle.UNDECORATED);
    }

    @Bean
    @Scope("prototype")
    @Qualifier("aboutDialogController")
    public AboutController aboutController() {
        return new AboutController();
    }

    /**
     * ***************************** GCodeEditor Tool *******************************
     */
    @Bean
    @Scope("prototype")
    @Qualifier("gCodeEditor")
    public FXMLDialog gCodeEditorDialog() {
        return new FXMLDialog(gCodeEditorController(), getClass().getResource("GCodeViewerDialog.fxml"), primaryStage);
    }

    @Bean
    @Scope("prototype")
    @Qualifier("gCodeEditorController")
    public GCodeViewerController gCodeEditorController() {
        return new GCodeViewerController();
    }

    /**
     * ***************************** GCodeEditor Tool *******************************
     */
    @Bean
    @Scope("prototype")
    @Qualifier("postProcessorsDialog")
    public FXMLDialog postProcessorsDialog() {
        return new FXMLDialog(postProcessorsController(), getClass().getResource("PostProcessors.fxml"), primaryStage);
    }

    @Bean
    @Scope("prototype")
    @Qualifier("postProcessorsController")
    public PostProcessorsController postProcessorsController() {
        return new PostProcessorsController();
    }


}
