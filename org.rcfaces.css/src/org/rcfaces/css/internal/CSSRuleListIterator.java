/*
 * $Id: CSSRuleListIterator.java,v 1.1.2.1 2012/11/15 10:13:48 oeuillot Exp $
 */
package org.rcfaces.css.internal;

import java.util.List;

import org.w3c.dom.css.CSSRule;
import org.w3c.dom.css.CSSStyleSheet;

import com.steadystate.css.dom.CSSRuleListImpl;

public class CSSRuleListIterator {

    private int position;

    private final CSSStyleSheet styleSheet;

    private final List<CSSRule> rules;

    public CSSRuleListIterator(CSSStyleSheet styleSheet) {
        this.styleSheet = styleSheet;

        rules = ((CSSRuleListImpl) styleSheet.getCssRules()).getRules();
    }

    public boolean hasNext() {
        return position < rules.size();
    }

    public CSSRule next() {
        CSSRule rule = rules.get(position);
        position++;

        return rule;
    }

    public int getLength() {
        return rules.size();
    }

    public CSSRule insert(String rule) {
        styleSheet.insertRule(rule, position);

        return rules.get(position);
    }

    public void delete() {
        position--;
        styleSheet.deleteRule(position);
    }
}
