package org.rcfaces.renderkit.html.internal.util;

import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.IResourceKeyParticipant;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.item.ICharSetItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:09 $
 */
public class FileItemSource implements StateHolder, IResourceKeyParticipant {
    

    private static final Log LOG = LogFactory.getLog(FileItemSource.class);

    private String source;

    private String charSet;

    private boolean transientState;

    private boolean frameworkResource;

    public FileItemSource(SelectItem selectItem) {
        source = (String) selectItem.getValue();

        if (selectItem instanceof ICharSetItem) {
            charSet = ((ICharSetItem) selectItem).getCharSet();
        }
    }

    public FileItemSource(String source, String userAgentVary, String charSet,
            boolean frameworkResource) {
        this.source = source;

        this.charSet = charSet;

        this.frameworkResource = frameworkResource;
    }

    public FileItemSource() {
    }

    public String getSource() {
        return source;
    }

    public String getCharSet() {
        return charSet;
    }

    public boolean isFrameworkResource() {
        return frameworkResource;
    }

    public boolean isTransient() {
        return transientState;
    }

    public void setTransient(boolean newTransientValue) {
        this.transientState = newTransientValue;
    }

    public void restoreState(FacesContext context, Object state) {
        Object ret[] = (Object[]) state;

        source = (String) ret[0];
        // userAgentVary = (String) ret[1];
        charSet = (String) ret[2];
    }

    public Object saveState(FacesContext context) {
        return new Object[] { source, null, charSet };
    }

    public void participeKey(StringAppender sa) {
        if (source != null) {
            sa.append(source);
        }
        /*
         * if (userAgentVary != null) {
         * sa.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR).append(
         * userAgentVary); }
         */
        if (charSet != null) {
            sa.append(IResourceKeyParticipant.RESOURCE_KEY_SEPARATOR).append(
                    charSet);
        }
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((charSet == null) ? 0 : charSet.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FileItemSource other = (FileItemSource) obj;
        if (charSet == null) {
            if (other.charSet != null) {
                return false;
            }
        } else if (!charSet.equals(other.charSet)) {
            return false;
        }
        if (source == null) {
            if (other.source != null) {
                return false;
            }
        } else if (!source.equals(other.source)) {
            return false;
        }
        return true;
    }

}