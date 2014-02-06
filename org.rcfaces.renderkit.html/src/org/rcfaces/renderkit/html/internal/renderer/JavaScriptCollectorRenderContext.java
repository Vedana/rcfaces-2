/*
 * $Id: JavaScriptCollectorRenderContext.java,v 1.4 2013/12/11 10:19:48 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.IAccessKeyCapability;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.internal.contentAccessor.BasicGenerationResourceInformation;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.ICompositeContentAccessorHandler;
import org.rcfaces.core.internal.contentAccessor.ICompositeContentAccessorHandler.ICompositeURLDescriptor;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.contentAccessor.IGenerationResourceInformation;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.repository.IRepository;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.script.AbstractScriptContentAccessorHandler;
import org.rcfaces.core.internal.script.GeneratedScriptInformation;
import org.rcfaces.core.internal.script.IScriptContentAccessorHandler;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.core.lang.OrderedSet;
import org.rcfaces.renderkit.html.internal.AbstractHtmlRenderer;
import org.rcfaces.renderkit.html.internal.AbstractHtmlWriter;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.AbstractJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlElements;
import org.rcfaces.renderkit.html.internal.IHtmlProcessContext;
import org.rcfaces.renderkit.html.internal.IHtmlRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptComponentRenderer;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.IObjectLiteralWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptEnableModeImpl;
import org.rcfaces.renderkit.html.internal.util.LazyCharArrayWriter;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/12/11 10:19:48 $
 */
public class JavaScriptCollectorRenderContext extends
        AbstractJavaScriptRenderContext {
    private static final Log LOG = LogFactory
            .getLog(JavaScriptCollectorRenderContext.class);

    private static final String DISABLE_VINIT_SEARCH_PROPERTY = "camelia.jsCollector.disable.vinit";

    private static final boolean PERFORM_ACCESSKEYS = false;

    // Le focus devrait faire l'affaire

    private final Set<Object> components = new OrderedSet<Object>();

    // private final List scriptsToInclude = new ArrayList();

    // private final List rawsToInclude = new ArrayList();

    private final boolean mergeScripts;

    private static final Integer UTF8_charset = new Integer(0);

    private static final Map<String, Integer> charSets = new HashMap<String, Integer>(
            8);

    private static final String MERGE_DEFAULT_CHARSET = "UTF-8";
    static {
        charSets.put("utf8", UTF8_charset);
        charSets.put("utf-8", UTF8_charset);
        charSets.put("utf_8", UTF8_charset);

        for (int i = 1; i < 16; i++) {
            Integer intISO8859x = new Integer(i);

            charSets.put("iso8859" + i, intISO8859x);
            charSets.put("iso8859-" + i, intISO8859x);
            charSets.put("iso8859_" + i, intISO8859x);
            charSets.put("iso-8859-" + i, intISO8859x);
            charSets.put("8859-" + i, intISO8859x);
            charSets.put("8859_" + i, intISO8859x);
        }
    }

    public JavaScriptCollectorRenderContext(FacesContext facesContext,
            boolean mergeScripts) {
        super(facesContext);

        this.mergeScripts = mergeScripts;
    }

    protected JavaScriptCollectorRenderContext(
            AbstractJavaScriptRenderContext parent, boolean mergeScripts) {
        super(parent);

        this.mergeScripts = mergeScripts;
    }

    public IJavaScriptRenderContext createChild() {
        return new JavaScriptCollectorRenderContext(this, mergeScripts);
    }

    public void declareLazyJavaScriptRenderer(IHtmlWriter writer) {
        // Ce sont des lazys mais ils n'ont pas besoin d'être initialisés

        // components.add(writer.getComponentRenderContext().getComponentClientId
        // ());
    }

    public boolean isJavaScriptRendererDeclaredLazy(IHtmlWriter writer) {
        return false;
    }

    public void initializeJavaScriptComponent(IJavaScriptWriter writer)
            throws WriterException {
        writeJsInitComponent(writer);

        IHtmlComponentRenderContext componentRenderContext = writer
                .getHtmlComponentRenderContext();

        if (componentRenderContext.hasClientDatas(true)) {
            encodeClientData(writer);
        }
    }

    public void initializePendingComponents(IJavaScriptWriter writer)
            throws WriterException {
        // Rien

    }

    public void pushChild(IJavaScriptRenderContext javaScriptRenderContext,
            IHtmlWriter htmlWriter) throws WriterException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Push child " + javaScriptRenderContext);
        }

        if (RcfacesContext.isJSF1_2() || RcfacesContext.isJSF2_0()) {
            // Nous sommes en asyncRenderMode= tree
            return;
        }

        flushComponents(htmlWriter, true);
    }

    public void popChild(IJavaScriptRenderContext javaScriptRenderContext,
            IHtmlWriter htmlWriter) throws WriterException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Pop child " + javaScriptRenderContext);
        }

        if (parent != null && (RcfacesContext.isJSF1_2() || RcfacesContext.isJSF2_0())
                && (parent instanceof JavaScriptCollectorRenderContext)) {
            // Nous sommes en asyncRenderMode= tree

            JavaScriptCollectorRenderContext jsParentRenderContext = ((JavaScriptCollectorRenderContext) parent);

            jsParentRenderContext.components.addAll(components);

            int mode = ((JavaScriptEnableModeImpl) htmlWriter
                    .getJavaScriptEnableMode()).getMode();

            String currentId = htmlWriter.getComponentRenderContext()
                    .getComponentClientId();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Add popped component '" + currentId + "'  mode=0x"
                        + Integer.toHexString(mode));
            }

            jsParentRenderContext.components.add(new ComponentId(currentId,
                    mode, null, null));

            if (waitingRequiredClassesNames != null
                    && waitingRequiredClassesNames.isEmpty() == false) {
                jsParentRenderContext.waitingRequiredClassesNames
                        .addAll(waitingRequiredClassesNames);
            }
            return;
        }

        flushComponents(htmlWriter, false);
    }

    protected IJavaScriptWriter createJavaScriptWriter(
            final IHtmlWriter writer,
            final IJavaScriptComponentRenderer javaScriptComponent)
            throws WriterException {

        final LazyCharArrayWriter bufferedWriter = new LazyCharArrayWriter(256);
        components.add(bufferedWriter);

        IJavaScriptWriter javaScriptWriter = new AbstractJavaScriptWriter() {

            private String componentVarName;

            private boolean initialized;

            protected String convertSymbol(String className, String memberName) {
                return JavaScriptCollectorRenderContext.this.convertSymbol(
                        className, memberName);
            }

            public String allocateString(String string) throws WriterException {
                if (string == null) {
                    return null;
                }

                boolean ret[] = new boolean[1];

                String varId = JavaScriptCollectorRenderContext.this
                        .allocateString(string, ret);
                if (ret[0] == false) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("String '" + string
                                + "' is already setted to var '" + varId + "'.");
                    }

                    return varId;
                }

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Allocate string '" + string + "' to var '"
                            + varId + "'.");
                }

                write("var ").write(varId).write("=").writeString(string)
                        .writeln(";");

                return varId;
            }

            public void end() throws WriterException {

                if (initialized && javaScriptComponent != null) {
                    javaScriptComponent.releaseJavaScript(this);
                }

                bufferedWriter.close();
                if (bufferedWriter.size() == 0) {
                    components.remove(bufferedWriter);
                }

                JavaScriptEnableModeImpl js = (JavaScriptEnableModeImpl) writer
                        .getJavaScriptEnableMode();

                int mode = js.getMode();

                if (initialized == false) {
                    String accessKey = null;

                    if ((mode & JavaScriptEnableModeImpl.ONACCESSKEY) > 0) {
                        UIComponent component = getWriter()
                                .getComponentRenderContext().getComponent();
                        if (component instanceof IAccessKeyCapability) {
                            accessKey = ((IAccessKeyCapability) component)
                                    .getAccessKey();
                        }
                    }

                    String subComponents[] = null;
                    if ((mode & JavaScriptEnableModeImpl.ONFOCUS) > 0) {
                        subComponents = ((AbstractHtmlWriter) getWriter())
                                .listSubFocusableComponents();
                    }

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Add component '"
                                + writer.getComponentRenderContext()
                                        .getComponentClientId() + "'  mode=0x"
                                + Integer.toHexString(js.getMode())
                                + " accessKey='" + accessKey
                                + "' subComponents=" + subComponents);
                    }

                    components.add(new ComponentId(
                            writer.getComponentRenderContext()
                                    .getComponentClientId(), js.getMode(),
                            accessKey, subComponents));

                    return;
                }

                int passiveModes = JavaScriptEnableModeImpl.PASSIVE_MASK & mode;

                // On mode initialisé, mais y a t-il des modes (
                if (passiveModes > 0) {

                    components.add(new ComponentId(
                            writer.getComponentRenderContext()
                                    .getComponentClientId(), passiveModes,
                            null, null));

                    return;
                }
            }

            public IJavaScriptWriter ensureInitialization()
                    throws WriterException {
                return this;
            }

            public String getComponentVarName() {
                if (componentVarName != null) {
                    return componentVarName;
                }

                componentVarName = getJavaScriptRenderContext()
                        .allocateVarName();

                return componentVarName;
            }

            public void setComponentVarName(String varName) {
                this.componentVarName = varName;
            }

            public FacesContext getFacesContext() {
                return writer.getComponentRenderContext().getFacesContext();
            }

            public IHtmlComponentRenderContext getHtmlComponentRenderContext() {
                return writer.getHtmlComponentRenderContext();
            }

            public IJavaScriptRenderContext getJavaScriptRenderContext() {
                return JavaScriptCollectorRenderContext.this;
            }

            public IHtmlWriter getWriter() {
                return writer;
            }

            public boolean isOpened() {
                return true;
            }

            public IJavaScriptWriter write(String string)
                    throws WriterException {
                isInitialized();

                char ch[] = string.toCharArray();

                bufferedWriter.write(ch, 0, ch.length);
                return this;
            }

            public IJavaScriptWriter write(char c) throws WriterException {
                isInitialized();
                bufferedWriter.write(c);

                return this;
            }

            public IJavaScriptWriter writeRaw(char[] dst, int pos, int length)
                    throws WriterException {
                isInitialized();

                bufferedWriter.write(dst, pos, length);

                return this;
            }

            public IComponentRenderContext getComponentRenderContext() {
                return writer.getComponentRenderContext();
            }

            public String getResponseCharacterEncoding() {
                return "UTF-8";
            }

            protected void isInitialized() throws WriterException {
                if (initialized) {
                    return;
                }
                initialized = true;

                javaScriptComponent.initializeJavaScriptComponent(this);
            }
        };

        return javaScriptWriter;
    }

    public void releaseComponentJavaScript(IJavaScriptWriter jsWriter,
            boolean sendComplete, AbstractHtmlRenderer htmlComponentRenderer)
            throws WriterException {

        if (sendComplete) {
            if (LOG_INTERMEDIATE_PROFILING.isTraceEnabled()) {
                jsWriter.writeCall("f_core", "Profile").writeln(
                        "false,\"javascript.completeComponent\");");
            }

            jsWriter.writeMethodCall("f_completeComponent").writeln(");");

            if (LOG_INTERMEDIATE_PROFILING.isTraceEnabled()) {
                jsWriter.writeCall("f_core", "Profile").writeln(
                        "true,\"javascript.completeComponent\");");
            }
        }
    }

    /*
     * public void addWaitingRequiredClassName(IClass clazz) { if (parent !=
     * null) { parent.addWaitingRequiredClassName(clazz); return; }
     * 
     * super.addWaitingRequiredClassName(clazz); }
     */

    public boolean isRequiresPending() {
        return false;
    }

    private void flushComponents(IHtmlWriter htmlWriter, boolean beginRender)
            throws WriterException {

        FacesContext facesContext = htmlWriter.getComponentRenderContext()
                .getFacesContext();

        List<Script> scripts = null;
        List<Raw> raws = null;

        for (Iterator it = components.iterator(); it.hasNext();) {
            Object object = it.next();

            if (object instanceof Script) {
                if (scripts == null) {
                    scripts = new ArrayList<Script>(8);
                }
                scripts.add((Script) object);

                it.remove();
                continue;
            }

            if (object instanceof Raw) {
                if (raws == null) {
                    raws = new ArrayList<Raw>(8);
                }
                raws.add((Raw) object);

                it.remove();
                continue;
            }
        }

        boolean isProfilerOn = isProfilerOn(htmlWriter);
        int profilerId = 0;
        boolean logProfiling = false;
        boolean logIntermediateProfiling = false;

        if (isProfilerOn) {
            if (LOG_INTERMEDIATE_PROFILING.isInfoEnabled()) {
                logProfiling = true;
            }
            if (LOG_INTERMEDIATE_PROFILING.isTraceEnabled()) {
                logIntermediateProfiling = true;
            }
        }

        IRepository.IFile filesToRequire[] = computeFilesToRequire();

        if (filesToRequire.length > 0 || scripts != null) {
            IJavaScriptWriter jsWriter = InitRenderer.openScriptTag(htmlWriter);

            initializeJavaScript(jsWriter, getRepository(), false);

            if (false && logProfiling) {
                jsWriter.writeCall("f_core", "Profile").writeln(
                        "false,\"javascriptCollector.includes\");");
            }

            if (filesToRequire.length > 0) {
                String cameliaClassLoader = convertSymbol("f_classLoader",
                        "_rcfacesClassLoader");

                jsWriter.writeCall(cameliaClassLoader, "f_requiresBundle");

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Write javascript dependencies: "
                            + Arrays.asList(filesToRequire));
                }

                ICriteria criteria = jsWriter.getJavaScriptRenderContext()
                        .getCriteria();

                for (int i = 0; i < filesToRequire.length; i++) {
                    IRepository.IFile file = filesToRequire[i];

                    if (i > 0) {
                        jsWriter.write(',');
                    }

                    String fileURI = file.getURI(criteria);
                    if (fileURI == null) {
                        throw new NullPointerException(
                                "Can not get URI of file '" + file
                                        + "' criteria='" + criteria + "'");
                    }
                    jsWriter.writeString(fileURI);
                }
                jsWriter.writeln(");");
            }

            if (false && logProfiling) {
                jsWriter.writeCall("f_core", "Profile").writeln(
                        "true,\"javascriptCollector.includes\");");
            }

            jsWriter.end();

            if (scripts != null) {
                includesScript(htmlWriter, scripts);
            }
        }

        IJavaScriptWriter jsWriter = null;
        if (isJavaScriptInitialized(facesContext) == false) {
            if (jsWriter == null) {
                jsWriter = InitRenderer.openScriptTag(htmlWriter);
            }

            initializeJavaScript(jsWriter, getRepository(), true);
        }

        if (logProfiling) {
            if (jsWriter == null) {
                jsWriter = InitRenderer.openScriptTag(htmlWriter);
            }

            jsWriter.writeCall("f_core", "Profile").writeln(
                    "false,\"javascriptCollector\");");
        }

        ExternalContext externalContext = facesContext.getExternalContext();
        Map<String, Object> requestMap = externalContext.getRequestMap();

        if (requestMap.put(DISABLE_VINIT_SEARCH_PROPERTY, Boolean.TRUE) == null) {
            if (jsWriter == null) {
                jsWriter = InitRenderer.openScriptTag(htmlWriter);
            }

            jsWriter.writeln("window._rcfacesDisableInitSearch=true");
        }

        if (raws != null) {
            if (jsWriter == null) {
                jsWriter = InitRenderer.openScriptTag(htmlWriter);
            }

            if (logProfiling) {
                jsWriter.writeCall("f_core", "Profile")
                        .writeNull()
                        .writeln(
                                ",\"javascriptCollector.raws(" + (raws.size())
                                        + ")\");");
            }

            int idx = 1;
            for (Iterator it = raws.iterator(); it.hasNext(); idx++) {
                Raw raw = (Raw) it.next();

                String text = raw.getText();

                jsWriter.writeln(text);

                if (logIntermediateProfiling) {
                    jsWriter.writeCall("f_core", "Profile")
                            .writeNull()
                            .writeln(
                                    ",\"javascriptCollector.raws(" + idx + "/"
                                            + (raws.size()) + ")\");");
                }
            }
        }

        if (components.isEmpty() == false) {

            if (logProfiling) {
                assert jsWriter != null : "JsWriter is NULL";

                jsWriter.writeCall("f_core", "Profile")
                        .writeNull()
                        .writeln(
                                ",\"javascriptCollector.components("
                                        + (components.size()) + ")\");");
            }

            List<ComponentId> initializeIds = new ArrayList<ComponentId>(16);
            List<String> accessIds = new ArrayList<String>(16);
            List<String> messageIds = new ArrayList<String>(16);
            List<String> focusIds = new ArrayList<String>();
            List<String> submitIds = new ArrayList<String>(16);
            List<String> hoverIds = new ArrayList<String>(16);
            List<String> layoutIds = new ArrayList<String>(16);

            for (Iterator it = components.iterator(); it.hasNext();) {
                Object object = it.next();

                if (object instanceof ComponentId) {
                    ComponentId componentId = (ComponentId) object;

                    initializeIds.add(componentId);

                    if (logIntermediateProfiling) {
                        if (jsWriter == null) {
                            jsWriter = InitRenderer.openScriptTag(htmlWriter);
                        }

                        writeInitIds(jsWriter, initializeIds, beginRender,
                                focusIds, hoverIds, messageIds, accessIds,
                                submitIds, layoutIds);

                        jsWriter.writeCall("f_core", "Profile")
                                .writeNull()
                                .writeln(
                                        ",\"javascriptCollector.initIds(#"
                                                + (profilerId++) + ")\");");
                    }

                    continue;
                }

                if (initializeIds.isEmpty() == false) {
                    if (jsWriter == null) {
                        jsWriter = InitRenderer.openScriptTag(htmlWriter);
                    }

                    writeInitIds(jsWriter, initializeIds, beginRender,
                            focusIds, hoverIds, messageIds, accessIds,
                            submitIds, layoutIds);

                    if (logIntermediateProfiling) {
                        jsWriter.writeCall("f_core", "Profile")
                                .writeNull()
                                .writeln(
                                        ",\"javascriptCollector.initIds(#"
                                                + (profilerId++) + ")\");");
                    }
                }

                LazyCharArrayWriter writer = (LazyCharArrayWriter) object;
                if (writer.size() == 0) {
                    writer.reset();
                    continue;
                }

                String buffer = writer.toString().trim();
                writer.reset();

                if (buffer.length() < 1) {
                    continue;
                }

                if (jsWriter == null) {
                    jsWriter = InitRenderer.openScriptTag(htmlWriter);
                }

                assert jsWriter != null : "JsWriter is null !";

                jsWriter.writeln(buffer);

                if (logIntermediateProfiling) {
                    jsWriter.writeCall("f_core", "Profile")
                            .writeNull()
                            .writeln(
                                    ",\"javascriptCollector.buffer(#"
                                            + (profilerId++) + ")\");");
                }
            }

            if (initializeIds.isEmpty() == false) {
                if (jsWriter == null) {
                    jsWriter = InitRenderer.openScriptTag(htmlWriter);
                }

                writeInitIds(jsWriter, initializeIds, beginRender, focusIds,
                        hoverIds, messageIds, accessIds, submitIds, layoutIds);
            }

            if (accessIds.isEmpty() == false || messageIds.isEmpty() == false
                    || focusIds.isEmpty() == false
                    || submitIds.isEmpty() == false
                    || hoverIds.isEmpty() == false
                    || layoutIds.isEmpty() == false) {
                if (jsWriter == null) {
                    jsWriter = InitRenderer.openScriptTag(htmlWriter);
                }

                String cameliaClassLoader = jsWriter
                        .getJavaScriptRenderContext().convertSymbol(
                                "f_classLoader", "_rcfacesClassLoader");

                if (accessIds.isEmpty() == false) {
                    jsWriter.writeCall(cameliaClassLoader, "f_initOnAccessIds");

                    IObjectLiteralWriter objWriter = jsWriter
                            .writeObjectLiteral(false);
                    for (Iterator it = accessIds.iterator(); it.hasNext();) {

                        objWriter.writeProperty((String) it.next())
                                .writeString((String) it.next());
                    }
                    objWriter.end().writeln(");");
                }

                if (messageIds.isEmpty() == false) {
                    boolean first = true;
                    jsWriter.writeCall(cameliaClassLoader, "f_initOnMessage")
                            .write('[');
                    for (Iterator it = messageIds.iterator(); it.hasNext();) {

                        if (first) {
                            first = false;

                        } else {
                            jsWriter.write(',');
                        }

                        jsWriter.writeString((String) it.next());
                    }
                    jsWriter.writeln("]);");
                }

                if (focusIds.isEmpty() == false) {
                    jsWriter.writeCall(cameliaClassLoader, "f_initOnFocusIds");

                    IObjectLiteralWriter objWriter = jsWriter
                            .writeObjectLiteral(false);
                    for (Iterator it = focusIds.iterator(); it.hasNext();) {

                        objWriter.writeProperty((String) it.next()).writeInt(1);
                    }
                    objWriter.end();
                    jsWriter.writeln(");");
                }

                if (submitIds.isEmpty() == false) {
                    jsWriter.writeCall(cameliaClassLoader, "f_initOnSubmitIds")
                            .write('[');

                    boolean first = true;
                    for (Iterator it = submitIds.iterator(); it.hasNext();) {
                        if (first) {
                            first = false;
                        } else {
                            jsWriter.write(',');
                        }

                        jsWriter.writeString((String) it.next());
                    }
                    jsWriter.writeln("]);");
                }

                if (hoverIds.isEmpty() == false) {
                    jsWriter.writeCall(cameliaClassLoader, "f_initOnOverIds");

                    IObjectLiteralWriter objWriter = jsWriter
                            .writeObjectLiteral(false);
                    for (Iterator it = hoverIds.iterator(); it.hasNext();) {
                        objWriter.writeProperty((String) it.next()).writeInt(1);
                    }
                    objWriter.end();
                    jsWriter.writeln(");");
                }

                if (layoutIds.isEmpty() == false) {
                    boolean first = true;
                    jsWriter.writeCall(cameliaClassLoader, "f_initOnLayoutIds")
                            .write('[');
                    for (Iterator it = layoutIds.iterator(); it.hasNext();) {

                        if (first) {
                            first = false;

                        } else {
                            jsWriter.write(',');
                        }

                        jsWriter.writeString((String) it.next());
                    }
                    jsWriter.writeln("]);");
                }

            }

            if (hasMessagesPending(htmlWriter.getHtmlComponentRenderContext()
                    .getHtmlRenderContext())) {
                if (jsWriter == null) {
                    jsWriter = InitRenderer.openScriptTag(htmlWriter);
                }

                if (logProfiling) {
                    jsWriter.writeCall("f_core", "Profile").writeNull()
                            .writeln(",\"javascriptCollector.messages\");");
                }

                writeMessages(jsWriter);
            }
        }

        if (logProfiling) {
            if (jsWriter != null) {
                jsWriter.writeCall("f_core", "Profile").writeln(
                        "true,\"javascriptCollector\");");
            }
        }

        if (jsWriter != null) {
            jsWriter.end();
        }

        components.clear();
    }

    private void includesScript(IHtmlWriter htmlWriter, List scripts)
            throws WriterException {

        IHtmlRenderContext htmlRenderContext = htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext();

        IHtmlProcessContext htmlProcessContext = htmlRenderContext
                .getHtmlProcessContext();

        if (mergeScripts && scripts.size() > 1) {
            // Resoudre les urls

            FacesContext facesContext = htmlWriter
                    .getHtmlComponentRenderContext().getFacesContext();

            IScriptContentAccessorHandler scriptContentAccessorHandler = AbstractScriptContentAccessorHandler
                    .getScriptContentAccessorHandler(facesContext);

            if (scriptContentAccessorHandler.isOperationSupported(
                    ICompositeContentAccessorHandler.COMPOSITE_OPERATION_ID,
                    null)) {

                ICompositeURLDescriptor scriptsMergeBuilder = scriptContentAccessorHandler
                        .createCompositeURLDescriptor(MERGE_DEFAULT_CHARSET);

                for (Iterator it = scripts.iterator(); it.hasNext();) {
                    Script script = (Script) it.next();

                    String src = script.getSource();
                    String charSet = script.getCharSet();

                    scriptsMergeBuilder.addUrl(src, charSet);
                }

                String mergeURL = scriptsMergeBuilder.generateURL();

                IContentAccessor contentAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext, mergeURL,
                                IContentFamily.SCRIPT);

                if (contentAccessor != null) {

                    IGenerationResourceInformation generationInformation = new BasicGenerationResourceInformation(
                            htmlWriter.getComponentRenderContext());

                    GeneratedScriptInformation generatedScriptInformation = new GeneratedScriptInformation();

                    String collectedURL = contentAccessor.resolveURL(
                            facesContext, generatedScriptInformation,
                            generationInformation);
                    if (collectedURL != null) {
                        String charSet = generatedScriptInformation
                                .getCharSet();
                        if (charSet == null) {
                            charSet = MERGE_DEFAULT_CHARSET;
                        }

                        scripts = Collections.singletonList(new Script(
                                collectedURL, charSet));
                    }
                }
            } else {
                LOG.debug("Script operation '"
                        + ICompositeContentAccessorHandler.COMPOSITE_OPERATION_ID
                        + "' is not supported !");
            }
        }

        for (Iterator it = scripts.iterator(); it.hasNext();) {
            Script script = (Script) it.next();

            String src = script.getSource();
            String charSet = script.getCharSet();

            htmlWriter.startElement(IHtmlElements.SCRIPT);
            htmlWriter.writeSrc(src);
            if (charSet != null && charSet.length() > 0) {
                htmlWriter.writeCharSet(charSet);
            }

            if (htmlProcessContext.useMetaContentScriptType() == false) {
                htmlWriter.writeType(IHtmlRenderContext.JAVASCRIPT_TYPE);
            }
            htmlWriter.endElement(IHtmlElements.SCRIPT);
        }
    }

    protected boolean isProfilerOn(IHtmlWriter writer) {
        return Boolean.FALSE.equals(writer.getHtmlComponentRenderContext()
                .getHtmlRenderContext().getProcessContext().getProfilerMode()) == false;
    }

    private static void writeInitIds(IJavaScriptWriter jsWriter,
            List<ComponentId> initializeIds, boolean beginRender,
            List<String> focusIds, List<String> hoverIds,
            List<String> messagesIds, List<String> accessIds,
            List<String> submitIds, List<String> layoutIds)
            throws WriterException {

        String currendId = null;
        if (beginRender) {
            currendId = jsWriter.getComponentRenderContext()
                    .getComponentClientId();
        }

        String cameliaClassLoader = jsWriter.getJavaScriptRenderContext()
                .convertSymbol("f_classLoader", "_rcfacesClassLoader");

        ComponentId currendIdDetected = null;

        List<ComponentId> others = null;

        boolean hasMessages = jsWriter.getFacesContext()
                .getClientIdsWithMessages().hasNext();

        boolean first = true;
        for (Iterator it = initializeIds.iterator(); it.hasNext();) {
            ComponentId componentId = (ComponentId) it.next();

            String clientId = componentId.clientId;

            if (clientId.equals(currendId)) {
                currendIdDetected = componentId;
                continue;
            }

            int mode = componentId.mode;

            if (mode == 0) {
                continue;
            }

            if ((mode & JavaScriptEnableModeImpl.ONINIT) == 0) {

                // Si pas de messages
                // ou s'il y a des messages et que le composant ne traite pas
                // les messages ...
                if (hasMessages == false
                        || (mode & JavaScriptEnableModeImpl.ONMESSAGE) == 0) {

                    // On met dans others tous les autres composants qui doivent
                    // etre initialisés hors ONINIT
                    if (others == null) {
                        others = new ArrayList(initializeIds.size());
                    }
                    others.add(componentId);
                    continue;
                }
            }

            if (first) {
                first = false;
                jsWriter.writeCall(cameliaClassLoader, "f_onInit");

            } else {
                jsWriter.write(',');
            }

            jsWriter.writeString(clientId);

            int passiveModes = JavaScriptEnableModeImpl.PASSIVE_MASK & mode;
            if (passiveModes > 0) {
                if (others == null) {
                    others = new ArrayList<ComponentId>(initializeIds.size());
                }
                others.add(componentId);
            }

        }

        if (first == false) {
            jsWriter.writeln(");");
        }

        if (others != null) {
            if (PERFORM_ACCESSKEYS) {
                for (Iterator it = others.iterator(); it.hasNext();) {
                    ComponentId componentId = (ComponentId) it.next();

                    if ((componentId.mode & JavaScriptEnableModeImpl.ONACCESSKEY) == 0) {
                        continue;
                    }

                    accessIds.add(componentId.clientId);
                    accessIds.add(componentId.accessKey);
                }
            }

            for (Iterator it = others.iterator(); it.hasNext();) {
                ComponentId componentId = (ComponentId) it.next();

                if ((componentId.mode & JavaScriptEnableModeImpl.ONSUBMIT) > 0) {
                    submitIds.add(componentId.clientId);
                }

                if ((componentId.mode & JavaScriptEnableModeImpl.ONFOCUS) > 0) {
                    focusIds.add(componentId.clientId);
                }

                if ((componentId.mode & JavaScriptEnableModeImpl.ONMESSAGE) > 0) {
                    messagesIds.add(componentId.clientId);
                }

                if ((componentId.mode & JavaScriptEnableModeImpl.ONOVER) > 0) {
                    hoverIds.add(componentId.clientId);
                }

                if ((componentId.mode & JavaScriptEnableModeImpl.ONLAYOUT) > 0) {
                    layoutIds.add(componentId.clientId);
                }
            }
        }

        initializeIds.clear();
        if (currendIdDetected != null) {
            initializeIds.add(currendIdDetected);
        }
    }

    public boolean isCollectorMode() {
        return true;
    }

    public void includeJavaScript(IHtmlWriter htmlWriter, String src,
            String javaScriptSrcCharSet) throws WriterException {

        components.add(new Script(src, javaScriptSrcCharSet));
    }

    public void writeRaw(IHtmlWriter htmlWriter, String text)
            throws WriterException {
        components.add(new Raw(text));

    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/12/11 10:19:48 $
     */
    private static class ComponentId {
        final int mode;

        final String clientId;

        final String accessKey;

        final String subClientIds[];

        public ComponentId(String clientId, int mode, String accessKey,
                String subClientIds[]) {
            this.mode = mode;
            this.clientId = clientId;
            this.accessKey = accessKey;
            this.subClientIds = subClientIds;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result
                    + ((clientId == null) ? 0 : clientId.hashCode());
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
            final ComponentId other = (ComponentId) obj;
            if (clientId == null) {
                if (other.clientId != null) {
                    return false;
                }
            } else if (!clientId.equals(other.clientId)) {
                return false;
            }
            return true;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/12/11 10:19:48 $
     */
    private static class Script {

        private final String src;

        private final String charSet;

        public Script(String src, String charSet) {
            this.src = src;
            this.charSet = charSet;
        }

        public String getCharSet() {
            return charSet;
        }

        public String getSource() {
            return src;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/12/11 10:19:48 $
     */
    private static class Raw {

        private final String text;

        public Raw(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }
}
