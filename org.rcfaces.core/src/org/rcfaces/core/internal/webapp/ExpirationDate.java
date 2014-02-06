/*
 * $Id: ExpirationDate.java,v 1.4 2013/11/13 12:53:25 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.webapp;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rcfaces.core.internal.lang.StringAppender;
import org.rcfaces.core.internal.util.Delay;

/**
 * 
 * @author Olivier Oeuillot (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:25 $
 */
public class ExpirationDate implements Serializable {

    private static final long serialVersionUID = 8408879197958606825L;

    private static final Log LOG = LogFactory.getLog(ExpirationDate.class);

    private final long expiresDate;

    private final long expiresDelay;

    private final String expiresMaxAge;

    protected ExpirationDate(long expires, long expiresDate) {
        this.expiresDate = expiresDate;

        this.expiresDelay = expires;
        if (expires >= Delay.SECOND) {
            this.expiresMaxAge = "max-age=" + (expires / Delay.SECOND); // en

        } else {
            this.expiresMaxAge = null;
        }
    }

    public static ExpirationDate parse(String servletName,
            String expireProperty, String expiresValue) {

        if (expiresValue.indexOf('/') >= 0) {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            long expiresDate;
            try {
                Date date = dateFormat.parse(expiresValue);

                expiresDate = date.getTime();

            } catch (ParseException e) {
                LOG.error("Can not parse date attribute ('" + expireProperty
                        + "') value='" + expiresValue + "'  for sevlet '"
                        + servletName + "'.", e);

                IllegalArgumentException ex = new IllegalArgumentException(
                        "Can not parse date attribute (" + expireProperty
                                + "') value='" + expiresValue + "'.");

                ex.initCause(e);

                throw ex;
            }

            return new ExpirationDate(-1, expiresDate);
        }

        long expires;

        try {
            expires = Delay.parseDelay(expiresValue);

        } catch (ParseException e) {
            LOG.error("Can not parse expire attribute ('" + expireProperty
                    + "') value='" + expiresValue + "'  for sevlet '"
                    + servletName + "'.", e);

            IllegalArgumentException ex = new IllegalArgumentException(
                    "Can not parse expire attribute ('" + expireProperty
                            + "') value='" + expiresValue + "'.");

            ex.initCause(e);

            throw ex;
        }

        return new ExpirationDate(expires, -1);
    }

    public static ExpirationDate fromDelay(long delay) {
        return new ExpirationDate(delay, -1);
    }

    public static ExpirationDate noExpiration() {
        return new ExpirationDate(-1, -1);
    }

    public void sendExpires(HttpServletResponse response) {

        long d = 0;
        if (expiresDate > 0) {
            d = expiresDate;

            if (LOG.isTraceEnabled()) {
                LOG.trace("Expiration date is setted.");
            }

        } else if (expiresDelay > 0) {
            d = System.currentTimeMillis() + expiresDelay;

            if (expiresMaxAge != null) {
                response.setHeader(ExtendedHttpServlet.HTTP_CACHE_CONTROL,
                        expiresMaxAge);
            }

            if (LOG.isTraceEnabled()) {
                LOG.trace("Compute expiration date from delay: " + expiresDelay
                        / Delay.SECOND + "s");
            }
        }

        if (d > 0) {
            d -= (d % 1000); // Retire les ms !

            if (LOG.isDebugEnabled()) {
                LOG.debug("Set expiration date to " + d + " (" + new Date(d)
                        + ")");
            }

            response.setDateHeader(ExtendedHttpServlet.HTTP_EXPIRES, d);

        } else if (LOG.isDebugEnabled()) {
            LOG.debug("No expiration defined");
        }

    }

    public long getExpiresDate() {
        return expiresDate;
    }

    public long getExpiresDelay() {
        return expiresDelay;
    }

    @Override
    public String toString() {
        StringAppender sa = new StringAppender("[ExpirationDate", 128);

        if (expiresDate > 0) {
            sa.append(" expiresDate='");
            sa.append(new SimpleDateFormat().format(new Date(expiresDate)));
            sa.append('\'');
        }

        if (expiresDelay > 0) {
            sa.append(" expiresDelay='");
            long d = expiresDelay;
            if (d >= Delay.YEAR) {
                sa.append(" YEAR=").append(d / Delay.YEAR);
                d %= Delay.YEAR;
            }
            if (d >= Delay.MONTH) {
                sa.append(" MONTH=").append(d / Delay.MONTH);
                d %= Delay.MONTH;
            }
            if (d >= Delay.DAY) {
                sa.append(" DAY=").append(d / Delay.DAY);
                d %= Delay.DAY;
            }
            if (d >= Delay.HOUR) {
                sa.append(" HOUR=").append(d / Delay.HOUR);
                d %= Delay.HOUR;
            }
            if (d >= Delay.MINUTE) {
                sa.append(" MINUTE=").append(d / Delay.MINUTE);
                d %= Delay.MINUTE;
            }
            if (d >= Delay.SECOND) {
                sa.append(" SECOND=").append(d / Delay.SECOND);
                d %= Delay.SECOND;
            }
            if (d > 0) {
                sa.append(" MILLIS=").append(d);
            }

            sa.append('\'');
        }

        sa.append(']');

        return sa.toString();
    }

}
