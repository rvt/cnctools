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

import com.rvantwisk.cnctools.controls.opengl.BeadActor;
import com.rvantwisk.cnctools.controls.opengl.OpenGLRenderer;
import com.rvantwisk.cnctools.opengl.AbstractActor;
import com.rvantwisk.cnctools.opengl.Camera;
import com.rvantwisk.cnctools.opengl.OpenGLImage;
import com.rvantwisk.gcodeparser.exceptions.SimException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Created by rvt on 12/6/13.
 */
public class GCodeViewerControl extends AnchorPane {

    private final double MOUSE_SENSETIVITY = 1.0;
    float lastMouseX = 0.0f;
    float lastMouseY = 0.0f;
    @FXML
    private OpenGLImage openGLImage;
    private OpenGLRenderer gCodeRender;

    public GCodeViewerControl() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GCodeViewer.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    private void show() {
        if (gCodeRender != null) {
            gCodeRender.stop();
        }

        new Thread("GCode Render") {
            public void run() {
                gCodeRender = new OpenGLRenderer(openGLImage.getReadHandler());
                gCodeRender.addActor(new BeadActor());
                gCodeRender.setSamples(8);
                gCodeRender.run();
            }
        }.start();

        while (gCodeRender == null) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException("Error while waiting for renderer to come alive");
            }
        }
    }

    @FXML
    void initialize() throws IOException {
        openGLImage.fitWidthProperty().bind(this.widthProperty());
        openGLImage.fitHeightProperty().bind(this.heightProperty());


        this.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                lastMouseX = (float) mouseEvent.getX();
                lastMouseY = (float) mouseEvent.getY();
            }
        });

        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                float dX = (float) (mouseEvent.getX() - lastMouseX);
                float dY = (float) (mouseEvent.getY() - lastMouseY);

                Camera c = new Camera(gCodeRender.getCamera());

                c.setWidth((float) openGLImage.getFitWidth());
                c.setHeight((float) openGLImage.getFitHeight());

                if (mouseEvent.isPrimaryButtonDown()) {
                    c.rotate(dX, dY);
                } else if (mouseEvent.isSecondaryButtonDown()) {
                    c.pan(dX, dY);
                }
                gCodeRender.setCamera(c.getReadOnly());
                lastMouseX = (float) mouseEvent.getX();
                lastMouseY = (float) mouseEvent.getY();
            }
        });

        this.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                Camera c = new Camera(gCodeRender.getCamera());
                if (!event.isInertia()) {
                    c.zoom((float) event.getDeltaX(), (float) event.getDeltaY());
                }
                gCodeRender.setCamera(c.getReadOnly());
                event.consume();
            }
        });


        // Set the camera for the renderer
        show();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Camera c = new Camera(gCodeRender.getCamera());
                c.setWidth((float) openGLImage.getFitWidth());
                c.setHeight((float) openGLImage.getFitHeight());
                gCodeRender.setCamera(c.getReadOnly());
            }
        });

    }

    public void addActor(final AbstractActor actor) throws SimException {
        gCodeRender.addActor(actor);
    }

    public void finalize() {
        if (gCodeRender != null)
            gCodeRender.stop();
    }

    public void destroy() {
        if (gCodeRender != null) {
            gCodeRender.stop();
        }
    }

}