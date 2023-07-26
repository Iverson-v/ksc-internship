package com.ksyun.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 
 */
public final class SwitcherUtils {

    public static final String ON = "on";

    public static final String OFF = "off";

    public static final String TRUE = "true";

    public static final String FALSE = "false";

    public static final String ONE = "1";

    public static final String ZERO = "0";

    private static final List<String> ON_VALUES = Arrays.asList(ON, TRUE, ONE);

    private static final List<String> OFF_VALUES = Arrays.asList(OFF, FALSE, ZERO);

    /**
     * 默认开启.
     */
    public static boolean isOn(String switchValue) {
        if (StringUtils.isBlank(switchValue)) {
            return true;
        }

        String lowerCaseSwitchValue = switchValue.trim().toLowerCase();

        if (!ON_VALUES.contains(lowerCaseSwitchValue) && !OFF_VALUES.contains(lowerCaseSwitchValue)) {
            throw new IllegalArgumentException("Illegal switch value. " + switchValue);
        }

        return ON_VALUES.contains(lowerCaseSwitchValue);
    }

    private SwitcherUtils(){

    }
}
