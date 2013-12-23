package com.rvantwisk.cnctools.controls.opengl;

import com.rvantwisk.cnctools.gcodeparser.GCodeParser;
import com.rvantwisk.cnctools.gcodeparser.MachineStatus;
import com.rvantwisk.cnctools.gcodeparser.MachineStatus2;
import com.rvantwisk.cnctools.gcodeparser.ParsedWord;
import com.rvantwisk.cnctools.gcodeparser.exceptions.SimException;
import com.rvantwisk.cnctools.gcodeparser.gcodes.MotionMode;
import com.rvantwisk.cnctools.gcodeparser.gcodes.Units;
import gnu.trove.list.array.TFloatArrayList;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

import java.util.Map;

/**
 * Render's GCode into OpenGLView
 * Created by rvt on 12/19/13.
 */
public class OpenGLMachineImpl implements OpenGLMachineController {
    final TFloatArrayList data = new TFloatArrayList();

    public static double curveSectionMM = 1.0;
    public static double AAXISSTEPDEGREES = 1.0; // When A axis rotaties, simulate it in this number of degrees
    public static double curveSectionInches = curveSectionMM / 25.4;
    final MachineStatus2 machine = new MachineStatus2();

    private MotionMode prevMotionMode = MotionMode.G0;
    private static int ROWSIZE = 7;

    private double lastX = 0;
    private double lastY = 0;
    private double lastZ = 0;
    private double lastA = 0;

    @Override
    public void startBlock(GCodeParser parser, MachineStatus machineStatus, Map<String, ParsedWord> currentBlock) {
        machine.setMachineStatus(machineStatus);
    }

    private void setMotionColor(final MotionMode m) {
        if (m == MotionMode.G0) {
            data.add(0.87f);
            data.add(0.33f);
            data.add(0.27f);
            data.add(1.0f);
        } else {
            data.add(0.33f);
            data.add(0.27f);
            data.add(0.87f);
            data.add(.5f);
        }
    }

    @Override
    public void endBlock(GCodeParser parser, MachineStatus machineStatus, Map<String, ParsedWord> currentBlock) throws SimException {

        // Set correct color's for current lines
        if (machine.getMotionMode() != prevMotionMode) {
            addData(lastX, lastY, lastZ, prevMotionMode);
            addData(lastX, lastY, lastZ, machine.getMotionMode());
        }

        switch (machine.getMotionMode()) {
            case G0:
            case G1:
                double rX = machine.getX();
                double rY = machine.getY();
                double rZ = machine.getZ();

                addData(rX, rY, rZ, machine.getMotionMode());
                break;
            case G2:
            case G3:
                drawArc(parser, machineStatus, currentBlock);
                break;
        }

        prevMotionMode = machine.getMotionMode();
        lastX = machine.getX();
        lastY = machine.getY();
        lastZ = machine.getZ();
        lastA = machine.getA();
    }

    public TFloatArrayList getVBOData() {
        return data;
    }

    public int getNumWords() {
        return data.size() / ROWSIZE;
    }

    private void addData(double x, double y, double z, MotionMode m) {
        double a = machine.getA();

        int steps = Math.abs((int) (Math.floor(a - lastA) / AAXISSTEPDEGREES));
        double stepSize = (a - lastA) / steps;


        for (int i = 0; i < steps; i++) {

            Vector3D rotatedLoc = new Rotation(new Vector3D(1.0, 0.0, 0.0), lastA / 360.0 * Math.PI * 2.0 + (stepSize * i) / 360.0 * Math.PI * 2.0).applyTo(new Vector3D(x, y, z));

            data.add((float) (rotatedLoc.getX() + machine.getOX()));
            data.add((float) (rotatedLoc.getY() + machine.getOY()));
            data.add((float) (rotatedLoc.getZ() + machine.getOZ()));

            setMotionColor(machine.getMotionMode());

        }

        Vector3D rotatedLoc = new Rotation(new Vector3D(1.0, 0.0, 0.0), a / 360.0 * Math.PI * 2.0).applyTo(new Vector3D(x, y, z));

        data.add((float) (rotatedLoc.getX() + machine.getOX()));
        data.add((float) (rotatedLoc.getY() + machine.getOY()));
        data.add((float) (rotatedLoc.getZ() + machine.getOZ()));

        setMotionColor(m);

    }


    // This routine was taken from : https://github.com/makerbot/ReplicatorG/blob/master/src/replicatorg/app/gcode/java
    // However this was modified to support P (number of turns)
    // add additional support for G18 and G19 planes
    private void drawArc(GCodeParser parser, MachineStatus machineStatus, Map<String, ParsedWord> currentBlock) throws SimException {


        double curveSection;
        if (machine.getActiveUnit() == Units.G20) {
            curveSection = curveSectionInches;
        } else {
            curveSection = curveSectionMM;
        }

        boolean clockwise = machine.getMotionMode() == MotionMode.G2;

        // angle variables.
        double angleA;
        double angleB;
        double angle;

        // delta variables.
        double aX;
        double aY;
        double bX;
        double bY;

        double i = currentBlock.get("I") == null ? 0.0f : currentBlock.get("I").value;
        double j = currentBlock.get("J") == null ? 0.0f : currentBlock.get("J").value;
        double P = currentBlock.get("P") == null ? 1.0f : currentBlock.get("P").value;
        double z = machine.getZ();

        final double cX = lastX + i;
        final double cY = lastY + j;

        aX = lastX - cX;
        aY = lastY - cY;
        bX = machine.getX() - cX;
        bY = machine.getY() - cY;

        // Clockwise
        if (machine.getMotionMode() == MotionMode.G2) {
            angleA = Math.atan2(bY, bX);
            angleB = Math.atan2(aY, aX);
        } else {
            angleA = Math.atan2(aY, aX);
            angleB = Math.atan2(bY, bX);
        }

        // Make sure angleB is always greater than angleA
        // and if not add 2PI so that it is (this also takes
        // care of the special case of angleA == angleB,
        // ie we want a complete circle)
        if (angleB <= angleA) {
            angleB += 2 * Math.PI * P;
        }
        angle = angleB - angleA;

        // calculate a couple useful things.
        final double radius = Math.sqrt(aX * aX + aY * aY);
        final double length = radius * angle;

        // for doing the actual move.
        int steps;
        int s;

        // Maximum of either 2.4 times the angle in radians
        // or the length of the curve divided by the curve section constant
        steps = (int) Math.ceil(Math.max(angle * 2.4, length / curveSection));

        int step;

        // this is the real draw action.
        double arcStartZ = lastZ;
        for (s = 1; s <= steps; s++) {

            if (!clockwise)
                step = s;
            else
                step = steps - s;

            final double ta = (angleA + angle * ((double) (step) / steps));

            addData(
                    (cX + radius * Math.cos(ta)),
                    (cY + radius * Math.sin(ta)),
                    (lastZ + (z - arcStartZ) * s / steps), machine.getMotionMode());
        }

    }
}
