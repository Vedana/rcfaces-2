/*
 * $Id: ResourceProxyHandlerImpl.java,v 1.4 2013/11/13 12:53:26 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentProxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Constants;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.contentAccessor.BasicContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IContentPath;
import org.rcfaces.core.internal.contentAccessor.IContentProxyHandler;
import org.rcfaces.core.internal.contentAccessor.IGeneratedResourceInformation;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.util.PathTypeTools;
import org.rcfaces.core.provider.AbstractProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
 */
public class ResourceProxyHandlerImpl extends AbstractProvider implements
        IContentProxyHandler, IResourceProxyHandler {

    private static final Log LOG = LogFactory
            .getLog(ResourceProxyHandlerImpl.class);

    private static final String RESOURCES_DEFAULT_PROXY_URL_PARAMETER = Constants
            .getPackagePrefix() + ".RESOURCE_PROXY_DEFAULT_URL";

    private static final String RESOURCES_PROXY_RULES_PATHS_PARAMETER = Constants
            .getPackagePrefix() + ".RESOURCE_PROXY_RULES_PATHS";

    private static final String ENABLE_FILTRED_RESOURCES_PROXY_PARAMETER = Constants
            .getPackagePrefix() + ".ENABLE_FILTRED_RESOURCES_PROXY";

    private static final String ENABLE_FRAMEWORK_RESOURCES_PROXY_PARAMETER = Constants
            .getPackagePrefix() + ".ENABLE_FRAMEWORK_RESOURCES_PROXY";

    protected static final String PROTOCOL_KEYWORD = "protocol";

    protected static final String LOCAL_NAME_KEYWORD = "host";

    protected static final String LOCAL_ADDR_KEYWORD = "addr";

    protected static final String LOCAL_PORT_KEYWORD = "port";

    protected static final String OPTIONAL_LOCAL_PORT_KEYWORD = "optional-port";

    protected static final String CONTEXT_PATH_KEYWORD = "context";

    protected static final int DEFAULT_HTTP_PORT = 80;

    protected static final int DEFAULT_HTTPS_PORT = 443;

    private static final String CACHED_URL_BASE_PROPERTY = "org.rcfaces.core.CACHED_URL_BASE";

    private static final String NO_BASE_URL_STRING = CACHED_URL_BASE_PROPERTY;

    private static final String DISABLED_URL = "disabled";

    private static final String PATTERN_RULE_PROPERTY = "org.rcfaces.proxy.PATTERN_RULES";

    private IPatternRule defaultRule;

    private IPatternRule rules[];

    private boolean enabled;

    private final Map<String, IKeywordValue> keywordValueMap = new HashMap<String, IKeywordValue>(
            16);

    private boolean filtredResourcesEnabled;

    private boolean frameworkResourceEnabled;

    public String getId() {
        return ID;
    }

    @Override
    public void startup(FacesContext facesContext) {

        ExternalContext externalContext = facesContext.getExternalContext();

        final String defaultURL = externalContext
                .getInitParameter(RESOURCES_DEFAULT_PROXY_URL_PARAMETER);

        String rulesPaths = externalContext
                .getInitParameter(RESOURCES_PROXY_RULES_PATHS_PARAMETER);

        if ((defaultURL == null || "none".equalsIgnoreCase(defaultURL))
                && (rulesPaths == null || rulesPaths.trim().length() == 0)) {
            LOG.info("Disable application proxy rewriting engine. (context parameter '"
                    + RESOURCES_DEFAULT_PROXY_URL_PARAMETER
                    + "="
                    + defaultURL
                    + "')");
            return;
        }

        enabled = true;

        if (defaultURL != null) {
            this.defaultRule = new DefaultPatternRule(defaultURL, null);
        }

        if (rulesPaths != null && rulesPaths.length() > 0) {
            loadRules(externalContext, rulesPaths);
        }

        filtredResourcesEnabled = "true".equalsIgnoreCase(externalContext
                .getInitParameter(ENABLE_FILTRED_RESOURCES_PROXY_PARAMETER));

        frameworkResourceEnabled = ("false".equalsIgnoreCase(externalContext
                .getInitParameter(ENABLE_FRAMEWORK_RESOURCES_PROXY_PARAMETER)) == false);

        LOG.info("Set proxy pattern to '" + defaultURL
                + "', filtredResourcesEnabled=" + filtredResourcesEnabled
                + ", frameworkResourceEnabled=" + frameworkResourceEnabled
                + ".");

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        if (rcfacesContext.getResourceProxyHandler() == null) {
            rcfacesContext.setResourceProxyHandler(this);

            if (rcfacesContext.getDefaultContentProxyHandler() == null) {
                rcfacesContext.setDefaultContentProxyHandler(this);
            }
        }

        fillKeywordValues(rcfacesContext, keywordValueMap);

    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isFiltredResourcesEnabled() {
        return filtredResourcesEnabled;
    }

    public boolean isFrameworkResourcesEnabled() {
        return frameworkResourceEnabled;
    }

    public IContentAccessor getProxyedContentAccessor(
            RcfacesContext rcfacesContext, FacesContext facesContext,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation[] contentInformationRef) {

        int pathType = contentAccessor.getPathType();
        if (pathType != IContentPath.RELATIVE_PATH_TYPE
                && pathType != IContentPath.ABSOLUTE_PATH_TYPE
                && pathType != IContentPath.CONTEXT_PATH_TYPE) {

            return contentAccessor;
        }

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        String url = (String) contentAccessor.getContentRef();

        if (pathType == IContentPath.RELATIVE_PATH_TYPE) {
            // A convertir en CONTEXT

            url = PathTypeTools.convertRelativePathToContextPath(facesContext,
                    url, null);

        } else if (pathType == IContentPath.ABSOLUTE_PATH_TYPE) {
            // A convertir en CONTEXT

            String converted = PathTypeTools.convertAbsolutePathToContextType(
                    facesContext, url);

            if (converted == null) {
                throw new FacesException("Can not convert '" + url
                        + "' to context path type.");
            }

            url = converted;
        }

        String converted = computeProxyedURL(facesContext, contentAccessor,
                contentInformationRef, url);

        if (LOG.isDebugEnabled()) {
            LOG.debug("computeNewURL returns '" + converted + "'.");
        }

        if (converted == null) {
            return contentAccessor;
        }

        return new BasicContentAccessor(facesContext, converted,
                contentAccessor, IContentPath.EXTERNAL_PATH_TYPE);
    }

    public String computeProxyedURL(FacesContext facesContext,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation[] contentInformationRef, String url) {

        if (rules != null) {
            for (int i = 0; i < rules.length; i++) {
                IPatternRule rule = rules[i];

                if (rule.acceptURL(url) == false) {
                    continue;
                }

                String ret = computeProxyedURL(facesContext, contentAccessor,
                        contentInformationRef, rule, url);
                if (ret != null) {
                    if (ret == DISABLED_URL) {
                        return null;
                    }
                    return ret;
                }
            }
        }

        if (defaultRule != null) {
            String ret = computeProxyedURL(facesContext, contentAccessor,
                    contentInformationRef, defaultRule, url);
            if (ret != null) {
                if (ret == DISABLED_URL) {
                    return null;
                }
                return ret;
            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    protected String computeProxyedURL(FacesContext facesContext,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation[] contentInformationRef,
            IPatternRule rule, String url) {

        Map<String, Object> requestMap = facesContext.getExternalContext()
                .getRequestMap();

        Map<IPatternRule, String> rulesRequestMap = (Map<IPatternRule, String>) requestMap
                .get(PATTERN_RULE_PROPERTY);
        if (rulesRequestMap == null) {
            rulesRequestMap = new HashMap<ResourceProxyHandlerImpl.IPatternRule, String>();
            requestMap.put(PATTERN_RULE_PROPERTY, rulesRequestMap);
        }

        String baseURL = rulesRequestMap.get(rule);

        if (baseURL == NO_BASE_URL_STRING) {
            return null;
        }
        if (baseURL == DISABLED_URL) {
            return DISABLED_URL;
        }

        if (baseURL == null) {
            baseURL = computeRequestURLBase(facesContext, contentAccessor,
                    contentInformationRef, rule);
            if (baseURL == null) {
                rulesRequestMap.put(rule, NO_BASE_URL_STRING);

                return null;
            }
            if (baseURL == DISABLED_URL) {
                rulesRequestMap.put(rule, DISABLED_URL);

                return baseURL;
            }
            if (baseURL.endsWith("/")) {
                baseURL = baseURL.substring(0, baseURL.length() - 1);
            }

            rulesRequestMap.put(rule, baseURL);
        }

        StringAppender sa = new StringAppender(baseURL, url.length() + 2);

        if (url.startsWith("/") == false) {
            sa.append('/');
        }

        sa.append(url);

        String converted = sa.toString();

        return converted;
    }

    protected String computeRequestURLBase(FacesContext facesContext,
            IContentAccessor contentAccessor,
            IGeneratedResourceInformation[] contentInformationRef,
            IPatternRule rule) {

        String url = rule.getBaseURI();

        if (DISABLED_URL.equals(url)) {
            return DISABLED_URL;
        }

        if (url.indexOf('[') < 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set URL base to pattern '" + url
                        + "'. (no replacement)");
            }

            return url;
        }

        char chs[] = url.toCharArray();

        StringAppender sa = new StringAppender(url.length() * 2);
        int first = 0;

        for (int i = 0; i < chs.length; i++) {
            char c = chs[i];

            if (c != '[') {
                continue;
            }

            if (first < i) {
                sa.append(chs, first, i - first);

                first = i;
            }

            int j = i + 1;
            for (i = j; i < chs.length; i++) {
                c = chs[i];
                if (c == ']') {
                    break;
                }
            }

            if (i == chs.length) {
                // PROBLEME !!!!

                throw new FacesException("Invalid pattern '" + url + "'.");
            }

            String keyword = url.substring(j, i);

            IKeywordValue keywordValue = keywordValueMap.get(keyword);
            if (keywordValue != null) {
                String ret = keywordValue.getValue(facesContext, keyword);
                if (ret != null) {
                    sa.append(ret);
                }
            }

            first = i + 1;
        }

        if (first == 0) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set URL base to pattern '" + url + "'.");
            }

        } else if (first < chs.length) {
            sa.append(chs, first, chs.length - first);
        }

        String prefix = sa.toString();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Set URL base to '" + prefix + "'.");
        }

        return prefix;
    }

    protected void fillKeywordValues(RcfacesContext rcfacesContext,
            Map<String, IKeywordValue> replaceMap) {

        replaceMap.put(PROTOCOL_KEYWORD, new IKeywordValue() {

            public String getValue(FacesContext facesContext, String keyword) {
                Object request = facesContext.getExternalContext().getRequest();
                if ((request instanceof ServletRequest) == false) {
                    return null;
                }

                ServletRequest servletRequest = (ServletRequest) request;

                String protocol = servletRequest.getScheme();
                if (protocol != null && protocol.length() > 0) {
                    return protocol;
                }

                return null;
            }
        });

        replaceMap.put(LOCAL_NAME_KEYWORD, new IKeywordValue() {

            public String getValue(FacesContext facesContext, String keyword) {
                Object request = facesContext.getExternalContext().getRequest();
                if ((request instanceof ServletRequest) == false) {
                    return null;
                }

                ServletRequest servletRequest = (ServletRequest) request;

                String localName = servletRequest.getServerName();
                if (localName != null && localName.length() > 0) {
                    return localName;
                }

                return null;
            }
        });

        replaceMap.put(LOCAL_ADDR_KEYWORD, new IKeywordValue() {

            public String getValue(FacesContext facesContext, String keyword) {
                Object request = facesContext.getExternalContext().getRequest();
                if ((request instanceof ServletRequest) == false) {
                    return null;
                }

                ServletRequest servletRequest = (ServletRequest) request;

                String localAddr = servletRequest.getServerName();
                if (localAddr != null && localAddr.length() > 0) {
                    InetAddress inetAddress;
                    try {
                        inetAddress = InetAddress.getByName(localAddr);

                    } catch (UnknownHostException e) {
                        LOG.error("Can not getByName '" + localAddr + "'.", e);
                        inetAddress = null;
                    }

                    if (inetAddress != null) {
                        String address = inetAddress.getHostAddress();
                        if (address != null) {
                            return address;
                        }
                    }
                }

                return null;
            }
        });

        replaceMap.put(LOCAL_PORT_KEYWORD, new IKeywordValue() {

            public String getValue(FacesContext facesContext, String keyword) {
                Object request = facesContext.getExternalContext().getRequest();
                if ((request instanceof ServletRequest) == false) {
                    return null;
                }

                ServletRequest servletRequest = (ServletRequest) request;

                int port = servletRequest.getServerPort();
                if (port > 0) {
                    return String.valueOf(port);
                }

                return null;
            }
        });

        replaceMap.put(OPTIONAL_LOCAL_PORT_KEYWORD, new IKeywordValue() {

            public String getValue(FacesContext facesContext, String keyword) {
                Object request = facesContext.getExternalContext().getRequest();
                if ((request instanceof ServletRequest) == false) {
                    return null;
                }

                ServletRequest servletRequest = (ServletRequest) request;

                String protocol = servletRequest.getScheme();
                int port = servletRequest.getServerPort();

                if ("http".equalsIgnoreCase(protocol)
                        && port == DEFAULT_HTTP_PORT) {

                    return null;

                } else if ("https".equalsIgnoreCase(protocol)
                        && port == DEFAULT_HTTPS_PORT) {
                    return null;
                }

                if (port > 0) {
                    return ":" + port;
                }

                return null;
            }
        });

        replaceMap.put(CONTEXT_PATH_KEYWORD, new IKeywordValue() {
            public String getValue(FacesContext facesContext, String keyword) {
                String requestContextPath = facesContext.getExternalContext()
                        .getRequestContextPath();
                if (requestContextPath != null) {
                    if (requestContextPath.length() > 0
                            && requestContextPath.charAt(0) == '/') {
                        return requestContextPath.substring(1);
                    }
                    return requestContextPath;
                }

                return null;
            }
        });
    }

    private void loadRules(ExternalContext externalContext, String rulesPath) {
        StringTokenizer st = new StringTokenizer(rulesPath, ",");

        List<IPatternRule> rules = new ArrayList<IPatternRule>(32);

        Digester digester = new Digester();
        fillDigesterRules(digester, rules);

        for (; st.hasMoreTokens();) {
            String tok = st.nextToken().trim();

            InputStream ins = externalContext.getResourceAsStream(tok);
            if (ins == null) {
                LOG.error("Can not open rules resource '" + tok + "'.");
                continue;
            }

            try {
                LOG.info("Open rules resource '" + tok + "'.");

                digester.parse(ins);

            } catch (Exception ex) {
                LOG.error("Can not parse rules from '" + tok + "'.", ex);

            } finally {
                try {
                    ins.close();
                } catch (IOException e) {
                    LOG.error(e);
                }
            }
        }

        if (rules.isEmpty() == false) {
            this.rules = rules.toArray(new IPatternRule[rules.size()]);
        }
    }

    protected void fillDigesterRules(Digester digester,
            final List<IPatternRule> rules) {

        final Map<String, String> infos = new HashMap<String, String>();

        digester.addRule("rules/rule", new Rule() {

            @Override
            public void end(String namespace, String name) {

                String url = infos.get("url");
                Map<String, Object> attributes = null;
                String pattern = infos.get("pattern");
                String regexp = infos.get("regexp");
                infos.clear();

                if (url == null) {
                    LOG.error("No URL defined for rule pattern='" + pattern
                            + "' or regexp='" + regexp + "'.");
                    return;
                }
                url = url.trim();

                if (DISABLED_URL.equalsIgnoreCase(url)) {
                    url = DISABLED_URL;
                }

                if (pattern != null) {
                    pattern = pattern.trim();

                    if (pattern.indexOf('*') >= 0) {
                        if (pattern.equals("*")) {
                            rules.add(new DefaultPatternRule(url, attributes));
                            return;
                        }

                        if (pattern.indexOf('*') == pattern.length() - 1) {
                            rules.add(new StartsWithRule(url, attributes,
                                    pattern));
                            return;
                        }

                        if (pattern.lastIndexOf('*') == 0) {
                            rules.add(new EndsWithRule(url, attributes, pattern));
                            return;
                        }

                        throw new FacesException(
                                "Invalid expression ! (Use regexp tag to declare more complex pattern) '"
                                        + pattern + "'.");
                    }

                    rules.add(new BasicPatternRule(url, attributes, pattern));
                    return;
                }

                if (regexp != null) {
                    regexp = regexp.trim();

                    rules.add(new RegExpPatternRule(url, attributes, regexp));
                    return;
                }

            }

        });
        digester.addRule("rules/rule/pattern", new Rule() {

            @Override
            public void body(String namespace, String name, String text) {
                infos.put("pattern", text);
            }
        });
        digester.addRule("rules/rule/regexp", new Rule() {

            @Override
            public void body(String namespace, String name, String text) {
                infos.put("regexp", text);
            }
        });
        digester.addRule("rules/rule/url", new Rule() {

            @Override
            public void body(String namespace, String name, String text) {
                infos.put("url", text);
            }
        });
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    protected interface IKeywordValue {
        String getValue(FacesContext facesContext, String keyword);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    protected static interface IPatternRule {
        String getPattern();

        String getBaseURI();

        Map<String, Object> listAttributes();

        boolean acceptURL(String url);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    protected static abstract class AbstractPatternRule implements IPatternRule {
        protected final String baseURI;

        protected final Map<String, Object> attributes;

        public AbstractPatternRule(String baseURI,
                Map<String, Object> attributes) {
            this.baseURI = baseURI;

            if (attributes == null) {
                attributes = Collections.emptyMap();
            }
            this.attributes = attributes;
        }

        public String getBaseURI() {
            return baseURI;
        }

        public Map<String, Object> listAttributes() {
            return attributes;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((attributes == null) ? 0 : attributes.hashCode());
            result = prime * result
                    + ((baseURI == null) ? 0 : baseURI.hashCode());
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
            AbstractPatternRule other = (AbstractPatternRule) obj;
            if (attributes == null) {
                if (other.attributes != null)
                    return false;
            } else if (!attributes.equals(other.attributes))
                return false;
            if (baseURI == null) {
                if (other.baseURI != null)
                    return false;
            } else if (!baseURI.equals(other.baseURI))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "[AbstractPatternRule baseURI='" + baseURI
                    + "' attributes='" + attributes + "']";
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    protected static class BasicPatternRule extends AbstractPatternRule {
        protected final String pattern;

        public BasicPatternRule(String baseURI, Map<String, Object> attributes,
                String pattern) {
            super(baseURI, attributes);

            this.pattern = pattern;
        }

        public String getPattern() {
            return pattern;
        }

        @Override
        public int hashCode() {
            return pattern.hashCode();
        }

        public boolean acceptURL(String url) {
            return pattern.equals(url);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            BasicPatternRule other = (BasicPatternRule) obj;
            if (pattern == null) {
                if (other.pattern != null)
                    return false;
            } else if (!pattern.equals(other.pattern))
                return false;
            return true;
        }

        @Override
        public String toString() {
            return "[BasicRule pattern='" + pattern + "' baseURI='" + baseURI
                    + "' attributes='" + attributes + "']";
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    protected static class StartsWithRule extends BasicPatternRule {

        private final String localPattern;

        public StartsWithRule(String baseURI, Map<String, Object> attributes,
                String pattern) {
            super(baseURI, attributes, pattern);

            localPattern = pattern.substring(0, pattern.length() - 1);
        }

        @Override
        public boolean acceptURL(String url) {
            return url.startsWith(localPattern);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result
                    + ((localPattern == null) ? 0 : localPattern.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            StartsWithRule other = (StartsWithRule) obj;
            if (localPattern == null) {
                if (other.localPattern != null)
                    return false;
            } else if (!localPattern.equals(other.localPattern))
                return false;
            return true;
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    protected static class EndsWithRule extends BasicPatternRule {

        private final String localPattern;

        public EndsWithRule(String baseURI, Map<String, Object> attributes,
                String pattern) {
            super(baseURI, attributes, pattern);

            localPattern = pattern.substring(1);
        }

        @Override
        public boolean acceptURL(String url) {
            return url.endsWith(localPattern);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result
                    + ((localPattern == null) ? 0 : localPattern.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            EndsWithRule other = (EndsWithRule) obj;
            if (localPattern == null) {
                if (other.localPattern != null)
                    return false;
            } else if (!localPattern.equals(other.localPattern))
                return false;
            return true;
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    protected static class RegExpPatternRule extends BasicPatternRule {

        private final Pattern localPattern;

        public RegExpPatternRule(String baseURI,
                Map<String, Object> attributes, String regex) {
            super(baseURI, attributes, regex);

            localPattern = Pattern.compile(regex);
        }

        @Override
        public boolean acceptURL(String url) {
            Matcher m = localPattern.matcher(url);

            return m.matches();
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result
                    + ((localPattern == null) ? 0 : localPattern.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            RegExpPatternRule other = (RegExpPatternRule) obj;
            if (localPattern == null) {
                if (other.localPattern != null)
                    return false;
            } else if (!localPattern.equals(other.localPattern))
                return false;
            return true;
        }

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:26 $
     */
    protected static class DefaultPatternRule extends BasicPatternRule {

        public DefaultPatternRule(String baseURI, Map<String, Object> attributes) {
            super(baseURI, attributes, "*");
        }

        @Override
        public boolean acceptURL(String url) {
            return true;
        }
    }
}
