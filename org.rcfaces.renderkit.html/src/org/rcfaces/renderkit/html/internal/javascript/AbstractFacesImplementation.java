/*
 * $Id: AbstractFacesImplementation.java,v 1.2 2013/07/03 12:25:10 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.javascript;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
 */
public abstract class AbstractFacesImplementation {
    

    private static final Log LOG = LogFactory
            .getLog(AbstractFacesImplementation.class);

    private static AbstractFacesImplementation singleton;

    private static final AbstractFacesImplementation UNKNOWN = new AbstractFacesImplementation() {
        

        public String getJavaScriptModuleName() {
            return null;
        }

    };

    static synchronized AbstractFacesImplementation get() {
        if (singleton != null) {
            return singleton;
        }

        try {
            Class clazz = AbstractFacesImplementation.class.getClassLoader()
                    .loadClass("com.sun.faces.RIConstants");

            String version = clazz.getPackage().getImplementationVersion();

            if (version != null) {
                LOG.info("Faces RI version '" + version + "' detected !");
            }

            /*
             * Désormais on envoie tous les inputs Hidden lors de requetes AJAX
             * Aussi singleton = new AbstractFacesImplementation() { private
             * static final String REVISION = "$Revision: 1.2 $";
             * 
             * public String getJavaScriptModuleName() { return
             * "com.sun.faces.RI_1_1_2"; } };
             */
        } catch (Throwable th) {
            // Pas de RI !
        }

        if (singleton == null) {
            singleton = UNKNOWN;
        }

        return singleton;
    }

    public abstract String getJavaScriptModuleName();

}
