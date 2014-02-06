/*
 * $Id: XhtmlNSAttributes.java,v 1.1 2013/01/11 15:45:02 jbmeslin Exp $
 */
package org.rcfaces.renderkit.html.internal.ns;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XhtmlNSAttributes {
    String[] value();
}
