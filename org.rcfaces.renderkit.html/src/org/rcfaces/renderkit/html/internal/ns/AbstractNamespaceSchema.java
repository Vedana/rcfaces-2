/*
 * $Id: AbstractNamespaceSchema.java,v 1.1 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.ns;

import java.util.HashMap;
import java.util.Map;

import org.rcfaces.renderkit.html.internal.ns.NamespaceServlet.IBuffer;

public abstract class AbstractNamespaceSchema implements INamespaceSchema {

    public final String name;

    private Map<String, IBuffer> bufferByName = new HashMap<String, IBuffer>();

    public AbstractNamespaceSchema(String name) {
        this.name = name;
    }

    protected IBuffer recordBuffer(NamespaceServlet servlet, String name,
            String content) {

        IBuffer buffer = servlet.recordBuffer(this, name, content);

        bufferByName.put(name, buffer);

        return buffer;
    }

    public final String getName() {
        return name;
    }

    public IBuffer getBuffer(String resourceName) {
        return bufferByName.get(resourceName);
    }

}
