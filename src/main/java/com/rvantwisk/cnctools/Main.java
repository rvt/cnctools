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

import com.rvantwisk.cnctools.controllers.FXMLDialog;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
public class Main extends Application {

    private static Stage stage;


    static public Stage getPrimaryStage() {
        return stage;
    }

    @Override
    public void start(Stage stage) throws Exception{

        ApplicationContext context = new AnnotationConfigApplicationContext(IndexerAppConfiguration.class);
        ScreensConfiguration screens = context.getBean(ScreensConfiguration.class);
        screens.setPrimaryStage(stage);
        this.stage = stage;
        screens.setContext(context);

        FXMLDialog dialog =  screens.cncTools();
        dialog.show();


    }


    // http://stackoverflow.com/questions/14010426/jpa-managed-entities-vs-javafx-properties
    // http://ugate.wordpress.com/2012/06/14/javafx-programmatic-pojo-bindings/
    // http://docs.oracle.com/javafx/2/layout/style_css.htm
    // http://docs.oracle.com/javafx/2/ui_controls/jfxpub-ui_controls.htm
    // http://docs.oracle.com/javafx/2/api/javafx/scene/doc-files/cssref.html
    //http://docs.oracle.com/javafx/2/fxml_get_started/jfxpub-fxml_get_started.htm
    // http://fxexperience.com/wp-content/uploads/2011/08/Introducing-FXML.pdf
    // http://gopinathb4u.wordpress.com/2010/12/30/create-dynamic-spring-beans/
    // http://stackoverflow.com/questions/9705440/prefill-listview-in-an-application-with-fxml

    public static void main(String[] args) {

        // Detect OS/X and set AWT to that we can use OPenGL, this might not be needed fro Java 8
        String osName = System.getProperty("os.name");
        if (osName.contains("OS X")) {
            System.setProperty("javafx.macosx.embedded", "true");
            java.awt.Toolkit.getDefaultToolkit();
        }

        launch(args);
    }


}
