/*
 * $Id: ParserException.java,v 1.2 2013/07/03 12:25:07 jbmeslin Exp $
 * 
 */

package org.rcfaces.core.internal.renderkit;

/**
 * Probleme d'analyse de l'aspect graphique d'un composant.
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:07 $
 */
public class ParserException extends Exception {
    

    private static final long serialVersionUID = -8711696478442649846L;

    public ParserException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
