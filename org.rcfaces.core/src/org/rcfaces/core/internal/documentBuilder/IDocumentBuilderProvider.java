/*
 * $Id: IDocumentBuilderProvider.java,v 1.1 2011/04/12 09:25:49 oeuillot Exp $
 */
package org.rcfaces.core.internal.documentBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.w3c.dom.Document;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:49 $
 */
public interface IDocumentBuilderProvider {

    void serialize(Writer writer, Document document) throws IOException;

    Document parse(Reader reader) throws IOException;
}
