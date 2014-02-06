/*
 * $Id: StringAppender2.java,v 1.3 2013/07/03 12:25:06 jbmeslin Exp $
 * 
 */
package org.rcfaces.core.internal.lang;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Java Team (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
 */
public final class StringAppender2 {
    

    private static final Log LOG = LogFactory.getLog(StringAppender2.class);

    private String initialValue = null;

    private int initialSize = 0;

    private char value[];

    private int count;

    public StringAppender2() {
        this(16);
    }

    public StringAppender2(int length) {
        if (length < 16) {
            length = 16;
        }
        initialSize = length;
    }

    public StringAppender2(String str) {
        this(str, 16);
    }

    public StringAppender2(String str, int length) {
        this(str.length() + length);

        initialValue = str;
    }

    public int length() {
        if (value == null) {
            if (initialValue == null) {
                return 0;
            }
            return initialValue.length();
        }
        return count;
    }

    private void expandCapacity(int minimumCapacity) {
        if (value == null) {
            throw new IllegalStateException(
                    "Can not expand capacity of an original string !");
        }

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

        preModification(-newLength);

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
        if (value == null) {
            if (initialValue == null) {
                throw new StringIndexOutOfBoundsException(index);
            }

            return initialValue.charAt(index);
        }

        if ((index < 0) || (index >= count)) {
            throw new StringIndexOutOfBoundsException(index);
        }
        return value[index];
    }

    private void getChars(int srcBegin, int srcEnd, char dst[], int dstBegin) {
        if (value == null) {
            if (initialValue == null) {
                throw new StringIndexOutOfBoundsException(srcBegin);
            }

            initialValue.getChars(srcBegin, srcEnd, dst, dstBegin);
            return;
        }

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
    public StringAppender2 append(String str) {
        if (str == null) {
            str = String.valueOf(str);
        }

        int len = str.length();
        if (len == 0) {
            return this;
        }

        preModification(len);

        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        str.getChars(0, len, value, count);
        count = newcount;

        return this;
    }

    public StringAppender2 append(StringAppender2 sb) {
        if (sb == null) {
            return append((String) null);
        }

        int len = sb.length();
        if (len == 0) {
            return this;
        }

        preModification(len);

        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        sb.getChars(0, len, value, count);
        count = newcount;
        return this;
    }

    public StringAppender2 append(char str[]) {
        return append(str, 0, str.length);
    }

    public StringAppender2 append(char str[], int offset, int len) {

        if (len == 0) {
            return this;
        }

        preModification(len);

        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        System.arraycopy(str, offset, value, count, len);
        count = newcount;

        return this;
    }

    public StringAppender2 append(String text, int offset, int len) {

        if (len == 0) {
            return this;
        }

        preModification(len);

        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        text.getChars(offset, offset + len, value, count);
        count = newcount;

        return this;
    }

    public StringAppender2 append(boolean b) {
        if (b) {
            return append("true");
        }

        return append("false");
    }

    public StringAppender2 append(char c) {
        return append(c, 1);
    }

    public StringAppender2 append(char c, int nb) {
        if (nb < 1) {
            return this;
        }

        preModification(nb);

        int newcount = count + nb;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        for (; nb > 0; nb--) {
            value[count++] = c;
        }

        return this;
    }

    public StringAppender2 append(int i) {
        return append(String.valueOf(i));
    }

    public StringAppender2 append(long l) {
        return append(String.valueOf(l));
    }

    public StringAppender2 append(float f) {
        return append(String.valueOf(f));
    }

    @Override
    public String toString() {
        if (value == null) {
            return initialValue;
        }
        return new String(value, 0, count);
    }

    public void copyInto(Writer writer) throws IOException {
        if (value == null) {
            if (initialValue == null) {
                return;
            }

            writer.write(initialValue);
            return;
        }

        if (count < 1) {
            return;
        }

        writer.write(value, 0, count);
    }

    public void ensure(int length) {
        if (value == null) {
            if (initialSize < length) {
                initialSize = length;
            }
            return;
        }

        int newcount = count + length;
        if (newcount <= value.length) {
            return;
        }
        expandCapacity(newcount);
    }

    public StringAppender2 insert(int offset, String str) {

        if (str == null) {
            str = String.valueOf(str);
        }

        int len = str.length();
        if (len == 0) {
            return this;
        }

        preModification(len);

        if ((offset < 0) || (offset > count)) {
            throw new StringIndexOutOfBoundsException();
        }

        int newcount = count + len;
        if (newcount > value.length) {
            expandCapacity(newcount);
        }

        System.arraycopy(value, offset, value, offset + len, count - offset);
        str.getChars(0, len, value, offset);
        count = newcount;
        return this;
    }

    public StringAppender2 insert(int offset, char c) {
        return insert(offset, c, 1);
    }

    public StringAppender2 insert(int offset, char c, int nb) {
        if (nb < 1) {
            return this;
        }

        preModification(nb);

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

    protected void preModification(int newSize) {
        if (value != null) {
            return;
        }

        if (newSize > 0) {
            if (initialValue != null) {
                newSize += initialValue.length();
            }

            if (initialSize < newSize) {
                initialSize = newSize;
            }

        } else if (newSize < 0) {
            initialSize = newSize;
        }

        if (initialSize < 16) {
            initialSize = 16;
        }

        value = new char[initialSize];
        if (initialValue != null) {
            if (initialSize > 0 && initialValue.length() > 0) {
                initialValue.getChars(0,
                        Math.min(initialValue.length(), initialSize), value, 0);
            }

            initialValue = null;
        }
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
                StringAppender2.this.append(cbuf, off, len);
            }

            public void write(char c) {
                StringAppender2.this.append(c);
            }

            @Override
            public void write(String s, int off, int len) {
                StringAppender2.this.append(s, off, len);
            }

            @Override
            public void write(String s) {
                StringAppender2.this.append(s);
            }
        };
    }
}
