/*
 * $Id: StringAppender.java,v 1.4 2013/11/13 12:53:22 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.lang;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Java Team (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.4 $ $Date: 2013/11/13 12:53:22 $
 */
public final class StringAppender implements CharSequence {

    private static final Log LOG = LogFactory.getLog(StringAppender.class);

    private char value[];

    private int count;

    public StringAppender() {
        this(16);
    }

    public StringAppender(int length) {
        value = new char[length];
    }

    public StringAppender(String str) {
        this(str, 16);
    }

    public StringAppender(String str, int length) {
        this(str.length() + length);
        append(str);
    }

    public StringAppender(char ch) {
        this(4);
        value[count++] = ch;
    }

    public int length() {
        return count;
    }

    private void expandCapacity(int minimumCapacity) {
        int newCapacity = (value.length + 1) * 2;
        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;

        } else if (minimumCapacity > newCapacity) {
            newCapacity = minimumCapacity;
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Expand capacity from '" + value.length + "' to '"
                    + newCapacity + "'.");
        }

        char newValue[] = new char[newCapacity];
        System.arraycopy(value, 0, newValue, 0, count);
        value = newValue;
    }

    public void setLength(int newLength) {
        if (newLength < 0) {
            throw new StringIndexOutOfBoundsException(newLength);
        }

        if (newLength > value.length) {
            expandCapacity(newLength);
        }

        if (count < newLength) {
            for (; count < newLength; count++) {
                value[count] = '\0';
            }
            return;
        }

        count = newLength;
    }

    public char charAt(int index) {
        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return value[index];
    }

    private void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        if (srcBegin < 0) {
            throw new StringIndexOutOfBoundsException(srcBegin);
        }
        if ((srcEnd < 0) || (srcEnd > count)) {
            throw new StringIndexOutOfBoundsException(srcEnd);
        }
        if (srcBegin > srcEnd) {
            throw new StringIndexOutOfBoundsException("srcBegin > srcEnd");
        }
        System.arraycopy(value, srcBegin, dst, dstBegin, srcEnd - srcBegin);
    }

    /*
     * public void setCharAt(int index, char ch) { if ((index < 0) || (index >=
     * count)) { throw new StringIndexOutOfBoundsException(index); }
     * value[index] = ch; }
     */
    public StringAppender append(String str) {
        if (str == null) {
            str = String.valueOf(str);
        }

        int len = str.length();
        if (len == 0) {
            return this;
        }

        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        str.getChars(0, len, value, count);
        count = newcount;

        return this;
    }

    public StringAppender append(StringAppender sb) {
        if (sb == null) {
            return append((String) null);
        }

        int len = sb.length();
        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        sb.getChars(0, len, value, count);
        count = newcount;
        return this;
    }

    public StringAppender append(StringAppender sb, int offset, int len) {
        if (sb == null) {
            return append((String) null);
        }

        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        sb.getChars(offset, offset + len, value, count);
        count = newcount;
        return this;
    }

    public StringAppender append(char str[]) {
        return append(str, 0, str.length);
    }

    public StringAppender append(char str[], int offset, int len) {
        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        System.arraycopy(str, offset, value, count, len);
        count = newcount;

        return this;
    }

    public StringAppender append(String text, int offset, int len) {
        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        text.getChars(offset, offset + len, value, count);
        count = newcount;

        return this;
    }

    public StringAppender append(Object obj) {
        return append(String.valueOf(obj));
    }

    public StringAppender append(boolean b) {
        if (b) {
            return append("true");
        }

        return append("false");
    }

    public StringAppender append(char c) {
        return append(c, 1);
    }

    public StringAppender append(char c, int nb) {
        if (nb < 1) {
            return this;
        }
        int newcount = count + nb;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        for (; nb > 0; nb--) {
            value[count++] = c;
        }
        return this;
    }

    public StringAppender append(int i) {
        return append(String.valueOf(i));
    }

    public StringAppender append(long l) {
        return append(String.valueOf(l));
    }

    public StringAppender append(float f) {
        return append(String.valueOf(f));
    }

    @Override
    public String toString() {
        if (count == 0) {
            return "";
        }
        return new String(value, 0, count);
    }

    public void copyInto(Writer writer) throws IOException {
        if (count < 1) {
            return;
        }

        writer.write(value, 0, count);
    }

    public void ensure(int length) {
        int newcount = count + length;
        if (newcount <= value.length) {
            return;
        }
        expandCapacity(newcount);
    }

    public StringAppender insert(int offset, String str) {
        if ((offset < 0) || (offset > count)) {
            throw new StringIndexOutOfBoundsException();
        }

        if (str == null) {
            str = String.valueOf(str);
        }
        int len = str.length();
        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        System.arraycopy(value, offset, value, offset + len, count - offset);
        str.getChars(0, len, value, offset);
        count = newcount;
        return this;
    }

    public StringAppender insert(int offset, char c) {
        return insert(offset, c, 1);
    }

    public StringAppender insert(int offset, char c, int nb) {
        if (nb < 1) {
            return this;
        }
        int newcount = count + nb;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        System.arraycopy(value, offset, value, offset + nb, count - offset);
        for (; nb > 0; nb--) {
            value[offset++] = c;
        }
        count = newcount;
        return this;
    }

    public Writer createWriter() {
        return new Writer() {

            @Override
            public void close() {
            }

            @Override
            public void flush() {
            }

            @Override
            public void write(char[] cbuf, int off, int len) {
                StringAppender.this.append(cbuf, off, len);
            }

            public void write(char c) {
                StringAppender.this.append(c);
            }

            @Override
            public void write(String s, int off, int len) {
                StringAppender.this.append(s, off, len);
            }

            @Override
            public void write(String s) {
                StringAppender.this.append(s);
            }
        };
    }

    public CharSequence subSequence(int start, int end) {
        StringAppender sa = new StringAppender(end - start);

        sa.append(value, start, end - start);

        return sa;
    }

    public byte[] getBytes(String charsetName)
            throws UnsupportedEncodingException {
        return toString().getBytes(charsetName);
    }
}
