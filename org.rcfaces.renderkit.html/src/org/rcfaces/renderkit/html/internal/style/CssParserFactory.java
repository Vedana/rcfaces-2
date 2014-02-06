/*
 * $Id: CssParserFactory.java,v 1.2 2013/07/03 12:25:10 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.style;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.Services;
import org.rcfaces.core.internal.content.IOperationContentLoader;
import org.rcfaces.core.internal.resource.IResourceLoaderFactory;
import org.rcfaces.core.internal.util.IPath;
import org.rcfaces.core.lang.IContentFamily;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
 */
public class CssParserFactory {
    

    private static final Log LOG = LogFactory.getLog(CssParserFactory.class);

    private static final String CSS_PARSER_SERVICE_ID = "org.rcfaces.css.CSS_PARSER";

    private static final String STEADY_STATE_PARSER_CLASSNAME = "org.rcfaces.css.internal.CssSteadyStateParser";

    public static final ICssParser getCssParser() {

        ICssParser cssParser = (ICssParser) Services.get().getService(
                CSS_PARSER_SERVICE_ID);
        if (cssParser != null) {
            return cssParser;
        }

        Class clazz = null;

        try {
            LOG.debug("Try CSS steady state parser ...");

            clazz = CssParserFactory.class.getClassLoader().loadClass(
                    STEADY_STATE_PARSER_CLASSNAME);

        } catch (Throwable ex) {
            LOG.trace(ex);
        }

        if (clazz == null) {
            LOG.info("No known css parsers found.");

            return null;
        }

        LOG.info("Instanciate css parser '" + clazz.getName() + "' ...");

        try {
            return (ICssParser) clazz.newInstance();

        } catch (Throwable e) {
            LOG.error("Can not instanciate css parser '" + clazz.getName()
                    + "'.", e);

            return null;
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
     */
    public interface ICssParser {
        String getParserName();

        String normalizeBuffer(Map applicationParameters,
                IResourceLoaderFactory resourceLoaderFactory,
                String styleSheetURL, String styleSheetBuffer,
                IParserContext mergeContext,
                IOperationContentLoader operationContentLoader,
                boolean mergeLinks) throws IOException;

        /**
         * 
         * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
         * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:10 $
         */
        public interface IParserContext {
            FacesContext getFacesContext();

            String getCharset();

            void setCharset(String charset);

            long getLastModifiedDate();

            void setLastModifiedDate(long lastModifiedDate);

            IPath processVersioning(IPath base, IPath path,
                    IContentFamily contentFamily);

            boolean isVersioningEnabled();
        }
    }

}
