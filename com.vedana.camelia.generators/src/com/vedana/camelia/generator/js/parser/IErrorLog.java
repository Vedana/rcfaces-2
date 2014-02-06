/*
 * $Id: IErrorLog.java,v 1.2 2009/01/15 15:16:52 oeuillot Exp $
 */
package com.vedana.camelia.generator.js.parser;

import com.vedana.js.dom.ASTNode;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.2 $ $Date: 2009/01/15 15:16:52 $
 */
public interface IErrorLog {
    void error(String message, ASTNode node, String fileName);

    void error(String message, ASTNode node, IJsClass clazz);
}
