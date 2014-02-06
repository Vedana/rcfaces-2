/*
 * $Id: IAsyncDecodeModeCapability.java,v 1.1 2011/04/12 09:25:26 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * A int value that indicates the decoding mode for asynchronous capable
 * component.
 * <LI>
 * <UL>
 * If value is "0:complete", 
 * </UL>
 * <UL>
 * If value is "1:partial", 
 * </UL>
 * </LI>
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:26 $
 */
public interface IAsyncDecodeModeCapability {

    int COMPLETE_ASYNC_DECODE_MODE = 0;

    int PARTIAL_ASYNC_DECODE_MODE = 1;

    int DEFAULT_ASYNC_DECODE_MODE = COMPLETE_ASYNC_DECODE_MODE;

    /**
     * Returns a int value that indicates the decoding mode for asynchronous
     * capable component.
     * 
     * @return 0:complete|1:partial
     */
    int getAsyncDecodeMode();

    /**
     * Sets a int value that indicates the decoding mode for asynchronous
     * capable component.
     * 
     * @param decodeMode
     *            0:complete|1:partial
     */
    void setAsyncDecodeMode(int decodeMode);
}
