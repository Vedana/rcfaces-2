/*
 * $Id: HtmlProcessContextImpl.java,v 1.4 2013/11/13 12:53:28 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentPath;
import org.rcfaces.core.internal.contentProxy.IResourceProxyHandler;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.AbstractProcessContext;
import org.rcfaces.core.internal.renderkit.IProcessContext;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.renderkit.html.internal.agent.ClientBrowserFactory;
import org.rcfaces.renderkit.html.internal.agent.IClientBrowser;
import org.rcfaces.renderkit.html.internal.css.ICssConfig;
import org.rcfaces.renderkit.html.internal.css.StylesheetsServlet;
import org.rcfaces.renderkit.html.internal.util.CarriageReturnNormalizerMode;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:28 $
 */
public class HtmlProcessContextImpl extends AbstractProcessContext implements
        IHtmlProcessContext {

    private static final Log LOG = LogFactory
            .getLog(HtmlProcessContextImpl.class);

    private static final String NAMESPACE_URI = "rcfaces.xsd";

    private static final String HTML_ESCAPING_DISABLED_ATTRIBUTE = "org.rcfaces.html.HTML_ESCAPING_DISABLED";

    private static final String CARRIAGE_RETURN_NORMALIZER_PARAMETER = "org.rcfaces.html.CARRIAGE_RETURN_NORMALIZER";

    private String styleSheetURI;

    private String styleSheetURIWithContextPath;

    private String nameSpaceURI;

    private final boolean useScriptCData;

    private final boolean useFlatIdentifier;

    private final String separatorChar;

    private final boolean useMetaContentScriptType;

    private final boolean useMetaContentStyleType;

    private final Set<String> systemParametersNames;

    // private final boolean keepDisabledState;

    private Boolean multiWindowMode;

    private Boolean debugMode;

    private Boolean profilerMode;

    private IClientBrowser clientBrowser;

    private boolean htmlEscapingDisabled;

    private Set<String> cssProcessRulesForce;

    private CarriageReturnNormalizerMode carriageReturnNormalizerMode;

    public HtmlProcessContextImpl(FacesContext facesContext) {
        super(facesContext);

        ExternalContext externalContext = facesContext.getExternalContext();

        Map applicationMap = externalContext.getInitParameterMap();

        useMetaContentScriptType = "false"
                .equalsIgnoreCase((String) applicationMap
                        .get(USE_META_CONTENT_SCRIPT_TYPE_PARAMETER)) == false;

        useMetaContentStyleType = "false"
                .equalsIgnoreCase((String) applicationMap
                        .get(USE_META_CONTENT_STYLE_TYPE_PARAMETER)) == false;

        useScriptCData = "false".equalsIgnoreCase((String) applicationMap
                .get(USE_SCRIPT_CDATA_PARAMETER)) == false;

        useFlatIdentifier = "true".equalsIgnoreCase((String) applicationMap
                .get(HTML_FLAT_IDENTIFIER_PARAMETER));

        String prf = (String) applicationMap
                .get(HTML_PROCESS_RULES_FORCED_PARAMETER);
        if (prf != null) {
            StringTokenizer st = new StringTokenizer(prf, ",; ");

            cssProcessRulesForce = new HashSet<String>(8);
            for (; st.hasMoreTokens();) {
                cssProcessRulesForce.add(st.nextToken());
            }
        }

        // keepDisabledState = "true".equalsIgnoreCase((String)
        // applicationMap.get(KEEP_DISABLED_STATE_PARAMETER));

        String debugModeParam = (String) applicationMap
                .get(DEBUG_MODE_APPLICATION_PARAMETER);
        if (debugModeParam != null) {
            debugMode = Boolean
                    .valueOf("true".equalsIgnoreCase(debugModeParam));
        }

        String profilerModeParam = (String) applicationMap
                .get(PROFILER_MODE_APPLICATION_PARAMETER);
        if (profilerModeParam != null) {
            profilerMode = Boolean.valueOf("true"
                    .equalsIgnoreCase(profilerModeParam));
        }

        String multiWindowModeParam = (String) applicationMap
                .get(MULTI_WINDOW_MODE_APPLICATION_PARAMETER);
        if (multiWindowModeParam != null) {
            multiWindowMode = Boolean.valueOf("true"
                    .equalsIgnoreCase(multiWindowModeParam));
        }

        systemParametersNames = new HashSet<String>();
        String systemParametersNamesParam = (String) applicationMap
                .get(SYSTEM_PARAMETERS_NAMES_APPLICATION_PARAMETER);
        if (systemParametersNamesParam != null) {
            StringTokenizer st = new StringTokenizer(
                    systemParametersNamesParam, ",; ");

            for (; st.hasMoreTokens();) {
                systemParametersNames.add(st.nextToken());
            }
        }

        separatorChar = getHtmlSeparatorChar(externalContext);

        ICssConfig cssConfig = StylesheetsServlet.getConfig(this);

        styleSheetURI = cssConfig.getDefaultStyleSheetURI();

        IResourceProxyHandler resourceProxyHandler = RcfacesContext
                .getInstance(facesContext).getResourceProxyHandler();
        if (resourceProxyHandler != null && resourceProxyHandler.isEnabled()
                && resourceProxyHandler.isFrameworkResourcesEnabled()) {

            styleSheetURIWithContextPath = resourceProxyHandler
                    .computeProxyedURL(facesContext, null, null, styleSheetURI);
        }

        if (styleSheetURIWithContextPath == null) {
            styleSheetURIWithContextPath = externalContext
                    .getRequestContextPath() + styleSheetURI;
        }

        htmlEscapingDisabled = "true".equalsIgnoreCase((String) applicationMap
                .get(HTML_ESCAPING_DISABLED_ATTRIBUTE));

        String crNormalizerMode = (String) applicationMap
                .get(CARRIAGE_RETURN_NORMALIZER_PARAMETER);
        if (crNormalizerMode != null) {
            if ("cr".equalsIgnoreCase(crNormalizerMode)) {
                carriageReturnNormalizerMode = CarriageReturnNormalizerMode.NormalizeToCR;

            } else if ("lf".equalsIgnoreCase(crNormalizerMode)) {
                carriageReturnNormalizerMode = CarriageReturnNormalizerMode.NormalizeToLF;

            } else if ("crlf".equalsIgnoreCase(crNormalizerMode)) {
                carriageReturnNormalizerMode = CarriageReturnNormalizerMode.NormalizeToCRLF;

            } else if ("none".equalsIgnoreCase(crNormalizerMode)
                    || "false".equalsIgnoreCase(crNormalizerMode)) {
                carriageReturnNormalizerMode = CarriageReturnNormalizerMode.None;

            } else {
                throw new FacesException("Invalid value ('" + crNormalizerMode
                        + "') for application parameter '"
                        + CARRIAGE_RETURN_NORMALIZER_PARAMETER + "'.");
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Initialize htmlRenderExternalContext useMetaContentScriptType="
                    + useMetaContentScriptType
                    + ", useScriptCData="
                    + useScriptCData
                    + ", useFlatIdentifier="
                    + useFlatIdentifier
                    + ", separatorChar='"
                    + separatorChar
                    + "'.");
        }
    }

    public IContentAccessor getModuleStyleSheetContentAccessor(
            String moduleName, String uri, IContentFamily contentType) {
        String url = getModuleStyleSheetURI(moduleName, uri, false);
        if (url == null) {
            return null;
        }

        IContentAccessor contentAccessor = ContentAccessorFactory
                .createFromWebResource(getFacesContext(), url, contentType);

        contentAccessor.setContentVersionHandler(null); // Pas besoin de version
        // !
        contentAccessor.setPathType(IContentPath.CONTEXT_PATH_TYPE);

        return contentAccessor;
    }

    public final String getModuleStyleSheetURI(String moduleName, String uri,
            boolean containsContextPath) {

        ICssConfig cssConfig = StylesheetsServlet.getConfig(this, moduleName);

        String styleSheetURI = cssConfig.getDefaultStyleSheetURI();
        String path = null;

        if (containsContextPath) {
            IResourceProxyHandler resourceProxyHandler = RcfacesContext
                    .getInstance(facesContext).getResourceProxyHandler();
            if (resourceProxyHandler != null
                    && resourceProxyHandler.isEnabled()
                    && resourceProxyHandler.isFrameworkResourcesEnabled()) {

                path = resourceProxyHandler.computeProxyedURL(facesContext,
                        null, null, styleSheetURI);
            }

            if (path == null) {
                path = FacesContext.getCurrentInstance().getExternalContext()
                        .getRequestContextPath()
                        + styleSheetURI;
            }
        } else {
            path = styleSheetURI;
        }

        if (uri == null) {
            return path;
        }

        StringAppender u = new StringAppender(path, uri.length() + 2);

        if (uri != null && uri.length() > 0) {
            if (uri.startsWith("/") == false) {
                u.append('/');

            } else if (u.charAt(u.length() - 1) != '/') {
                u.append('/');
            }
            u.append(uri);
        } else {
            u.append('/');
        }

        String ret = u.toString();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Compute stylesheet uri '" + uri
                    + "' (containsContextPath=" + containsContextPath
                    + ") => '" + ret + "'.");
        }

        return ret;
    }

    public IContentAccessor getStyleSheetContentAccessor(String uri,
            IContentFamily contentType) {
        String url = getStyleSheetURI(uri, false);
        if (url == null) {
            return null;
        }

        IContentAccessor contentAccessor = ContentAccessorFactory
                .createFromWebResource(getFacesContext(), url, contentType);

        contentAccessor.setContentVersionHandler(null); // Pas besoin de version
        // !
        contentAccessor.setPathType(IContentPath.CONTEXT_PATH_TYPE);

        return contentAccessor;
    }

    public final String getStyleSheetURI(String uri, boolean containsContextPath) {
        String ret = null;
        if (uri != null) {
            StringAppender u = new StringAppender(
                    styleSheetURIWithContextPath.length() + uri.length() + 2);

            if (containsContextPath) {
                u.append(styleSheetURIWithContextPath);

            } else {
                u.append(styleSheetURI);
            }

            if (uri != null && uri.length() > 0) {
                if (uri.startsWith("/") == false) {
                    u.append('/');

                } else if (u.charAt(u.length() - 1) != '/') {
                    u.append('/');
                }
                u.append(uri);
            } else {
                u.append('/');
            }

            ret = u.toString();

        } else if (containsContextPath) {
            ret = styleSheetURIWithContextPath;

        } else {
            ret = styleSheetURI;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Compute stylesheet uri '" + uri
                    + "' (containsContextPath=" + containsContextPath
                    + ") => '" + ret + "'.");
        }

        return ret;
    }

    public boolean isFlatIdentifierEnabled() {
        return useFlatIdentifier;
    }

    static final String getHtmlSeparatorChar(ExternalContext externalContext) {

        Map applicationMap = externalContext.getInitParameterMap();

        String separatorChar = (String) applicationMap
                .get(HTML_SEPARATOR_CHAR_PARAMETER);

        if (separatorChar != null && separatorChar.length() > 0) {
            return separatorChar;
        }

        return null; // NamingContainer.SEPARATOR_CHAR;
    }

    public String getNamingSeparator() {
        return separatorChar;
    }

    public boolean useMetaContentScriptType() {
        return useMetaContentScriptType;
    }

    public boolean useMetaContentStyleType() {
        return useMetaContentStyleType;
    }

    public boolean useScriptCData() {
        return useScriptCData;
    }

    public Boolean getDebugMode() {
        return debugMode;
    }

    public Boolean getProfilerMode() {
        return profilerMode;
    }

    public String getNameSpaceURI() {
        return getStyleSheetURI(NAMESPACE_URI, true);
    }

    public static IHtmlProcessContext getHtmlProcessContext(
            FacesContext facesContext) {

        IProcessContext processContext = getProcessContext(facesContext);

        if (processContext instanceof IHtmlProcessContext) {
            return (IHtmlProcessContext) processContext;
        }

        IHtmlProcessContext htmlProcessContext = new HtmlProcessContextImpl(
                facesContext);
        setProcessContext(htmlProcessContext);

        return htmlProcessContext;
    }

    public Boolean getMultiWindowMode() {
        return multiWindowMode;
    }

    public IClientBrowser getClientBrowser() {
        if (clientBrowser != null) {
            return clientBrowser;
        }

        clientBrowser = ClientBrowserFactory.Get().get(getFacesContext());

        return clientBrowser;
    }

    public Set<String> getSystemParametersNames() {
        return systemParametersNames;
    }

    public boolean isHtmlEscapingDisabled() {
        return htmlEscapingDisabled;
    }

    public Set<String> listCssProcessRulesForced() {
        return cssProcessRulesForce;
    }

    public CarriageReturnNormalizerMode getCarriageReturnNormalizerMode() {
        return carriageReturnNormalizerMode;
    }

    // public boolean keepDisabledState() {
    // return keepDisabledState;
    // }

}
