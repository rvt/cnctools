package com.rvantwisk.gcodegenerator.interfaces;

import java.util.Map;

/**
 * Created by rvt on 12/31/13.
 */
public interface PostProcessorConfig {
    String getName();

    boolean getHasToolChanger();

    String getPreabmle();

    String getPostamble();

    int getDecimalsF();

    int getDecimalsS();

    int getDecimalsOthers();

    String getDialect();

    boolean getSpaceBetweenWords();

    public Map<String, String> getAxisMapping();

    public Map<String, Integer> getAxisDecimals();
}
