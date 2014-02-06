/*
 * $Id: PageConfiguration.java,v 1.2 2013/07/03 12:25:05 jbmeslin Exp $
 */
package org.rcfaces.core.internal.tools;

import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.ILiteralLocaleCapability;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.capability.IPageConfigurator;
import org.rcfaces.core.internal.converter.LocaleConverter;
import org.rcfaces.core.internal.renderkit.AbstractProcessContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:05 $
 */
public class PageConfiguration {
    

    private static final Log LOG = LogFactory.getLog(PageConfiguration.class);

    private static final String SCRIPT_TYPE_PROPERTY = "org.rcfaces.core.internal.PageConfiguration.SCRIPT_TYPE";

    private static final String LITERAL_LOCALE_PROPERTY = "org.rcfaces.core.internal.PageConfiguration.LITERAL_LOCALE";

    private static final String LITERAL_LOCALE_PARAMETER = Constants
            .getPackagePrefix()
            + ".LITERAL_LOCALE";

    public static void setPageConfigurator(FacesContext facesContext,
            IPageConfigurator pageConfigurator) {

        String pageScriptType = pageConfigurator.getPageScriptType();
        if (pageScriptType != null) {
            setAttribute(facesContext, SCRIPT_TYPE_PROPERTY, pageScriptType);
        }

        Locale locale = pageConfigurator.getLiteralLocale();
        if (locale != null) {
            setAttribute(facesContext, LITERAL_LOCALE_PROPERTY, locale);
        }
    }

    private static void setAttribute(FacesContext facesContext, String name,
            Object value) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        Map requestMap = facesContext.getExternalContext().getRequestMap();
        requestMap.put(name, value);

        UIViewRoot viewRoot = facesContext.getViewRoot();
        if (viewRoot != null) {
            Map viewAttributes = viewRoot.getAttributes();

            viewAttributes.put(name, value);
        }
    }

    private static Object getAttribute(FacesContext facesContext, String name) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        Map requestMap = facesContext.getExternalContext().getRequestMap();
        Object value = requestMap.get(name);
        if (value != null) {
            return value;
        }

        UIViewRoot viewRoot = facesContext.getViewRoot();
        if (viewRoot != null) {
            Map viewAttributes = viewRoot.getAttributes();

            value = viewAttributes.get(name);
            if (value != null) {
                return value;
            }
        }

        return null;
    }

    private static final IPageConfigurator getPageConfiguration(
            FacesContext facesContext) {
        UIViewRoot viewRoot = facesContext.getViewRoot();
        if (viewRoot == null) {
            return null;
        }

        return (IPageConfigurator) ComponentTools.findComponent(viewRoot,
                IPageConfigurator.class);
    }

    public static final String getScriptType(FacesContext facesContext) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        String scriptType = (String) getAttribute(facesContext,
                SCRIPT_TYPE_PROPERTY);

        IPageConfigurator scriptTypeConfigurator = getPageConfiguration(facesContext);
        if (scriptTypeConfigurator != null) {
            scriptType = scriptTypeConfigurator.getPageScriptType();

            if (scriptType != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Script type detected = " + scriptType);
                }

                setAttribute(facesContext, SCRIPT_TYPE_PROPERTY, scriptType);
                return scriptType;
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("No script type detected !");
        }

        return null;
    }

    public static Locale getLiteralLocale(IProcessContext processContext,
            UIComponent original) {

        UIComponent component = original;

        Locale locale = null;
        for (; component != null; component = component.getParent()) {

            if (component instanceof ILiteralLocaleCapability) {

                locale = ((ILiteralLocaleCapability) component)
                        .getLiteralLocale();
                if (locale != null) {
                    return locale;
                }

                continue;
            }
        }

        if (processContext == null) {
            processContext = AbstractProcessContext
                    .getProcessContext(FacesContext.getCurrentInstance());
        }

        FacesContext facesContext = processContext.getFacesContext();

        locale = processContext.getDefaultLiteralLocale();
        if (locale != null) {
            return locale;
        }

        locale = (Locale) getAttribute(facesContext, LITERAL_LOCALE_PROPERTY);
        if (locale != null) {
            return locale;
        }

        locale = getDefaultLiteralLocale(facesContext);
        if (locale != null) {
            setAttribute(facesContext, LITERAL_LOCALE_PROPERTY, locale);
            return locale;
        }

        if (original == null) {
            throw new FacesException(
                    "You must specify a default locale for literals !");
        }

        throw new FacesException(
                "You must specify a default locale for literals for component: "
                        + original.getId());
    }

    public static Locale getDefaultLiteralLocale(FacesContext facesContext) {
        IPageConfigurator scriptTypeConfigurator = getPageConfiguration(facesContext);
        if (scriptTypeConfigurator != null) {
            Locale locale = scriptTypeConfigurator.getLiteralLocale();

            if (locale != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Default locale detected = " + locale);
                }

                return locale;
            }
        }

        Map applicationInitMap = facesContext.getExternalContext()
                .getInitParameterMap();
        String value = (String) applicationInitMap
                .get(LITERAL_LOCALE_PARAMETER);
        if (value == null) {
            return null;
        }

        Locale locale = (Locale) LocaleConverter.SINGLETON.getAsObject(null,
                null, value);
        if (locale != null) {
            return locale;
        }

        throw new FacesException("Unknown locale name '" + value
                + "' defined into application init parameters. (web.xml)");
    }

    public static TimeZone getLiteralTimeZone(IProcessContext processContext,
            UIComponent component) {
        // TODO Auto-generated method stub
        return null;
    }
}
