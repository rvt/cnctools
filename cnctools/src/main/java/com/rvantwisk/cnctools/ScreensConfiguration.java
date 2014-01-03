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
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

@Lazy
@Configuration
public class ScreensConfiguration {
    private Stage primaryStage;
    private ApplicationContext applicationContext;
    private static ScreensConfiguration screensConfiguration;

    public ScreensConfiguration() {
        screensConfiguration = this;
    }

    private Map<String, Boolean> registeredbeans = new HashMap<>();

    public static ScreensConfiguration getInstance() {
        return screensConfiguration;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }


    public void setContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Add's a new bean to Spring's context
     *
     * @param name
     */
    public void registerBean(final String name) {
        if (registeredbeans.containsKey(name)) {
            return;
        }
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClassName(name);
        beanDefinition.setAutowireCandidate(true);
        AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
        registry.registerBeanDefinition(name, beanDefinition);
        factory.autowireBeanProperties(this,
                AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
        registeredbeans.put(name, Boolean.TRUE);
    }

    public ApplicationContext getContext() {
        return applicationContext;
    }


    /**
     * ***************************** errorDialog *******************************
     */
    @Bean(name = "errorDialog")
    @Scope("prototype")
    public FXMLDialog errorDialog() {
        return new FXMLDialog(errorController(), getClass().getResource("Error.fxml"), primaryStage, StageStyle.UNDECORATED);
    }

    @Bean(name = "errorController")
    @Scope("prototype")
    public ErrorController errorController() {
        return new ErrorController();
    }


    /**
     * ***************************** projectDialog *******************************
     */
    @Bean(name = "projectDialog")
    @Scope("prototype")
    public FXMLDialog projectDialog() {
        return new FXMLDialog(projectController(), getClass().getResource("AddProject.fxml"), primaryStage);
    }

    @Bean(name = "projectController")
    @Scope("prototype")
    public AddProjectController projectController() {
        return new AddProjectController();
    }

    /**
     * ***************************** millTaskDialog *******************************
     */
    @Bean(name = "millTaskDialog")
    @Scope("prototype")

    public FXMLDialog millTaskDialog() {
        return new FXMLDialog(millTaskController(), getClass().getResource("AddMillTask.fxml"), primaryStage);
    }

    @Bean(name = "millTaskController")
    @Scope("prototype")
    public AddMillTaskController millTaskController() {
        return new AddMillTaskController();
    }


    /**
     * ***************************** Tools configuration *******************************
     */
    @Bean(name = "toolConfigurationsDialog")
    @Scope("prototype")

    public FXMLDialog toolConfigurationsDialog() {
        ToolConfigurationsController tcc = new ToolConfigurationsController();
        tcc.setMode(ToolConfigurationsController.Mode.EDIT);
        return new FXMLDialog(toolConfigurationsController(), getClass().getResource("ToolConfigurations.fxml"), primaryStage);
    }

    @Bean(name = "toolConfigurationsController")
    @Scope("prototype")
    public ToolConfigurationsController toolConfigurationsController() {
        return new ToolConfigurationsController();
    }

    /**
     * ***************************** Edit Tool *******************************
     */
    @Bean(name = "toolEditDialog")
    @Scope("prototype")

    public FXMLDialog toolEditDialog() {
        return new FXMLDialog(toolEditController(), getClass().getResource("ToolEdit.fxml"), primaryStage);
    }

    @Bean(name = "toolEditController")
    @Scope("prototype")
    public ToolEditController toolEditController() {
        return new ToolEditController();
    }

    /**
     * ***************************** About Tool *******************************
     */
    @Bean(name = "aboutDialog")

    public FXMLDialog aboutDialog() {
        return new FXMLDialog(aboutController(), getClass().getResource("About.fxml"), primaryStage, StageStyle.UNDECORATED);
    }

    @Bean(name = "aboutController")
    @Scope("prototype")
    public AboutController aboutController() {
        return new AboutController();
    }

    /**
     * ***************************** GCodeEditor Tool *******************************
     */
    @Bean(name = "postProcessorsDialog")
    @Scope("prototype")

    public FXMLDialog postProcessorsDialog() {
        return new FXMLDialog(postProcessorsController(), getClass().getResource("PostProcessors.fxml"), primaryStage);
    }

    @Bean(name = "postProcessorsController")
    @Scope("prototype")
    public PostProcessorsController postProcessorsController() {
        return new PostProcessorsController();
    }

    /**
     * ***************************** Task Edit Controller *******************************
     */
    @Bean(name = "taskEditDialog")
    @Scope("prototype")

    public FXMLDialog taskEditDialog() {
        return new FXMLDialog(taskEditController(), getClass().getResource("TaskEdit.fxml"), primaryStage);
    }

    @Bean(name = "taskEditController")
    @Scope("prototype")
    public TaskEditController taskEditController() {
        return new TaskEditController();
    }

    /**
     * ***************************** cncTools *******************************
     */
    @Bean(name = "cncTools")
    @Scope("prototype")

    public FXMLDialog cncTools() {
        return new FXMLDialog(cncToolsController(), getClass().getResource("CNCTools.fxml"), primaryStage);
    }

    @Bean(name = "cncToolsController")
    @Scope("prototype")
    public CNCToolsController cncToolsController() {
        return new CNCToolsController(this);
    }

}
