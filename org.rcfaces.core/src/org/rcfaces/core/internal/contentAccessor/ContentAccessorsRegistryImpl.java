/*
 * $Id: ContentAccessorsRegistryImpl.java,v 1.2 2013/01/11 15:46:59 jbmeslin Exp $
 */
package org.rcfaces.core.internal.contentAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.core.provider.AbstractProvider;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/01/11 15:46:59 $
 */
public class ContentAccessorsRegistryImpl extends AbstractProvider implements
        IContentAccessorRegistry {
    private static final Log LOG = LogFactory
            .getLog(ContentAccessorsRegistryImpl.class);

    private static final IContentAccessorHandler[] CONTENT_ACCESSOR_HANDLER_EMPTY_ARRAY = new IContentAccessorHandler[0];

    private final Map<IContentFamily, IContentAccessorHandler[]> contentAccessorsByType = new HashMap<IContentFamily, IContentAccessorHandler[]>(
            8);

    private IContentAccessorHandler defaultContentAccessors[] = CONTENT_ACCESSOR_HANDLER_EMPTY_ARRAY;

    public ContentAccessorsRegistryImpl() {
        // Dans le constructeur car celui-ci est utilisé par d'autres registry
        RcfacesContext rcfacesContext = RcfacesContext.getCurrentInstance();

        if (rcfacesContext.getContentAccessorRegistry() == null) {
            rcfacesContext.setContentAccessorRegistry(this);
        }
    }

    public String getId() {
        return "ContentAccessorsRegistry";
    }

    public IContentAccessorHandler[] listContentAccessorHandlers(
            IContentFamily type) {
        IContentAccessorHandler contentAccessorHandlers[] = contentAccessorsByType
                .get(type);
        if (contentAccessorHandlers == null) {
            return defaultContentAccessors;
        }

        return contentAccessorHandlers;
    }

    public void declareContentAccessorHandler(IContentFamily contentFamily,
            IContentAccessorHandler contentAccessorHandler) {

        if (contentFamily == null) {
            // On fait l'ajout
            for (Iterator<IContentFamily> it = contentAccessorsByType.keySet()
                    .iterator(); it.hasNext();) {

                contentFamily = it.next();

                declareContentAccessorHandler(contentFamily,
                        contentAccessorHandler);
            }

            // Puis on declare les defaults

            List<IContentAccessorHandler> l = new ArrayList<IContentAccessorHandler>(
                    Arrays.asList(defaultContentAccessors));
            l.add(contentAccessorHandler);
            defaultContentAccessors = l.toArray(new IContentAccessorHandler[l
                    .size()]);

            return;
        }

        List<IContentAccessorHandler> l = new ArrayList<IContentAccessorHandler>();
        IContentAccessorHandler cah[] = listContentAccessorHandlers(contentFamily);
        if (cah.length > 0) {
            l.addAll(Arrays.asList(cah));

        } else if (defaultContentAccessors.length > 0) {
            l.addAll(Arrays.asList(defaultContentAccessors));
        }
        l.add(contentAccessorHandler);

        contentAccessorsByType.put(contentFamily,
                l.toArray(new IContentAccessorHandler[l.size()]));

    }

    @Override
    public void configureRules(Digester digester) {
        super.configureRules(digester);

        // Il faut lire la config pour declarer les accesseurs
    }

}
