/*
 * $Id: DocumentBuilderProvider.java,v 1.2 2013/07/03 12:25:08 jbmeslin Exp $
 */
package org.rcfaces.core.internal.documentBuilder;

import java.io.Reader;
import java.io.Writer;

import javax.faces.FacesException;

import org.w3c.dom.Document;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.2 $ $Date: 2013/07/03 12:25:08 $
 */
public class DocumentBuilderProvider implements IDocumentBuilderProvider {
    

    public Document parse(Reader reader) {
        throw new FacesException("Not implemented !");
    }

    public void serialize(Writer writer, Document document) {
        throw new FacesException("Not implemented !");
    }

}
