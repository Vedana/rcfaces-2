package org.rcfaces.css.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.content.AbstractBufferOperationContentModel.ContentInformation;
import org.rcfaces.core.internal.content.IOperationContentLoader;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory;
import org.rcfaces.core.internal.style.IStyleParser;
import org.rcfaces.core.internal.util.IPath;
import org.rcfaces.core.internal.util.Path;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.css.internal.rules.CssFunctionRule;
import org.rcfaces.css.internal.rules.CssPropertyRule;
import org.rcfaces.css.internal.rules.IPropertyRuleProcessor;
import org.rcfaces.css.internal.rules.UserAgentPropertyRule;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ConditionalSelector;
import org.w3c.css.sac.DescendantSelector;
import org.w3c.css.sac.ElementSelector;
import org.w3c.css.sac.ErrorHandler;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SelectorList;
import org.w3c.css.sac.SiblingSelector;
import org.w3c.css.sac.SimpleSelector;
import org.w3c.dom.css.CSSCharsetRule;
import org.w3c.dom.css.CSSImportRule;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleDeclaration;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.stylesheets.MediaList;

import com.steadystate.css.dom.CSSOMObject;
import com.steadystate.css.dom.CSSValueImpl;
import com.steadystate.css.dom.Property;
import com.steadystate.css.parser.CSSOMParser;
import com.steadystate.css.parser.LexicalUnitImpl;
import com.steadystate.css.parser.SACParserCSS3;
import com.steadystate.css.parser.selectors.SelectorsRule;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.3.2.7 $ $Date: 2013/12/13 13:24:32 $
 */
public class CssSteadyStateParser extends CssParserProvider implements
        IStyleParser {

    public static final Log PARSING_LOG = LogFactory
            .getLog("org.rcfaces.css.Parsing");

    private static final Log LOG = LogFactory
            .getLog(CssSteadyStateParser.class);

    static {
        LOG.info("Enable 'CssSteadyState' css parser.");
    }

    private static final Set<String> VALID_ELEMENTS = new HashSet<String>(
            Arrays.asList(new String[] { "A", "ABBR", "ACRONYM", "ADDRESS",
                    "APPLET", "AREA", "B", "BASE", "BASEFONT", "BDO", "BIG",
                    "BLOCKQUOTE", "BODY", "BR", "BUTTON", "CAPTION", "CENTER",
                    "CITE", "CODE", "COL", "COLGROUP", "DD", "DEL", "DFN",
                    "DIR", "DIV", "DL", "DT", "EM", "FIELDSET", "FONT", "FORM",
                    "FRAME", "FRAMESET", "H1", "H2", "H3", "H4", "H5", "H6",
                    "HEAD", "HR", "HTML", "I", "IFRAME", "IMG", "INPUT", "INS",
                    "ISINDEX", "KBD", "LABEL", "LEGEND", "LI", "LINK", "MAP",
                    "MENU", "META", "NOFRAMES", "NOSCRIPT", "OBJECT", "OL",
                    "OPTGROUP", "OPTION", "P", "PARAM", "PRE", "Q", "S",
                    "SAMP", "SCRIPT", "SELECT", "SMALL", "SPAN", "STRIKE",
                    "STRONG", "STYLE", "SUB", "SUP", "TABLE", "TBODY", "TD",
                    "TEXTAREA", "TFOOT", "TH", "THEAD", "TITLE", "TR", "TT",
                    "U", "UL", "VAR" }));

    static final String VIRTUAL_RULE_PROPERTY = "VIRTUAL_RULE";

    static final String IMPORTED_RULE_PROPERTY = "IMPORTED";

    private static final String CSS_PROPERTIES_RULES_PATH = "org/rcfaces/css/internal/rules/css-properties.xml";

    static final String DELETED_RULE_PROPERTY = "DELETED";

    private final Map<String, CssPropertyRule> propertyRulesByName = new HashMap<String, CssPropertyRule>();

    private final Map<String, CssFunctionRule> functionRulesByName = new HashMap<String, CssFunctionRule>();

    public CssSteadyStateParser() {
    }

    @Override
    public void startup(FacesContext facesContext) {
        super.startup(facesContext);

        loadCssPropertiesRules();
    }

    private void loadCssPropertiesRules() {
        InputStream ins = getClass().getClassLoader().getResourceAsStream(
                CSS_PROPERTIES_RULES_PATH);
        if (ins == null) {
            LOG.error("Can not load resource '" + CSS_PROPERTIES_RULES_PATH
                    + "'");
            return;
        }

        try {
            Digester digester = new Digester();
            digester.push(this);

            digester.addObjectCreate("css-properties/css-property",
                    CssPropertyRule.class);

            digester.addCallMethod("css-properties/css-property/name",
                    "addName", 1);
            digester.addCallParam("css-properties/css-property/name", 0);

            digester.addObjectCreate("css-properties/css-property/user-agent",
                    UserAgentPropertyRule.class);
            digester.addSetProperties("css-properties/css-property/user-agent");
            digester.addSetNext("css-properties/css-property/user-agent",
                    "addAgent");

            digester.addSetNext("css-properties/css-property", "addCssProperty");

            digester.addObjectCreate("css-properties/css-function",
                    CssFunctionRule.class);

            digester.addCallMethod("css-properties/css-function/name",
                    "addName", 1);
            digester.addCallParam("css-properties/css-function/name", 0);

            digester.addObjectCreate("css-properties/css-function/user-agent",
                    UserAgentPropertyRule.class);
            digester.addSetProperties("css-properties/css-function/user-agent");
            digester.addSetNext("css-properties/css-function/user-agent",
                    "addAgent");

            digester.addSetNext("css-properties/css-function", "addCssFunction");

            try {
                digester.parse(ins);

            } catch (Exception ex) {
                LOG.error("Can not parse Css properties '"
                        + CSS_PROPERTIES_RULES_PATH + "'", ex);
            }
        } finally {
            try {
                ins.close();
            } catch (IOException ex) {
                LOG.debug("Can not close input stream ? (" + ins + ")");
            }
        }
    }

    public void addCssProperty(CssPropertyRule rule) {
        for (String name : rule.listAgentNames()) {
            propertyRulesByName.put(name, rule);
        }
    }

    public void addCssFunction(CssFunctionRule rule) {
        for (String name : rule.listAgentNames()) {
            functionRulesByName.put(name, rule);
        }
    }

    @Override
    public String getParserName() {
        return "Steady State Css parser";
    }

    @Override
    public String normalizeBuffer(String styleSheetURL,
            final String styleSheetContent, IParserContext parserContext)
            throws IOException {

        CSSOMParser parser = new CSSOMParser(new SACParserCSS3());

        InputSource inputSource = new InputSource(new StringReader(
                styleSheetContent));
        inputSource.setTitle(styleSheetURL);

        parser.setErrorHandler(new ErrorHandler() {

            private String getErrorLine(CSSParseException ex) {
                int ln = ex.getLineNumber();

                int beginIdx = 0;
                for (; ln > 0; ln--) {
                    int newIdx = styleSheetContent.indexOf('\n', beginIdx);
                    if (newIdx < 0) {
                        beginIdx = -1;
                        break;
                    }
                    beginIdx = newIdx + 1;
                }
                if (beginIdx < 0) {
                    return "*** Out of buffer ***";
                }

                StringBuilder sb = new StringBuilder(256);

                ln = ex.getLineNumber();

                if (beginIdx > 1) {
                    beginIdx -= 2;

                    for (int i = 0; i < 3 && beginIdx > 1; i++) {
                        int newIdx = styleSheetContent.lastIndexOf('\n',
                                beginIdx);
                        if (newIdx < 1) {
                            break;
                        }
                        beginIdx = newIdx - 1;
                        ln--;
                    }
                }

                int endIdx = beginIdx;
                for (int i = 0; i < 7; i++, ln++) {
                    int newIdx = styleSheetContent.indexOf('\n', endIdx);
                    if (newIdx < 0) {
                        sb.append("[" + ln
                                + ((ex.getLineNumber() == ln) ? "**" : "")
                                + "] " + styleSheetContent.substring(endIdx));
                        break;
                    }
                    sb.append("[" + ln
                            + ((ex.getLineNumber() == ln) ? "**" : "") + "] "
                            + styleSheetContent.substring(endIdx, newIdx));
                    endIdx = newIdx + 1;
                }

                return sb.toString();
            }

            @Override
            public void warning(CSSParseException exception)
                    throws CSSException {
                String text = getErrorLine(exception);

                PARSING_LOG.warn("Warning at line=" + exception.getLineNumber()
                        + " col=" + exception.getColumnNumber() + " text='"
                        + text + "'", exception);
            }

            @Override
            public void fatalError(CSSParseException exception)
                    throws CSSException {
                String text = getErrorLine(exception);

                PARSING_LOG.fatal("FATAL at line=" + exception.getLineNumber()
                        + " col=" + exception.getColumnNumber() + " text='"
                        + text + "'", exception);
            }

            @Override
            public void error(CSSParseException exception) throws CSSException {
                String text = getErrorLine(exception);

                PARSING_LOG.error("ERROR at line=" + exception.getLineNumber()
                        + " col=" + exception.getColumnNumber() + " text='"
                        + text + "'", exception);
            }
        });
        CSSStyleSheet styleSheet = parser.parseStyleSheet(inputSource, null,
                styleSheetURL);

        IPath base = new Path(styleSheetURL).makeRelative();
        IPath relative = new Path("..").append(base.removeLastSegments(1));

        if (LOG.isDebugEnabled()) {
            LOG.debug("Merge buffer of '" + base + "' relative='" + relative
                    + "'");
        }

        computeStyleSheet(styleSheet, base, relative, parserContext);

        return styleSheet.toString();
    }

    private void computeStyleSheet(CSSStyleSheet styleSheet, IPath basePath,
            IPath relativePath, IParserContext parserContext) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Refactor stylesheet: '" + basePath + "'.");
        }

        CSSRuleListIterator ruleList = new CSSRuleListIterator(styleSheet);
        for (; ruleList.hasNext();) {
            CSSRule rule = ruleList.next();

            if (rule instanceof CSSCharsetRule) {
                // On enleve le Charset systematiquement !
                ruleList.delete();

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Remove charset rule");
                }
                continue;
            }

            if (rule instanceof CSSStyleRule) {
                CSSStyleRule styleRule = (CSSStyleRule) rule;

                if (styleRule instanceof CSSOMObject) {
                    if (isVirtualRule((CSSOMObject) styleRule)) {
                        continue;
                    }
                }

                CSSStyleDeclaration declaration = styleRule.getStyle();

                if (styleRule instanceof SelectorsRule) {
                    verifySelectors(
                            ((SelectorsRule) styleRule).getSelectorList(),
                            basePath.toString());
                }

                if (parserContext.isProcessRulesEnabled()) {
                    if (propertyRulesByName.isEmpty() == false) {
                        processPropertyRule(styleRule,
                                new CssPropertyListIterator(styleRule));
                    }

                    if (functionRulesByName.isEmpty() == false) {
                        processValueRule(styleRule,
                                new CssPropertyListIterator(styleRule));
                    }
                }

                for (int j = 0; j < declaration.getLength(); j++) {
                    String property = declaration.item(j);

                    CSSValue value = declaration.getPropertyCSSValue(property);

                    alterCssValue(parserContext, basePath, value, relativePath);
                }

                if (declaration.getLength() == 0) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Remove rule=>" + declaration);
                    }
                    ruleList.delete();
                }

                continue;
            }

            if ((rule instanceof CSSImportRule) == false) {
                continue;
            }

            CSSImportRule importRule = (CSSImportRule) rule;

            String href = importRule.getHref();

            if (href == null || href.length() < 1) {
                continue;
            }

            href = href.trim();

            if (href.toLowerCase().startsWith("url(")) {
                int idx2 = href.lastIndexOf(')');
                if (idx2 < 0) {
                    idx2 = href.length();
                }

                href = href.substring(4, idx2).trim();

                if (href.startsWith("\"") && href.endsWith("\"")) {
                    href = href.substring(1, href.length() - 1);

                } else if (href.startsWith("'") && href.endsWith("'")) {
                    href = href.substring(1, href.length() - 1);
                }
            }

            if (isValidURL(href) == false) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Ignore import rule: absolute or protocol, # or ? detected. ("
                            + href + ")");
                }
                continue;
            }

            // @TODO on doit verifier les medias ...

            IPath contextRelativePath = new Path(href);

            IPath newRelativePath = relativePath.append(contextRelativePath
                    .removeLastSegments(1));

            contextRelativePath = basePath.removeLastSegments(1).append(
                    contextRelativePath);

            String importedPath = contextRelativePath.toString();

            if (LOG.isDebugEnabled()) {
                LOG.debug("Import rule detected: '" + href + "' realPath='"
                        + importedPath + "' relativePath='" + relativePath
                        + "'.");
            }

            if (parserContext.isMergeImportsEnabled() == false) {
                // Il faut obtenir une URL versionnée ! (avec le contenu
                // versionné !)

                if (parserContext.isVersioningEnabled()) { // Forcement
                    IPath newVersionedPath = parserContext
                            .processVersioning(basePath, contextRelativePath,
                                    IContentFamily.STYLE);

                    if (newVersionedPath.equals(contextRelativePath) == false) {
                        StringAppender sa = new StringAppender("@import url(",
                                256).append(newVersionedPath.toString())
                                .append(')');

                        MediaList mediaList = importRule.getMedia();
                        if (mediaList != null) {
                            sa.append(' ').append(mediaList.toString());
                        }
                        sa.append(';');

                        importRule.setCssText(sa.toString());
                    }
                }

                continue;
            }

            try {
                ContentInformation contentInformationRef[] = new ContentInformation[1];

                String childContent = parserContext.getOperationContentLoader()
                        .loadContent(parserContext.getFacesContext(),
                                parserContext.getResourceLoaderFactory(),
                                importedPath, parserContext.getCharset(),
                                contentInformationRef);

                if (childContent == null) {
                    LOG.error("Can not load css resource '" + importedPath
                            + "'.");
                    continue;
                }

                if (contentInformationRef[0] != null) {
                    ContentInformation contentInformation = contentInformationRef[0];

                    long lastModified = contentInformation.getLastModified();
                    if (parserContext.getLastModifiedDate() < 0
                            || lastModified > parserContext
                                    .getLastModifiedDate()) {
                        parserContext.setLastModifiedDate(lastModified);
                    }
                }

                InputSource inputSource = new InputSource(new StringReader(
                        childContent));
                inputSource.setTitle(importedPath);

                CSSOMParser parser = new CSSOMParser(new SACParserCSS3());

                CSSStyleSheet importedStyleSheet = parser.parseStyleSheet(
                        inputSource, null, importedPath);

                computeStyleSheet(importedStyleSheet, contextRelativePath,
                        newRelativePath, parserContext);

                ruleList.delete();

                CSSRuleList importedRuleList = importedStyleSheet.getCssRules();

                for (int j = 0; j < importedRuleList.getLength(); j++) {
                    CSSRule cssRule = importedRuleList.item(j);

                    if (cssRule instanceof CSSStyleRule) {
                        CSSStyleRule cssStyleRule = (CSSStyleRule) cssRule;

                        if (cssStyleRule.getStyle().getLength() == 0) {

                            if (LOG.isTraceEnabled()) {
                                LOG.trace("Remove rule '" + cssRule + "'");
                            }

                            continue;
                        }
                    }
                    if (rule instanceof CSSCharsetRule) {
                        continue;
                    }

                    CSSRule newRule = ruleList.insert(cssRule.toString());
                    ruleList.next();

                    ((CSSOMObject) newRule).setUserData(IMPORTED_RULE_PROPERTY,
                            cssRule);
                }

            } catch (IOException ex) {
                LOG.error("Can not inline css '" + importedPath + "'", ex);
            }
        }

        if (LOG.isTraceEnabled()) {
            LOG.debug("Css content of '" + basePath + "': " + styleSheet);
        }
    }

    private boolean isVirtualRule(CSSOMObject styleRule) {
        return styleRule.getUserData(VIRTUAL_RULE_PROPERTY) != null;
    }

    private void setVirtualRule(CSSOMObject styleRule) {
        styleRule.setUserData(VIRTUAL_RULE_PROPERTY, Boolean.TRUE);
    }

    private void alterCssValue(IParserContext parserContext, IPath basePath,
            CSSValue value, IPath relativePath) {

        if (value instanceof CSSPrimitiveValue) {
            alterCssPrimitiveValue(parserContext, basePath,
                    (CSSPrimitiveValue) value, relativePath);
        }

        if (value instanceof CSSValueList) {
            CSSValueList list = (CSSValueList) value;

            for (int k = 0; k < list.getLength(); k++) {
                alterCssPrimitiveValue(parserContext, basePath,
                        (CSSPrimitiveValue) list.item(k), relativePath);
            }
        }
    }

    private void verifySelectors(SelectorList selectors, String source) {
        for (int i = 0; i < selectors.getLength(); i++) {
            Selector selector = selectors.item(i);

            verifySelector(selector, source);
        }
    }

    private void verifySelector(Selector selector, String source) {
        if (selector instanceof ConditionalSelector) {
            ConditionalSelector conditionalSelector = (ConditionalSelector) selector;

            SimpleSelector simpleSelector = conditionalSelector
                    .getSimpleSelector();
            if (simpleSelector instanceof ElementSelector) {
                if ("*".equals(((ElementSelector) simpleSelector)
                        .getLocalName())) {
                    PARSING_LOG.error("* simple selector is not necessery '"
                            + selector + "'. [source:" + source + "]");
                }
            }
        }

        if (selector instanceof DescendantSelector) {
            DescendantSelector descendantSelector = (DescendantSelector) selector;

            verifySelector(descendantSelector.getAncestorSelector(), source);
            verifySelector(descendantSelector.getSimpleSelector(), source);
            return;
        }

        if (selector instanceof SiblingSelector) {
            SiblingSelector descendantSelector = (SiblingSelector) selector;

            verifySelector(descendantSelector.getSiblingSelector(), source);
            verifySelector(descendantSelector.getSelector(), source);
            return;
        }

        if (selector instanceof ElementSelector) {
            ElementSelector elementSelector = (ElementSelector) selector;

            String name = elementSelector.getLocalName().toUpperCase();

            if (VALID_ELEMENTS.contains(name) == false) {
                PARSING_LOG.error("Invalid element name '" + name + "': rule="
                        + selector + " [source:" + source + "]");
            }
        }
    }

    private void alterCssPrimitiveValue(IParserContext parserContext,
            IPath basePath, CSSPrimitiveValue value, IPath relativePath) {
        if (parserContext.isResourceURLConversionEnabled() == false
                || value.getPrimitiveType() != CSSPrimitiveValue.CSS_URI) {
            return;
        }

        String href = value.getStringValue().trim();
        if (href.length() < 1) {
            return;
        }

        if (isValidURL(href) == false) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignore import rule: absolute or protocol, # or ? detected. ("
                        + href + ")");
            }
            return;
        }

        IPath path = new Path(href);
        if (path.isAbsolute()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Path relocation: Ignore absolute path '" + path
                        + "'.");
            }
            return;
        }

        IPath contextRelativePath = basePath.removeLastSegments(1).append(path);

        if (parserContext.isVersioningEnabled()) {
            contextRelativePath = parserContext.processVersioning(basePath,
                    contextRelativePath, IContentFamily.IMAGE);
        } else if (parserContext.isMergeImportsEnabled()
                || parserContext.isProcessRulesEnabled()) {
            // La servlet de merge
            contextRelativePath = new Path("..").append(contextRelativePath);
        }

        value.setStringValue(CSSPrimitiveValue.CSS_URI,
                contextRelativePath.toString());

        if (LOG.isDebugEnabled()) {
            LOG.debug("Path relocation: change path '" + href + "' to '"
                    + contextRelativePath + "'.");
        }
    }

    private boolean isValidURL(String href) {
        if (href.length() < 1 || href.indexOf(':') >= 0
                || href.indexOf("//") >= 0 || href.charAt(0) == '/'
                || href.indexOf('#') >= 0 || href.indexOf('?') >= 0) {
            // On ne traite pas les absolus
            return false;
        }

        return true;
    }

    private void processPropertyRule(CSSStyleRule rule,
            CssPropertyListIterator declarationList) {

        // System.out.println("=> " + rule.getSelectorText());
        for (; declarationList.hasNext();) {
            Property p = declarationList.next();

            if (p.getUserData(DELETED_RULE_PROPERTY) != null
                    || p.getUserData(VIRTUAL_RULE_PROPERTY) != null) {
                continue;
            }

            String name = p.getName();
            CSSValue value = p.getValue();

            CssPropertyRule cpr = propertyRulesByName.get(name);
            // System.out.println("  '" + name + "' => " + cpr);
            if (cpr == null) {
                continue;
            }

            Set<String> prefixes = new HashSet<String>();
            for (UserAgentPropertyRule ur : cpr.listAgentRules()) {

                IPropertyRuleProcessor rp = ur.getRuleProcessorImpl();
                if (rp != null) {
                    rp.process(declarationList, ur, p);
                    continue;
                }

                String prefix = ur.getPrefix();
                if (prefix != null && prefixes.add(prefix)) {
                    declarationList.addProperty(prefix + name, value, p, true);
                    continue;
                }
            }
        }
    }

    private void processValueRule(CSSStyleRule rule,
            CssPropertyListIterator declarationList) {

        // System.out.println("=> " + rule.getSelectorText());
        for (; declarationList.hasNext();) {
            Property p = declarationList.next();

            if (p.getUserData(DELETED_RULE_PROPERTY) != null
                    || p.getUserData(VIRTUAL_RULE_PROPERTY) != null) {
                continue;
            }

            CSSValue value = p.getValue();
            short valueType = value.getCssValueType();

            if (valueType == CSSValue.CSS_PRIMITIVE_VALUE
                    || valueType == CSSValue.CSS_VALUE_LIST) {
                processValue(value, p, declarationList);
            }
        }
    }

    private void processValue(CSSValue value, Property p,
            CssPropertyListIterator declarationList) {
        short valueType = value.getCssValueType();

        if (valueType == CSSValue.CSS_VALUE_LIST) {
            CSSValueList vl = (CSSValueList) value;
            for (int i = 0; i < vl.getLength(); i++) {
                CSSValue v = vl.item(i);

                processValue(v, p, declarationList);
            }

            return;
        }

        if (valueType != CSSValue.CSS_PRIMITIVE_VALUE) {
            return;
        }

        Object v = ((CSSValueImpl) value).getValue();
        if ((v instanceof LexicalUnit) == false) {
            return;
        }

        LexicalUnit lu = (LexicalUnit) v;

        processLexicalUnit(lu, p, declarationList);
    }

    private void processLexicalUnit(LexicalUnit lu, Property p,
            CssPropertyListIterator declarationList) {

        if (lu.getLexicalUnitType() == LexicalUnit.SAC_FUNCTION) {
            String funcName = lu.getFunctionName();

            CssFunctionRule cfr = functionRulesByName.get(funcName);
            if (cfr != null) {
                Set<String> prefixes = new HashSet<String>();
                for (UserAgentPropertyRule ur : cfr.listAgentRules()) {

                    IPropertyRuleProcessor rp = ur.getRuleProcessorImpl();
                    if (rp != null) {
                        rp.process(declarationList, ur, p);
                        continue;
                    }

                    String prefix = ur.getPrefix();
                    if (prefix != null && prefixes.add(prefix)) {

                        ((LexicalUnitImpl) lu).setFunctionName(prefix
                                + funcName);

                        Property newProperty = (Property) p.clone();

                        ((LexicalUnitImpl) lu).setFunctionName(funcName);

                        declarationList.addProperty(p.getName(),
                                newProperty.getValue(), newProperty, false);

                        continue;
                    }
                }

            }
        }

        if (lu.getNextLexicalUnit() != null) {
            processLexicalUnit(lu.getNextLexicalUnit(), p, declarationList);
        }
    }

    public static void main(String[] args) throws IOException {
        CssSteadyStateParser parser = new CssSteadyStateParser();
        parser.loadCssPropertiesRules();

        File outputDir = new File(args[0]);
        File inputDir = new File(args[1]);

        if (inputDir.exists() == false) {
            return;
        }

        final String charset = "UTF-8";

        File[] sources = inputDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if (name.endsWith(".css")) {
                    return true;
                }
                return false;
            }
        });

        outputDir.mkdirs();

        IParserContext parserContext = new IParserContext() {

            @Override
            public void setLastModifiedDate(long lastModifiedDate) {
            }

            @Override
            public IPath processVersioning(IPath base, IPath path,
                    IContentFamily contentFamily) {
                return null;
            }

            @Override
            public boolean isVersioningEnabled() {
                return false;
            }

            @Override
            public boolean isProcessRulesEnabled() {
                return true;
            }

            @Override
            public boolean isMergeImportsEnabled() {
                return false;
            }

            @Override
            public boolean isResourceURLConversionEnabled() {
                return false;
            }

            @Override
            public IResourceLoaderFactory getResourceLoaderFactory() {
                return null;
            }

            @Override
            public IOperationContentLoader getOperationContentLoader() {
                return null;
            }

            @Override
            public long getLastModifiedDate() {
                return 0;
            }

            @Override
            public FacesContext getFacesContext() {
                return null;
            }

            @Override
            public String getCharset() {
                return charset;
            }

            @Override
            public void enableProcessRules() {

            }

            @Override
            public void enableMergeImports() {

            }
        };

        for (File input : sources) {
            File output = new File(outputDir, input.getName());

            FileInputStream fin = new FileInputStream(input);
            InputStreamReader reader = new InputStreamReader(fin);

            StringAppender sa = new StringAppender(32000);

            char[] buf = new char[1024];
            for (;;) {
                int r = reader.read(buf, 0, buf.length);
                if (r < 1) {
                    break;
                }

                sa.append(buf, 0, r);
            }

            reader.close();

            String normalized = parser.normalizeBuffer(
                    input.toURI().toString(), sa.toString(), parserContext);

            normalized = normalized.replace("\r", "");

            FileOutputStream fout = new FileOutputStream(output);
            OutputStreamWriter writer = new OutputStreamWriter(fout, charset);
            writer.write("@charset \"" + charset + "\";\n");
            writer.write(normalized);
            writer.close();
        }

    }
}