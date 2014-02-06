/*
 * $Id: AbstractComponentDecorator.java,v 1.2 2013/01/11 15:45:05 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.decorator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.rcfaces.core.internal.renderkit.IComponentData;
import org.rcfaces.core.internal.renderkit.IRequestContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.ns.INamespaceConfiguration;
import org.rcfaces.renderkit.html.internal.ns.INamespaceContributor;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:05 $
 */
public class AbstractComponentDecorator implements IComponentDecorator,
        INamespaceContributor {

    private IComponentDecorator subWriter;

    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final String defaultUnlockedProperties[];

    public AbstractComponentDecorator() {
        Set<String> unlockedProperties = new HashSet<String>();
        addUnlockProperties(unlockedProperties);

        if (unlockedProperties.isEmpty() == false) {
            defaultUnlockedProperties = (String[]) unlockedProperties
                    .toArray(new String[unlockedProperties.size()]);

        } else {
            defaultUnlockedProperties = EMPTY_STRING_ARRAY;
        }
    }

    protected void addUnlockProperties(Set<String> unlockedProperties) {
    }

    public String[] getDefaultUnlockedProperties(FacesContext facesContext,
            UIComponent component) {

        String defaultUnlockedProperties[] = this.defaultUnlockedProperties;
        if (subWriter != null) {
            String subWriterDefaultUnlockedProperties[] = subWriter
                    .getDefaultUnlockedProperties(facesContext, component);
            if (subWriterDefaultUnlockedProperties != null
                    && subWriterDefaultUnlockedProperties.length > 0) {
                if (defaultUnlockedProperties == null
                        || defaultUnlockedProperties.length == 0) {
                    subWriterDefaultUnlockedProperties = defaultUnlockedProperties;

                } else {
                    Set<String> set = new HashSet<String>(
                            Arrays.asList(defaultUnlockedProperties));
                    set.addAll(Arrays
                            .asList(subWriterDefaultUnlockedProperties));

                    defaultUnlockedProperties = set.toArray(new String[set
                            .size()]);
                }
            }
        }

        return defaultUnlockedProperties;
    }

    public void encodeContainer(IHtmlWriter writer, Renderer renderer)
            throws WriterException {
        if (subWriter != null) {
            subWriter.encodeContainer(writer, renderer);
        }
    }

    public void encodeContainerEnd(IHtmlWriter writer, Renderer renderer)
            throws WriterException {
        if (subWriter != null) {
            subWriter.encodeContainerEnd(writer, renderer);
        }
    }

    public void encodeJavaScript(IJavaScriptWriter jsWriter)
            throws WriterException {

        if (subWriter != null) {
            subWriter.encodeJavaScript(jsWriter);
        }
    }

    public void decode(IRequestContext requestContext, UIComponent component,
            IComponentData componentData) {

        if (subWriter != null) {
            subWriter.decode(requestContext, component, componentData);
        }
    }

    public void addChildDecorator(IComponentDecorator writer) {
        if (subWriter != null) {
            subWriter.addChildDecorator(writer);
            return;
        }

        this.subWriter = writer;
    }

    public void declare(INamespaceConfiguration nameSpaceProperties) {

    }

}
