/*
 * $Id: IAsyncRenderModeCapability.java,v 1.1 2011/04/12 09:25:24 oeuillot Exp $
 */
package org.rcfaces.core.component.capability;

/**
 * A int value that indicates the rendering mode for asynchronous capable
 * component.
 * <LI>
 * <UL>
 * If value is "0:none", the rendering is synchronous (made with the englobing
 * page rendering).
 * </UL>
 * <UL>
 * If value is "1:buffer", the rendering is calculated with the page and may
 * contains HTML elements, it is sent to the client only when needed
 * (asynchrponously).
 * </UL>
 * <UL>
 * If value is "2:tree", the rendering is calculated only when needed (the HTML
 * elements are ignored)
 * </UL>
 * </LI>
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:24 $
 */
public interface IAsyncRenderModeCapability {

    int NONE_ASYNC_RENDER_MODE = 0;

    int BUFFER_ASYNC_RENDER_MODE = 1;

    int TREE_ASYNC_RENDER_MODE = 2;

    int DEFAULT_ASYNC_RENDER_MODE = NONE_ASYNC_RENDER_MODE;

    /**
     * Returns a int value that indicates the rendering mode for asynchronous
     * capable component.
     * 
     * @return 0:none|1:buffer|2:tree
     */
    int getAsyncRenderMode();

    /**
     * Sets a int value that indicates the rendering mode for asynchronous
     * capable component.
     * 
     * @param renderMode
     *            0:none|1:buffer|2:tree
     */
    void setAsyncRenderMode(int renderMode);
}
