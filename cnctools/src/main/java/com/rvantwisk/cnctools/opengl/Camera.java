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

/**
 * Created by rvt on 12/12/13.
 */
public class Camera extends ReadonlyCamera {

    public Camera(Camera other) {
        super(other);
    }
    public Camera(ReadonlyCamera other) {
        super(other);
    }

    public ReadonlyCamera getReadOnly() {
        return new ReadonlyCamera(this);
    }

    public void zoom(float delta_x, float delta_y) {

         float old_zoom = zoom_factor;

        if (delta_y > 0) {
            zoom_factor = Math.min(this.zoom_factor * 1.2f, ZOOM_MAX);
        } else if (delta_y < 0) {
            zoom_factor = Math.max(this.zoom_factor * 0.83f, ZOOM_MIN);
        }

        if (this.isOrtho) {
            x = this.x * zoom_factor / old_zoom;
            y = this.y * zoom_factor / old_zoom;
        }

    }

    public void rotate(float delta_x, float delta_y) {
         azimuth = this.azimuth + delta_x;
         elevation = this.elevation - delta_y;
    }

    public void pan(float delta_x, float delta_y) {
         x = this.x + delta_x / zoom_factor;
         z = this.z - delta_y / zoom_factor;
    }

    public void offset(float delta_x, float delta_y) {
         offset_x = this.offset_x + delta_x / zoom_factor;
         offset_y = this.offset_y - delta_y / zoom_factor;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setOffset_x(float offset_x) {
        this.offset_x = offset_x;
    }

    public void setOffset_y(float offset_y) {
        this.offset_y = offset_y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public void setZoom_factor(float zoom_factor) {
        this.zoom_factor = zoom_factor;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public void setZOOM_MIN(float ZOOM_MIN) {
        this.ZOOM_MIN = ZOOM_MIN;
    }

    public void setZOOM_MAX(float ZOOM_MAX) {
        this.ZOOM_MAX = ZOOM_MAX;
    }

    public void setFOVY(float FOVY) {
        this.FOVY = FOVY;
    }

    public void setZOOM_ORTHO_ADJ(float ZOOM_ORTHO_ADJ) {
        this.ZOOM_ORTHO_ADJ = ZOOM_ORTHO_ADJ;
    }

}