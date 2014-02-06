/*
 * $Id: FakeServletResponse.java,v 1.1 2011/04/12 09:25:20 oeuillot Exp $
 */
package org.rcfaces.core.internal.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: oeuillot $)
 * @version $Revision: 1.1 $ $Date: 2011/04/12 09:25:20 $
 */
public class FakeServletResponse implements ServletResponse {

    public static final ServletResponse SINGLETON = new FakeServletResponse();

    public void flushBuffer() throws IOException {

    }

    public int getBufferSize() {

        return 0;
    }

    public String getCharacterEncoding() {

        return null;
    }

    public String getContentType() {

        return null;
    }

    public Locale getLocale() {

        return null;
    }

    public ServletOutputStream getOutputStream() throws IOException {

        return null;
    }

    public PrintWriter getWriter() throws IOException {

        return null;
    }

    public boolean isCommitted() {

        return false;
    }

    public void reset() {

    }

    public void resetBuffer() {

    }

    public void setBufferSize(int arg0) {

    }

    public void setCharacterEncoding(String arg0) {

    }

    public void setContentLength(int arg0) {

    }

    public void setContentType(String arg0) {

    }

    public void setLocale(Locale arg0) {

    }

}
