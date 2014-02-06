/*
 * $Id: JavaScriptEnableModeImpl.java,v 1.3 2013/07/03 12:25:09 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import java.io.Serializable;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:09 $
 */
public class JavaScriptEnableModeImpl implements IJavaScriptEnableMode,
        Serializable {

    

    private static final long serialVersionUID = 8767842193854145775L;

    private static final String STRING_EMPTY_ARRAY[] = new String[0];

    public static final int ONINIT = 0x001;

    public static final int ONSUBMIT = 0x010;

    public static final int ONFOCUS = 0x100;

    public static final int ONACCESSKEY = 0x200;

    public static final int ONMESSAGE = 0x400;

    public static final int ONOVER = 0x800;

    public static final int ONLAYOUT = 0x1000;

    public static final int PASSIVE_MASK = ONLAYOUT;

    private int mode = 0;

    JavaScriptEnableModeImpl() {
    }

    public void enableOnInit() {
        mode |= ONINIT;
    }

    public void enableOnSubmit() {
        mode |= ONSUBMIT;
    }

    public void enableOnFocus() {
        mode |= ONFOCUS;
    }

    public void enableOnAccessKey() {
        mode |= ONACCESSKEY;
    }

    public void enableOnMessage() {
        mode |= ONMESSAGE;
    }

    public void enableOnOver() {
        mode |= ONOVER;
    }

    public void enableOnLayout() {
        mode |= ONLAYOUT;
    }

    public int getMode() {
        return mode;
    }

    public String toString() {
        String s = "[JavaScriptEnabledMode";
        if ((mode & ONINIT) > 0) {
            s += " INIT";
        }
        if ((mode & ONSUBMIT) > 0) {
            s += " SUBMIT";
        }
        if ((mode & ONFOCUS) > 0) {
            s += " FOCUS";
        }
        if ((mode & ONACCESSKEY) > 0) {
            s += " ACCESSKEY";
        }
        if ((mode & ONOVER) > 0) {
            s += " OVER";
        }
        if ((mode & ONMESSAGE) > 0) {
            s += " MESSAGE";
        }
        if ((mode & ONLAYOUT) > 0) {
            s += " LAYOUT";
        }

        return s + " (" + mode + ")]";
    }

    public boolean isOnInitEnabled() {
        return (getMode() & ONINIT) > 0;
    }

}