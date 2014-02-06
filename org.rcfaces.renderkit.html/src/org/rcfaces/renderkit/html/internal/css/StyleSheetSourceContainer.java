/*
 * $Id: StyleSheetSourceContainer.java,v 1.3 2013/11/13 12:53:30 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.css;

import java.io.IOException;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.renderkit.AbstractRendererTypeFactory;
import org.rcfaces.core.internal.repository.SourceContainer;
import org.rcfaces.renderkit.html.internal.agent.IUserAgent;
import org.rcfaces.renderkit.html.internal.agent.IUserAgentRules;
import org.rcfaces.renderkit.html.internal.agent.UserAgentRuleTools;
import org.rcfaces.renderkit.html.internal.renderer.HtmlRendererTypeFactory;
import org.xml.sax.Attributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
 */
public class StyleSheetSourceContainer extends SourceContainer<IUserAgent> {

    private static final Log LOG = LogFactory
            .getLog(StyleSheetSourceContainer.class);

    private static final String CSS_REPOSITORY_TYPE = "css";

    private static final IUserAgent NO_PARAMETER = new IUserAgent() {

        public BrowserType getBrowserType() {
            throw new IllegalStateException("NO PARAMETER");
        }

        public Integer getMajorVersion() {
            throw new IllegalStateException("NO PARAMETER");
        }

        public Integer getMinorVersion() {
            throw new IllegalStateException("NO PARAMETER");
        }

        public Integer getReleaseVersion() {
            throw new IllegalStateException("NO PARAMETER");
        }

        public void textForm(StringBuilder sb) {
            throw new IllegalStateException("NO PARAMETER");
        }

        public Object saveState(FacesContext context) {
            return null;
        }

        public void restoreState(FacesContext context, Object state) {
        }

        public boolean isTransient() {
            return false;
        }

        public void setTransient(boolean newTransientValue) {
        }

        public IUserAgent reduce() {
            return this;
        }

        public IUserAgent reduceByBrowserType() {
            return this;
        }

        public boolean equalsType(IUserAgent clientBrowser) {
            return false;
        }

    };

    private final HtmlRendererTypeFactory factory;

    public StyleSheetSourceContainer(ServletConfig config, String module,
            String charSet, boolean canUseGzip, boolean canUseETag,
            boolean canUseHash, String repositoryVersion, String renderKitId)
            throws ServletException {
        super(config, CSS_REPOSITORY_TYPE, Collections.singleton(module),
                charSet, canUseGzip, canUseETag, canUseHash,
                EXTERNAL_REPOSITORIES_CONFIG_NAME, repositoryVersion);

        RcfacesContext rcfacesContext = getRcfacesContext();

        factory = (HtmlRendererTypeFactory) AbstractRendererTypeFactory.get(
                rcfacesContext, renderKitId);
    }

    protected StringAppender preConstructBuffer(
            BasicParameterizedContent parameterizedBuffer, StringAppender buffer) {

        buffer.append("@charset \"").append(getCharSet()).append("\";\n");

        return buffer;
    }

    @Override
    protected IUserAgent noParameter() {
        return NO_PARAMETER;
    }

    protected SourceFile createSourceFile(String baseDirectory, String body,
            Attributes attributes) {

        UserAgentSourceFile sf = (UserAgentSourceFile) super.createSourceFile(
                baseDirectory, body, attributes);
        if (sf == null) {
            return null;
        }

        String ua = attributes.getValue("userAgent");
        if (ua != null) {
            IUserAgentRules userAgentRules = UserAgentRuleTools
                    .constructUserAgentRules(ua, true, true, 0,
                            factory.listFeaturesByNames());
            sf.setUserAgent(userAgentRules);
        }

        return sf;
    }

    protected SourceFile newSourceFile() {
        return new UserAgentSourceFile();
    }

    protected void addURLContent(URLConnection urlConnection,
            StringAppender buffer) throws IOException {

        StringAppender defferedBuffer = new StringAppender(32000);

        super.addURLContent(urlConnection, defferedBuffer);

        String db = defferedBuffer.toString();
        int idx = db.indexOf('\n');
        if (idx > 0) {
            String firstLine = db.substring(0, idx).toLowerCase().trim();
            if (firstLine.startsWith("@charset")) {
                buffer.append(defferedBuffer, idx + 1, defferedBuffer.length()
                        - idx - 1);

                buffer.append('\n');
                return;
            }
        }

        buffer.append(defferedBuffer);
        buffer.append('\n');
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    protected class UserAgentSourceFile extends SourceFile {
        private IUserAgentRules userAgent;

        public IUserAgentRules getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(IUserAgentRules userAgent) {
            this.userAgent = userAgent;
        }

        public String toString() {
            return "[UserAgentSourceFile fileName='" + getFileName()
                    + "' userAgentRules='" + getUserAgent() + "']";
        }

    }

    protected IParameterizedContent<IUserAgent> createParameterizedContent(
            IUserAgent parameter) {
        return new UserAgentContent(parameter);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    protected class UserAgentContent extends BasicParameterizedContent {

        public UserAgentContent(IUserAgent parameter) {
            super(parameter);
        }

        protected List<SourceFile> filterFiles(List<SourceFile> files) {

            List<SourceFile> l = super.filterFiles(files);

            if (parameter == noParameter()) {
                for (Iterator it = l.iterator(); it.hasNext();) {
                    UserAgentSourceFile sf = (UserAgentSourceFile) it.next();

                    IUserAgentRules ua = sf.getUserAgent();

                    if (ua == null) {
                        continue;
                    }

                    it.remove();
                }

                return l;
            }

            for (Iterator it = l.iterator(); it.hasNext();) {
                UserAgentSourceFile sf = (UserAgentSourceFile) it.next();

                IUserAgentRules ua = sf.getUserAgent();

                if (ua == null || ua.accepts(parameter)) {
                    continue;
                }

                it.remove();
            }

            return l;
        }
    }
}
