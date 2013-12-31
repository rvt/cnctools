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

package com.rvantwisk.cnctools.opengl;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import org.lwjgl.util.stream.StreamHandler;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

/**
 * Created by rvt on 12/9/13.
 */
public class OpenGLImage extends ImageView {


    public StreamHandler getReadHandler() {
        return new StreamHandler() {
            private WritableImage renderImage;

            public int getWidth() {
                return (int) getFitWidth();
            }

            public int getHeight() {
                return (int) getFitHeight();
            }

            public void process(final int width, final int height, final ByteBuffer data, final int stride, final Semaphore signal) {
                // This method runs in the background rendering thread
                // TODO: Run setPixels on the PlatformImage in this thread, run pixelsDirty on JFX application thread with runLater.
                Platform.runLater(new Runnable() {
                    public void run() {
                        try {
                            // If we're quitting, discard update
                            if (!isVisible())
                                return;

                            // Detect resize and recreate the image
                            if (renderImage == null || (int) renderImage.getWidth() != width || (int) renderImage.getHeight() != height) {
                                renderImage = new WritableImage(width, height);
                                setImage(renderImage);
                            }

                            // Upload the image to JavaFX
                            renderImage.getPixelWriter().setPixels(0, 0, width, height, PixelFormat.getByteBgraPreInstance(), data, stride);
                        } finally {
                            // Notify the render thread that we're done processing
                            signal.release();
                        }
                    }
                });
            }
        };
    }
}
