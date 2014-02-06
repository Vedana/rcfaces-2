/*
 * $Id: JavaScriptRenderer.java,v 1.4 2013/11/13 12:53:30 jbmeslin Exp $
 * 
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.contentAccessor.ContentAccessorFactory;
import org.rcfaces.core.internal.contentAccessor.IContentAccessor;
import org.rcfaces.core.internal.renderkit.IComponentWriter;
import org.rcfaces.core.internal.renderkit.WriterException;
import org.rcfaces.core.internal.repository.IHierarchicalRepository;
import org.rcfaces.core.internal.repository.IHierarchicalRepository.IHierarchicalFile;
import org.rcfaces.core.internal.repository.IHierarchicalRepository.IModule;
import org.rcfaces.core.internal.repository.IHierarchicalRepository.ISet;
import org.rcfaces.core.internal.repository.IRepository;
import org.rcfaces.core.internal.repository.IRepository.ICriteria;
import org.rcfaces.core.internal.repository.IRepository.IFile;
import org.rcfaces.core.internal.tools.ContextTools;
import org.rcfaces.core.lang.IContentFamily;
import org.rcfaces.renderkit.html.component.JavaScriptComponent;
import org.rcfaces.renderkit.html.internal.IHtmlComponentRenderContext;
import org.rcfaces.renderkit.html.internal.IHtmlWriter;
import org.rcfaces.renderkit.html.internal.IJavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.IJavaScriptWriter;
import org.rcfaces.renderkit.html.internal.JavaScriptRenderContext;
import org.rcfaces.renderkit.html.internal.agent.UserAgentRuleTools;
import org.rcfaces.renderkit.html.internal.decorator.IComponentDecorator;
import org.rcfaces.renderkit.html.internal.decorator.JavaScriptFilesCollectorDecorator;
import org.rcfaces.renderkit.html.internal.javascript.IJavaScriptRepository;
import org.rcfaces.renderkit.html.internal.javascript.JavaScriptRepositoryServlet;
import org.rcfaces.renderkit.html.internal.util.FileItemSource;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:30 $
 */
public class JavaScriptRenderer extends AbstractFilesCollectorRenderer {
    

    private static final Log LOG = LogFactory.getLog(JavaScriptRenderer.class);

    protected void encodeEnd(IComponentWriter writer) throws WriterException {

        IHtmlWriter htmlWriter = (IHtmlWriter) writer;

        IHtmlComponentRenderContext htmlComponentRenderContext = htmlWriter
                .getHtmlComponentRenderContext();

        FacesContext facesContext = htmlComponentRenderContext
                .getFacesContext();

        JavaScriptComponent javaScriptComponent = (JavaScriptComponent) htmlComponentRenderContext
                .getComponent();

        String userAgent = javaScriptComponent.getUserAgent(facesContext);
        if (userAgent != null && userAgent.length() > 0) {
            if (htmlWriter.getHtmlComponentRenderContext()
                    .getHtmlRenderContext().isUserAgentVary() == false) {
                throw new FacesException(
                        "In order to use userAgentVary property, you must declare <v:init userAgentVary=\"true\" ...>");
            }

            if (UserAgentRuleTools.accept(facesContext, javaScriptComponent) == false) {
                return;
            }
        }

        IJavaScriptRenderContext javaScriptRenderContext = htmlWriter
                .getHtmlComponentRenderContext().getHtmlRenderContext()
                .getJavaScriptRenderContext();

        String requiredFiles = javaScriptComponent
                .getRequiredFiles(facesContext);
        String requiredClasses = javaScriptComponent
                .getRequiredClasses(facesContext);
        String requiredModules = javaScriptComponent
                .getRequiredModules(facesContext);
        String requiredSets = javaScriptComponent.getRequiredSets(facesContext);

        if (requiredFiles != null || requiredClasses != null
                || requiredModules != null || requiredSets != null) {
            addRequires(htmlWriter, javaScriptRenderContext, requiredFiles,
                    requiredClasses, requiredModules, requiredSets);
        }

        String src = javaScriptComponent.getSrc(facesContext);
        if (src != null) {
            IContentAccessor contentAccessor = ContentAccessorFactory
                    .createFromWebResource(facesContext, src,
                            IContentFamily.SCRIPT);

            src = contentAccessor.resolveURL(facesContext, null, null);

            if (src != null) {
                includeScript(htmlWriter, javaScriptRenderContext, src);
            }
        }

        FileItemSource sources[] = listSources(writer
                .getComponentRenderContext());
        if (sources != null && sources.length > 0) {
            for (int i = 0; i < sources.length; i++) {
                FileItemSource fileItemSource = sources[i];

                IContentAccessor contentAccessor = ContentAccessorFactory
                        .createFromWebResource(facesContext,
                                fileItemSource.getSource(),
                                IContentFamily.SCRIPT);

                String source = contentAccessor.resolveURL(facesContext, null,
                        null);

                if (source != null) {
                    includeScript(htmlWriter, javaScriptRenderContext, source);
                }
            }
        }

        String text = javaScriptComponent.getText(facesContext);
        if (text != null) {
            text = text.trim();

            if (text.length() > 0) {
                includeRawString(htmlWriter, javaScriptRenderContext, text);
            }
        }

        List children = javaScriptComponent.getChildren();
        if (children.isEmpty() == false) {
            for (Iterator it = children.iterator(); it.hasNext();) {
                UIComponent component = (UIComponent) it.next();
                if ((component instanceof UIOutput) == false) {
                    continue;
                }

                UIOutput output = (UIOutput) component;
                Object value = output.getValue();
                if (value == null) {
                    continue;
                }

                text = convertValue(facesContext, output, value);
                if (text == null) {
                    continue;
                }

                text = text.trim();
                if (text.length() == 0) {
                    continue;
                }

                includeRawString(htmlWriter, javaScriptRenderContext, text);
            }
        }
    }

    private void includeRawString(IHtmlWriter htmlWriter,
            IJavaScriptRenderContext javaScriptRenderContext, String text)
            throws WriterException {

        javaScriptRenderContext.writeRaw(htmlWriter, text);
    }

    private void includeScript(IHtmlWriter htmlWriter,
            IJavaScriptRenderContext javaScriptRenderContext, String src)
            throws WriterException {

        JavaScriptComponent javaScriptComponent = (JavaScriptComponent) htmlWriter
                .getComponentRenderContext().getComponent();

        String javaScriptSrcCharSet = javaScriptComponent
                .getSrcCharSet(htmlWriter.getComponentRenderContext()
                        .getFacesContext());

        javaScriptRenderContext.includeJavaScript(htmlWriter, src,
                javaScriptSrcCharSet);
    }

    public static void addRequires(IHtmlWriter writer,
            IJavaScriptRenderContext javaScriptRenderContext,
            String requiredFiles, String requiredClasses,
            String requiredModules, String requiredSets) throws WriterException {

        FacesContext facesContext = writer.getComponentRenderContext()
                .getFacesContext();
        IJavaScriptRepository repository = JavaScriptRepositoryServlet
                .getRepository(facesContext);
        if (repository == null) {
            LOG.error("JavaScript repository is not created yet !");
            return;
        }

        final List<Object> files = new ArrayList<Object>(32);

        if (requiredFiles != null) {
            StringTokenizer st = new StringTokenizer(requiredFiles, ",");
            for (; st.hasMoreTokens();) {
                String requiredFile = st.nextToken().trim();

                IFile file = repository.getSetByName(requiredFile);
                if (file == null) {
                    file = repository.getModuleByName(requiredFile);
                    if (file == null) {
                        file = repository.getFileByName(requiredFile);
                    }
                }

                if (file == null) {
                    LOG.error("Can not find required file '" + requiredFile
                            + "' !");
                    continue;
                }

                files.add(file);
            }
        }

        if (requiredClasses != null) {
            StringTokenizer st = new StringTokenizer(requiredClasses, ",");
            for (; st.hasMoreTokens();) {
                String className = st.nextToken().trim();

                if ("all".equals(className)) {
                    IModule mds[] = repository.listModules();
                    for (int i = 0; i < mds.length; i++) {
                        IHierarchicalFile rfs[] = mds[i].listDependencies();

                        files.addAll(Arrays.asList(rfs));
                    }

                    continue;
                }

                IJavaScriptRepository.IClass clazz = repository
                        .getClassByName(className);

                if (clazz == null) {
                    LOG.error("Can not find required class '" + className
                            + "' !");
                    continue;
                }

                files.add(clazz.getFile());
            }
        }

        if (requiredModules != null) {
            StringTokenizer st = new StringTokenizer(requiredModules, ",");
            for (; st.hasMoreTokens();) {
                String moduleName = st.nextToken().trim();

                if ("all".equals(moduleName)) {
                    IModule modules[] = repository.listModules();

                    for (int i = 0; i < modules.length; i++) {
                        IModule module = modules[i];

                        files.addAll(Arrays.asList(module.listDependencies()));
                    }

                    continue;
                }

                IModule module = repository.getModuleByName(moduleName);

                if (module == null) {
                    LOG.error("Can not find required module '" + moduleName
                            + "' !");
                    continue;
                }

                files.addAll(Arrays.asList(module.listDependencies()));
            }
        }

        if (requiredSets != null) {
            StringTokenizer st = new StringTokenizer(requiredSets, ",");
            for (; st.hasMoreTokens();) {
                String setName = st.nextToken().trim();

                if ("all".equals(setName)) {
                    ISet sets[] = repository.listSets();

                    for (int i = 0; i < sets.length; i++) {
                        ISet set = sets[i];

                        files.addAll(Arrays.asList(set.listDependencies()));
                    }

                    continue;
                }

                ISet set = repository.getSetByName(setName);

                if (set == null) {
                    LOG.error("Can not find required set '" + setName + "' !");
                    continue;
                }

                files.addAll(Arrays.asList(set.listDependencies()));
            }
        }

        if (files.isEmpty()) {
            return;
        }

        if (javaScriptRenderContext.isCollectorMode()) {
            javaScriptRenderContext.appendRequiredFiles(files
                    .toArray(new IFile[files.size()]));

            return;
        }

        IRepository.IContext repositoryContext;

        repositoryContext = JavaScriptRepositoryServlet
                .getContextRepository(facesContext);

        IFile fs[] = repository
                .computeFiles(files,
                        IHierarchicalRepository.FILE_COLLECTION_TYPE,
                        repositoryContext);
        if (fs.length < 1) {
            return;
        }

        IJavaScriptWriter jsWriter = InitRenderer.openScriptTag(writer);

        JavaScriptRenderContext
                .initializeJavaScript(jsWriter, repository, true);

        String cameliaClassLoader = jsWriter.getJavaScriptRenderContext()
                .convertSymbol("f_classLoader", "_rcfacesClassLoader");

        jsWriter.writeCall(cameliaClassLoader, "f_requiresBundle");

        ICriteria criteria = repositoryContext.getCriteria();
        for (int i = 0; i < fs.length; i++) {
            String src = fs[i].getURI(criteria);
            if (src == null) {
                throw new NullPointerException("Can not get URI of file '"
                        + fs[i] + "' criteria='" + criteria + "'");
            }

            if (i > 0) {
                jsWriter.write(',');
            }
            jsWriter.writeString(src);
        }

        jsWriter.writeln(");");

        jsWriter.end();
    }

    public boolean getRendersChildren() {
        return true;
    }

    public void encodeChildren(FacesContext facesContext, UIComponent component) {
    }

    protected IComponentDecorator createComponentDecorator(
            FacesContext facesContext, UIComponent component) {

        return new JavaScriptFilesCollectorDecorator(component);
    }
}
