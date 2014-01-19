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

import com.rvantwisk.cnctools.gcode.CncToolsGCodegenerator;
import com.rvantwisk.gcodegenerator.GCodeBuilder;
import math.geom2d.AffineTransform2D;
import math.geom2d.Box2D;
import math.geom2d.Point2D;
import math.geom2d.circulinear.*;
import math.geom2d.conic.Circle2D;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.conic.Ellipse2D;
import math.geom2d.curve.Curve2D;
import math.geom2d.curve.CurveArray2D;
import math.geom2d.domain.Contour2D;
import math.geom2d.line.LineSegment2D;
import math.geom2d.polygon.LinearRing2D;
import math.geom2d.polygon.Polyline2D;
import math.geom2d.polygon.Rectangle2D;
import math.geom3d.Point3D;

import java.util.*;

/**
 * Class to employ simply facing and pocketing strategies
 * This doesn't try to optimize cutting strategy, for example by using biarc's or other technique to
 * decrease total milling time.
 * The shape must be constructed in such a way that you can reach each end through s straight line without crossing a boundary
 *
 * @Author R. van Twisk
 * TODO: see if we can interface with libACTP or libarea to we cna handle more complex millign strategies
 */
public class FacingHelper {
    private final CncToolsGCodegenerator gCode;
    // Curve that hold's the data to be faced/pocketed
    private CirculinearCurve2D domain;
    private boolean spindleCW = true;
    private double radialDepth = 0.0; // Step over
    private double axialDepth = 0.0; // STep Depth
    private double millSize = 0.0; // Size of endmill
    private double rapidClearance = 1.0;
    private double stockClearance = 5.0;
    private double zTop;
    private double zSafe;
    private double zFinal;
    private double edgeClearance = 0.0;
    private boolean edgeCleanup;
    private boolean edgeCleanupClimb;
    private double angle = 0.0; // In case of ZIGZAG/LINEAR method
    private CutStrategy cutStrategy = CutStrategy.ZIGZAG;
    private boolean cuttingClimb = false;
    // Variables used during calculations
    private CirculinearContour2D inside = null; // Contains the inside of teh total milled area
    private CirculinearContour2D edge = null; // Contains the inside of teh total milled area

    public FacingHelper(CncToolsGCodegenerator gCodeGenerator) {
        this.gCode = gCodeGenerator;
    }

    /**
     * Create a eliptical Curve, reference is located at lower left
     *
     * @param width
     * @param height
     * @return TODO: USe circle for Eclipse optimisation, the current library (java geom) doesn't have the capability for buffering Eclipse
     */
    public static CirculinearCurve2D getEllipseDomain(double width, double height, int segments) {
        Ellipse2D p2D = new Ellipse2D(width / 2.0, height / 2.0, width / 2.0, height/ 2.0);
        CirculinearContourArray2D array = new CirculinearContourArray2D();
        array.add(p2D.asPolyline(segments));
        return array;
    }

    /**
     * Create a Rectangular domain with a reference in the lower left corner
     *
     * @param width
     * @param height
     * @return
     */
    public static CirculinearCurve2D getRectangularDomain(double width, double height) {
        Rectangle2D p2D = new Rectangle2D(new Point2D(0.0, 0.0), new Point2D(width, height));
        return p2D.boundary();
    }

    /**
     * Create a circular domain with the reference located in the lower left
     *
     * @param r
     * @return
     */
    public static CirculinearCurve2D getCircleDomain(double r) {
        return new Circle2D(r, r, r);
    }

    public void calculate() {

        // Build the inside of the shape
        CirculinearDomain2D buffer = domain.buffer(radialDepth + edgeClearance);
        Iterator<? extends CirculinearContour2D> iter = buffer.boundary().continuousCurves().iterator();
        inside = iter.next();
        inside = iter.next();

        // Calculate the edge
        buffer = domain.buffer(radialDepth);
        iter = buffer.boundary().continuousCurves().iterator();
        edge = iter.next();
        edge = iter.next();

        // Build each layer till final depth
        double z = zTop;
        while ((z - axialDepth) >= zFinal) {
            z = z - axialDepth;
            calculatePaths(angle, z);
        }
        if (z > zFinal) {
            calculatePaths(angle, zFinal);
        }
        gCode.addBlock(GCodeBuilder.builder().G0().Z(zSafe));

        // draw(buffer, null);
    }

    private void calculatePaths(double angle, double zHeight) {
        gCode.comment("New layer at angle [" + angle + "] and depth [" + zHeight + "]");
        angle = Math.toRadians(angle);

        Point3D[] pocketPoints = null;
        switch (cutStrategy) {
            case ZIGZAG:
            case LINEAR:
                pocketPoints = buildLinearorZigzagPath(angle, zHeight);
                break;
        }

        // Set direction of cutting
        if (cuttingClimb ^ spindleCW) {
            Collections.reverse(Arrays.asList(pocketPoints));
        }

        // Move Z up to safe
        gCode.addBlock(GCodeBuilder.builder().G0().Z(zSafe));

        // Move to X/Y coords to start
        gCode.addBlock(GCodeBuilder.builder().X(pocketPoints[0].getX()).Y(pocketPoints[0].getY()));

        // Entry Move (vertical)
        gCode.addBlock(GCodeBuilder.builder().G1().X(pocketPoints[0].getX()).Y(pocketPoints[0].getY()).Z(zHeight));

        // Generate GCode for clearing
        createGCodeFromPoint3D(pocketPoints);

        // If edge needs to be cleaned up we can do that here
        if (edgeCleanup) {
            gCode.addBlock(GCodeBuilder.builder().G0().Z(zSafe));
            gCode.addBlock(GCodeBuilder.builder().X(inside.firstPoint().x()).Y(inside.firstPoint().y()));
            gCode.addBlock(GCodeBuilder.builder().G1().Z(zHeight));
            createGCodeFromCirculinearContour2D(edge);
        }

        // Move to above startpoint
        gCode.addBlock(GCodeBuilder.builder().G0().Z(zSafe));
    }

    /**
     * Build a linear of zigzag pattern
     *
     * @param angle
     * @param zHeight
     * @return
     */
    private Point3D[] buildLinearorZigzagPath(double angle, double zHeight) {
        AffineTransform2D transform = AffineTransform2D.createRotation(-angle);

        // Rotate design according to requested cut angle
        Contour2D insideTransformed = inside.transform(transform);
        Curve2D domainTransformed = domain.transform(transform);

        // Place lines at distance of radialAxis on top of the design and calculate all interestions between teh design and the CNC path's
        Box2D size = domainTransformed.boundingBox();
        CurveArray2D millPaths = new CirculinearCurveArray2D();

        double x = size.getMinX();
        LineSegment2D lastAdded = null;
        while (x < size.getMaxX()) {
            List<Point2D> points = new ArrayList<>(insideTransformed.intersections(new LineSegment2D(x, size.getMinY(), x, size.getMaxY())));
            if (points.size() == 2) {
                lastAdded = new LineSegment2D(points.get(0), points.get(1));
                millPaths.add(lastAdded);
            } else if (points.size() > 2) {
                throw new RuntimeException("Shape seems to be constructed in such a way that we get multiple intersection points.");
            }
            x = x + radialDepth;
        }

        // If we are more then 5% of radial depth to the edge of inside we add a last step, else we assume the endmill will beable to handle
        // the extra material to be removed
        if (Math.abs(lastAdded.lastPoint().x() - size.getMaxX()) > radialDepth / 5.0) {
            List<Point2D> points = new ArrayList<>(insideTransformed.intersections(new LineSegment2D(size.getMaxX(), size.getMinY(), size.getMaxX(), size.getMaxY())));
            if (points.size() == 2) {
                lastAdded = new LineSegment2D(points.get(0), points.get(1));
                millPaths.add(lastAdded);
            } else if (points.size() > 2) {
                throw new RuntimeException("Shape seems to be constructed in such a way that we get multiple intersection points.");
            }
        }

        // Re-order of line segment's ensures that they are always upwards
        millPaths = reOrderLineSegment2DUpwards(millPaths);

        // Rotate back the created array of CNC paths so they are corectly alliged with the design
        millPaths = millPaths.transform(AffineTransform2D.createRotation(angle));

        // build a cut strategy for ZIGZAG and LINEAR
        Point3D[] arrayPoints = null;
        switch (cutStrategy) {
            case ZIGZAG:
                arrayPoints = buildCutPath_ZIGZAG(millPaths, zHeight);
                break;
            case LINEAR:
                arrayPoints = buildCutPath_LINEAR(millPaths, zHeight);
                break;
        }
        return arrayPoints;
    }

    /**
     * Build a ZIGZAG cutting path cutting in both directions
     *
     * @param array
     * @return Array of G0LineSegment2D or LineSegment2D with teh total pat to follow (last to first, first to last...)
     */
    private Point3D[] buildCutPath_ZIGZAG(CurveArray2D array, double zHeight) {

        final int size = array.size();
        Point3D[] sorted = new Point3D[size * 2];

        // Swap every other line segment
        final LineSegment2D[] swapped = new LineSegment2D[size];
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                swapped[i] = (LineSegment2D) array.get(i);
            } else {
                swapped[i] = (LineSegment2D) array.get(i).reverse();
            }
        }

        // Add first point
        int pos = 0;
        LineSegment2D item = swapped[0];
        sorted[pos++] = new Point3D(item.firstPoint().x(), item.firstPoint().y(), zHeight);

        // Follow path
        for (int i = 0; i < size; i++) {
            item = swapped[i];
            sorted[pos++] = new Point3D(item.lastPoint().x(), item.lastPoint().y(), zHeight);

            if (i < (size - 1)) {
                LineSegment2D nextItem = swapped[i + 1];
                sorted[pos++] = new Point3D(nextItem.firstPoint().x(), nextItem.firstPoint().y(), zHeight);
            }
        }

        return sorted;
    }

    /**
     * Build a Linear cut path, eg cutting into one direction only
     *
     * @param array
     * @param zHeight
     * @return
     */
    private Point3D[] buildCutPath_LINEAR(CurveArray2D array, double zHeight) {

        Point3D[] sorted = null;
        LineSegment2D item;
        final int size = array.size();

        sorted = new Point3D[size * 5 - 3];
        int pos = 0;
        item = (LineSegment2D) array.get(0);
        sorted[pos++] = new Point3D(item.firstPoint().x(), item.firstPoint().y(), zHeight);
        for (int i = 0; i < size; i++) {
            item = (LineSegment2D) array.get(i);
            sorted[pos++] = new Point3D(item.lastPoint().x(), item.lastPoint().y(), zHeight);

            if (i < (size - 1)) {
                LineSegment2D nextItem = (LineSegment2D) array.get(i + 1);
                sorted[pos++] = new G0Point3D(item.lastPoint().x(), item.lastPoint().y(), zSafe);
                sorted[pos++] = new G0Point3D(item.firstPoint().x(), item.firstPoint().y(), zSafe);
                sorted[pos++] = new Point3D(item.firstPoint().x(), item.firstPoint().y(), zHeight);
                sorted[pos++] = new Point3D(nextItem.firstPoint().x(), nextItem.firstPoint().y(), zHeight);
            }
        }

        return sorted;
    }

    /**
     * Create G-Code based on point array
     *
     * @param arrayPoint
     */
    private void createGCodeFromPoint3D(final Point3D[] arrayPoint) {
        for (Point3D item : arrayPoint) {
            if (item instanceof G0Point3D) {
                gCode.addBlock(GCodeBuilder.builder().G0().X(item.getX()).Y(item.getY()).Z(item.getZ()));
            } else {
                gCode.addBlock(GCodeBuilder.builder().G1().X(item.getX()).Y(item.getY()).Z(item.getZ()));
            }
        }
    }

    /**
     * Re-order a array of LineSegment2D in such a way that all vector's point upwards
     *
     * @param array
     * @return
     */
    private CirculinearCurveArray2D reOrderLineSegment2DUpwards(CurveArray2D array) {
        CirculinearCurveArray2D sorted = new CirculinearCurveArray2D();
        for (Curve2D anArray : (Iterable<Curve2D>) array) {
            LineSegment2D item = (LineSegment2D) anArray;

            if (item.firstPoint().y() < item.lastPoint().y()) {
                sorted.add(item);
            } else {
                sorted.add(new LineSegment2D(item.firstPoint().x(), item.lastPoint().y(), item.lastPoint().x(), item.firstPoint().y()));
            }
        }
        return sorted;
    }

    private void createGCodeFromCirculinearContour2D(CirculinearContour2D foo) {


        List<CirculinearContour2D> curves = new ArrayList<>(foo.continuousCurves());

        // Reverse order if required
        if (!(this.edgeCleanupClimb != spindleCW)) {
            Collections.reverse(curves);
        }

        for (CirculinearContour2D circulinearContour2D : curves) {
            createGCodeFromGeom(circulinearContour2D);
        }
    }

    private void createGCodeFromGeom(CirculinearContour2D item2) {

        if (item2 instanceof Circle2D) {
            Circle2D c = (Circle2D) item2;

            for (LinearRing2D lr : c.asPolyline(30).continuousCurves()) {
                for (Point2D p : lr.vertices()) {
                    gCode.addBlock(GCodeBuilder.builder().X(p.x()).Y(p.y()));
                }
            }

        } else if (item2 instanceof GenericCirculinearRing2D) {
            GenericCirculinearRing2D item3 = (GenericCirculinearRing2D) item2;

            for (CirculinearElement2D item4 : item3.smoothPieces()) {

                if (item4 instanceof LineSegment2D) {
                    LineSegment2D ls = (LineSegment2D) item4;

                    Point2D fp = ls.firstPoint();
                    gCode.addBlock(GCodeBuilder.builder().X(fp.x()).Y(fp.y()));
                    Point2D lp = ls.lastPoint();
                    gCode.addBlock(GCodeBuilder.builder().X(lp.x()).Y(lp.y()));


                } else if ((item4 instanceof CircleArc2D)) {
                    CircleArc2D ca = (CircleArc2D) item4;

                    Polyline2D lines = ca.asPolyline(10);
                    for (Point2D p : lines.vertices()) {
                        gCode.addBlock(GCodeBuilder.builder().X(p.x()).Y(p.y()));
                    }


                } else {
                    throw new RuntimeException("Unknown CirculinearElement2D encountered [" + item4.getClass().toString() + "]");
                }
            }
        }
    }


    /***********************************************************************************/
    /******************************* Getters and Setters *******************************/

    /**
     * Get the first item from a collection
     *
     * @param in
     * @return Note: We could use iteratables from guave?
     */
    private Point2D getFirstPoint(final Collection<Point2D> in) {
        return in.iterator().hasNext() ? in.iterator().next() : null;
    }

    /**
     * From a number of point's in a collection, rotate the collection in such a way that the found point is as close as possible to closeAt
     *
     * @param in
     * @param closeAt
     * @return
     */
    private Collection<Point2D> rotateClosest(final Collection<Point2D> in, final Point2D closeAt) {
        int optimum = 0;
        double distance = Double.MAX_VALUE;
        int step = 0;
        for (Point2D p : in) {
            double thisD = p.distance(closeAt);
            if (thisD < distance) {
                distance = thisD;
                optimum = step;
            }
            step++;
        }
        List<Point2D> rotated = new ArrayList<>();
        rotated.addAll(in);
        Collections.rotate(rotated, -optimum);
        return rotated;
    }

    /**
     * *******************************************************************************
     */


    public CirculinearCurve2D getDomain() {
        return domain;
    }

    public void setDomain(CirculinearCurve2D domain) {
        this.domain = domain;
    }

    public double getRadialDepth() {
        return radialDepth;
    }

    public void setRadialDepth(double radialDepth) {
        this.radialDepth = radialDepth;
    }

    public double getAxialDepth() {
        return axialDepth;
    }

    public void setAxialDepth(double axialDepth) {
        this.axialDepth = axialDepth;
    }

    public double getMillSize() {
        return millSize;
    }

    public void setMillSize(double millSize) {
        this.millSize = millSize;
    }

    public double getRapidClearance() {
        return rapidClearance;
    }

    public void setRapidClearance(double rapidClearance) {
        this.rapidClearance = rapidClearance;
    }

    public double getStockClearance() {
        return stockClearance;
    }

    public void setStockClearance(double stockClearance) {
        this.stockClearance = stockClearance;
    }

    public double getzTop() {
        return zTop;
    }

    public void setzTop(double zTop) {
        this.zTop = zTop;
    }

    public double getzSafe() {
        return zSafe;
    }

    public void setzSafe(double zSafe) {
        this.zSafe = zSafe;
    }

    public double getzFinal() {
        return zFinal;
    }

    public void setzFinal(double zFinal) {
        this.zFinal = zFinal;
    }

    public boolean isEdgeCleanup() {
        return edgeCleanup;
    }

    public void setEdgeCleanup(boolean edgeCleanup) {
        this.edgeCleanup = edgeCleanup;
    }

    public boolean isEdgeCleanupClimb() {
        return edgeCleanupClimb;
    }

    public void setEdgeCleanupClimb(boolean edgeCleanupClimb) {
        this.edgeCleanupClimb = edgeCleanupClimb;
    }

    public boolean isSpindleCW() {
        return spindleCW;
    }

    public void setSpindleCW(boolean spindleCW) {
        this.spindleCW = spindleCW;
    }

    public CutStrategy getCutStrategy() {
        return cutStrategy;
    }

    public void setCutStrategy(CutStrategy cutStrategy) {
        this.cutStrategy = cutStrategy;
    }

    public boolean isCuttingClimb() {
        return cuttingClimb;
    }

    public void setCuttingClimb(boolean cuttingClimb) {
        this.cuttingClimb = cuttingClimb;
    }

    public double getEdgeClearance() {
        return edgeClearance;
    }

    public void setEdgeClearance(double edgeClearance) {
        this.edgeClearance = edgeClearance;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public static enum CutStrategy {
        LINEAR,
        SPIRAL_OUT,
        ZIGZAG
    }


}
