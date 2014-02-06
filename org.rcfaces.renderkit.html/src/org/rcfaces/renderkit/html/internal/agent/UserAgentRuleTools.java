/*
 * $Id: UserAgentRuleTools.java,v 1.2 2013/11/13 12:53:32 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.agent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.renderkit.AbstractRendererTypeFactory;
import org.rcfaces.renderkit.html.component.capability.IUserAgentVaryCapability;
import org.rcfaces.renderkit.html.internal.agent.IUserAgent.BrowserType;
import org.rcfaces.renderkit.html.internal.renderer.HtmlRendererTypeFactory;
import org.rcfaces.renderkit.html.item.IUserAgentVaryFileItem;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/11/13 12:53:32 $
 */
public class UserAgentRuleTools {

    private static final Log LOG = LogFactory.getLog(UserAgentRuleTools.class);

    private static final String CACHED_USER_AGENTS_PROPERTY = "org.rcfaces.renderkit.html.USER_AGENTS";

    public static final String FEATURE_PREFIX = "@";

    private static final Map<String, BrowserType> AGENT_NAMES = new HashMap<String, BrowserType>();
    static {
        AGENT_NAMES.put(IUserAgentVaryCapability.MICROSOFT_INTERNET_EXPLORER,
                BrowserType.MICROSOFT_INTERNET_EXPLORER);
        AGENT_NAMES.put(IUserAgentVaryCapability.INTERNET_EXPLORER,
                BrowserType.MICROSOFT_INTERNET_EXPLORER);

        AGENT_NAMES.put(IUserAgentVaryCapability.FIREFOX, BrowserType.FIREFOX);
        AGENT_NAMES.put(IUserAgentVaryCapability.FIREFOX_LITE,
                BrowserType.FIREFOX);
        AGENT_NAMES.put(IUserAgentVaryCapability.FIREFOX_LITE2,
                BrowserType.FIREFOX);

        AGENT_NAMES.put(IUserAgentVaryCapability.SAFARI, BrowserType.SAFARI);

        AGENT_NAMES.put(IUserAgentVaryCapability.OPERA, BrowserType.OPERA);

        AGENT_NAMES.put(IUserAgentVaryCapability.CHROME, BrowserType.CHROME);

        AGENT_NAMES.put(IUserAgentVaryCapability.IOS, BrowserType.IOS);
        AGENT_NAMES.put(IUserAgentVaryCapability.IPHONE, BrowserType.IOS);

        AGENT_NAMES.put(IUserAgentVaryCapability.ANDROID, BrowserType.ANDROID);
    }

    public static boolean accept(FacesContext facesContext,
            IUserAgentVaryCapability userAgentVaryCapability) {
        String userAgent = userAgentVaryCapability.getUserAgent();

        return accept(facesContext, userAgent);
    }

    public static boolean accept(FacesContext facesContext,
            IUserAgentVaryFileItem userAgentVaryFileItem) {
        String userAgent = userAgentVaryFileItem.getUserAgent();

        return accept(facesContext, userAgent);
    }

    public static IUserAgentRules constructUserAgentRules(String expression,
            boolean acceptMore, boolean acceptNot, int priority,
            Map<String, IUserAgentRules> featuresByName) {
        return new AgentRules(expression, acceptMore, acceptNot, featuresByName);
    }

    @SuppressWarnings("unchecked")
    public static boolean accept(FacesContext facesContext, String userAgentText) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Verify browser '" + userAgentText + "'");
        }

        if (userAgentText == null || userAgentText.length() == 0) {
            return true;
        }

        userAgentText = userAgentText.trim();

        if (facesContext == null) {
            facesContext = FacesContext.getCurrentInstance();
        }

        ExternalContext externalContext = facesContext.getExternalContext();

        Map<String, Object> sessionMap = externalContext.getSessionMap();

        Map<String, Boolean> userAgentVersions = (Map<String, Boolean>) sessionMap
                .get(CACHED_USER_AGENTS_PROPERTY);
        if (userAgentVersions == null) {
            userAgentVersions = new HashMap<String, Boolean>(4);
            sessionMap.put(CACHED_USER_AGENTS_PROPERTY, userAgentVersions);

        } else {
            Boolean b;

            synchronized (userAgentVersions) {
                b = userAgentVersions.get(userAgentText);
            }

            if (b != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Cached browser '" + userAgentText
                            + "' value => " + b);
                }

                return b.booleanValue();
            }
        }

        IClientBrowser client = ClientBrowserFactory.Get().get(facesContext);

        if (client == null) {
            // Pas de client détecté !

            if (LOG.isDebugEnabled()) {
                LOG.debug("No client detected return FALSE");
            }

            synchronized (userAgentVersions) {
                userAgentVersions.put(userAgentText, Boolean.FALSE);
            }
            return false;
        }

        Map<String, Object> applicationMap = externalContext
                .getApplicationMap();

        Map<String, AgentRules> agentRulesCache = (Map<String, AgentRules>) applicationMap
                .get(CACHED_USER_AGENTS_PROPERTY);
        if (agentRulesCache == null) {
            agentRulesCache = new HashMap<String, UserAgentRuleTools.AgentRules>();
            applicationMap.put(CACHED_USER_AGENTS_PROPERTY, agentRulesCache);
        }

        AgentRules agentRules;
        synchronized (agentRulesCache) {
            agentRules = agentRulesCache.get(userAgentText);
            if (agentRules == null) {
                HtmlRendererTypeFactory factory = (HtmlRendererTypeFactory) AbstractRendererTypeFactory
                        .get(facesContext);

                agentRules = new AgentRules(userAgentText, true, true,
                        factory.listFeaturesByNames());

                agentRulesCache.put(userAgentText, agentRules);
            }
        }

        boolean ret = agentRules.accepts(client);

        synchronized (userAgentVersions) {
            userAgentVersions.put(userAgentText, Boolean.valueOf(ret));
        }
        return ret;
    }

    private static BrowserType computeBrowserType(String agent) {
        BrowserType type = AGENT_NAMES.get(agent.trim().toLowerCase());
        if (type == null) {
            return BrowserType.UNKNOWN;
        }
        return type;
    }

    private static class AgentRules implements IUserAgentRules {

        private final Map<BrowserType, List<AgentRule>> rulesByBrowserType;

        private final int rulesCount;

        private AgentRules(Map<BrowserType, List<AgentRule>> rulesByBrowserType) {
            this.rulesByBrowserType = rulesByBrowserType;
            this.rulesCount = count(rulesByBrowserType);
        }

        public AgentRules(String expression, boolean acceptMore,
                boolean acceptNot, Map<String, IUserAgentRules> aliases) {

            Map<BrowserType, List<AgentRule>> map = new HashMap<IUserAgent.BrowserType, List<AgentRule>>(
                    4);

            List<String> features = null;

            StringTokenizer st = new StringTokenizer(expression.trim(),
                    " ,\t\n");
            for (; st.hasMoreTokens();) {
                String token = st.nextToken();

                if (token.startsWith(FEATURE_PREFIX)) {
                    String featureName = token.substring(FEATURE_PREFIX
                            .length());

                    IUserAgentRules fc = aliases.get(featureName);
                    if (fc == null) {
                        LOG.error("Can not find feature '" + featureName
                                + "' specified in navigators rule '" + token
                                + "'");
                        continue;
                    }

                    if (features == null) {
                        features = new ArrayList<String>();
                    }

                    features.add(featureName);
                    continue;
                }

                AgentRule agentRule = new AgentRule(token, acceptMore,
                        acceptNot);

                List<AgentRule> ags = map.get(agentRule.getBrowserType());
                if (ags == null) {
                    ags = new ArrayList<UserAgentRuleTools.AgentRule>(4);
                    map.put(agentRule.getBrowserType(), ags);
                }

                ags.add(agentRule);
            }

            map = normalize(map);

            if (features != null) {
                for (String feature : features) {
                    IUserAgentRules fc = aliases.get(feature);

                    AgentRules agentRules = (AgentRules) fc;

                    map = intercept(map, agentRules.rulesByBrowserType);
                }
            }

            rulesByBrowserType = map;
            rulesCount = count(rulesByBrowserType);
        }

        public int rulesCount() {
            return rulesCount;
        }

        private static Map<BrowserType, List<AgentRule>> normalize(
                Map<BrowserType, List<AgentRule>> mapRules) {

            for (List<AgentRule> rules : mapRules.values()) {
                if (rules.size() < 2) {
                    continue;
                }

                List<AgentRule> l = normalizeAgentRuleList(rules);

                rules.clear();
                rules.addAll(l);
            }

            return mapRules;
        }

        private static List<AgentRule> normalizeAgentRuleList(
                List<AgentRule> rules) {

            List<AgentRule> valid = null;
            List<AgentRule> invalid = null;
            int count = 0;

            for (AgentRule rule : rules) {
                List<AgentRule> lst;
                if (rule.not) {
                    if (invalid == null) {
                        invalid = new ArrayList<UserAgentRuleTools.AgentRule>();
                    }
                    lst = invalid;
                } else {
                    if (valid == null) {
                        valid = new ArrayList<UserAgentRuleTools.AgentRule>();
                    }
                    lst = valid;
                }

                lst.add(rule);
                count++;
            }

            if (invalid != null) {
                sortAndNormalizeAgentRuleList(invalid);
            }

            if (valid != null) {
                sortAndNormalizeAgentRuleList(valid);
            }

            if (invalid == null) {
                return valid;
            }
            if (valid == null) {
                return invalid;
            }

            List<AgentRule> ret = new ArrayList<UserAgentRuleTools.AgentRule>(
                    count);
            ret.addAll(invalid);
            ret.addAll(valid);

            return ret;
        }

        private static void sortAndNormalizeAgentRuleList(List<AgentRule> agents) {

            if (agents.size() < 2) {
                return;
            }

            Collections.sort(agents);

            AgentRule last = null;
            for (Iterator<AgentRule> it = agents.iterator(); it.hasNext();) {
                AgentRule ar = it.next();

                if (ar.andMore == false) {
                    if (last != null && last.versionEquals(ar)) {
                        it.remove();
                        continue;
                    }
                    last = ar;
                    continue;
                }

                // On efface les suivants !!!

                for (; it.hasNext();) {
                    it.next();

                    it.remove();
                }
                break;
            }
        }

        public boolean accepts(IUserAgent proposal) {

            List<AgentRule> ags = rulesByBrowserType.get(proposal
                    .getBrowserType());
            if (ags == null) {
                return false;
            }

            for (AgentRule rule : ags) {
                if (rule.accepts(proposal)) {
                    return !rule.not;
                }
            }

            return false;
        }

        public void textForm(StringBuilder sb, String separator) {

            for (List<AgentRule> ags : rulesByBrowserType.values()) {
                for (AgentRule rule : ags) {

                    if (separator != null && sb.length() > 0) {
                        sb.append(separator);
                    }

                    rule.textForm(sb);
                }
            }
        }

        public IUserAgentRules merge(IUserAgentRules other) {
            AgentRules o = (AgentRules) other;

            Map<BrowserType, List<AgentRule>> map = intercept(
                    rulesByBrowserType, o.rulesByBrowserType);

            return new AgentRules(map);
        }

        private static Map<BrowserType, List<AgentRule>> intercept(
                Map<BrowserType, List<AgentRule>> map1,
                Map<BrowserType, List<AgentRule>> map2) {
            Map<BrowserType, List<AgentRule>> ret = new HashMap<IUserAgent.BrowserType, List<AgentRule>>();

            Set<BrowserType> set = new HashSet<IUserAgent.BrowserType>(
                    map1.keySet());
            set.addAll(map2.keySet());

            for (BrowserType bt : set) {
                List<AgentRule> l1 = map1.get(bt);
                List<AgentRule> l2 = map2.get(bt);

                if (l1 == null) {
                    ret.put(bt, l2);
                    continue;
                }
                if (l2 == null) {
                    ret.put(bt, l1);
                    continue;
                }

                List<AgentRule> ls = interceptAgentRuleList(l1, l2);

                ret.put(bt, ls);
            }

            return ret;
        }

        private static List<AgentRule> interceptAgentRuleList(
                List<AgentRule> l1, List<AgentRule> l2) {

            List<AgentRule> accepted = new ArrayList<UserAgentRuleTools.AgentRule>(
                    l1.size() + l2.size());

            for (AgentRule a1 : l1) {
                for (AgentRule a2 : l2) {
                    if (a2.not == a1.not && a1.accepts(a2)) {
                        accepted.add(a2);
                        continue;
                    }
                }
            }

            for (AgentRule a1 : l2) {
                for (AgentRule a2 : l1) {
                    if (a2.not == a1.not && a1.accepts(a2)) {
                        accepted.add(a2);
                        continue;
                    }
                }
            }

            accepted = normalizeAgentRuleList(accepted);

            return accepted;
        }

        private static int count(
                Map<BrowserType, List<AgentRule>> rulesByBrowserType) {
            int count = 0;

            for (List<AgentRule> ags : rulesByBrowserType.values()) {
                count += ags.size();
            }

            return count;
        }

        public IUserAgent reduce(IUserAgent userAgent) {
            List<AgentRule> ars = rulesByBrowserType.get(userAgent
                    .getBrowserType());

            if (ars == null || ars.isEmpty()) {
                return null;
            }

            Integer uMajor = userAgent.getMajorVersion();
            Integer uMinor = userAgent.getMinorVersion();
            Integer uRelease = userAgent.getReleaseVersion();

            for (AgentRule ar : ars) {
                Integer aMajor = ar.getMajorVersion();
                if (aMajor == null) {
                    // On prend tous les majors !
                    return ar;
                }
                if (aMajor.equals(uMajor) == false) {
                    // Cela ne nous concerne pas !
                    continue;
                }

                // Le Major est le même

                Integer aMinor = ar.getMinorVersion();
                if (aMinor == null) {
                    // On prend tous les Minors !
                    return ar;
                }
                if (aMinor.equals(uMinor) == false) {
                    // Cela ne nous concerne pas !
                    continue;
                }

                // Le Major et le Minor sont le mêmes

                Integer aRelease = ar.getReleaseVersion();
                if (aRelease == null) {
                    // On prend tous les Releases !
                    return ar;
                }
                if (aRelease.equals(uRelease) == false) {
                    // Cela ne nous concerne pas !
                    continue;
                }

                return userAgent;
            }

            return null;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime
                    * result
                    + ((rulesByBrowserType == null) ? 0 : rulesByBrowserType
                            .hashCode());
            result = prime * result + rulesCount;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            AgentRules other = (AgentRules) obj;
            if (rulesCount != other.rulesCount)
                return false;
            if (rulesByBrowserType == null) {
                if (other.rulesByBrowserType != null)
                    return false;
            } else if (!rulesByBrowserType.equals(other.rulesByBrowserType))
                return false;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[AgentRules rulesByBrowserType='")
                    .append(rulesByBrowserType).append("' rulesCount='")
                    .append(rulesCount).append("']");
            return builder.toString();
        }

    }

    public static class AgentRule extends BasicUserAgent implements
            Comparable<AgentRule> {

        private boolean andMore;

        private boolean not;

        private Float priority;

        private boolean transientState;

        public AgentRule(String expression, boolean acceptMore,
                boolean acceptNot) {

            String name = null;
            String version = null;

            if (acceptNot && expression.charAt(0) == '!') {
                not = true;
                expression = expression.substring(1);
            }

            int idx2 = expression.indexOf(";q=");
            if (idx2 > 0) {
                String q = expression.substring(idx2 + 3);
                expression = expression.substring(0, idx2);

                priority = Float.valueOf(q);
            }

            int idx = expression.indexOf('/');
            if (idx < 0) {
                int len = expression.length();
                for (int i = 0; i < len; i++) {
                    char ch = expression.charAt(i);

                    if (Character.isDigit(ch)) {
                        name = expression.substring(0, i);
                        version = expression.substring(i);
                        break;
                    }
                }

                if (name == null) {
                    name = expression;
                }

            } else {
                name = expression.substring(0, idx);
                version = expression.substring(idx + 1);
            }

            type = computeBrowserType(name);
            if (type == BrowserType.UNKNOWN) {
                LOG.info("Unknown browser for '" + name + "'");
            }

            if (version != null) {
                version = version.trim();
                if (acceptMore && version.endsWith("+")) {
                    andMore = true;
                    version = version.substring(0, version.length() - 1).trim();
                }

                StringTokenizer st2 = new StringTokenizer(version, ".");
                if (st2.hasMoreTokens()) {
                    String token = st2.nextToken();
                    majorVersion = Integer.valueOf(token);

                    if (st2.hasMoreTokens()) {
                        token = st2.nextToken();
                        minorVersion = Integer.valueOf(token);

                        if (st2.hasMoreTokens()) {
                            token = st2.nextToken();
                            releaseVersion = Integer.valueOf(token);
                        }
                    }
                }
            }
        }

        protected boolean versionEquals(AgentRule proposal) {

            if (majorVersion != null) {
                if (proposal.getMajorVersion() == null) {
                    return false;
                }

                if (majorVersion.intValue() != proposal.getMajorVersion()
                        .intValue()) {
                    return false;
                }
            }

            if (minorVersion != null) {
                if (proposal.getMinorVersion() == null) {
                    return false;
                }

                if (minorVersion.intValue() != proposal.getMinorVersion()
                        .intValue()) {
                    return false;
                }
            }

            if (releaseVersion != null) {
                if (proposal.getReleaseVersion() == null) {
                    return false;
                }

                if (releaseVersion.intValue() != proposal.getReleaseVersion()
                        .intValue()) {
                    return false;
                }
            }

            return true;
        }

        public boolean accepts(IUserAgent proposal) {
            if (type != BrowserType.UNKNOWN
                    && type != proposal.getBrowserType()) {
                return false;
            }

            if (majorVersion != null) {
                if (proposal.getMajorVersion() == null) {
                    return false;
                }

                if (andMore == false) {
                    if (majorVersion.intValue() != proposal.getMajorVersion()
                            .intValue()) {
                        return false;
                    }
                }

                if (majorVersion.intValue() > proposal.getMajorVersion()
                        .intValue()) {
                    return false;
                }

                if (majorVersion.intValue() < proposal.getMajorVersion()
                        .intValue()) {
                    return true;
                }
            }

            if (minorVersion != null) {
                if (proposal.getMinorVersion() == null) {
                    return false;
                }

                if (andMore == false) {
                    if (minorVersion.intValue() != proposal.getMinorVersion()
                            .intValue()) {
                        return false;
                    }
                }

                if (minorVersion.intValue() > proposal.getMinorVersion()
                        .intValue()) {
                    return false;
                }

                if (minorVersion.intValue() < proposal.getMinorVersion()
                        .intValue()) {
                    return true;
                }
            }

            if (releaseVersion != null) {
                if (proposal.getReleaseVersion() == null) {
                    return false;
                }

                if (andMore == false) {
                    if (releaseVersion.intValue() != proposal
                            .getReleaseVersion().intValue()) {
                        return false;
                    }
                }

                if (releaseVersion.intValue() > proposal.getReleaseVersion()
                        .intValue()) {
                    return false;
                }

                if (releaseVersion.intValue() < proposal.getReleaseVersion()
                        .intValue()) {
                    return true;
                }
            }

            return true;
        }

        public Float getPriority() {
            return priority;
        }

        public void textForm(StringBuilder sb) {

            if (not) {
                sb.append('!');
            }

            sb.append(getBrowserType().shortName());

            if (getMajorVersion() != null) {
                sb.append('/').append(getMajorVersion());

                if (getMinorVersion() != null) {
                    sb.append('.').append(getMinorVersion());

                    if (getReleaseVersion() != null) {
                        sb.append('.').append(getReleaseVersion());
                    }
                }

                if (andMore) {
                    sb.append('+');
                }
            }

            if (priority != null && priority.floatValue() > 0f) {
                sb.append(";q=").append(priority.toString());
            }
        }

        public int compareTo(AgentRule other) {
            if (this.not) {
                if (other.not == false) {
                    return -1;
                }
            } else if (other.not) {
                return 1;
            }

            int ret = this.getBrowserType().compareTo(other.getBrowserType());
            if (ret != 0) {
                return ret;
            }

            if (this.getPriority() == null) {
                if (other.getPriority() != null) {
                    return -1;
                }
            } else if (other.getPriority() == null) {
                return 1;
            }

            float retf = this.getPriority().floatValue()
                    - other.getPriority().floatValue();
            if (retf < 0.0f) {
                return -1;
            }
            if (retf > 0.0f) {
                return 1;
            }

            if (this.getMajorVersion() == null) {
                if (other.getMajorVersion() != null) {
                    return -1;
                }
            } else if (other.getMajorVersion() == null) {
                return 1;
            }

            ret = this.getMajorVersion().intValue()
                    - other.getMajorVersion().intValue();
            if (ret != 0) {
                return ret;
            }

            if (this.getMinorVersion() == null) {
                if (other.getMinorVersion() != null) {
                    return -1;
                }
            } else if (other.getMinorVersion() == null) {
                return 1;
            }

            ret = this.getMinorVersion().intValue()
                    - other.getMinorVersion().intValue();
            if (ret != 0) {
                return ret;
            }

            if (this.getReleaseVersion() == null) {
                if (other.getReleaseVersion() != null) {
                    return -1;
                }
            } else if (other.getReleaseVersion() == null) {
                return 1;
            }

            ret = this.getReleaseVersion().intValue()
                    - other.getReleaseVersion().intValue();
            if (ret != 0) {
                return ret;
            }

            if (this.andMore) {
                if (other.andMore == false) {
                    return 1;
                }
            } else if (other.andMore) {
                return -1;
            }

            return 0;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + (andMore ? 1231 : 1237);
            result = prime * result + (not ? 1231 : 1237);
            result = prime * result
                    + ((priority == null) ? 0 : priority.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!super.equals(obj))
                return false;
            if (getClass() != obj.getClass())
                return false;
            AgentRule other = (AgentRule) obj;
            if (andMore != other.andMore)
                return false;
            if (not != other.not)
                return false;
            if (priority == null) {
                if (other.priority != null)
                    return false;
            } else if (!priority.equals(other.priority))
                return false;
            return true;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("[AgentRule type='").append(type)
                    .append("' majorVersion='").append(majorVersion)
                    .append("' minorVersion='").append(minorVersion)
                    .append("' releaseVersion='").append(releaseVersion)
                    .append("' andMore='").append(andMore).append("' not='")
                    .append(not).append("']");
            return builder.toString();
        }

        public Object saveState(FacesContext context) {
            Object[] ret = new Object[4];

            ret[0] = saveState(context);
            ret[1] = (andMore) ? Boolean.TRUE : null;
            ret[2] = (not) ? Boolean.TRUE : null;
            ret[3] = priority;

            return ret;
        }

        public void setTransient(boolean newTransientValue) {
            this.transientState = newTransientValue;
        }

        public void restoreState(FacesContext context, Object state) {
            Object[] array = (Object[]) state;

            super.restoreState(context, array[0]);

            andMore = (array[1] != null);
            not = (array[2] != null);
            priority = (Float) array[3];
        }

        public boolean isTransient() {
            return transientState;
        }

    }
}
