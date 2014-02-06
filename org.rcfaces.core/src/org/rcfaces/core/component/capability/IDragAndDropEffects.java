/*
 * $Id: IDragAndDropEffects.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */

package org.rcfaces.core.component.capability;

/**
 * 
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IDragAndDropEffects {

    int UNKNOWN_DND_EFFECT = -1;

    int NONE_DND_EFFECT = 0x00;

    int DEFAULT_DND_EFFECT = 0x01;

    int COPY_DND_EFFECT = 0x02;

    int LINK_DND_EFFECT = 0x04;

    int MOVE_DND_EFFECT = 0x08;

    int ANY_DND_EFFECT = DEFAULT_DND_EFFECT | COPY_DND_EFFECT | LINK_DND_EFFECT
            | MOVE_DND_EFFECT;
}