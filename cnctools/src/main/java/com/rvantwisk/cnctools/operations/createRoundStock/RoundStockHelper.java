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

import com.rvantwisk.cnctools.operations.math.Intersect;
import com.rvantwisk.cnctools.operations.math.Point;
import com.rvantwisk.gcodegenerator.GCodeBuilder;
import com.rvantwisk.gcodegenerator.interfaces.GCodeGenerator;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/4/13
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class RoundStockHelper {

    private Double radialDepth = 0.0; // Step over
    private Double axialDepth = 0.0; // STep Depth
    private Double millSize = 0.0; // Size of endmill
    private Double rapidClearance = 1.0;
    private Double stockClearance = 5.0;
    private Double feedRate = 600.0;

    private Double stockSize = 0.0; // Stock size square
    private Double finalSize = 0.0; // Stock Size final
    private Double stockLength = 0.0; // length of the stock

    // Intermediate results

    private List<Point> intersectionPoints;
    private Point millPA;
    private Point millPB;
    private Point millCenter;
    private Point pa;
    private Point pb;
    private Point center;
    private Double nextDepth;
    private Point startPoint;

    final GCodeGenerator gCode;

    public RoundStockHelper(GCodeGenerator gcodeGenerator) {
        gCode = gcodeGenerator;
    }

    public void calculate() {
        nextDepth = stockSize * Math.sqrt(2);

        // gCode.addBlock(GCodeBuilder.builder().F(feedRate));


        gCode.addBlock(GCodeBuilder.builder().G0().Z(stockSize * Math.sqrt(2) + stockClearance));
        gCode.addBlock(GCodeBuilder.builder().A(0.0).X(0.0).Y(0.0));

        while ((calculateBase()) > -1) {
            gCode.comment("Angle at 0.0");
            calculate(0.0);
            gCode.comment("Angle at 180.0");
            calculate(180.0);
            gCode.comment("Angle at 270.0");
            calculate(270.0);
            gCode.comment("Angle at 90.0");
            calculate(90.0);
        }
        gCode.commentLarge("start final rounds");
        fullRoundMillSteps();
        gCode.addBlock(GCodeBuilder.builder().G0().Z(stockSize * Math.sqrt(2) + stockClearance));
        gCode.addBlock(GCodeBuilder.builder().X(0.0));
        // Reset A to 0 degrees
        gCode.addBlock(GCodeBuilder.builder().G92("A", 0.0));
        gCode.commentLarge("Done");
    }

    /**
     * Rest milling around the full stock of wood
     */
    private void fullRoundMillSteps() {
        Double offsetAngle = 0.0;

        gCode.addBlock(GCodeBuilder.builder().G0().Z(stockSize * Math.sqrt(2) + stockClearance));
        gCode.addBlock(GCodeBuilder.builder().A(0.0).X(0.0));
        while (nextDepth > finalSize) {
            offsetAngle = fullRoundMillStep(offsetAngle);

            nextDepth = nextDepth - axialDepth;
            if (nextDepth <= finalSize) {
                nextDepth = -1.0;
            }
        }

        nextDepth = finalSize;
        fullRoundMillStep(offsetAngle);
    }


    /**
     * Rest milling around the full stock of wood at a offset angle
     */
    private Double fullRoundMillStep(Double offsetAngle) {
        gCode.commentLarge("Full mill step at : Z=" + nextDepth);

        Double totalAngle = (stockLength / radialDepth) * 360.0;
        Double millLength = (stockLength / radialDepth) * Math.PI * finalSize * 2.0;
        Double G93F = this.feedRate / millLength;

        gCode.addBlock(GCodeBuilder.builder().G1().Z(nextDepth));
        gCode.addBlock(GCodeBuilder.builder().G93(G93F).A(offsetAngle + totalAngle).X(stockLength));
        gCode.addBlock(GCodeBuilder.builder().G0().Z(nextDepth + rapidClearance));
        gCode.addBlock(GCodeBuilder.builder().X(0.0));

        return totalAngle + offsetAngle;
    }

    private Double calculateBase() {

        pa = new Point(0.0, stockSize * Math.sqrt(2.0));
        pb = new Point(stockSize * Math.sqrt(2.0), 0.0);
        center = new Point(0.0, 0.0);

        boolean doNext = true;
        nextDepth = nextDepth - axialDepth;
        if (nextDepth < finalSize + axialDepth) {
            return -1.0;
        }

        // Calculate intersection points and take the closest intersection point to zero angle

        intersectionPoints = Intersect.getCircleLine(pa, pb, center, nextDepth);
        if (intersectionPoints.size() == 2) {
            if (intersectionPoints.get(0).x < intersectionPoints.get(1).x) {
                startPoint = intersectionPoints.get(0);
            } else {
                startPoint = intersectionPoints.get(1);
            }
        } else if (intersectionPoints.size() == 1) {
            startPoint = intersectionPoints.get(0);
        } else {
            // This means we can turn the wood completly around within tolrance of the axialDepth
            return -1.0;
        }

        // Calculate the mill tip ends and the center of the mill at startPoint
        Point rotated = startPoint.rot90(false).normalize();
        millPA = rotated.mul(radialDepth).add(startPoint);
        millPB = rotated.mul(-(millSize - radialDepth)).add(startPoint);
        millCenter = new Point((millPA.x + millPB.x) / 2.0, (millPA.y + millPB.y) / 2.0);

        return nextDepth;
    }

    private void calculate(Double angleOffset) {
        // Start mill operation
        Double currentAngle = Point.angleBetween2Lines(Point.zero, pa, Point.zero, millCenter) * (180.0 / Math.PI);
        Double startAngle = currentAngle;
        Double stepAngle = Point.angleBetween2Lines(Point.zero, startPoint, Point.zero, millPA) * (180.0 / Math.PI);

        gCode.addBlock(GCodeBuilder.builder().G0().Z(stockSize * Math.sqrt(2) + stockClearance));
        gCode.addBlock(GCodeBuilder.builder().A(currentAngle + 1.0 + angleOffset));

        Boolean isEnd;
        do {
            // Remove some material
            gCode.comment("Remove stock");
            gCode.addBlock(GCodeBuilder.builder().G1().Z(nextDepth));
            gCode.addBlock(GCodeBuilder.builder().G1().A(currentAngle + angleOffset));
            gCode.addBlock(GCodeBuilder.builder().X(stockLength));

            gCode.addBlock(GCodeBuilder.builder().G0().Z(nextDepth + rapidClearance));
            gCode.addBlock(GCodeBuilder.builder().G0().A(currentAngle + angleOffset + 1.0).X(0.0));

            currentAngle = currentAngle - Math.abs(stepAngle);

            // Test if we reached the end of this round
            isEnd = reachedEndOf(-startAngle, currentAngle);

            if (isEnd) {
                gCode.addBlock(GCodeBuilder.builder().G0().Z(stockSize * Math.sqrt(2) + stockClearance));
            }

        } while (!isEnd);

    }

    /**
     * Calculate of we reached the end of this millrun
     *
     * @param neededAngle
     * @param currentMillAngle
     * @return
     */
    private boolean reachedEndOf(Double neededAngle, double currentMillAngle) {
        Double A = currentMillAngle / (180.0 / Math.PI);
        Point testmillCenter = new Point(nextDepth * Math.sin(A), nextDepth * Math.cos(A));
        Point testmillPA = testmillCenter.rot90(true).normalize().mul(millSize / 2.0).add(testmillCenter);
        Point testmillPB = testmillCenter.rot90(false).normalize().mul(millSize / 2.0).add(testmillCenter);
        Double testmillPAngle = Point.angleBetween2Lines(Point.zero, pa, Point.zero, testmillPB) * (180.0 / Math.PI);

        return neededAngle > testmillPAngle && neededAngle > currentMillAngle;
    }

    /**********************************************************************************************/
    /** getters and setters **/
    /**
     * ******************************************************************************************
     */

    public Double getStockSize() {
        return stockSize;
    }

    public void setStockSize(Double stockSize) {
        if (stockSize < finalSize) {
            stockSize = finalSize;
        }
        this.stockSize = stockSize;
    }

    public Double getFinalSize() {
        return finalSize;
    }

    public void setFinalSize(Double finalSize) {
        // Reset stock size to be at least finalSize
        if (stockSize < finalSize)
            stockSize = finalSize;
        this.finalSize = finalSize;
    }

    public Double getradialDepth() {
        return radialDepth;
    }

    public void setRadialDepth(Double radialDepth) {
        if (radialDepth <= 0.0)
            throw new IllegalArgumentException("Step size cannot be negative");
        this.radialDepth = radialDepth;
    }

    public Double getaxialDepth() {
        return axialDepth;
    }

    public void setAxialDepth(Double axialDepth) {
        if (axialDepth <= 0.0)
            throw new IllegalArgumentException("Step size cannot be < negative");
        this.axialDepth = axialDepth;
    }

    public Double getMillSize() {
        return millSize;
    }

    public void setMillSize(Double millSize) {
        if (millSize < 0.0)
            throw new IllegalArgumentException("Mill Size cannot be < negative");
        this.millSize = millSize;
    }

    public void setStockLength(Double stockLength) {
        this.stockLength = stockLength;
    }


    public Double getStockLength() {
        return stockLength;
    }

    public Double getRapidClearance() {
        return rapidClearance;
    }

    public void setRapidClearance(Double rapidClearance) {
        this.rapidClearance = rapidClearance;
    }

    public Double getStockClearance() {
        return stockClearance;
    }

    public void setStockClearance(Double stockClearance) {
        this.stockClearance = stockClearance;
    }

    public Double getFeedRate() {
        return feedRate;
    }

    public void setFeedRate(Double feedRate) {
        this.feedRate = feedRate;
    }


}
