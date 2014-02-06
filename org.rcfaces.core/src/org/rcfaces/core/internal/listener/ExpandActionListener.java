package org.rcfaces.core.internal.listener;

import org.rcfaces.core.event.ExpandEvent;
import org.rcfaces.core.event.IExpandListener;

/**
 * @author meslin.jb@vedana.com
 */
public class ExpandActionListener extends AbstractActionListener implements
        IExpandListener {
    

    private static final Class actionParameters[] = { ExpandEvent.class };

    public ExpandActionListener() {
    }

    public ExpandActionListener(String expression) {
        super(expression);
    }

    public ExpandActionListener(String expression, boolean partialRendering) {
        super(expression, partialRendering);
    }

    protected Class[] listParameterClasses() {
        return actionParameters;
    }

    public void processExpand(ExpandEvent event) {
        process(event);
    }
}
