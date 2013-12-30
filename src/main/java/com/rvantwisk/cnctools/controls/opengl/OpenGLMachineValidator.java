package com.rvantwisk.cnctools.controls.opengl;

import com.rvantwisk.cnctools.gcodeparser.AbstractMachineValidator;
import com.rvantwisk.cnctools.gcodeparser.MachineStatus;
import com.rvantwisk.cnctools.gcodeparser.MachineStatusHelper;
import com.rvantwisk.cnctools.gcodeparser.ParsedWord;
import com.rvantwisk.cnctools.gcodeparser.exceptions.SimException;
import com.rvantwisk.cnctools.gcodeparser.exceptions.SimValidationException;
import com.rvantwisk.cnctools.gcodeparser.gcodes.*;

import java.util.Map;

/**
 * Created by rvt on 12/21/13.
 */
public class OpenGLMachineValidator extends AbstractMachineValidator {


    @Override
    public void preVerify(Map<String, ParsedWord> block) throws SimException {

        // test for modals within the same group
        if (
                hasMultipleWords(block, MotionMode.class) ||
                        hasMultipleWords(block, SpindleMode.class) ||
                        hasMultipleWords(block, FeedRateMode.class) ||
                        hasMultipleWords(block, SFOverrideMode.class) ||
                        hasMultipleWords(block, ActivePlane.class) ||
                        hasMultipleWords(block, Units.class) ||
                        hasMultipleWords(block, CutterRadiusCompMode.class) ||
                        hasMultipleWords(block, CutterLengthCompMode.class) ||
                        hasMultipleWords(block, CoordinateSystemMode.class) ||
                        hasMultipleWords(block, PathControleMode.class) ||
                        hasMultipleWords(block, DistanceMode.class) ||
                        hasMultipleWords(block, RetrackMode.class) ||
                        hasMultipleWords(block, ReferenceLocation.class) ||
                        hasMultipleWords(block, PredefinedPosition.class) ||
                        hasMultipleWords(block, AxisOffset.class)

                ) {
            throw new SimValidationException("Two M or G codes used in the same model group [" + block.toString() + "]");
        }

        // Validate modal codes that uses axis values
        if (wordCount(block, MotionMode.class) > 0 &&
                (wordCount(block, PredefinedPosition.class) > 0 ||
                        wordCount(block, ReferenceLocation.class) > 0 ||
                        wordCount(block, AxisOffset.class) > 0 ||
                        block.containsKey("G43_1") ||
                        block.containsKey("G92")
                )) {
            throw new SimValidationException("Cannot use two g codes that both use axis values");
        }

        if (block.containsKey("G43")) {
            Double H = block.containsKey("H") ? block.get("H").value : null;
            if (H != null && (H < 0 || H.intValue() != H)) {
                throw new SimValidationException("G43 has invalid H value.");
            }
        }

        if (block.containsKey("F") && block.get("F").value < 0) {
            throw new SimValidationException("Negative F word used");
        }

        if (block.containsKey("G64")) {
            Double P = block.containsKey("P") ? block.get("P").value : null;
            Double Q = block.containsKey("Q") ? block.get("Q").value : null;
            if (Q != null && P == null) {
                throw new SimValidationException("Q word given without P word on G64");
            }
        }
    }

    @Override
    public void postVerify(MachineStatus machineStatus) throws SimValidationException {
        if (machineStatus.getModalVars().get("F") < 0.00001 && hasAny(machineStatus.getModals(), new String[]{"G1", "G2", "G3"})) {
            throw new SimValidationException("Cannot do motion with zero feedrate");
        }

        if (hasAny(machineStatus.getModals(), new String[]{"G2", "G3"}) && machineStatus.getModalVars().containsKey("P")) {
            double P = machineStatus.getModalVars().get("P") == null ? 1.0f : machineStatus.getModalVars().get("P");
            if (P != (int) P) {
                throw new SimValidationException("P value not a integer with G2 or g3");
            }
        }

        if (hasAny(machineStatus.getModals(), new String[]{"G53"}) && !hasAny(machineStatus.getModals(), new String[]{"G0", "G1"})) {
            throw new SimValidationException("Must use g0 or g1 with G53");
        }

        if (hasAny(machineStatus.getModals(), new String[]{"G53"}) && hasAny(machineStatus.getModals(), new String[]{"G41", "G42", "G41_1", "G42_1"})) {
            throw new SimValidationException("Cannot use G53 with cutter compensation.");
        }

        MachineStatusHelper machine=new MachineStatusHelper();
        machine.setMachineStatus(machineStatus);

        if (machine.getActivePlane() != ActivePlane.G17 && hasAny(machineStatus.getModals(), new String[]{"G2", "G3"})) {
            throw new SimValidationException("Arc currently only support G17, please report and we will update!");
        }


    }
}
