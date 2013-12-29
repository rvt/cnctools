package com.rvantwisk.cnctools.controllers;

import com.rvantwisk.cnctools.misc.AbstractController;
import com.rvantwisk.cnctools.controls.ToolParametersControl;
import com.rvantwisk.cnctools.data.ToolParameter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Created with IntelliJ IDEA.
 * User: rvt
 * Date: 10/11/13
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ToolEditController extends AbstractController {

    @FXML
    private ToolParametersControl toolParameters; // if you are wondering why this controller is injected, it's becaused it's the <fx:id> name + Controller appeneded

    private Result result;

    @FXML
    void onClose(ActionEvent event) {
        result = Result.DISMISS;
        getDialog().close();
    }

    @FXML
    void onUse(ActionEvent event) {
        result = Result.USE;
        getDialog().close();
    }

    public Result getResult() {
        return result;
    }

    public ToolParameter getTool() {
        return toolParameters.getTool();
    }

    public void setTool(ToolParameter tool) {
        toolParameters.setTool(tool);
    }

}
