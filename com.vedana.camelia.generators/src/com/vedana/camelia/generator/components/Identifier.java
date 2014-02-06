/*
 * $Id: Identifier.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.components;

import com.vedana.camelia.generator.components_1_1.CameliaGenerator.RenderKit;

public class Identifier {
    public final String renderKitId;

    public final String componentId;

    public Identifier(RenderKit renderKit, String componentId) {
        this.renderKitId = renderKit.componentTypePrefix;
        this.componentId = componentId;
    }

    public Identifier(String renderKitId, String componentId) {
        this.renderKitId = renderKitId;
        this.componentId = componentId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((componentId == null) ? 0 : componentId.hashCode());
        result = prime * result
                + ((renderKitId == null) ? 0 : renderKitId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Identifier other = (Identifier) obj;
        if (componentId == null) {
            if (other.componentId != null)
                return false;
        } else if (!componentId.equals(other.componentId))
            return false;
        if (renderKitId == null) {
            if (other.renderKitId != null)
                return false;
        } else if (!renderKitId.equals(other.renderKitId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return renderKitId + ":" + componentId;
    }

}
