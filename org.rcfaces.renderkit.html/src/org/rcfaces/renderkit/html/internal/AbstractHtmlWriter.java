/**
 * $Id: AbstractHtmlWriter.java,v 1.2 2013/01/11 15:45:00 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.faces.component.UIComponent;
import javax.faces.context.ResponseWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.manager.ITransientAttributesManager;
import org.rcfaces.core.internal.renderkit.AbstractRenderContext;
import org.rcfaces.core.internal.renderkit.IComponentRenderContext;
import org.rcfaces.core.internal.renderkit.IRenderContext;
import org.rcfaces.core.internal.renderkit.ISgmlWriter;
import org.rcfaces.core.internal.renderkit.WriterException;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:00 $
 */
public abstract class AbstractHtmlWriter extends
        AbstractHtmlComponentRenderContext implements IHtmlWriter {

    private static final Log LOG = LogFactory.getLog(AbstractHtmlWriter.class);

    private static final String[] STRING_EMPTY_ARRAY = new String[0];

    private static final char LF = '\n';

    private static final String TAG_STACK_PROPERTY = "org.rcfaces.core.internal.writer.TAG_STACK";

    private static final String NONE_WAI_ROLE_NS = "none";

    protected static final boolean VERIFY_TAG_STACK = LOG.isDebugEnabled();

    private static final String SUB_COMPONENTS_IDS_PROPERTY = "org.rcfaces.core.internal.writer.SUB_COMPONENTS_IDS";

    private static final String RCFACES_NS = "v";

    static {
        if (VERIFY_TAG_STACK) {
            LOG.debug("Verify tags stack enabled.");
        }
    }

    private final ResponseWriter responseWriter;

    protected final AbstractRenderContext renderContext;

    private ICssWriter cssWriter;

    private Set<String> subComponents;

    public AbstractHtmlWriter(AbstractRenderContext renderContext) {
        this(renderContext, renderContext.getFacesContext().getResponseWriter());
    }

    @SuppressWarnings("unchecked")
    protected AbstractHtmlWriter(AbstractRenderContext renderContext,
            ResponseWriter responseWriter) {
        super(renderContext.getFacesContext(), renderContext.getComponent(),
                renderContext.getComponentClientId());

        this.renderContext = renderContext;

        this.responseWriter = responseWriter;

        subComponents = (Set<String>) ((ITransientAttributesManager) renderContext
                .getComponent())
                .getTransientAttribute(SUB_COMPONENTS_IDS_PROPERTY);
    }

    public final IComponentRenderContext getComponentRenderContext() {
        return this;
    }

    public final IHtmlComponentRenderContext getHtmlComponentRenderContext() {
        return this;
    }

    public final Object getAttribute(String key) {
        return renderContext.getComponentContextAttribute(key);
    }

    public final Object removeAttribute(String key) {
        return renderContext.removeComponentContextAttribute(key);
    }

    public final Object setAttribute(String key, Object value) {
        return renderContext.setComponentContextAttribute(key, value);
    }

    public boolean containsAttribute(String key) {
        return renderContext.containsComponentContextAttribute(key);
    }

    public final IRenderContext getRenderContext() {
        return renderContext;
    }

    public ISgmlWriter write(String s) throws WriterException {
        closeCssWriter();

        try {
            responseWriter.write(s);

        } catch (IOException e) {
            throw new WriterException(null, e, renderContext.getComponent());
        }

        return this;
    }

    public ISgmlWriter write(char buf[], int offset, int length)
            throws WriterException {
        closeCssWriter();

        try {
            responseWriter.write(buf, offset, length);

        } catch (IOException e) {
            throw new WriterException(null, e, renderContext.getComponent());
        }

        return this;
    }

    protected void closeCssWriter() throws WriterException {
        if (cssWriter == null) {
            return;
        }

        cssWriter.done();

        cssWriter = null;
    }

    public ISgmlWriter writeText(String s) throws WriterException {
        closeCssWriter();

        try {
            responseWriter.writeText(s, null);

        } catch (IOException e) {
            throw new WriterException(null, e, renderContext.getComponent());
        }

        return this;
    }

    public ISgmlWriter write(char c) throws WriterException {
        closeCssWriter();

        try {
            responseWriter.write(c);

        } catch (IOException e) {
            throw new WriterException(null, e, renderContext.getComponent());
        }

        return this;
    }

    public ISgmlWriter write(int value) throws WriterException {
        return write(String.valueOf(value));
    }

    public ISgmlWriter writeln() throws WriterException {
        return write(LF);
    }

    public ISgmlWriter writeAttribute(String name, String value)
            throws WriterException {

        try {
            responseWriter.writeAttribute(name, value, null);

        } catch (IOException e) {
            throw new WriterException(null, e, renderContext.getComponent());
        }

        return this;
    }

    public ISgmlWriter writeAttribute(String name, String values[],
            String separator) throws WriterException {

        String value;
        if (values.length == 1) {
            value = values[0];

        } else {
            StringAppender sa = new StringAppender(
                    (values.length + separator.length()) * 32);
            for (int i = 0; i < values.length; i++) {
                if (i > 0) {
                    sa.append(separator);
                }

                sa.append(values[i]);
            }

            value = sa.toString();
        }

        try {
            responseWriter.writeAttribute(name, value, null);

        } catch (IOException e) {
            throw new WriterException(null, e, renderContext.getComponent());
        }

        return this;
    }

    public ISgmlWriter writeAttribute(String name, long value)
            throws WriterException {
        return writeAttribute(name, String.valueOf(value));
    }

    public ISgmlWriter writeAttribute(String name, boolean value)
            throws WriterException {
        return writeAttribute(name, String.valueOf(value));
    }

    public String getResponseCharacterEncoding() {
        return responseWriter.getCharacterEncoding();
    }

    public ISgmlWriter writeAttribute(String name) throws WriterException {
        closeCssWriter();

        try {
            responseWriter.writeAttribute(name, Boolean.TRUE, null);

        } catch (IOException e) {
            throw new WriterException(null, e, renderContext.getComponent());
        }

        return this;
    }

    public ISgmlWriter writeComment(String comment) throws WriterException {
        closeCssWriter();

        try {
            responseWriter.writeComment(comment);

        } catch (IOException e) {
            throw new WriterException(null, e, renderContext.getComponent());
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public ISgmlWriter startElement(String name, UIComponent component)
            throws WriterException {
        closeCssWriter();

        if (VERIFY_TAG_STACK) {
            Stack<Object> tagStack = (Stack<Object>) getFacesContext()
                    .getExternalContext().getRequestMap()
                    .get(TAG_STACK_PROPERTY);
            if (tagStack == null) {
                tagStack = new Stack<Object>();

                getFacesContext().getExternalContext().getRequestMap()
                        .put(TAG_STACK_PROPERTY, tagStack);
            }

            tagStack.push(name);
            tagStack.push(component);
        }

        try {
            responseWriter.startElement(name, component);

        } catch (IOException e) {
            throw new WriterException(null, e, component);
        }

        return this;
    }

    public ISgmlWriter startElement(String name) throws WriterException {
        return startElement(name, this.getComponent());
    }

    public ISgmlWriter endElement(String name) throws WriterException {
        closeCssWriter();

        if (VERIFY_TAG_STACK) {
            Stack tagStack = (Stack) getFacesContext().getExternalContext()
                    .getRequestMap().get(TAG_STACK_PROPERTY);
            if (tagStack == null || tagStack.isEmpty()) {
                throw new IllegalStateException("Tag stack is empty !");
            }

            UIComponent poppedComponent = (UIComponent) tagStack.pop();
            String current = (String) tagStack.pop();

            if (current.equals(name) == false) {
                throw new IllegalStateException("Invalid close of tag '" + name
                        + "' current='" + current + "' poped component='"
                        + poppedComponent + "'!");
            }
        }

        try {
            responseWriter.endElement(name);

        } catch (IOException e) {
            throw new WriterException(null, e, renderContext.getComponent());
        }

        return this;
    }

    public ISgmlWriter writeURIAttribute(String name, Object value)
            throws WriterException {
        closeCssWriter();

        try {
            responseWriter.writeURIAttribute(name, value, null);

        } catch (IOException e) {
            throw new WriterException(null, e, renderContext.getComponent());
        }

        return this;
    }

    public ISgmlWriter endComponent() throws WriterException {
        closeCssWriter();

        try {
            responseWriter.flush();

        } catch (IOException e) {
            throw new WriterException(null, e, renderContext.getComponent());
        }

        return this;
    }

    public final IHtmlWriter writeMaxLength(int maxLength)
            throws WriterException {
        writeAttribute("maxlength", maxLength);

        return this;
    }

    public final IHtmlWriter writeSize(int size) throws WriterException {
        writeAttribute("size", size);

        return this;
    }

    public final IHtmlWriter writeType(String type) throws WriterException {
        writeAttribute("type", type);

        return this;
    }

    public final IHtmlWriter writeId(String id) throws WriterException {
        writeAttribute("id", id);

        return this;
    }

    public final IHtmlWriter writeName(String name) throws WriterException {
        writeAttribute("name", name);

        return this;
    }

    public final IHtmlWriter writeClass(String className)
            throws WriterException {
        /*
         * if (className.indexOf('@')>=0) { throw new
         * IllegalArgumentException("Invalid className='"+className+"'"); }
         */
        writeAttribute("class", className);

        return this;
    }

    public final IHtmlWriter writeDisabled() throws WriterException {
        writeAttribute("DISABLED");

        return this;
    }

    public final IHtmlWriter writeReadOnly() throws WriterException {
        writeAttribute("READONLY");

        return this;
    }

    public final IHtmlWriter writeValue(String value) throws WriterException {
        writeAttribute("value", value);

        return this;
    }

    public final IHtmlWriter writeAccessKey(String accessKey)
            throws WriterException {
        writeAttribute("accessKey", accessKey);

        return this;
    }

    public final IHtmlWriter writeTabIndex(int tabIndex) throws WriterException {
        writeAttribute("tabIndex", tabIndex);

        return this;
    }

    public final IHtmlWriter writeChecked() throws WriterException {
        writeAttribute("CHECKED");

        return this;
    }

    public final IHtmlWriter writeFor(String id) throws WriterException {
        writeAttribute("for", id);

        return this;
    }

    public final IHtmlWriter writeHeight(int height) throws WriterException {
        writeAttribute("height", height);

        return this;
    }

    public IHtmlWriter writeHeight(String height) throws WriterException {
        writeAttribute("height", height);

        return this;
    }

    public final IHtmlWriter writeBorder(int size) throws WriterException {
        writeAttribute("border", size);

        return this;
    }

    public final IHtmlWriter writeStyle(String style) throws WriterException {
        writeAttribute("style", style);

        return this;
    }

    public final IHtmlWriter writeTitle(String title) throws WriterException {
        writeAttribute("title", title);

        return this;
    }

    public final IHtmlWriter writeWidth(int width) throws WriterException {
        writeAttribute("width", width);

        return this;
    }

    public final IHtmlWriter writeWidth(String width) throws WriterException {
        writeAttribute("width", width);

        return this;
    }

    public IHtmlWriter writeAlign(String align) throws WriterException {
        writeAttribute("align", align);

        return this;
    }

    public IHtmlWriter writeVAlign(String valign) throws WriterException {
        writeAttribute("valign", valign);

        return this;
    }

    public IHtmlWriter writeCellPadding(int cellPadding) throws WriterException {
        writeAttribute("cellpadding", cellPadding);

        return this;
    }

    public IHtmlWriter writeCellSpacing(int cellSpacing) throws WriterException {
        writeAttribute("cellspacing", cellSpacing);

        return this;
    }

    public IHtmlWriter writeSrc(String src) throws WriterException {
        writeURIAttribute("src", src);

        return this;
    }

    public IHtmlWriter writeAlt(String alt) throws WriterException {
        writeAttribute("alt", alt);

        return this;
    }

    public IHtmlWriter writeMultiple() throws WriterException {
        writeAttribute("multiple");

        return this;
    }

    public ICssWriter writeStyle() {
        if (cssWriter != null) {
            return cssWriter;
        }
        cssWriter = new CssWriter(this);

        return cssWriter;
    }

    public ICssWriter writeStyle(int size) {
        if (cssWriter != null) {
            return cssWriter;
        }

        cssWriter = new CssWriter(this, size);

        return cssWriter;
    }

    public IHtmlWriter writeColSpan(int colspan) throws WriterException {
        writeAttribute("colspan", colspan);

        return this;
    }

    public IHtmlWriter writeRowSpan(int rowspan) throws WriterException {
        writeAttribute("rowspan", rowspan);

        return this;
    }

    public IHtmlWriter writeCols(int cols) throws WriterException {
        writeAttribute("cols", cols);

        return this;
    }

    public IHtmlWriter writeRows(int rows) throws WriterException {
        writeAttribute("rows", rows);

        return this;
    }

    public IHtmlWriter writeLabel(String label) throws WriterException {
        writeAttribute("label", label);

        return this;
    }

    public IHtmlWriter writeSelected() throws WriterException {
        writeAttribute("SELECTED");

        return this;
    }

    public IHtmlWriter writeHRef(String url) throws WriterException {
        writeURIAttribute("href", url);

        return this;
    }

    public IHtmlWriter writeHRef_JavascriptVoid0() throws WriterException {
        writeURIAttribute("href", "javascript:void("
                + ((HtmlRenderContext) renderContext).allocateJavaScriptVoid0()
                + ")");

        return this;
    }

    public IHtmlWriter writeRel(String rel) throws WriterException {
        writeAttribute("rel", rel);

        return this;
    }

    public IHtmlWriter writeDir(String direction) throws WriterException {
        writeAttribute("dir", direction);

        return this;
    }

    public IHtmlWriter writeCharSet(String charset) throws WriterException {
        writeAttribute("charset", charset);

        return this;
    }

    public IHtmlWriter writeAutoComplete(String mode) throws WriterException {
        writeAttribute("autocomplete", mode);

        return this;
    }

    public IHtmlWriter writeHttpEquiv(String equiv, String content)
            throws WriterException {
        writeAttribute("http-equiv", equiv);

        writeAttribute("content", content);

        return this;
    }

    @SuppressWarnings("unused")
    public IHtmlWriter writeRole(String role) throws WriterException {
        if (Constants.ACCESSIBILITY_ROLE_SUPPORT == false) {
            return this;
        }

        String waiRoleNS = getHtmlRenderContext().getWaiRolesNS();

        if (NONE_WAI_ROLE_NS.equals(waiRoleNS)) {
            return this;
        }

        if (waiRoleNS != null && waiRoleNS.length() > 0) {
            writeAttribute(waiRoleNS + ":role", role);
            return this;
        }

        writeAttribute("role", role);

        return this;
    }

    public IHtmlWriter writeAriaActivedescendant(String clientId)
            throws WriterException {
        writeAttribute("aria-activedescendant", clientId);
        return this;
    }

    public IHtmlWriter writeAriaRequired(boolean required)
            throws WriterException {
        writeAttribute("aria-required", required);
        return this;
    }

    public IHtmlWriter writeAriaControls(String[] listId)
            throws WriterException {
        if (listId == null || listId.length == 0) {
            return this;
        }

        if (listId.length == 1) {
            writeAttribute("aria-controls", listId[0]);
            return this;
        }

        StringAppender list = new StringAppender(listId[0],
                (listId.length - 1) * 33);

        for (int i = 1; i < listId.length; i++) {
            list.append(' ').append(listId[i]);
        }

        writeAttribute("aria-controls", list.toString());
        return this;
    }

    public IHtmlWriter writeAriaDisabled(boolean disabled)
            throws WriterException {
        writeAttribute("aria-disabled", disabled);
        return this;
    }

    public IHtmlWriter writeAriaExpanded(boolean expanded)
            throws WriterException {
        writeAttribute("aria-expanded", expanded);
        return this;
    }

    public IHtmlWriter writeAriaLabel(String ariaLabel) throws WriterException {
        writeAttribute("aria-label", ariaLabel);
        return this;
    }

    public IHtmlWriter writeAriaLabelledBy(String clientId)
            throws WriterException {
        writeAttribute("aria-labelledby", clientId);
        return this;
    }

    public IHtmlWriter writeAriaLevel(int level) throws WriterException {
        writeAttribute("aria-level", level);
        return this;
    }

    public IHtmlWriter writeAriaSelected(boolean selected)
            throws WriterException {
        writeAttribute("aria-selected", selected);
        return this;
    }

    public void addSubFocusableComponent(String subComponentClientId) {
        if (subComponents == null) {
            subComponents = new HashSet<String>(4);

            ((ITransientAttributesManager) renderContext.getComponent())
                    .setTransientAttribute(SUB_COMPONENTS_IDS_PROPERTY,
                            subComponents);
        }

        getJavaScriptEnableMode().enableOnFocus();

        subComponents.add(subComponentClientId);
    }

    public String[] listSubFocusableComponents() {
        if (subComponents == null) {
            return STRING_EMPTY_ARRAY;
        }

        return subComponents.toArray(new String[subComponents.size()]);
    }

    public String getRcfacesNamespace() {
        return RCFACES_NS;
    }

    public String computeRcfacesNamespace(String name) {
        return RCFACES_NS + ":" + name;
    }

    public IHtmlWriter writeAttributeNS(String name, String value)
            throws WriterException {

        writeAttribute(computeRcfacesNamespace(name), value);
        return this;
    }

    public IHtmlWriter writeAttributeNS(String name, long value)
            throws WriterException {

        writeAttribute(computeRcfacesNamespace(name), value);
        return this;
    }

    public IHtmlWriter writeAttributeNS(String name, boolean value)
            throws WriterException {

        writeAttribute(computeRcfacesNamespace(name), value);
        return this;
    }

    public IHtmlWriter writeURIAttributeNS(String name, Object value)
            throws WriterException {

        writeURIAttribute(computeRcfacesNamespace(name), value);
        return this;
    }

    public IHtmlWriter writeAttributeNS(String name, String[] values,
            String separator) throws WriterException {

        writeAttribute(computeRcfacesNamespace(name), values, separator);
        return this;
    }

    public IHtmlWriter startElementNS(String name) throws WriterException {

        startElement(computeRcfacesNamespace(name));
        return this;
    }

    public IHtmlWriter endElementNS(String name) throws WriterException {
        endElement(computeRcfacesNamespace(name));
        return this;
    }
}