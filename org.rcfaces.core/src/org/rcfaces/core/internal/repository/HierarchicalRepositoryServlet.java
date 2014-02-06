/*
 * $Id: HierarchicalRepositoryServlet.java,v 1.4 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.repository;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.repository.IHierarchicalRepository.ISet;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.repository.IRepository.IFile;
import org.rcfaces.core.lang.OrderedSet;

/**
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:25 $
 */
public abstract class HierarchicalRepositoryServlet extends RepositoryServlet {

    private static final long serialVersionUID = -8178257331664082960L;

    private static final Log LOG = LogFactory
            .getLog(HierarchicalRepositoryServlet.class);

    private static final String SET_PREFIX = ".sets";

    private static final String MODULES_PREFIX = ".modules";

    private static final String GROUP_ALL_DEFAULT_VALUE = null;

    private static final String BOOT_SET_DEFAULT_VALUE = null;

    @SuppressWarnings("unchecked")
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        IHierarchicalRepository hierarchicalRepository = getHierarchicalRepository();

        String groupAll = config.getInitParameter(getParameterPrefix()
                + MODULES_PREFIX + ".GROUP_ALL_FILES");
        if (groupAll == null) {
            groupAll = getGroupAllDefaultValue();
        }
        if ("false".equals(groupAll)) {
            LOG.debug("Disabled module group.");

        } else if (groupAll != null) {
            if ("all".equalsIgnoreCase(groupAll)) {
                LOG.debug("Concat all files for all modules.");

            } else {
                LOG.debug("Concat all files for modules: " + groupAll);
            }

            for (StringTokenizer st = new StringTokenizer(groupAll, ";, "); st
                    .hasMoreTokens();) {
                String name = st.nextToken();

                if ("all".equalsIgnoreCase(name)) {
                    IHierarchicalRepository.IModule modules[] = hierarchicalRepository
                            .listModules();
                    for (int i = 0; i < modules.length; i++) {
                        modules[i].setGroupAllFiles(true);
                    }

                    continue;
                }

                IHierarchicalRepository.IModule module = hierarchicalRepository
                        .getModuleByName(name);
                if (module == null) {
                    throw new IllegalArgumentException("Can not find module '"
                            + name + "' to enable 'groupAll'.");
                }

                module.setGroupAllFiles(true);
            }
        }

        String bootSet = config.getInitParameter(getParameterPrefix()
                + ".BOOT_SET");
        boolean defaultSettings = false;
        if (bootSet == null) {
            bootSet = getBootSetDefaultValue();
            defaultSettings = true;
        }

        if (bootSet != null) {
            StringTokenizer st = new StringTokenizer(bootSet, ",; \n\t\r");
            if (st.countTokens() != 1) {
                throw new ServletException(
                        "Only one SET can be specified as BOOT_SET !");
            }

            bootSet = st.nextToken();

            String bootSetName = getParameterPrefix() + SET_PREFIX + "."
                    + bootSet;
            String parameterValue = config.getInitParameter(bootSetName);

            IHierarchicalRepository.ISet set;

            if (parameterValue != null) {
                set = initializeModuleSet(bootSet, parameterValue);

            } else if (defaultSettings) {
                set = initializeDefaultSet();

            } else {
                throw new ServletException("Set specified by " + bootSetName
                        + " is not defined !");
            }

            if (set == null) {
                throw new IllegalArgumentException("Can not find boot set '"
                        + bootSet + "'.");
            }

            hierarchicalRepository.setBootSet(set);
        }

        Enumeration<String> parameterNames = config.getInitParameterNames();
        String setPrefix = getParameterPrefix() + SET_PREFIX;
        for (; parameterNames.hasMoreElements();) {
            String parameterName = parameterNames.nextElement();

            if (parameterName.startsWith(setPrefix) == false) {
                continue;
            }

            String parameterValue = config.getInitParameter(parameterName);

            parameterName = parameterName.substring(setPrefix.length() + 1);
            if (parameterName.length() < 1) {
                continue;
            }

            if (parameterName.equals(bootSet)) {
                continue;
            }

            LOG.debug("Group modules '" + parameterValue + "' into the set '"
                    + parameterName + "'.");

            initializeModuleSet(parameterName, parameterValue);
        }
    }

    protected String getBootSetDefaultValue() {
        return BOOT_SET_DEFAULT_VALUE;
    }

    protected String getGroupAllDefaultValue() {
        return GROUP_ALL_DEFAULT_VALUE;
    }

    protected final IHierarchicalRepository getHierarchicalRepository() {
        return (IHierarchicalRepository) getRepository();
    }

    private ISet initializeModuleSet(String setName, String moduleNames) {

        Collection<String> l = new OrderedSet<String>();

        if ("all".equals(moduleNames.trim())) {
            /*
             * IModule modules[]=getHierarchicalRepository().listModules();
             * for(int i=0;i<modules.length;i++) {
             * l.add(modules[i].getFilename()); }
             */

            if (true) {
                throw new IllegalArgumentException("Not yet implemented !");
            }
        } else {
            StringTokenizer st = new StringTokenizer(moduleNames, ",; \n\t\r");

            for (; st.hasMoreTokens();) {
                String moduleName = st.nextToken();
                l.add(moduleName);
            }
        }

        String uri = getSetURI(setName);

        return getHierarchicalRepository().declareSet(setName, uri,
                l.toArray(new String[l.size()]));
    }

    protected ISet initializeDefaultSet() {
        return null;
    }

    protected abstract String getSetURI(String setName);

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:25 $
     */
    protected abstract class HierarchicalRecord extends Record {

        public HierarchicalRecord(IFile file, ICriteria criteria) {
            super(file, criteria);
        }

        @Override
        public void verifyModifications() {

            boolean modified = false;

            if (file instanceof IHierarchicalRepository.ISet) {
                modified = verifySetModifications((IHierarchicalRepository.ISet) file);

            } else if (file instanceof IHierarchicalRepository.IModule) {
                modified = verifyModuleModifications((IHierarchicalRepository.IModule) file);

            } else {
                super.verifyModifications();
                return;
            }

            if (modified == false) {
                return;
            }

            resetRecord();
        }

        private boolean verifyModuleModifications(
                IHierarchicalRepository.IModule module) {
            return verifyFilesModifications(module.listDependencies());
        }

        private boolean verifySetModifications(IHierarchicalRepository.ISet set) {
            return verifyFilesModifications(set.listDependencies());
        }

        @Override
        public byte[] getBuffer() throws IOException {
            if (buffer != null) {
                return buffer;
            }

            if (file instanceof IHierarchicalRepository.ISet) {
                return getSetBuffer();
            }

            if (file instanceof IHierarchicalRepository.IModule) {
                return getModuleBuffer();
            }

            return super.getBuffer();
        }

        private byte[] getModuleBuffer() throws IOException {
            IHierarchicalRepository.IModule module = (IHierarchicalRepository.IModule) file;

            return getFilesBuffer(module.listDependencies());
        }

        private byte[] getSetBuffer() throws IOException {
            IHierarchicalRepository.ISet set = (IHierarchicalRepository.ISet) file;

            return getFilesBuffer(set.listDependencies());
        }
    }
}
