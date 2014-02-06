/*
 * $Id: RepositoryManagerImpl.java,v 1.4 2013/11/13 12:53:25 jbmeslin Exp $
 */
package org.rcfaces.core.internal.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.rcfaces.core.internal.RcfacesContext;
import org.rcfaces.core.provider.AbstractProvider;
import org.xml.sax.Attributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:25 $
 */
public class RepositoryManagerImpl extends AbstractProvider implements
        IRepositoryManager {

    private static final String ID = "org.rcfaces.core.REPOSITORY_MANAGER";

    private static final String[] STRING_EMPTY_ARRAY = new String[0];

    private Map<String, String[]> repositories;

    private Map<String, List<String>> repositoriesList = new HashMap<String, List<String>>(
            16);

    public String getId() {
        return ID;
    }

    @Override
    public void startup(FacesContext facesContext) {
        super.startup(facesContext);

        RcfacesContext rcfacesContext = RcfacesContext
                .getInstance(facesContext);

        repositories = new HashMap<String, String[]>(repositoriesList.size());
        for (Map.Entry<String, List<String>> entry : repositoriesList
                .entrySet()) {
            String familyName = entry.getKey();
            List<String> l = entry.getValue();

            String r[] = l.toArray(new String[l.size()]);

            repositories.put(familyName, r);
        }
        repositoriesList = null;

        rcfacesContext.setRepositoryManager(this);
    }

    public String[] listFamilies() {
        Collection<String> c = repositories.keySet();

        return c.toArray(new String[c.size()]);
    }

    public String[] listRepositoryLocations(String family) {
        String rls[] = repositories.get(family);
        if (rls == null) {
            return STRING_EMPTY_ARRAY;
        }

        return rls;
    }

    @Override
    public void configureRules(Digester digester) {
        super.configureRules(digester);

        digester.addRule("rcfaces-config/repositories/repository", new Rule() {

            @Override
            public void begin(String namespace, String name,
                    Attributes attributes) throws Exception {

                super.digester.push(new RepositoryBean());
            }

            @Override
            public void end(String namespace, String name) throws Exception {
                RepositoryBean adapterBean = (RepositoryBean) super.digester
                        .pop();

                addRepositoryBean(adapterBean);
            }
        });

        digester.addBeanPropertySetter(
                "rcfaces-config/repositories/repository/location", "location");

        digester.addBeanPropertySetter(
                "rcfaces-config/repositories/repository/type", "type");
    }

    protected void addRepositoryBean(RepositoryBean adapterBean) {
        String type = adapterBean.getType();
        String location = adapterBean.getLocation();

        if (type == null || location == null) {
            throw new FacesException("Invalid repository parameters (type=\""
                    + type + "\", location=\"" + location + "\")");
        }

        List<String> l = repositoriesList.get(type);
        if (l == null) {
            l = new ArrayList<String>();

            repositoriesList.put(type, l);
        }

        l.add(location);
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:25 $
     */
    public static class RepositoryBean {

        public String location;

        public String type;

        public final String getLocation() {
            return location;
        }

        public final void setLocation(String location) {
            this.location = location;
        }

        public final String getType() {
            return type;
        }

        public final void setType(String type) {
            this.type = type;
        }

    }
}
