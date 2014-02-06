/*
 * $Id: HeadingTools.java,v 1.1 2013/01/11 15:45:06 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.util;

import java.util.Map;

import javax.faces.component.UIComponent;

import org.rcfaces.core.component.capability.IHeadingLevelCapability;
import org.rcfaces.core.component.capability.IHeadingZoneCapability;
import org.rcfaces.core.component.capability.IWAIRoleCapability;
import org.rcfaces.renderkit.html.internal.Constants;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:06 $
 */
public class HeadingTools {

    private static final String HEADING_LEVEL_PROPERTY = "org.rcfaces.html.HEADING_LEVEL";

    public static int computeHeadingLevel(UIComponent component) {

        if (component instanceof IWAIRoleCapability) {

            IWAIRoleCapability waiRole = (IWAIRoleCapability) component;

            int ariaLevel = waiRole.getAriaLevel();
            if (ariaLevel > 0) {
                return ariaLevel;
            }
        }

        Map<String, Object> componentAttributes = component.getAttributes();
        Integer hl = (Integer) componentAttributes.get(HEADING_LEVEL_PROPERTY);
        if (hl != null) {
            int level = hl.intValue();

            return level;
        }

        int level = 0;

        String headingLevelValue = null;
        if (component instanceof IHeadingLevelCapability) {
            headingLevelValue = ((IHeadingLevelCapability) component)
                    .getHeadingLevel();
        }

        if (headingLevelValue != null && headingLevelValue.length() > 0) {
            if (isOnlyDigit(headingLevelValue, 0)) {
                level = Integer.parseInt(headingLevelValue);

            } else if (isOnlyDigit(headingLevelValue, 1)) {
                char cmd = headingLevelValue.charAt(0);
                int delta = Integer.parseInt(headingLevelValue.substring(1)
                        .trim());

                if (cmd == '+') {
                    int parentLevel = getParentHeadingLevel(component);
                    if (parentLevel > 0) {
                        level = parentLevel + delta;
                    } else {
                        level = parentLevel;
                    }

                } else if (cmd == '-') {
                    int parentLevel = getParentHeadingLevel(component);
                    if (parentLevel > 0) {
                        level = parentLevel - delta;

                    } else {
                        level = parentLevel;
                    }
                }
            }
        } else {
            level = getParentHeadingLevel(component);
        }

        componentAttributes.put(HEADING_LEVEL_PROPERTY, new Integer(level));

        return level;
    }

    private static int getParentHeadingLevel(UIComponent child) {
        for (child = child.getParent(); child != null; child = child
                .getParent()) {

            if (child instanceof IHeadingLevelCapability) {

                int level = computeHeadingLevel(child);
                if (level <= 0) {
                    return level;
                }

                if (child instanceof IHeadingZoneCapability) {
                    IHeadingZoneCapability hzc = (IHeadingZoneCapability) child;

                    if (hzc.isHeadingZone()) {
                        level++;
                    }
                }

                return level;
            }

        }

        if (Constants.DISABLE_HEADING_ZONE) {
            return -1;
        }

        return 1;
    }

    private static boolean isOnlyDigit(String value, int index) {

        for (int i = index; i < value.length(); i++) {
            char ch = value.charAt(i);

            if (Character.isDigit(ch)) {
                continue;
            }

            return false;
        }

        return true;
    }

}
