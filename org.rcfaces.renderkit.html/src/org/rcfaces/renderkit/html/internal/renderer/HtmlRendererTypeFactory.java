/*
 * $Id: HtmlRendererTypeFactory.java,v 1.1 2013/01/11 15:45:01 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.renderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.FactoryFinder;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import javax.faces.render.Renderer;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.component.capability.ILookAndFeelCapability;
import org.rcfaces.core.internal.renderkit.AbstractRendererTypeFactory;
import org.rcfaces.core.internal.util.ClassLocator;
import org.rcfaces.renderkit.html.internal.agent.ClientBrowserFactory;
import org.rcfaces.renderkit.html.internal.agent.IUserAgent;
import org.rcfaces.renderkit.html.internal.agent.IUserAgentRules;
import org.rcfaces.renderkit.html.internal.agent.UserAgentRuleTools;
import org.xml.sax.Attributes;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:01 $
 */
public class HtmlRendererTypeFactory extends AbstractRendererTypeFactory {

    private static final Log LOG = LogFactory
            .getLog(HtmlRendererTypeFactory.class);

    private static final String RENDERKIT_ID = RenderKitFactory.HTML_BASIC_RENDER_KIT;

    private static final String LOOK_ID_CACHE_PROPERTY = "org.rcfaces.html.LOOK_ID_CACHE";

    private static final String NO_LOOKID_RENDERER = "###NONE###";

    private static final String LOOK_ID_SEPARATOR = ":";

    private static final String USER_AGENT_SEPARATOR = "__";

    private final List<LookIdRulesBean> lookIdRulesBeans = new ArrayList<HtmlRendererTypeFactory.LookIdRulesBean>();

    private final Map<String, IUserAgentRules> featureByName = new HashMap<String, IUserAgentRules>(
            64);

    private final Map<String, LookIdRule> lookIdRulesByName = new HashMap<String, LookIdRule>(
            128);

    public String getId() {
        return PREFIX_PROVIDER_ID + getRenderKitId();
    }

    protected String getRenderKitId() {
        return RENDERKIT_ID;
    }

    public HtmlRendererTypeFactory() {
        LOG.debug("Instanciate HTML renderer type factory.");
    }

    private static String constructKey(String componentFamilly,
            String rendererType, String lookId) {
        if (lookId != null) {
            return componentFamilly + "$ " + rendererType + "$ " + lookId;

        }
        return componentFamilly + "$ " + rendererType;
    }

    public String computeRendererType(FacesContext facesContext,
            UIComponent component, String componentFamilly,
            String baseRendererType) {

        if (baseRendererType == null) {
            return null;
        }

        String rendererType = baseRendererType;
        String lookId = null;

        if (component instanceof ILookAndFeelCapability) {
            lookId = ((ILookAndFeelCapability) component).getLookId();
            if (lookId != null) {
                rendererType = baseRendererType + LOOK_ID_SEPARATOR + lookId;
            }
        }

        if (lookIdRulesByName == null || lookIdRulesByName.isEmpty()) {
            return rendererType;
        }

        ExternalContext externalContext = facesContext.getExternalContext();

        Map<String, Object> requestMap = externalContext.getRequestMap();

        Map<String, String> lookIdCache = (Map<String, String>) requestMap
                .get(LOOK_ID_CACHE_PROPERTY);
        if (lookIdCache == null) {
            lookIdCache = new HashMap<String, String>();

            requestMap.put(LOOK_ID_CACHE_PROPERTY, lookIdCache);
        }

        String rendererTypeKey = constructKey(componentFamilly,
                baseRendererType, lookId);

        String computedRendererType = lookIdCache.get(rendererTypeKey);
        if (computedRendererType != null) {
            if (computedRendererType == NO_LOOKID_RENDERER) {
                return rendererType;
            }
            return computedRendererType;
        }

        LookIdRule uac = lookIdRulesByName.get(rendererTypeKey);
        if (uac != null) {
            IUserAgent userAgent = ClientBrowserFactory.Get().get(facesContext);

            String agentRendererType = uac.accept(userAgent);

            if (agentRendererType != null) {
                rendererType = agentRendererType;

                lookIdCache.put(rendererTypeKey, agentRendererType);

            } else {
                lookIdCache.put(rendererTypeKey, NO_LOOKID_RENDERER);
            }

        } else {
            lookIdCache.put(rendererTypeKey, NO_LOOKID_RENDERER);
        }

        return rendererType;
    }

    @Override
    public void configureRules(Digester digester) {
        super.configureRules(digester);

        final boolean[] enabled = new boolean[1];

        digester.addRule("rcfaces-config/features-compatibilities/feature",
                new Rule() {

                    @Override
                    public void begin(String namespace, String name,
                            Attributes attributes) throws Exception {

                        FeatureCompatibility featureCompatibility = new FeatureCompatibility();
                        digester.push(featureCompatibility);
                    }

                    @Override
                    public void end(String namespace, String name)
                            throws Exception {
                        FeatureCompatibility featureCompatibility = (FeatureCompatibility) digester
                                .pop();

                        String userAgentRules = featureCompatibility.featureRule;

                        IUserAgentRules uars = UserAgentRuleTools
                                .constructUserAgentRules(
                                        userAgentRules,
                                        true,
                                        true,
                                        0,
                                        HtmlRendererTypeFactory.this.featureByName);

                        featureByName.put(featureCompatibility.name, uars);
                    }

                });

        digester.addBeanPropertySetter(
                "rcfaces-config/features-compatibilities/feature/feature-name",
                "name");

        digester.addBeanPropertySetter(
                "rcfaces-config/features-compatibilities/feature/feature-rule",
                "featureRule");

        digester.addRule("rcfaces-config/renderKit", new Rule() {

            @Override
            public void begin(String namespace, String name,
                    Attributes attributes) throws Exception {
                super.begin(namespace, name, attributes);

                String id = attributes.getValue("id");
                if (id == null) {
                    id = RenderKitFactory.HTML_BASIC_RENDER_KIT;
                }

                if (getRenderKitId().equals(id)) {
                    enabled[0] = true;
                } else {
                    enabled[0] = false;
                }
            }
        });

        digester.addObjectCreate("rcfaces-config/renderKit/renderer",
                LookIdRulesBean.class);

        digester.addBeanPropertySetter(
                "rcfaces-config/renderKit/renderer/priority", "priority");

        digester.addBeanPropertySetter(
                "rcfaces-config/renderKit/renderer/renderer-type",
                "rendererType");

        digester.addBeanPropertySetter(
                "rcfaces-config/renderKit/renderer/renderer-class",
                "rendererClass");

        digester.addBeanPropertySetter(
                "rcfaces-config/renderKit/renderer/component-family",
                "componentFamilly");

        digester.addBeanPropertySetter(
                "rcfaces-config/renderKit/renderer/lookId", "lookId");

        digester.addBeanPropertySetter(
                "rcfaces-config/renderKit/renderer/renderer-lookId", "lookId");

        digester.addRule("rcfaces-config/renderKit/renderer/rules/navigator",
                new Rule() {

                    @Override
                    public void body(String namespace, String name, String text)
                            throws Exception {
                        LookIdRulesBean lookIdRules = (LookIdRulesBean) digester
                                .peek();

                        lookIdRules.navigators.add(text.trim());
                    }

                });

        digester.addRule("rcfaces-config/renderKit/renderer/rules/feature",
                new Rule() {

                    @Override
                    public void body(String namespace, String name, String text)
                            throws Exception {
                        LookIdRulesBean lookIdRules = (LookIdRulesBean) digester
                                .peek();

                        lookIdRules.features.add(text.trim());
                    }

                });

        digester.addRule("rcfaces-config/renderKit/renderer", new Rule() {

            @Override
            public void end(String namespace, String name) throws Exception {
                LookIdRulesBean lookIdRulesBean = (LookIdRulesBean) digester
                        .peek(); // Le pop est fait par le
                                 // addObjectCreate("rcfaces-config/renderKit/renderer")

                lookIdRulesBeans.add(lookIdRulesBean);
            }

        });
    }

    @Override
    public void startup(FacesContext facesContext) {
        super.startup(facesContext);

        RenderKitFactory factory = (RenderKitFactory) FactoryFinder
                .getFactory(FactoryFinder.RENDER_KIT_FACTORY);

        RenderKit renderKit = factory.getRenderKit(facesContext,
                getRenderKitId());

        for (LookIdRulesBean bean : lookIdRulesBeans) {
            bean.commit(facesContext, renderKit, lookIdRulesByName,
                    featureByName);
        }

        lookIdRulesBeans.clear();
    }

    public static class Navigator {
        private String navigatorClass;

        private String navigatorVersion;
    }

    public static class LookIdRule implements Comparable<LookIdRule> {

        private final String lookIdRendererType;

        private final IUserAgentRules userAgentRules;

        private LookIdRule next;

        public LookIdRule(IUserAgentRules rules, String lookIdRendererType) {
            this.userAgentRules = rules;
            this.lookIdRendererType = lookIdRendererType;
        }

        public void setNext(LookIdRule next) {
            this.next = next;
        }

        public String accept(IUserAgent proposal) {
            if (userAgentRules.accepts(proposal)) {
                return lookIdRendererType;
            }

            if (next != null) {
                return next.accept(proposal);
            }

            return null;
        }

        public int compareTo(LookIdRule o) {
            return userAgentRules.rulesCount() - o.userAgentRules.rulesCount();
        }

        public LookIdRule getNext() {
            return next;
        }

    }

    public static class LookIdRulesBean {

        protected final List<String> features = new ArrayList<String>();

        protected final List<String> navigators = new ArrayList<String>();

        private String componentFamilly;

        private String rendererType;

        private String rendererClass;

        private String lookId;

        private int priority;

        public LookIdRulesBean() {

        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public String getComponentFamilly() {
            return componentFamilly;
        }

        public void setComponentFamilly(String componentFamilly) {
            this.componentFamilly = componentFamilly;
        }

        public String getLookId() {
            return lookId;
        }

        public void setLookId(String lookId) {
            this.lookId = lookId;
        }

        public String getRendererType() {
            return rendererType;
        }

        public void setRendererType(String rendererType) {
            int idx = rendererType.indexOf(':');
            if (idx >= 0) {
                lookId = rendererType.substring(idx + 1);
                rendererType = rendererType.substring(0, idx);
            }

            this.rendererType = rendererType;
        }

        public String getRendererClass() {
            return rendererClass;
        }

        public void setRendererClass(String rendererClass) {
            this.rendererClass = rendererClass;
        }

        void commit(FacesContext facesContext, RenderKit renderKit,
                Map<String, LookIdRule> lookIdRules,
                Map<String, IUserAgentRules> featuresByName) {
            if (componentFamilly == null) {
                throw new FacesException("Component Familly is not defined !");
            }
            if (rendererType == null) {
                throw new FacesException("Renderer Type is not defined !");
            }
            if (rendererClass == null) {
                throw new FacesException("Renderer Class is not defined !");
            }

            StringBuilder navigatorsString = new StringBuilder(
                    navigators.size() * 32);
            for (String navigator : navigators) {
                navigatorsString.append(' ').append(navigator.trim());
            }

            for (String feature : features) {
                navigatorsString.append(' ');

                if (feature.startsWith(UserAgentRuleTools.FEATURE_PREFIX) == false) {
                    navigatorsString.append(UserAgentRuleTools.FEATURE_PREFIX);
                }

                navigatorsString.append(feature.trim());
            }

            IUserAgentRules userAgentRules = UserAgentRuleTools
                    .constructUserAgentRules(navigatorsString.toString(), true,
                            true, priority, featuresByName);

            String rendererClassName = getRendererClass();
            Class< ? extends Renderer> rendererClass;
            try {
                rendererClass = ClassLocator.load(rendererClassName, this,
                        facesContext, Renderer.class);

            } catch (ClassNotFoundException ex) {
                LOG.debug("Class of renderer not found '" + rendererClassName
                        + "'.", ex);

                throw new FacesException("Class of renderer not found '"
                        + rendererClassName + "'.", ex);
            }

            Renderer renderer;
            try {
                renderer = rendererClass.newInstance();

            } catch (Exception ex) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Can not instanciate renderer instance. ('"
                            + rendererClass + "')", ex);
                }

                throw new FacesException(
                        "Can not instanciate renderer instance. ('"
                                + rendererClass + "')", ex);
            }

            StringBuilder sb = new StringBuilder(256);
            sb.append(getRendererType());
            if (lookId != null) {
                sb.append(LOOK_ID_SEPARATOR).append(lookId);
            }
            if (userAgentRules.rulesCount() > 0) {
                userAgentRules.textForm(sb, USER_AGENT_SEPARATOR);
            }

            String lookIdRendererType = sb.toString();

            renderKit.addRenderer(getComponentFamilly(), lookIdRendererType,
                    renderer);

            if (userAgentRules.rulesCount() > 0) {

                String rendererTypeKey = constructKey(componentFamilly,
                        rendererType, lookId);
                LookIdRule old = lookIdRules.get(rendererTypeKey);

                LookIdRule lookIdRule = new LookIdRule(userAgentRules,
                        lookIdRendererType);

                if (old == null) {
                    lookIdRules.put(rendererTypeKey, lookIdRule);

                } else if (lookIdRule.compareTo(old) < 0) {
                    lookIdRule.setNext(old);
                    lookIdRules.put(rendererTypeKey, lookIdRule);

                } else {
                    LookIdRule prev = old;
                    LookIdRule next = old.getNext();
                    for (; next != null; next = next.getNext()) {
                        if (lookIdRule.compareTo(next) < 0) {
                            break;
                        }

                        prev = next;
                    }

                    prev.setNext(lookIdRule);
                    if (next != null) {
                        lookIdRule.setNext(next);
                    }
                }
            }
        }
    }

    /**
     * 
     * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
     * @version $Revision: 1.1 $ $Date: 2013/01/11 15:45:01 $
     */
    public static class FeatureCompatibility {
        private String name;

        private String featureRule;

        public void setFeatureRule(String userAgentRules) {
            this.featureRule = userAgentRules;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFeatureRule() {
            return featureRule;
        }

        public String getName() {
            return name;
        }

    }

    public Map<String, IUserAgentRules> listFeaturesByNames() {
        return featureByName;
    }
}
