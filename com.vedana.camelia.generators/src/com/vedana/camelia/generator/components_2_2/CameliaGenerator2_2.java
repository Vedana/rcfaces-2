/*
 * $Id: CameliaGenerator2_2.java,v 1.1 2014/02/05 16:03:40 jbmeslin Exp $
 * 
 * $Log: CameliaGenerator2_2.java,v $
 * Revision 1.1  2014/02/05 16:03:40  jbmeslin
 * passage en versio, 2_2 + maven
 *
 * Revision 1.4  2013/12/11 10:16:44  jbmeslin
 * Declaration du behavior RCFaces
 *
 * Revision 1.3  2013/01/28 16:39:45  jbmeslin
 * Ajout du constructeur dans l'énumération des porperties
 *
 * Revision 1.2  2013/01/11 15:31:51  jbmeslin
 * *** empty log message ***
 *
 * Revision 1.1  2011/10/12 15:54:01  oeuillot
 * Fork JSF 2.1
 *
 * Revision 1.3  2010/05/20 12:07:25  oeuillot
 * *** empty log message ***
 *
 * Revision 1.2  2009/01/15 15:16:52  oeuillot
 * Refonte complete du compilateur pour le gï¿½nï¿½rateur JS->JAVA
 *
 * Revision 1.1  2008/01/02 09:40:19  oeuillot
 * *** empty log message ***
 *
 * Revision 1.5  2006/12/18 15:14:02  oeuillot
 * *** empty log message ***
 *
 * Revision 1.4  2006/11/27 14:24:11  oeuillot
 * *** empty log message ***
 *
 * Revision 1.3  2006/11/21 13:37:20  oeuillot
 * *** empty log message ***
 *
 * Revision 1.2  2006/11/14 17:06:38  oeuillot
 * *** empty log message ***
 *
 * Revision 1.1  2006/11/14 17:06:10  oeuillot
 * *** empty log message ***
 *
 */
package com.vedana.camelia.generator.components_2_2;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nu.xom.Element;
import nu.xom.Elements;

import com.vedana.camelia.generator.components.CapabilitiesRepository;
import com.vedana.camelia.generator.components.IFactory;
import com.vedana.camelia.generator.components_1_2.CameliaGenerator1_2;

/**
 * @author Olivier Oeuillot
 * @version $Revision: 1.1 $
 */
public class CameliaGenerator2_2 extends CameliaGenerator1_2 {

    protected CameliaGenerator2_2(Config config) throws MalformedURLException {
        super(config);
    }

    public static void main(String args[]) throws Exception {

        Config config = parseConfig(args);

        CameliaGenerator2_2 generator = new CameliaGenerator2_2(config);

        generator.generate(config);
    }

    @Override
    protected IFactory createFactory() {
        return new Factory2_2();
    }

    @Override
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
            } else {
            	properties.put("DEFAULT", "default");
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

            out.println("public enum Properties {");

            if (renderKit.mergedProperties == false) {
                List<String> l = new ArrayList<String>(properties.keySet());
                Collections.sort(l);
                for (Iterator<String> it = l.iterator(); it.hasNext();) {
                    String name = it.next();
                    String value = name; // properties.get(name);

                    out.print("\t" + value + "(\""+properties.get(value)+"\")");

                    if (it.hasNext()) {
                        out.println(",");
                    } else {
                        out.println(";");
                    }
                }
                
                out.println("\n\tprivate Properties(String fieldKey) { \n\t\t this.fieldKeys = fieldKey; \n\t}");
            	
                out.println("\n\tprivate final String fieldKeys;\n");
            	
                out.println("\t@Override \n\tpublic String toString() { \n\t	return fieldKeys; \n\t}");
            }
            
           

            out.println("}");

            out.close();
            fout.close();
        }
    }
    
    
    private final Map<String, Behavior> behaviors = new HashMap<String, Behavior>();
    
    @Override
    public void parse(CapabilitiesRepository cr, Element xml, Config config)
    		throws IntrospectionException {
    	super.parse(cr, xml, config);
    	
    	Elements behavior = xml.getChildElements("behavior");
        for (int i = 0; i < behavior.size(); i++) {
            Element element = behavior.get(i);

            Behavior behaviorObject = new Behavior(element);
            
            behaviors.put(behaviorObject.getId(), behaviorObject);
        }
    	
    }
    
    @Override
    protected void writeBehaviors(PrintWriter out, RenderKit renderKit) {
    	//pas de super pas behavior avant
    	
    	for (String id : behaviors.keySet())  {
			Behavior b =  behaviors.get(id);
			
		 	out.println("\t<behavior>");
            out.println("\t\t<behavior-id>"+id+"</behavior-id>");
            out.println("\t\t<behavior-class>"+b.getBehaviorClass()+"</behavior-class>");
            out.println("\t</behavior>");
		}
    	
    	
    	
    }

    public static String transformCode(String code) {
        String c = code;

        c = c.replaceAll("engine\\.getValueBindingProperty\\(",
                "getEngine().getValueExpressionProperty(");

        c = c.replaceAll("engine\\.getProperty\\(", "getStateHelper().eval(");
        c = c.replaceAll("engine\\.getStringProperty\\(",
                "(String)getStateHelper().eval(");

        c = c.replaceAll("engine\\.setProperty\\(", "getStateHelper().put(");

        c = c.replaceAll("engine\\.setValue\\(", "getStateHelper().put(");

        c = c.replaceAll("engine\\.getValue\\(", "getStateHelper().eval(");
        
        
        //c = c.replaceAll("\\(Properties\\.VALUE", "(Properties.VALUE.toString().toLowerCase()");

       // c = c.replaceAll("engine\\.getValue\\(Properties\\.VALUE", "getStateHelper().eval(Properties.VALUE.toString().toLowerCase()");

        c = c.replaceAll("engine\\.isPropertySetted\\(", "isPropertySetted(");

        c = c.replaceAll("engine", "getComponentEngine()");

        if (false) {
            int nextIdx = -1;
            for (;;) {
                int idx = c.indexOf("Properties.", nextIdx);
                if (idx < 0) {
                    break;
                }

                int idx2 = c.indexOf('.', idx) + 1;
                int idx3 = idx2;

                String p = "";
                boolean upper = false;
                for (; idx3 < c.length(); idx3++) {
                    char ch = c.charAt(idx3);

                    if (ch == '_') {
                        upper = true;
                        continue;
                    }

                    if (Character.isJavaIdentifierPart(ch)) {
                        if (upper) {
                            p += Character.toUpperCase(ch);
                            upper = false;
                            continue;
                        }

                        p += Character.toLowerCase(ch);

                        continue;
                    }
                    break;
                }

                String prop = c.substring(idx2, idx3);

                prop = "Properties." + p;

                c = c.substring(0, idx) + prop + c.substring(idx3);
                nextIdx = idx + 2;
            }
        }

        return CameliaGenerator1_2.transformCode(c);

    }

    public static String convertAttributeName(String id) {
        return id; // Character.toUpperCase(id.charAt(0)) + id.substring(1);
    }

    protected void writeConverterHandlerImports(PrintWriter w) {
        w.println("import javax.faces.view.facelets.ConverterConfig;");
    }

    protected void addDefaultProperties(Map<String, String> properties) {
        super.addDefaultProperties(properties);

        properties.put("SAVED_STATE", "savedState");
    }
}