/*
 * $Id: ImageAccessorTools.java,v 1.2 2013/01/11 15:46:59 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.rcfaces.core.component.ToolBarComponent;
import org.rcfaces.core.component.capability.IImageCapability;
import org.rcfaces.core.component.capability.ISeverityImagesCapability;
import org.rcfaces.core.component.capability.IStatesImageCapability;
import org.rcfaces.core.component.familly.IContentAccessors;
import org.rcfaces.core.internal.capability.IComponentEngine;
import org.rcfaces.core.internal.component.IExpandImageAccessors;
import org.rcfaces.core.internal.component.IImageAccessors;
import org.rcfaces.core.internal.component.ISeverityImageAccessors;
import org.rcfaces.core.internal.component.IToolBarImageAccessors;
import org.rcfaces.core.internal.component.Properties;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.lang.IContentFamily;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:59 $
 */
public class ImageAccessorTools {
 
    private static final IContentAccessors NO_IMAGE_ACCESSORS = new IImageAccessors() {

        public IContentAccessor getImageAccessor() {
            return null;
        }
    };

    private static final IContentAccessors NO_IMAGE_STATES_ACCESSORS = new IExpandImageAccessors() {
 
        public IContentAccessor getImageAccessor() {
            return null;
        }

        public IContentAccessor getDisabledImageAccessor() {
            return null;
        }

        public IContentAccessor getHoverImageAccessor() {
            return null;
        }

        public IContentAccessor getSelectedImageAccessor() {
            return null;
        }

        public IContentAccessor getExpandedImageAccessor() {
            return null;
        }
    };

    private static final IContentAccessors NO_IMAGE_SEVERITY_ACCESSSORS = new ISeverityImageAccessors() {

        public IContentAccessor getErrorImageAccessor() {
            return null;
        }

        public IContentAccessor getFatalImageAccessor() {
            return null;
        }

        public IContentAccessor getInfoImageAccessor() {
            return null;
        }

        public IContentAccessor getWarnImageAccessor() {
            return null;
        }

        public IContentAccessor getImageAccessor() {
            return null;
        }

    };

    public static IContentAccessors createImageAccessor(
            FacesContext facesContext, Object value) {
        if (value == null) {
            return NO_IMAGE_ACCESSORS;
        }

        return ContentAccessorFactory.createSingleImageWebResource(
                facesContext, value, IContentFamily.IMAGE);
    }

    public static IContentAccessors createImageAccessors(
            FacesContext facesContext, IImageCapability component,
            IComponentEngine engine) {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        Object imageContent = engine.getProperty(Properties.IMAGE_URL,
                facesContext);

        return createImageAccessor(facesContext, imageContent);
    }

    public static IContentAccessors createImageAccessors(
            FacesContext facesContext, IStatesImageCapability component,
            IComponentEngine engine) {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        final IContentAccessor imageContentAccessor = createSubAccessor(
                facesContext, engine, Properties.IMAGE_URL, null);

        final IContentAccessor disabledImageContentAccessor = createSubAccessor(
                facesContext, engine, Properties.DISABLED_IMAGE_URL,
                imageContentAccessor);

        final IContentAccessor selectedImageContentAccessor = createSubAccessor(
                facesContext, engine, Properties.SELECTED_IMAGE_URL,
                imageContentAccessor);

        final IContentAccessor hoverImageContentAccessor = createSubAccessor(
                facesContext, engine, Properties.HOVER_IMAGE_URL,
                imageContentAccessor);

        final IContentAccessor expandedImageContentAccessor = createSubAccessor(
                facesContext, engine, Properties.EXPANDED_IMAGE_URL,
                imageContentAccessor);

        if (imageContentAccessor == null
                && selectedImageContentAccessor == null
                && disabledImageContentAccessor == null
                && hoverImageContentAccessor == null
                && expandedImageContentAccessor == null) {

            return NO_IMAGE_STATES_ACCESSORS;
        }

        return new IExpandImageAccessors() {
 
            public IContentAccessor getImageAccessor() {
                return imageContentAccessor;
            }

            public IContentAccessor getSelectedImageAccessor() {
                return selectedImageContentAccessor;
            }

            public IContentAccessor getDisabledImageAccessor() {
                return disabledImageContentAccessor;
            }

            public IContentAccessor getHoverImageAccessor() {
                return hoverImageContentAccessor;
            }

            public IContentAccessor getExpandedImageAccessor() {
                return expandedImageContentAccessor;
            }

        };
    }

    public static IContentAccessors createImageAccessors(
            FacesContext facesContext, ISeverityImagesCapability component,
            IComponentEngine engine) {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        final IContentAccessor imageContentAccessor = createSubAccessor(
                facesContext, engine, Properties.IMAGE_URL, null);

        final IContentAccessor infoImageContentAccessor = createSubAccessor(
                facesContext, engine, Properties.INFO_IMAGE_URL,
                imageContentAccessor);

        final IContentAccessor warnImageContentAccessor = createSubAccessor(
                facesContext, engine, Properties.WARN_IMAGE_URL,
                imageContentAccessor);

        final IContentAccessor errorImageContentAccessor = createSubAccessor(
                facesContext, engine, Properties.ERROR_IMAGE_URL,
                imageContentAccessor);

        final IContentAccessor fatalImageContentAccessor = createSubAccessor(
                facesContext, engine, Properties.FATAL_IMAGE_URL,
                imageContentAccessor);

        if (imageContentAccessor == null && infoImageContentAccessor == null
                && warnImageContentAccessor == null
                && errorImageContentAccessor == null
                && fatalImageContentAccessor == null) {
            return NO_IMAGE_SEVERITY_ACCESSSORS;
        }

        return new ISeverityImageAccessors() {

            public IContentAccessor getErrorImageAccessor() {
                return errorImageContentAccessor;
            }

            public IContentAccessor getFatalImageAccessor() {
                return fatalImageContentAccessor;
            }

            public IContentAccessor getInfoImageAccessor() {
                return infoImageContentAccessor;
            }

            public IContentAccessor getWarnImageAccessor() {
                return warnImageContentAccessor;
            }

            public IContentAccessor getImageAccessor() {
                return imageContentAccessor;
            }
        };
    }

    private static IContentAccessor createSubAccessor(
            FacesContext facesContext, IComponentEngine engine,
            Serializable attributeName, IContentAccessor rootImageContentAccessor) {

        Object imageContent = engine.getProperty(attributeName, facesContext);

        if (imageContent == null) {
            return null;
        }

        if (rootImageContentAccessor != null) {
            return ContentAccessorFactory.createFromWebResource(facesContext,
                    imageContent, rootImageContentAccessor);
        }

        return ContentAccessorFactory.createFromWebResource(facesContext,
                imageContent, IContentFamily.IMAGE);
    }

    public static IToolBarImageAccessors createImageAccessors(
            FacesContext facesContext, ToolBarComponent component,
            IComponentEngine engine) {
        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        final IContentAccessor separatorContentAccessor = createSubAccessor(
                facesContext, engine, Properties.SEPARATOR_IMAGE_URL, null);

        final IContentAccessor controlImageContentAccessor = createSubAccessor(
                facesContext, engine, Properties.CONTROL_IMAGE_URL, null);

        return new IToolBarImageAccessors() {
 
            public IContentAccessor getControlImageAccessor() {
                return controlImageContentAccessor;
            }

            public IContentAccessor getSeparatorImageAccessor() {
                return separatorContentAccessor;
            }

        };
    }
}
