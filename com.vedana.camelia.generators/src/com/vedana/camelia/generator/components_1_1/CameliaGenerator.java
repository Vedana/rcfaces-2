/*
 * $Id: CameliaGenerator.java,v 1.9 2014/01/13 15:45:06 oeuillot Exp $
 */
package com.vedana.camelia.generator.components_1_1;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.ParseException;
import nu.xom.ValidityException;

import com.vedana.camelia.generator.components.AttributeDoc;
import com.vedana.camelia.generator.components.CapabilitiesRepository;
import com.vedana.camelia.generator.components.IFactory;
import com.vedana.camelia.generator.components.Identifier;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.9 $
 */
public class CameliaGenerator {

    private final CapabilitiesRepository capabilitiesRepository;

    private final LinkedHashMap<Identifier, Component1_1> components = new LinkedHashMap<Identifier, Component1_1>();

    private final Map<Identifier, FacesComponent> facesComponents = new HashMap<Identifier, FacesComponent>();

    private final Map<String, Method1_1> methods = new HashMap<String, Method1_1>();

    private final Map<String, Tag> tags = new HashMap<String, Tag>();

    private Map<String, AttributeDoc> attributesDoc = new HashMap<String, AttributeDoc>();

    private Map<String, ComponentDoc> componentsDoc = new HashMap<String, ComponentDoc>();

    private Map<String, TagLibDoc> tagLibsDoc = new HashMap<String, TagLibDoc>();

    public static final Set<String> componentsAutodoc = new HashSet<String>();

    public static final Set<String> attributesAutodoc = new HashSet<String>();

    public static final String CAMELIA_CORE_PACKAGE = "org.rcfaces.core";

    public static final String CAMELIA_COMPONENT_ID = "org.rcfaces";

    public static final String CAMELIA_RENDERKIT_PACKAGE = "org.rcfaces.renderkit";

    private static final String CORE_PHASE_LISTENERS[] = {
            "org.rcfaces.core.internal.config.ServicesRegistryImpl",
            "org.rcfaces.core.internal.listener.CameliaPhaseListener" };

    private static final String HTML_PHASE_LISTENERS[] = { "org.rcfaces.renderkit.html.internal.phase.ParameterDecoderPhaseListener" };

    private static final boolean DECLARE_PATCHED_NUMBER_CONVERTER = false;

    private static final String NUMBER_CONVERTERS[] = { "Byte", "Short",
            "Integer", "Long", "Float", "Double" };

    public static final String PROPERTY_PREFIX = "";

    private static final Set<String> INTEGER_TYPE = new HashSet<String>();
    static {
        INTEGER_TYPE.add("Byte");
        INTEGER_TYPE.add("Short");
        INTEGER_TYPE.add("Integer");
        INTEGER_TYPE.add("Long");
    }

    private static final IFactory FACTORY = new IFactory() {

        public Capability1_1 newCapability(String id, BeanInfo beanInfo,
                boolean clearCachedValue, boolean forceComponent,
                boolean required, boolean onlyValueBinding, String defaultValue)
                throws IntrospectionException {
            return new Capability1_1(id, beanInfo, clearCachedValue,
                    forceComponent, required, onlyValueBinding, defaultValue);
        }

        public Action1_1 newAction(String id, String type, boolean defaultAction) {
            return new Action1_1(id, type, defaultAction);
        }

        public Attribute1_1 newAttribute(Element attributeXml) {
            return new Attribute1_1(attributeXml);
        }

        public Component1_1 newComponent(CapabilitiesRepository cr,
                Element componentXml, Map<String, RenderKit> renderKits,
                boolean bootStrap, Map<Identifier, Component1_1> components,
                Map<Identifier, FacesComponent> facesComponents)
                throws IntrospectionException {
            return new Component1_1(cr, componentXml, renderKits, bootStrap,
                    components, facesComponents);
        }

        public ComponentDoc newComponentDoc(Element tagXml) {
            return new ComponentDoc(tagXml);
        }

        public FacesComponent newFacesComponent(Element componentXml,
                Map<String, RenderKit> renderKits) {
            return new FacesComponent(this, componentXml, renderKits);
        }

        public FacesField newFacesField(int modifiers, String attId,
                String type, String value) {
            return new FacesField(modifiers, attId, type, value);
        }

        public FacesTagAttribute newFacesTagAttribute(String attId,
                String type, boolean required, boolean generate) {
            return new FacesTagAttribute(attId, type, required, generate);
        }

        public Method1_1 newMethod(Element methodXml) {
            return new Method1_1(methodXml);
        }

        public Tag newTag(Element tagXml, Map<String, RenderKit> renderKits) {
            return new Tag(tagXml, renderKits);
        }

        public TagLibDoc newTagLibDoc(Element tagXml) {
            return new TagLibDoc(tagXml);
        }

    };

    public File templateBase;

    private IFactory factory;

    protected CameliaGenerator(Config config) throws MalformedURLException {

        List<URL> l = new ArrayList<URL>();
        if (config.classPath != null) {
            for (StringTokenizer st = new StringTokenizer(config.classPath,
                    File.pathSeparator); st.hasMoreTokens();) {
                File f = new File(st.nextToken());
                if (f.exists() == false) {
                    System.err.println("*** File not found: " + f);
                    continue;
                }
                l.add(f.toURL());
            }
        }

        URLClassLoader classLoader = new URLClassLoader(l.toArray(new URL[l
                .size()]));

        this.capabilitiesRepository = new CapabilitiesRepository(classLoader,
                getFactory());

    }

    public void parse(CapabilitiesRepository cr, Element xml, Config config)
            throws IntrospectionException {
        Elements facesComponentsXml = xml.getChildElements("facesComponent");
        for (int i = 0; i < facesComponentsXml.size(); i++) {
            Element componentXml = facesComponentsXml.get(i);

            FacesComponent component = getFactory().newFacesComponent(
                    componentXml, config.renderKits);
            facesComponents.put(component.getComponentType(), component);
        }

        Elements componentsXml = xml.getChildElements("component");
        for (int i = 0; i < componentsXml.size(); i++) {
            Element componentXml = componentsXml.get(i);

            Component1_1 component = getFactory().newComponent(cr,
                    componentXml, config.renderKits, config.bootStrap,
                    components, facesComponents);
            components.put(component.getIdentifier(), component);
        }

        Elements methodsXml = xml.getChildElements("method");
        for (int i = 0; i < methodsXml.size(); i++) {
            Element methodXml = methodsXml.get(i);

            Method1_1 method = getFactory().newMethod(methodXml);
            methods.put(method.getSignature(), method);
        }

        Elements tagsXml = xml.getChildElements("tag");
        for (int i = 0; i < tagsXml.size(); i++) {
            Element tagXml = tagsXml.get(i);

            Tag tag = getFactory().newTag(tagXml, config.renderKits);
            tags.put(tag.getName(), tag);
        }

        Elements attributesDocXML = xml.getChildElements("attributeDoc");
        for (int i = 0; i < attributesDocXML.size(); i++) {
            Element tagXml = attributesDocXML.get(i);

            AttributeDoc attributeDoc = new AttributeDoc(tagXml);
            attributesDoc.put(attributeDoc.getName(), attributeDoc);
        }

        Elements componentsDocXML = xml.getChildElements("componentDoc");
        for (int i = 0; i < componentsDocXML.size(); i++) {
            Element tagXml = componentsDocXML.get(i);

            ComponentDoc componentDoc = getFactory().newComponentDoc(tagXml);
            componentsDoc.put(componentDoc.getName(), componentDoc);
        }

        Elements taglibs = xml.getChildElements("taglib");
        for (int i = 0; i < taglibs.size(); i++) {
            Element tagXml = taglibs.get(i);

            TagLibDoc tagLibDoc = getFactory().newTagLibDoc(tagXml);
            tagLibsDoc.put(tagLibDoc.getName(), tagLibDoc);
        }
    }

    private File getDest(Component1_1 component) {

        RenderKit renderKit = component.getPackageRenderKit();

        return renderKit.buildDir;
    }

    private void buildBootStrap(Map<String, RenderKit> renderKits)
            throws IOException {

        for (Iterator<Component1_1> it = components.values().iterator(); it
                .hasNext();) {
            Component1_1 component = it.next();

            if (component.getPackageRenderKit().buildDir == null) {
                continue;
            }

            File cFile = new File(component.getPackageRenderKit().buildDir,
                    component.getComponentFileName());

            System.out.println("[" + component.getPackageRenderKit().getName()
                    + "] Generating bootStrap component '" + component.getId()
                    + "' => " + cFile);

            cFile.getParentFile().mkdirs();

            FileOutputStream fout = new FileOutputStream(cFile);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fout,
                    "utf8"));

            component.writeBootStrap(out);

            out.close();
            fout.close();
        }

    }

    private void buildComponents(Map<String, RenderKit> renderKits)
            throws IOException {

        Map<String, String> events = new HashMap<String, String>();

        for (Iterator<Component1_1> it = components.values().iterator(); it
                .hasNext();) {
            Component1_1 component = it.next();

            component.updateCapabilities(components, facesComponents,
                    capabilitiesRepository);
        }

        for (Iterator<Component1_1> it = components.values().iterator(); it
                .hasNext();) {
            Component1_1 component = it.next();

            if (component.getPackageRenderKit().buildDir == null) {
                continue;
            }

            File cFile = new File(component.getPackageRenderKit().buildDir,
                    component.getComponentFileName());

            System.out.println("[" + component.getPackageRenderKit().getName()
                    + "] Generating component '" + component.getId() + "' => "
                    + cFile);

            cFile.getParentFile().mkdirs();

            FileOutputStream fout = new FileOutputStream(cFile);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fout,
                    "utf8"));

            component.writeSource(out, components, events, methods,
                    facesComponents, componentsDoc, attributesDoc);

            out.close();
            fout.close();
        }

        /*
         * storeEvents(events, new File(dest, packageDest + "Events.java"));
         */

        for (Iterator<RenderKit> it = renderKits.values().iterator(); it
                .hasNext();) {

            RenderKit renderKit = it.next();
            String renderKitName = renderKit.getName();

            if (renderKit.buildDir == null) {
                continue;
            }

            File dest = new File(renderKit.buildDir, renderKit.metaInfFolder);

            // File destHtml = new File(destHtml, CAMELIA_HTML_FOLDER);
            makeTld(dest, "rcfaces-" + renderKitName + ".tld", renderKit);
            makeFacesConfig(dest, "faces-config.xml", renderKit);

            if (renderKit.generateFacelets == false) {
                continue;
            }
            makeFaceletsTaglib(dest,
                    "rcfaces-" + renderKitName + ".taglib.xml", renderKit);
        }
    }

    private void makeTld(File dest, String tldFileName, RenderKit renderKit)
            throws IOException {
        File file = new File(dest, tldFileName);

        System.out.println("[" + renderKit.getName() + "] Generating TLD => "
                + file);

        file.getParentFile().mkdirs();

        FileOutputStream fout = new FileOutputStream(file);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(fout, "utf8"));

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

        writeTldNameSpace(out);

        writeTlibVersion(out);

        writeJspVersion(out);

        out.println("\t<short-name>" + renderKit.prefix + "</short-name>");
        out.println("\t<uri>" + renderKit.url + "</uri>");

        TagLibDoc tagLibDoc = tagLibsDoc.get(renderKit);
        if (tagLibDoc != null) {
            tagLibDoc.writeTld(out);
        }

        for (Iterator<Component1_1> it = components.values().iterator(); it
                .hasNext();) {
            Component1_1 component = it.next();

            if (renderKit.equals(component.getPackageRenderKit()) == false) {
                continue;
            }

            component.writeTld(out, components, facesComponents, componentsDoc,
                    attributesDoc);
        }

        for (Iterator<Tag> it = tags.values().iterator(); it.hasNext();) {
            Tag tag = it.next();

            if (renderKit.equals(tag.getRenderKit()) == false) {
                continue;
            }

            tag.writeTld(out);
        }

        if (NUMBER_CONVERTERS != null && renderKit.getName().equals("core")) {
            for (int i = 0; i < NUMBER_CONVERTERS.length; i++) {
                String type = NUMBER_CONVERTERS[i];

                writeConverter(out, type, INTEGER_TYPE.contains(type));
            }
        }

        out.println("</taglib>");
        out.close();
        fout.close();
    }

    protected void writeJspVersion(PrintWriter out) {
        out.println("\t<jsp-version>1.2</jsp-version>");
    }

    protected void writeTlibVersion(PrintWriter out) {
        out.println("\t<tlib-version>1.0</tlib-version>");
    }

    protected void writeTldNameSpace(PrintWriter out) {
        out.println("<!DOCTYPE taglib PUBLIC \"-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN\" \"http://java.sun.com/dtd/web-jsptaglibrary_1_2.dtd\">");
        out.println("<taglib>");
    }

    private void writeConverter(PrintWriter out, String type,
            boolean integerType) {
        out.println("\t<tag>");
        out.println("\t\t<description>Register a "
                + type
                + "Converter instance on the UIComponent associated\n with the closest parent UIComponent custom action.</description>");
        out.println("\t\t<name>convert" + type + "</name>");
        out.println("\t\t<tag-class>org.rcfaces.core.internal.taglib.Convert"
                + type + "Tag</tag-class>");
        out.println("\t\t<tei-class>org.rcfaces.core.internal.taglib.CameliaTagExtraInfo</tei-class>");
        out.println("\t\t<body-content>empty</body-content>");

        writeConverterTldAttribute0(
                out,
                "currencyCode",
                false,
                false,
                "ISO 4217 currency code, applied only when formatting currencies.",
                "java.lang.String");
        writeConverterTldAttribute0(out, "currencySymbol", false, false,
                "Currency symbol, applied only when formatting currencies.",
                "java.lang.String");
        writeConverterTldAttribute0(
                out,
                "groupingUsed",
                false,
                false,
                "Flag specifying whether formatted output will contain grouping separators.  Expressions must  evaluate to a boolean.  Default value is true.",
                "boolean");
        writeConverterTldAttribute0(
                out,
                "locale",
                false,
                false,
                "Locale whose predefined styles for numbers are used during formatting and parsing.  If not specified, the Locale returned by  FacesContext.getViewRoot().getLocale() will be used.  Expressions must evaluate to a java.util.Locale.",
                "java.util.Locale");
        if (integerType == false) {
            writeConverterTldAttribute0(
                    out,
                    "integerOnly",
                    false,
                    false,
                    "Flag specifying whether only the integer part of the value will be formatted and parsed. Expressions must evaluate to a boolean.\n Default value is false.",
                    "boolean");
            writeConverterTldAttribute0(
                    out,
                    "maxFractionDigits",
                    false,
                    false,
                    "Maximum number of digits that will be formatted in the fractional portion of the output.  Expressions  must evaluate to an int.",
                    "int");
            writeConverterTldAttribute0(
                    out,
                    "minFractionDigits",
                    false,
                    false,
                    "Minimum number of digits that will be formatted in the fractional portion of the output.  Expressions must evaluate to an int.",
                    "int");
        }
        writeConverterTldAttribute0(
                out,
                "maxIntegerDigits",
                false,
                false,
                "Maximum number of digits that will be formatted in the integer portion of the output.  Expressions must evaluate to an int.",
                "int");
        writeConverterTldAttribute0(
                out,
                "minIntegerDigits",
                false,
                false,
                "Minimum number of digits that will be formatted in the integer portion of the output.  Expressions must evaluate to an int.",
                "int");
        writeConverterTldAttribute0(
                out,
                "pattern",
                false,
                false,
                "Custom formatting pattern which determins how the number string should be formatted and parsed.",
                "java.lang.String");
        writeConverterTldAttribute0(
                out,
                "type",
                false,
                false,
                "Specifies how the number string will be formatted and parsed.  Valid values are \"number\", \"currency\", and \"percentage\".  Default value is \"number\".",
                "java.lang.String");
        writeConverterTldAttribute0(out, "defaultValue", false, false,
                "Specifies the default value if the string is null or empty.",
                "java.lang.Number");
        out.println("\t</tag>");

        CameliaGenerator.componentsAutodoc.add("convert" + type);

    }

    private void writeConverterTldAttribute0(PrintWriter out,
            String attributeName, boolean required, boolean rtexprvalue,
            String doc, String type) {
        out.println("\t\t<attribute>");

        if (doc != null) {
            out.println("\t\t\t<description><![CDATA[\n" + attributeName
                    + "]]></description>");
        }

        out.println("\t\t\t<name>" + attributeName + "</name>");
        if (required) {
            out.println("\t\t\t<required>false</required>");
        }

        writeConverterTldAttribute(out, rtexprvalue, type);

        out.println("\t\t</attribute>");

    }

    protected void writeConverterTldAttribute(PrintWriter out,
            boolean rtexprvalue, String type) {

        if (rtexprvalue) {
            out.println("\t\t\t<rtexprvalue>false</rtexprvalue>");
        }
    }

    private void makeFacesConfig(File dest, String facesConfigFileName,
            RenderKit renderKit) throws IOException {
        File file = new File(dest, facesConfigFileName);

        System.out.println("[" + renderKit.getName()
                + "] Generating FacesConfig => " + file);

        FileOutputStream fout = new FileOutputStream(file);
        PrintWriter out = new PrintWriter(new OutputStreamWriter(fout, "utf8"));

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE faces-config PUBLIC \"-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.1//EN\" \"http://java.sun.com/dtd/web-facesconfig_1_1.dtd\">");
        out.println("<faces-config>");

        if (renderKit.equals("core")) {
            writeCoreApplication(out);

            // out.println("\t<factory />");
        }

        for (Iterator<Component1_1> it = components.values().iterator(); it
                .hasNext();) {
            Component1_1 component = it.next();

            if (renderKit.equals(component.getPackageRenderKit()) == false) {
                continue;
            }

            component.writeFacesConfig(out, componentsDoc, attributesDoc);
        }

        writeOtherFacesComponentComponent(out, renderKit);

        if (renderKit.getName().equals("core")) {
            if (DECLARE_PATCHED_NUMBER_CONVERTER) {
                out.println("\t<converter>");
                out.println("\t\t<converter-id>javax.faces.Number</converter-id>");
                out.println("\t\t<converter-class>com.vedana.faces.internal.converter.PatchedNumberConverter</converter-class>");
                out.println("\t</converter>");
            }

            if (NUMBER_CONVERTERS != null) {
                for (int i = 0; i < NUMBER_CONVERTERS.length; i++) {
                    String type = NUMBER_CONVERTERS[i];

                    out.println("\t<converter>");
                    out.println("\t\t<converter-id>org.rcfaces." + type
                            + "</converter-id>");
                    out.println("\t\t<converter-class>org.rcfaces.core.converter."
                            + type + "Converter</converter-class>");
                    out.println("\t</converter>");

                }
            }

        }

        System.out.println("Build for familly '" + renderKit.getName()
                + "' => " + renderKit.renderKitPackage);

        if (renderKit != null && renderKit.renderKitPackage != null) {

            boolean first = true;

            for (Iterator<Component1_1> it = components.values().iterator(); it
                    .hasNext();) {
                Component1_1 component = it.next();

                boolean generateRender = false;

                if (renderKit.renderRenderKitsName.contains(component
                        .getPackageRenderKit().getName())) {
                    generateRender = true;

                } else if (component.packageKit == renderKit) {
                    generateRender = true;
                }
                if (component.render == false) {
                    generateRender = false;
                }

                System.out.println("  " + component.getId() + " => "
                        + generateRender);

                if (generateRender == false) {
                    continue;
                }

                if (first) {
                    first = false;
                    out.println("\t<render-kit>");
                }

                component.writeFacesConfigRenderer(out, renderKit);

                /*
                 * File d=new File(dest, component.getRendererFileName()); if
                 * (d.exists()) { continue; }
                 * 
                 * FileWriter fout2 = new FileWriter(d); PrintWriter out2 = new
                 * PrintWriter(fout2);
                 * 
                 * component.writeRendererSkeletton(out2);
                 * 
                 * out2.close(); fout2.close();
                 */
            }

            if (first == false) {
                out.println("\t</render-kit>");
            }
        }

        if (renderKit.phaseListeners != null) {
            out.println("\t<lifecycle>");
            for (int i = 0; i < renderKit.phaseListeners.length; i++) {
                String phaseListener = renderKit.phaseListeners[i];

                out.println("\t\t<phase-listener>" + phaseListener
                        + "</phase-listener>");
            }
            out.println("\t</lifecycle>");
        }

        writeBehaviors(out, renderKit); 

        out.println("</faces-config>");
        out.close();
        fout.close();
    }

    protected void writeBehaviors(PrintWriter out, RenderKit renderKit) {
		
	}

    protected void writeOtherFacesComponentComponent(PrintWriter out,
            RenderKit renderKit) {
    }

    protected void writeCoreApplication(PrintWriter out) {
        out.println("\t<application>");
        out.println("\t\t<state-manager>org.rcfaces.core.util.ViewSessionPatchStateManager</state-manager>");
        out.println("\t\t<locale-config/>");
        out.println("\t</application>");
    }

    private void makeFaceletsTaglib(File dest, String faceletsTaglib,
            RenderKit renderKit) throws IOException {
        File file = new File(dest, faceletsTaglib);

        System.out.println("[" + renderKit.getName()
                + "] Generating FaceletsTaglib => " + file);

        PrintWriter out = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"));

        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.println("<!DOCTYPE facelet-taglib PUBLIC \"-//Sun Microsystems, Inc.//DTD Facelet Taglib 1.0//EN\" \"http://java.sun.com/dtd/facelet-taglib_1_0.dtd\">");
        out.println("<facelet-taglib>");

        out.println("\t<namespace>http://rcfaces.org/" + renderKit.name
                + "</namespace>");

        for (Iterator<Component1_1> it = components.values().iterator(); it
                .hasNext();) {
            Component1_1 component = it.next();

            if (renderKit.equals(component.getPackageRenderKit()) == false) {
                continue;
            }

            component.writeFaceletComponent(out, components);
        }

        if (NUMBER_CONVERTERS != null && renderKit.getName().equals("core")
                && renderKit.buildDir != null) {
            for (int i = 0; i < NUMBER_CONVERTERS.length; i++) {
                String type = NUMBER_CONVERTERS[i];

                out.println("\t<tag>");
                out.println("\t\t<tag-name>convert" + type + "</tag-name>");
                out.println("\t\t<converter>");
                out.println("\t\t\t<converter-id>org.rcfaces." + type
                        + "</converter-id>");
                out.println("\t\t\t<handler-class>org.rcfaces.core.internal.facelets."
                        + type + "ConverterHandler</handler-class>");
                out.println("\t\t</converter>");
                out.println("\t</tag>");

            }
        }

        for (Iterator<Tag> it = tags.values().iterator(); it.hasNext();) {
            Tag tag = it.next();

            if (tag.getRenderKit() != null) {
                if (tag.getRenderKit().equals(renderKit) == false) {
                    continue;
                }
            } else if (renderKit != null) {
                continue;
            }

            tag.writeFaceletComponent(out);
        }

        out.println("</facelet-taglib>");
        out.close();
    }

    private void buildTags(Map<String, RenderKit> renderKits)
            throws IOException {

        for (Iterator<Component1_1> it = components.values().iterator(); it
                .hasNext();) {
            Component1_1 component = it.next();

            if (component.getPackageRenderKit().buildDir == null) {
                continue;
            }

            File cFile = new File(component.getPackageRenderKit().buildDir,
                    component.getTagFileName());

            cFile.getParentFile().mkdirs();

            FileOutputStream fout = new FileOutputStream(cFile);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fout,
                    "utf8"));

            component.writeTag(out, components, methods, facesComponents);

            out.close();
            fout.close();
        }

    }

    protected void storeProperties(Map<String, RenderKit> renderKits)
            throws IOException {

        for (Iterator<RenderKit> it2 = renderKits.values().iterator(); it2
                .hasNext();) {
            RenderKit renderKit = it2.next();

            if (renderKit.buildDir == null) {
                continue;
            }

            Map<String, String> properties = renderKit.properties;

            if (renderKit.name.equals("core")) {

                addDefaultProperties(properties);
            }

            String packageDest = renderKit.internalComponentPackage.replace(
                    '.', '/');
            File cFile = new File(renderKit.buildDir, packageDest
                    + "/Properties.java");

            System.out.println("[" + renderKit.getName()
                    + "] Generating properties => " + cFile);

            cFile.getParentFile().mkdirs();

            FileOutputStream fout = new FileOutputStream(cFile);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fout,
                    "utf8"));

            out.println("package " + renderKit.internalComponentPackage + ";");
            out.println("");
            out.println("import org.rcfaces.core.internal.Constants;");

            if (renderKit.name.equals("core") == false) {
                out.println("import org.rcfaces.core.internal.component.PropertiesRepository;");
            }

            out.println("");

            out.println("public class Properties {");

            if (renderKit.mergedProperties == false) {
                List<String> l = new ArrayList<String>(properties.keySet());
                Collections.sort(l);
                for (Iterator<String> it = l.iterator(); it.hasNext();) {
                    String name = it.next();
                    String value = properties.get(name);

                    out.println("\tpublic static final String " + name
                            + " = \"" + value + "\";");
                }

                out.println("\tstatic {");
                out.println("\t\tif (Constants.COMPACTED_PROPERTY_NAME) {");
                out.print("\t\t\tPropertiesRepository.declareProperties(new String[] {");

                boolean first = true;
                for (Iterator<String> it = l.iterator(); it.hasNext();) {
                    String name = it.next();

                    if (first) {
                        first = false;

                    } else {
                        out.print(',');
                    }

                    out.print(name);
                }
                out.println("});");
                out.println("\t\t}");
                out.println("\t}");
            }

            out.println("}");

            out.close();
            fout.close();
        }
    }

    protected void addDefaultProperties(Map<String, String> properties) {
        properties.put("DATA", "data");
        // properties.put("CONVERTER_ID", "camelia.converterId");
        properties.put("VALUE", "value");
        properties.put("ITEM_DESCRIPTION", "itemDescription");
        properties.put("ITEM_DISABLED", "itemDisabled");
        properties.put("ITEM_LABEL", "itemLabel");
        properties.put("ITEM_VALUE", "itemValue");
        properties.put("RENDERED", "rendered");
        properties.put("GLOBAL_ONLY", "globalOnly");
        properties.put("SHOW_DETAIL", "showDetail");
        properties.put("SHOW_SUMMARY", "showSummary");
        properties.put("FIRST", "first");
        properties.put("IMMEDIATE", "immediate");
        properties.put("ROW_INDEX", "rowIndex");
        properties.put("CONVERTER", "converter");
    }

    public static void main(String args[]) throws Exception {

        Config config = parseConfig(args);

        CameliaGenerator generator = new CameliaGenerator(config);

        generator.generate(config);
    }

    protected static Config parseConfig(String args[]) {

        Config config = new Config();

        RenderKit mergePropertiesPackage = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("+renderKit:")) {
                String name = arg.substring(arg.indexOf(':') + 1);

                RenderKit r = config.renderKits.get(name);
                if (r == null) {
                    r = new RenderKit(name, mergePropertiesPackage);
                    config.renderKits.put(name, r);
                }

                String p = args[++i];

                if (p.equals("-") == false) {
                    r.buildDir = new File(p);

                    if (name.equals("core")) {
                        //mergePropertiesPackage = r;
                    } else {
                        //r.prefix = name;
                        // ??? r.renderRenderKitsName.add(name);
                    }
                }

                continue;
            }

            if (arg.startsWith("+renderKitPrefix:")) {
                String name = arg.substring(arg.indexOf(':') + 1);
                String prefix = args[++i];

                RenderKit r = config.renderKits.get(name);
                if (r == null) {
                    r = new RenderKit(name, mergePropertiesPackage);
                    config.renderKits.put(name, r);
                }

                r.prefix = prefix;
                continue;
            }

            if (arg.startsWith("+renderKitRender:")) {
                String name = arg.substring(arg.indexOf(':') + 1);
                String renderKitsName = args[++i];

                RenderKit r = config.renderKits.get(name);
                if (r == null) {
                    r = new RenderKit(name, mergePropertiesPackage);
                    config.renderKits.put(name, r);
                }

                StringTokenizer st = new StringTokenizer(renderKitsName, ",");
                for (; st.hasMoreTokens();) {
                    r.renderRenderKitsName.add(st.nextToken());
                }
                continue;
            }

            if (arg.startsWith("+renderKitPackage:")) {
                String name = arg.substring(arg.indexOf(':') + 1);

                RenderKit r = config.renderKits.get(name);
                if (r == null) {
                    r = new RenderKit(name, mergePropertiesPackage);
                    config.renderKits.put(name, r);
                }

                r.renderKitPackage = args[++i];
                continue;
            }

            if (arg.startsWith("+tagPackage:")) {
                String name = arg.substring(arg.indexOf(':') + 1);

                RenderKit r = config.renderKits.get(name);
                if (r == null) {
                    r = new RenderKit(name, mergePropertiesPackage);
                    config.renderKits.put(name, r);
                }

                r.tagPackage = args[++i];
                continue;
            }

            if (arg.startsWith("+componentPackage:")) {
                String name = arg.substring(arg.indexOf(':') + 1);

                RenderKit r = config.renderKits.get(name);
                if (r == null) {
                    r = new RenderKit(name, mergePropertiesPackage);
                    config.renderKits.put(name, r);
                }

                r.componentPackage = args[++i];
                continue;
            }

            if (arg.startsWith("+internalComponentPackage:")) {
                String name = arg.substring(arg.indexOf(':') + 1);

                RenderKit r = config.renderKits.get(name);
                if (r == null) {
                    r = new RenderKit(name, mergePropertiesPackage);
                    config.renderKits.put(name, r);
                }

                r.internalComponentPackage = args[++i];
                continue;
            }

            if (arg.startsWith("+propertiesPackage:")) {
                String name = arg.substring(arg.indexOf(':') + 1);

                RenderKit r = config.renderKits.get(name);
                if (r == null) {
                    r = new RenderKit(name, mergePropertiesPackage);
                    config.renderKits.put(name, r);
                }

                r.propertiesPackage = args[++i];
                continue;
            }

            if (arg.startsWith("+componentTypePrefix:")) {
                String name = arg.substring(arg.indexOf(':') + 1);

                RenderKit r = config.renderKits.get(name);
                if (r == null) {
                    r = new RenderKit(name, mergePropertiesPackage);
                    config.renderKits.put(name, r);
                }

                r.componentTypePrefix = args[++i];
                continue;
            }

            if (arg.startsWith("+metaInfFolder:")) {
                String name = arg.substring(arg.indexOf(':') + 1);

                RenderKit r = config.renderKits.get(name);
                if (r == null) {
                    r = new RenderKit(name, mergePropertiesPackage);
                    config.renderKits.put(name, r);
                }

                r.metaInfFolder = args[++i];
                continue;
            }

            if (arg.startsWith("-facelets:")) {
                String name = arg.substring(arg.indexOf(':') + 1);

                RenderKit r = config.renderKits.get(name);
                if (r == null) {
                    r = new RenderKit(name, mergePropertiesPackage);
                    config.renderKits.put(name, r);
                }

                r.generateFacelets = false;
                continue;
            }

            if (arg.startsWith("+bootStrap")) {
                config.bootStrap = true;
                continue;
            }

            if (arg.startsWith("+template")) {
                config.templateDirectory = args[++i];
                continue;
            }
            if (arg.startsWith("+classPath")) {
                config.classPath = args[++i];
                continue;
            }

            if (arg.startsWith("+repository")) {
                config.repositories.add(new File(args[++i]));
                continue;
            }

            if (arg.equals("+constants1")) {
                config.constants1 = true;
                continue;
            }
        }

        return config;
    }

    public static class Config {
        String classPath = null;

        String templateDirectory = null;

        Map<String, RenderKit> renderKits = new HashMap<String, RenderKit>();

        List<File> repositories = new ArrayList<File>();

        public boolean bootStrap;

        public boolean constants1;
    }

    protected void generate(Config config) throws ValidityException,
            ParseException, IOException, IntrospectionException {

        templateBase = new File(config.templateDirectory);

        Builder builder = new Builder();

        for (Iterator<File> it = config.repositories.iterator(); it.hasNext();) {
            File t = it.next();

            if (t.exists() == false) {
                continue;
            }

            File files[] = t.listFiles(new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    if (name.endsWith(".xml")) {
                        return true;
                    }
                    return false;
                }

            });

            if (files == null || files.length < 0) {
                continue;
            }

            for (int i = 0; i < files.length; i++) {
                Document document = builder.build(files[i]);

                parse(capabilitiesRepository, document.getRootElement(), config);
            }
        }

        if (config.bootStrap) {
            buildBootStrap(config.renderKits);

        } else if (config.renderKits.isEmpty() == false) {
            RenderKit coreRenderKit = config.renderKits.get("core");
            if (coreRenderKit != null && coreRenderKit.buildDir != null) {
                buildFacesComponents(coreRenderKit);
            }

            buildComponents(config.renderKits);
            buildTags(config.renderKits);

            buildFaceletsHandlers(config.renderKits);
        }

        storeProperties(config.renderKits);
    }

    private void buildFacesComponents(RenderKit renderKit) throws IOException {

        for (Iterator<FacesComponent> it = facesComponents.values().iterator(); it
                .hasNext();) {
            FacesComponent component = it.next();
            File cFile = new File(renderKit.buildDir,
                    component.getComponentFileName());

            System.out.println("[" + renderKit.getName()
                    + "] Generating faces component '" + component.getId()
                    + "' => " + cFile);

            cFile.getParentFile().mkdirs();

            FileOutputStream fout = new FileOutputStream(cFile);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(fout,
                    "utf-8"));

            component.writeSource(out, methods, templateBase);

            out.close();
            fout.close();
        }
    }

    private void buildFaceletsHandlers(Map<String, RenderKit> renderKits)
            throws IOException {

        for (Iterator<Component1_1> it = components.values().iterator(); it
                .hasNext();) {
            Component1_1 component = it.next();

            if (component.getPackageRenderKit().generateFacelets == false) {
                continue;
            }

            if (component.getPackageRenderKit().buildDir == null) {
                continue;
            }

            String handlerFileName = component.getFaceletsHandlerFileName();
            if (handlerFileName == null) {
                continue;
            }
            File cFile = new File(component.getPackageRenderKit().buildDir,
                    handlerFileName);

            cFile.getParentFile().mkdirs();

            component.writeFaceletsHandler(cFile, component, components);
        }

        if (NUMBER_CONVERTERS != null) {
            RenderKit coreRenderKit = renderKits.get("core");
            if (coreRenderKit != null && coreRenderKit.buildDir != null) {

                for (int i = 0; i < NUMBER_CONVERTERS.length; i++) {
                    String type = NUMBER_CONVERTERS[i];

                    String converterPackage = "org.rcfaces.core.internal.facelets";

                    File cFile = new File(coreRenderKit.buildDir,
                            converterPackage.replace('.', '/'));

                    cFile = new File(cFile, type + "ConverterHandler.java");

                    cFile.getParentFile().mkdirs();

                    writeConverterHandler(cFile, type);
                }
            }
        }
    }

    private void writeConverterHandler(File file, String type)
            throws IOException {
        PrintWriter w = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"));

        w.println("package org.rcfaces.core.internal.facelets;");
        w.println();

        writeConverterHandlerImports(w);
        w.println();

        w.println("public class " + type
                + "ConverterHandler extends CameliaConverterHandler {");
        w.println();

        w.println("\tpublic " + type
                + "ConverterHandler(ConverterConfig config) {");
        w.println("\t\tsuper(config);");
        w.println("\t}");

        w.println();

        if (writeOverride()) {
            w.println("\t@Override");

        }
        w.println("\tprotected String getConverterId() {");
        w.println("\t\treturn \"org.rcfaces." + type + "\";");
        w.println("\t}");
        w.println();
        w.println("}");

        w.close();
    }

    protected void writeConverterHandlerImports(PrintWriter w) {
        w.println("import com.sun.facelets.tag.jsf.ConverterConfig;");
    }

    protected boolean writeOverride() {
        return false;
    }

    public static String formatComment(String string, int tabNumber) {
        if (string == null) {
            return string;
        }

        string = string.trim();
        if (string.length() < 1) {
            return string;
        }

        StringTokenizer st = new StringTokenizer(string, "\n");

        StringBuffer sb = new StringBuffer(string.length() + 8);

        for (; st.hasMoreElements();) {
            if (sb.length() > 0) {
                sb.append("\n");
            }

            String tok = st.nextToken().trim();

            for (int i = 0; i < tabNumber; i++) {
                sb.append('\t');
            }
            sb.append(" * " + tok);
        }

        return sb.toString();
    }

    private static void makeCameliaDoc() throws IOException {

        PrintWriter out = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream("camelia-doc2.xml"), "utf8"));

        out.println("<camelia-components>");

        List<String> atts = new ArrayList<String>(attributesAutodoc);

        Collections.sort(atts);

        for (Iterator<String> it = atts.iterator(); it.hasNext();) {
            String id = it.next();

            out.println("\t<attributeDoc id=\"" + id + "\">");
            out.println("\t\t<doc><![CDATA[");
            out.println("]]></doc>");
            out.println("\t\t<tooltip><![CDATA[");
            out.println("]]></tooltip>");
            out.println("\t\t<wtpEditorClass></wtpEditorClass>");
            out.println("\t\t<type></type>");
            out.println("\t\t<icon></icon>");
            out.println("\t</attributeDoc>");
            out.println();
        }

        List<String> cps = new ArrayList<String>(componentsAutodoc);

        Collections.sort(cps);

        for (Iterator<String> it = cps.iterator(); it.hasNext();) {
            String id = it.next();
            out.println("\t<componentDoc id=\"" + id + "\">");
            out.println("\t\t<doc><![CDATA[");

            out.println("]]></doc>");
            out.println("\t\t<family></family>");
            out.println("\t\t<icon></icon>");
            out.println("\t</componentDoc>");
            out.println();
        }

        out.println("</camelia-components>");

        out.close();
    }

    protected final IFactory getFactory() {
        if (factory != null) {
            return factory;
        }
        factory = createFactory();

        return factory;
    }

    protected IFactory createFactory() {
        return FACTORY;
    }

    public static class RenderKit {

        public boolean generateFacelets = true;

        public String metaInfFolder;

        public String name;

        public String prefix;

        public Map<String, String> properties = new HashMap<String, String>() {

            @Override
            public String put(String key, String value) {
                if (key.toUpperCase().equals(key) == false) {
                    System.out.println("Invalid key ?");
                }
                return super.put(key, value);
            }
        };

        public File buildDir;

        public String url;

        public String renderKitPackage;

        public String componentPackage;

        public String faceletsPackage;

        public String internalComponentPackage;

        public String propertiesPackage;

        public String tagPackage;

        public String componentTypePrefix;

        public Set<String> renderRenderKitsName = new HashSet<String>();

        public boolean mergedProperties;

        private String phaseListeners[];

        public RenderKit(String name, RenderKit mergePropertiesRenderKit) {
            this.name = name;

            metaInfFolder = "META-INF";

            if ("core".equals(name)) {
                prefix = "v";

                componentPackage = CameliaGenerator.CAMELIA_CORE_PACKAGE
                        + ".component";

                faceletsPackage = CameliaGenerator.CAMELIA_CORE_PACKAGE
                        + ".internal.facelets";

                internalComponentPackage = CameliaGenerator.CAMELIA_CORE_PACKAGE
                        + ".internal.component";

                tagPackage = CameliaGenerator.CAMELIA_CORE_PACKAGE
                        + ".internal.taglib";

                componentTypePrefix = CameliaGenerator.CAMELIA_COMPONENT_ID
                        + "." + name;

                phaseListeners = CORE_PHASE_LISTENERS;

            } else {
                componentPackage = CameliaGenerator.CAMELIA_RENDERKIT_PACKAGE
                        + "." + name + ".component";

                faceletsPackage = CameliaGenerator.CAMELIA_RENDERKIT_PACKAGE
                        + "." + name + ".internal.facelets";

                internalComponentPackage = componentPackage;

                tagPackage = CameliaGenerator.CAMELIA_RENDERKIT_PACKAGE + "."
                        + name + ".internal.taglib";

                renderKitPackage = CameliaGenerator.CAMELIA_RENDERKIT_PACKAGE
                        + "." + name + ".internal.renderer";

                componentTypePrefix = CameliaGenerator.CAMELIA_COMPONENT_ID
                        + "." + name;

                if (name.equals("html")) {
                    phaseListeners = HTML_PHASE_LISTENERS;
                }

            }

            if (mergePropertiesRenderKit != null) {
                propertiesPackage = mergePropertiesRenderKit.propertiesPackage;
                properties = mergePropertiesRenderKit.properties;
                mergedProperties = true;

            } else {
                propertiesPackage = internalComponentPackage;
            }

            if ("html".equals(name)) {
                prefix = "vh";
                renderRenderKitsName.add("core");
                renderRenderKitsName.add("html");
            }

            if ("svg".equals(name)) {
                prefix = "vs";
            }

            url = "http://rcfaces.org/" + name;
        }

        public String getName() {
            return name;
        }
    }
}
