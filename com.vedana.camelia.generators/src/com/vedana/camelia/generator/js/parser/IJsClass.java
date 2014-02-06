/*
 * $Id: IJsClass.java,v 1.1 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import java.util.List;
import java.util.Set;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2009/01/15 15:16:52 $
 */
public interface IJsClass extends IJsMetaProperties {

    IJsType[] listParameters();

    String getName();

    boolean isFinal();

    boolean isPublic();

    boolean isProtected();

//    JsModifier getModifier();

    IJsMember getMember(String name);

    IJsMember getMember(String name, int mask);

    boolean containsDependency(JsStats stats, String className);

    IJsType getParent();

    IJsMember[] listMembers();

    IJsType[] listAspects();

    boolean containsAspect(IJsType aspectClass);

    JsBundleClass getBundle(String bundle);

    boolean isAspect();

    List<IJsClass> listReverseAspect();

    String getPackageName();

    Set<IJsType> listDependencies(JsStats stats);

    String toString(IJsClass relativeClass, boolean printTemplate);
}