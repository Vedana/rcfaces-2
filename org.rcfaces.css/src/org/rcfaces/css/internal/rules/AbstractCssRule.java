/*
 * $Id: AbstractCssRule.java,v 1.1.2.1 2012/11/27 16:23:02 oeuillot Exp $
 */
package org.rcfaces.css.internal.rules;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1.2.1 $ $Date: 2012/11/27 16:23:02 $
 */
public abstract class AbstractCssRule {
    private final List<String> names = new ArrayList<String>();

    private final List<UserAgentPropertyRule> agents = new ArrayList<UserAgentPropertyRule>();

    public void addName(String name) {
        names.add(name);
    }

    public void addAgent(UserAgentPropertyRule rule) {
        agents.add(rule);
    }

    public List<String> listAgentNames() {
        return names;
    }

    public List<UserAgentPropertyRule> listAgentRules() {
        return agents;
    }
}
