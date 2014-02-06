/*
 * $Id: JavaScriptWriterImpl.java,v 1.4 2013/11/13 12:53:28 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import java.util.Arrays;


import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.repository.IRepository;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:28 $
 */
public final class JavaScriptWriterImpl extends AbstractJavaScriptWriter {
    

    private static final Log LOG = LogFactory
            .getLog(JavaScriptWriterImpl.class);

    private IHtmlWriter writer;

    private boolean start = false;

    private int initialized = 0;

    private boolean initializing = false;

    private String varId = null;

    protected IJavaScriptComponentRenderer javaScriptComponent;

    private boolean useMetaContentScriptType;

    private boolean useScriptCData;

    private IJavaScriptRenderContext javascriptRenderContext;

    public IHtmlWriter getWriter() {
        return writer;
    }

    public String getResponseCharacterEncoding() {
        return writer.getResponseCharacterEncoding();
    }

    public final FacesContext getFacesContext() {
        return getHtmlComponentRenderContext().getFacesContext();
    }

    public final IHtmlComponentRenderContext getHtmlComponentRenderContext() {
        return (IHtmlComponentRenderContext) writer.getComponentRenderContext();
    }

    public final IComponentRenderContext getComponentRenderContext() {
        return getHtmlComponentRenderContext();
    }

    public final IJavaScriptRenderContext getJavaScriptRenderContext() {
        return javascriptRenderContext;
    }

    public void setWriter(IHtmlWriter writer,
            IJavaScriptComponentRenderer javaScriptComponent,
            IJavaScriptRenderContext javascriptRenderContext,
            boolean useMetaContentScriptType, boolean useScriptCData)
            throws WriterException {
        this.writer = writer;
        this.javaScriptComponent = javaScriptComponent;
        this.useMetaContentScriptType = useMetaContentScriptType;
        this.useScriptCData = useScriptCData;
        this.javascriptRenderContext = javascriptRenderContext;
        start = false;

        if (LOG.isDebugEnabled()) {
            LOG.debug("Initialize Writer componentId='"
                    + writer.getComponentRenderContext().getComponentClientId()
                            + "' requiresPending="
                            + (javascriptRenderContext != null && javascriptRenderContext
                                    .isRequiresPending()) + ".");
        }

        if (javascriptRenderContext != null
                && javascriptRenderContext.isRequiresPending()) {
            // Ben non, on peut pas utiliser l'attribut requires car il ne faut
            // pas être dans un bloc Javascript
            // pour utiliser l'include

            isInitialized(false);
        }
    }

    public IJavaScriptWriter write(char c) throws WriterException {
        isInitialized(true);

        writer.write(c);

        return this;
    }

    public IJavaScriptWriter write(String string) throws WriterException {
        isInitialized(true);

        writer.write(string);

        return this;
    }

    public IJavaScriptWriter ensureInitialization() throws WriterException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Ensure initialization");
        }

        isInitialized(true);

        return this;
    }

    public void end() throws WriterException {
        /*
         * if (start == false) { return; }
         */
        writeFooter();
        start = false;
    }

    private boolean isInitialized(boolean full) throws WriterException {
        if (start) {
            return true;
        }

        writeHeader(full);

        return start;
    }

    protected void isInitialized() throws WriterException {
        isInitialized(true);
    }

    protected void writeHeader(boolean full) throws WriterException {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Write header full=" + full + " initializing="
                    + initializing + " init state=" + initialized);
        }

        if (initializing || initialized == 2) {
            writeScriptStart();
            return;
        }

        if (initialized == 0) {
            try {
                initializing = true;
                initialized = 1;

                if (javaScriptComponent != null) {
                    javaScriptComponent.initializeJavaScript(this);
                }

                writeJavaScriptDependencies();

                if (full == false) {
                    return;
                }

            } finally {
                initializing = false;
            }
        }

        try {
            initializing = true;
            initialized = 2;

            if (javaScriptComponent != null) {
                javaScriptComponent.initializeJavaScriptComponent(this);
            }

        } finally {
            initializing = false;
        }

        if (full && start == false) {
            writeScriptStart();
        }
    }

    protected void writeScriptStart() throws WriterException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Start script");
        }
        start = true;

        writer.startElement(IHtmlWriter.SCRIPT);

        if (useMetaContentScriptType == false) {
            writer.writeType(IHtmlRenderContext.JAVASCRIPT_TYPE);
        }
        if (getHtmlRenderContext().getProcessContext().isDesignerMode()) {
            writer.writeAttributeNS("rcfaces", "core");
        }

        if (useScriptCData) {
            write(IHtmlRenderContext.JAVASCRIPT_CDATA_BEGIN);
        }
        writeln();
    }

    protected void writeScriptEnd() throws WriterException {
        if (LOG.isTraceEnabled()) {
            LOG.trace("End script");
        }

        if (useScriptCData) {
            write(IHtmlRenderContext.JAVASCRIPT_CDATA_END);
        }
        writer.endElement(IHtmlWriter.SCRIPT);
        start = false;
    }

    public void writeJavaScriptDependencies() throws WriterException {
        if (javascriptRenderContext.isRequiresPending() == false) {
            return;
        }

        String cameliaClassLoader = convertSymbol("f_classLoader",
                "_rcfacesClassLoader");

        writeCall(cameliaClassLoader, "f_requiresBundle");

        IRepository.IFile filesToRequire[] = javascriptRenderContext
                .popRequiredFiles();

        if (LOG.isDebugEnabled()) {
            LOG.debug("Write javascript dependencies: "
                    + Arrays.asList(filesToRequire));
        }

        ICriteria criteria = javascriptRenderContext.getCriteria();
        for (int i = 0; i < filesToRequire.length; i++) {
            IRepository.IFile file = filesToRequire[i];

            if (i > 0) {
                write(',');
            }

            String fileURI = file.getURI(criteria);
            if (fileURI == null) {
                throw new NullPointerException("Can not get URI of file '"
                        + fileURI + "' criteria='" + criteria + "'");
            }

            writeString(fileURI);
        }
        writeln(");");

        // On ferme et rouvre pour pouvoir prendre en compte les document.write
        // du FClass.Require
        writeScriptEnd();
    }

    private void writeInclude(String baseURI, String src)
            throws WriterException {
    }

    private void writeRequestedModule() throws WriterException {

    }

    protected void writeFooter() throws WriterException {
        if (javaScriptComponent != null) {
            javaScriptComponent.releaseJavaScript(this);
            javaScriptComponent = null;
        }

        if (start) {
            writeScriptEnd();
        }
    }

    public boolean isOpened() {
        return start;
    }

    public String getComponentVarName() {
        if (varId != null) {
            return varId;
        }

        // Plus d'exception car on peut initialiser le componentVarName en
        // conséquence ...
        // throw new FacesException("Var is not initialized yet !");
        return null;
    }

    public void setComponentVarName(String varName) {
        if (LOG.isDebugEnabled()) {
            IComponentRenderContext componentRenderContext = getHtmlComponentRenderContext();
            if (componentRenderContext != null) {
                LOG.debug("Set component (id='"
                        + componentRenderContext.getComponentClientId()
                        + "') var name to '" + varName + "'.");
            } else {
                LOG.debug("Set component (id='?') var name to '" + varName
                        + "'.");
            }
        }

        this.varId = varName;
    }

    public IJavaScriptWriter writeRaw(char[] dst, int pos, int length)
            throws WriterException {
        writer.write(dst, pos, length);

        return this;
    }

    public String allocateString(String string) throws WriterException {
        if (string == null) {
            return null;
        }

        boolean ret[] = new boolean[1];

        String varId = javascriptRenderContext.allocateString(string, ret);
        if (ret[0] == false) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("String '" + string + "' is already setted to var '"
                        + varId + "'.");
            }

            return varId;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Allocate string '" + string + "' to var '" + varId
                    + "'.");
        }

        write("var ").write(varId).write("=").writeString(string).writeln(";");

        return varId;
    }

    protected final String convertSymbol(String className, String memberName) {
        String converted = javascriptRenderContext.convertSymbol(className,
                memberName);

        if (LOG.isTraceEnabled()) {
            if (memberName.equals(converted) == false) {
                LOG.trace("Convert symbol '" + className + "." + memberName
                        + "' to '" + converted + "'.");
            }
        }

        return converted;
    }
}
