package com.rvantwisk.gcodegenerator.interfaces;

import java.util.Map;

/**
 * Created by rvt on 12/31/13.
 */
public interface PostProcessorConfig {

    boolean isHasToolChanger();

    String getPreamble();

    String getPostamble();

    int getDecimalsF();

    int getDecimalsS();

    int getDecimalsOthers();

    public Map<String, String> getAxisMapping();

    public Map<String, Integer> getAxisDecimals();

}
