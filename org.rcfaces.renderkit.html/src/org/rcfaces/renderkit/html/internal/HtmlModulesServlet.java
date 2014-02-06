/*
 * $Id: HtmlModulesServlet.java,v 1.2 2013/01/11 15:45:00 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.rcfaces.core.internal.webapp.ConfiguredHttpServlet;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:45:00 $
 */
public class HtmlModulesServlet extends ConfiguredHttpServlet {


    private static final long serialVersionUID = -4351260850085049246L;

    private static final String MODULES_PARAMETER = Constants
            .getPackagePrefix() + ".MODULES";

    private Set modules;

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        modules = parseModules(config.getServletContext());
    }

    protected Set getModules() {
        return modules;
    }

    private static Set parseModules(ServletContext context) {
        Set<String> modulesNames = null;

        String modulesNamesList = context.getInitParameter(MODULES_PARAMETER);
        if (modulesNamesList != null && modulesNamesList.trim().length() > 0) {
            StringTokenizer st = new StringTokenizer(modulesNamesList, ",;");

            modulesNames = new HashSet<String>(st.countTokens());
            for (; st.hasMoreTokens();) {
                String moduleName = st.nextToken();

                if (moduleName.equals("*")) {
                    modulesNames = null;
                    break;
                }

                modulesNames.add(moduleName);
            }
        }

        return modulesNames;
    }
}
