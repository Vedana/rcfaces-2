/*
 * $Id: IInputTypeCapability.java,v 1.1 2011/04/12 09:25:27 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:27 $
 */
public interface IInputTypeCapability {
    int AS_PUSH_BUTTON = 1;

    int AS_CHECK_BUTTON = 2;

    int AS_DROP_DOWN_MENU = 4;

    int AS_RADIO_BUTTON = 8;

    int AS_SUBMIT_BUTTON = 16;

    int AS_RESET_BUTTON = 32;

    int AS_SEPARATOR = 64;

    int getInputType();

    void setInputType(int inputType);
}
