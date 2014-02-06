/*
 * $Id: LoadBundleRenderer.java,v 1.3 2013/11/13 12:53:30 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.component.StateHolder;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.converter.LocaleConverter;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.repository.IRepository.IFile;
import org.rcfaces.core.internal.tools.ContextTools;
import org.rcfaces.renderkit.html.component.LoadBundleComponent;
import org.rcfaces.renderkit.html.internal.AbstractHtmlRenderer;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.clientBundle.ClientResourceBundleServlet;
import org.rcfaces.renderkit.html.internal.clientBundle.IClientBundleRepository;
import org.rcfaces.renderkit.html.internal.javascript.IJavaScriptRepository;
import org.rcfaces.renderkit.html.internal.javascript.JavaScriptRepositoryServlet;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
 */
public class LoadBundleRenderer extends AbstractHtmlRenderer {

    private static final Log LOG = LogFactory.getLog(LoadBundleRenderer.class);

    private static final String BUNDLE_REQUIRED_CLASSES = "f_resourceBundle";

    private static final boolean DEFAULT_CLIENT_SIDE = true;

    private static final boolean DEFAULT_SERVER_SIDE = true;

    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        LoadBundleComponent loadClientBundleComponent = (LoadBundleComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        Locale locale = ContextTools.getUserLocale(facesContext);

        String baseName = loadClientBundleComponent.getBaseName(facesContext);
        String bundleName = loadClientBundleComponent
                .getBundleName(facesContext);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Reference baseName=" + baseName + " bundleName="
                    + bundleName);
        }

        boolean serverSide = false;
        boolean clientSide = false;

        String side = loadClientBundleComponent.getSide(facesContext);
        if (side == null) {
            serverSide = DEFAULT_SERVER_SIDE;
            clientSide = DEFAULT_CLIENT_SIDE;
            
        } else {
            StringTokenizer st = new StringTokenizer(side.toLowerCase(), ",");
            for (; st.hasMoreTokens();) {
                String token = st.nextToken().trim().toLowerCase();

                if ("server".equals(token)) {
                    serverSide = true;
                    continue;
                }

                if ("client".equals(token)) {
                    clientSide = true;
                    continue;
                }

                throw new IllegalArgumentException("Invalid side value '"
                        + token + "'.");
            }
        }

        if (serverSide) {

            Map<String, Object> scopeMap = null;

            String scope = loadClientBundleComponent
                    .getServerScope(facesContext);
            if (scope != null) {
                scope = scope.toLowerCase();

                if ("session".equals(scope)) {
                    scopeMap = facesContext.getExternalContext()
                            .getSessionMap();

                } else if ("application".equals(scope)) {
                    scopeMap = facesContext.getExternalContext()
                            .getApplicationMap();

                } else if ("request".equals(scope)) {
                    scopeMap = null;

                } else {
                    throw new IllegalArgumentException(
                            "Invalid serverScope value '" + scope + "'.");
                }
            }

            if (scopeMap == null) {
                scopeMap = facesContext.getExternalContext().getRequestMap();
            }

            scopeMap.put(bundleName, new BundleMap(baseName, locale));
        }

        if (clientSide) {

            IClientBundleRepository bundleRepository = ClientResourceBundleServlet
                    .getBundleRepository(facesContext);
            if (bundleRepository == null) {
                throw new WriterException(
                        "Client-Bundle engine is not initialized !", null,
                        loadClientBundleComponent);
            }

            JavaScriptRenderer.addRequires(htmlWriter, htmlWriter
                    .getHtmlComponentRenderContext().getHtmlRenderContext()
                    .getJavaScriptRenderContext(), null,
                    BUNDLE_REQUIRED_CLASSES, null, null);

            IJavaScriptRepository repository = JavaScriptRepositoryServlet
                    .getRepository(facesContext);

            IJavaScriptWriter jsWriter = InitRenderer.openScriptTag(htmlWriter);

            JavaScriptRenderContext.initializeJavaScript(jsWriter, repository,
                    true);

            jsWriter.writeCall("f_resourceBundle", "Load")
                    .writeString(bundleName).write(',').writeString(baseName)
                    .write(',');

            IFile file = bundleRepository.getFileByName(baseName);

            String bundleURI = file.getURI(jsWriter
                    .getJavaScriptRenderContext().getCriteria());

            String uri = htmlWriter.getHtmlComponentRenderContext()
                    .getHtmlRenderContext().getHtmlProcessContext()
                    .getAbsolutePath(bundleURI, true);
            jsWriter.writeString(uri);

            if (loadClientBundleComponent.isOverride(facesContext)) {
                jsWriter.write(',').writeBoolean(true);
            }

            jsWriter.writeln(");");

            jsWriter.end();
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.3 $ $Date: 2013/11/13 12:53:30 $
     */
    public static class BundleMap implements Map, StateHolder, Externalizable {

        private static final long serialVersionUID = 2827938468480044306L;

        private ResourceBundle bundle;

        private List<String> values;

        private Set<String> keys;

        private Set<Map.Entry<String, Object>> entrySet;

        private String baseName;

        private Locale locale;

        public BundleMap() {
        }

        public BundleMap(String baseName, Locale locale) {
            setProperties(baseName, locale);
        }

        protected void setProperties(String baseName, Locale locale) {
            this.baseName = baseName;
            this.locale = locale;

            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Get resourceBundle name='" + baseName
                            + "' locale='" + locale + "'.");
                }

                ClassLoader classLoader = Thread.currentThread()
                        .getContextClassLoader();

                this.bundle = ResourceBundle.getBundle(baseName, locale,
                        classLoader);

            } catch (MissingResourceException ex) {
                LOG.error("Resource bundle '" + baseName
                        + "' could not be found. (locale " + locale + ")", ex);

                throw new FacesException("Resource bundle '" + baseName
                        + "' could not be found. (locale " + locale + ")", ex);
            }
        }

        // Optimized methods

        public Object get(Object key) {
            try {
                return bundle.getObject(key.toString());

            } catch (Throwable th) {
                return "MISSING: " + key + " :MISSING";
            }
        }

        public boolean isEmpty() {
            return keySet().isEmpty();
        }

        public boolean containsKey(Object key) {
            return keySet().contains(key);
        }

        public Collection<String> values() {
            if (values != null) {
                return values;
            }

            values = new ArrayList<String>(size());
            for (Iterator it = keySet().iterator(); it.hasNext();) {
                String key = (String) it.next();

                String value = bundle.getString(key);
                values.add(value);
            }

            return values;
        }

        public int size() {
            return keySet().size();
        }

        public boolean containsValue(Object value) {
            return values().contains(value);
        }

        public Set entrySet() {
            if (entrySet != null) {
                return entrySet;
            }

            entrySet = new HashSet<Map.Entry<String, Object>>(size());

            for (Iterator<String> it = keySet().iterator(); it.hasNext();) {
                final String key = it.next();

                entrySet.add(new Map.Entry<String, Object>() {
 
                    public String getKey() {
                        return key;
                    }

                    public Object getValue() {
                        return bundle.getObject(key);
                    }

                    public Object setValue(Object value) {
                        throw new UnsupportedOperationException(this.getClass()
                                .getName() + " UnsupportedOperationException");
                    }
                });
            }

            return entrySet;
        }

        public Set<String> keySet() {
            if (keys != null) {
                return keys;
            }

            keys = new HashSet<String>();
            for (Enumeration<String> en = bundle.getKeys(); en
                    .hasMoreElements();) {
                keys.add(en.nextElement());
            }

            return keys;
        }

        // Unsupported methods

        public Object remove(Object key) {
            throw new UnsupportedOperationException(this.getClass().getName()
                    + " UnsupportedOperationException");
        }

        public void putAll(Map t) {
            throw new UnsupportedOperationException(this.getClass().getName()
                    + " UnsupportedOperationException");
        }

        public Object put(Object key, Object value) {
            throw new UnsupportedOperationException(this.getClass().getName()
                    + " UnsupportedOperationException");
        }

        public void clear() {
            throw new UnsupportedOperationException(this.getClass().getName()
                    + " UnsupportedOperationException");
        }

        public boolean isTransient() {
            return false;
        }

        public void setTransient(boolean newTransientValue) {
        }

        public void restoreState(FacesContext context, Object state) {
            Object ret[] = (Object[]) state;

            String baseName = (String) ret[0];
            Locale locale = (Locale) LocaleConverter.SINGLETON.getAsObject(
                    context, null, (String) ret[1]);

            setProperties(baseName, locale);
        }

        public Object saveState(FacesContext context) {
            return new Object[] {
                    baseName,
                    LocaleConverter.SINGLETON
                            .getAsString(context, null, locale) };
        }

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            String baseName = (String) in.readObject();
            Locale locale = (Locale) in.readObject();

            setProperties(baseName, locale);
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(baseName);
            out.writeObject(locale);
        }

    }
}
