/*
 * $Id: AbstractProcessContext.java,v 1.3 2014/02/05 16:05:53 jbmeslin Exp $
 */
package org.rcfaces.core.internal.renderkit;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.renderkit.designer.IDesignerEngine;
import org.rcfaces.core.internal.tools.ContextTools;
import org.rcfaces.core.internal.tools.PageConfiguration;
import org.rcfaces.core.internal.util.PathUtil;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2014/02/05 16:05:53 $
 */
public abstract class AbstractProcessContext implements IProcessContext {

    private static final Log LOG = LogFactory
            .getLog(AbstractProcessContext.class);

    private static final String EXTERNAL_CONTEXT_PROPERTY = "org.rcfaces.renderkit.core.EXTERNAL_CONTEXT";

    private static final String DESIGNER_ENGINE_PROPERTY_NAME = "org.rcfaces.renderkit.core.DESIGNER_ENGINE";

    protected final RcfacesContext rcfacesContext;

    protected final FacesContext facesContext;

    private final String contextPath;

    private final String servletPath;

    private final IDesignerEngine designerEngine;

    private String baseHREF;

    private Locale userLocale;

    private boolean designerMode;

    private boolean pageConfiguratorInitialized;

    private Locale defaultAttributesLocale;

    private String scriptType;

    private TimeZone timeZone;

    private Calendar calendar;

    private TimeZone forcedDateTimeZone;

    private TimeZone defaultTimeZone;

    private Calendar forcedDateCalendar;

    protected AbstractProcessContext(FacesContext facesContext) {
        this.facesContext = facesContext;

        ExternalContext externalContext = facesContext.getExternalContext();

        contextPath = externalContext.getRequestContextPath();
        String servletPath = externalContext.getRequestServletPath();
        int idx = servletPath.lastIndexOf('/');
        if (idx >= 0) {
            servletPath = servletPath.substring(0, idx);
        }

        this.servletPath = servletPath;

        rcfacesContext = RcfacesContext.getInstance(facesContext);

        this.designerEngine = (IDesignerEngine) externalContext
                .getApplicationMap().get(DESIGNER_ENGINE_PROPERTY_NAME);

        if (designerEngine != null) {
            this.designerMode = true;

        } else {
            this.designerMode = rcfacesContext.isDesignerMode();
        }
    }

    public final FacesContext getFacesContext() {
        return facesContext;
    }

    public Boolean getDebugMode() {
        return null;
    }

    public Boolean getProfilerMode() {
        return null;
    }

    public boolean isDesignerMode() {
        return designerMode;
    }

    public final Locale getUserLocale() {
        if (userLocale != null) {
            return userLocale;
        }

        userLocale = ContextTools.getUserLocale(null);

        return userLocale;
    }

    public Calendar getUserCalendar() {
        if (calendar != null) {
            return calendar;
        }

        TimeZone timeZone = getUserTimeZone();
        Locale locale = getUserLocale();

        if (timeZone != null) {
            calendar = Calendar.getInstance(timeZone, locale);
            return calendar;
        }

        calendar = Calendar.getInstance(locale);

        return calendar;
    }

    public TimeZone getUserTimeZone() {
        if (timeZone != null) {
            return timeZone;
        }

        timeZone = ContextTools.getUserTimeZone(null);

        if (timeZone == null) {

        }

        return timeZone;
    }

    public final String getAbsolutePath(String uri, boolean containsContextPath) {

        String contextPath;
        if (containsContextPath) {
            contextPath = this.contextPath;
        } else {
            contextPath = "";
        }

        if (uri == null || uri.length() < 1) {
            // Retourne le context path

            String p;
            if (containsContextPath) {
                p = contextPath + servletPath;
            } else {
                p = servletPath;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Returns path='" + p + "' [uri=null]  ('"
                        + contextPath + "''" + servletPath + "'.)");
            }

            return p;
        }

        if (uri.charAt(0) == '/') {
            // URL absolue
            String p;
            if (containsContextPath) {
                p = contextPath + uri;

            } else if (uri.startsWith(this.contextPath)) {
                p = uri.substring(this.contextPath.length());

            } else {
                p = uri;
            }

            p = PathUtil.normalizePath(p);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Returns path='" + p + "' [uri=absolute]  ('"
                        + contextPath + "''" + servletPath + "''" + uri + "'.)");
            }
            return p;
        }

        // C'est un URI relatif !

        if (baseHREF != null) {
            if (baseHREF.charAt(0) == '/') {
                // base HREF absolue !

                String p = PathUtil.normalizePath(baseHREF + uri);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Returns path='" + p
                            + "' [uri=relative,baseHREF=absolute]  ('"
                            + baseHREF + uri + "'.)");
                }
                return p;
            }
            // base HREF relatif !

            String p = PathUtil.normalizePath(contextPath + servletPath + "/"
                    + baseHREF + uri);

            if (LOG.isDebugEnabled()) {
                LOG.debug("Returns path='" + p
                        + "' [uri=relative,baseHREF=relative]  ('"
                        + contextPath + "''" + servletPath + "'/'" + baseHREF
                        + uri + "'.)");
            }
            return p;
        }

        String p = PathUtil
                .normalizePath(contextPath + servletPath + "/" + uri);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Returns path='" + p
                    + "' [uri=relative,baseHREF=null] ('" + contextPath + "''"
                    + servletPath + "'/'" + uri + "'.)");
        }
        return p;
    }

    public final String getRelativePath(String uri) {
        return null;
    }

    public final String getBaseHREF() {
        return baseHREF;
    }

    public final void changeBaseHREF(String baseHREF) {
        String base = baseHREF;

        if (base != null) {
            base = normalizeBaseHREF(base);
        }

        this.baseHREF = base;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Set baseHREF to '" + base + "' (param '" + baseHREF
                    + "'.");
        }
    }

    private String normalizeBaseHREF(String baseHREF) {
        if (baseHREF.equals("/")) {
            return baseHREF;
        }

        int idx = baseHREF.lastIndexOf('/'); // Retire le dernier segment qui
        // doit  être un fichier
        if (idx < 1) {
            return null;
        }

        baseHREF = baseHREF.substring(0, idx);

        if (baseHREF.charAt(0) == '/') {
            return baseHREF;
        }

        return PathUtil.normalizePath(contextPath + servletPath + baseHREF);
    }

    protected static void setProcessContext(IProcessContext externalContext) {
        Map<String, Object> requestMap = externalContext.getFacesContext()
                .getExternalContext().getRequestMap();
        IProcessContext old = (IProcessContext) requestMap.put(
                EXTERNAL_CONTEXT_PROPERTY, externalContext);
        if (old != null) {
            throw new FacesException("External constext is already defined ! ("
                    + old + ")");
        }
    }

    public static IProcessContext getProcessContext(FacesContext facesContext) {

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        ExternalContext externalContext = facesContext.getExternalContext();

        Map<String, Object> requestMap = externalContext.getRequestMap();
        IProcessContext processContext = (IProcessContext) requestMap
                .get(EXTERNAL_CONTEXT_PROPERTY);

        if (processContext == null) {
            return new AbstractProcessContext(facesContext) {

                public String getNamingSeparator() {
                    throw new IllegalStateException("Temporary process context");
                }

                public Boolean getMultiWindowMode() {
                    throw new IllegalStateException("Temporary process context");
                }
            };
        }

        return processContext;
    }

    public final String getScriptType() {
        initializePageConfigurator();

        return scriptType;
    }

    public final Locale getDefaultLiteralLocale() {
        initializePageConfigurator();

        return defaultAttributesLocale;
    }

    public TimeZone getDefaultTimeZone() {
        initializePageConfigurator();

        return defaultTimeZone;
    }

    public TimeZone getForcedDateTimeZone() {
        initializePageConfigurator();

        return forcedDateTimeZone;
    }

    public Calendar getForcedDateCalendar() {
        if (forcedDateCalendar != null) {
            return forcedDateCalendar;
        }

        TimeZone timeZone = getForcedDateTimeZone();
        if (timeZone == null) {
            return null;
        }

        forcedDateCalendar = Calendar.getInstance(forcedDateTimeZone);

        return forcedDateCalendar;
    }

    private void initializePageConfigurator() {
        if (pageConfiguratorInitialized) {
            return;
        }

        pageConfiguratorInitialized = true;

        FacesContext facesContext = getFacesContext();

        scriptType = PageConfiguration.getScriptType(facesContext);
        defaultAttributesLocale = PageConfiguration
                .getDefaultLiteralLocale(facesContext);

        Map<String, Object> applicationMap = facesContext.getExternalContext()
                .getApplicationMap();

        defaultTimeZone = getTimeZone(applicationMap,
                DEFAULT_TIMEZONE_PARAMETER);

        forcedDateTimeZone = getTimeZone(applicationMap,
                FORCED_DATE_TIMEZONE_PARAMETER);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Page configurator of view "
                    + facesContext.getViewRoot().getId() + ": scriptType="
                    + scriptType + " defaultAttributesLocale="
                    + defaultAttributesLocale);

        }
    }

    private TimeZone getTimeZone(Map<String, Object> applicationMap,
            String defaultTimezoneParameter) {

        Object defaultTimeZone = applicationMap.get(defaultTimezoneParameter);
        if ((defaultTimeZone instanceof String)
                && ((String) defaultTimeZone).length() > 0) {
            TimeZone timeZone = TimeZone.getTimeZone((String) defaultTimeZone);

            if (timeZone == null) {
                throw new FacesException("Can not get timeZone associated to '"
                        + defaultTimeZone + "'");
            }

            applicationMap.put(defaultTimezoneParameter, timeZone);

            return timeZone;
        }

        return (TimeZone) defaultTimeZone;
    }

    public final RcfacesContext getRcfacesContext() {
        return rcfacesContext;
    }

    public final IDesignerEngine getDesignerEngine() {
        return designerEngine;
    }

}
