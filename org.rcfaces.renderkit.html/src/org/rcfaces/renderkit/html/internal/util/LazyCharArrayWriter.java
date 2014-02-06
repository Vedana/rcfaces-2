/*
 * $Id: LazyCharArrayWriter.java,v 1.1 2011/04/12 09:28:27 oeuillot Exp $
 */
package org.rcfaces.renderkit.html.internal.util;

import java.io.IOException;
import java.io.Writer;

public class LazyCharArrayWriter extends Writer {

    protected final int initialSize;

    /**
     * The buffer where data is stored.
     */
    protected char buf[];

    /**
     * The number of chars in the buffer.
     */
    protected int count;

    /**
     * Creates a new CharArrayWriter with the specified initial size.
     * 
     * @param initialSize
     *            an int specifying the initial buffer size.
     * @exception IllegalArgumentException
     *                if initialSize is negative
     */
    public LazyCharArrayWriter(int initialSize) {
        if (initialSize < 0) {
            throw new IllegalArgumentException("Negative initial size: "
                    + initialSize);
        }
        this.initialSize = initialSize;
    }

    protected void initialize(int len) {
        if (buf != null) {
            int newcount = count + len;
            if (newcount <= buf.length) {
                return;
            }

            char newbuf[] = new char[newcount + Math.max(len, 16)];
            System.arraycopy(buf, 0, newbuf, 0, count);
            buf = newbuf;

            return;
        }

        if (initialSize > len) {
            len = initialSize;
        }

        buf = new char[len];
    }

    /**
     * Writes a character to the buffer.
     */
    @Override
    public void write(int c) {
        initialize(1);

        int newcount = count + 1;
        if (newcount > buf.length) {
            char newbuf[] = new char[Math.max(buf.length << 1, newcount)];
            System.arraycopy(buf, 0, newbuf, 0, count);
            buf = newbuf;
        }
        buf[count] = (char) c;
        count = newcount;
    }

    /**
     * Writes characters to the buffer.
     * 
     * @param c
     *            the data to be written
     * @param off
     *            the start offset in the data
     * @param len
     *            the number of chars that are written
     */
    @Override
    public void write(char c[], int off, int len) {
        if ((off < 0) || (off > c.length) || (len < 0)
                || ((off + len) > c.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }

        if (len < 1) {
            return;
        }

        initialize(len);

        System.arraycopy(c, off, buf, count, len);
        count += len;
    }

    /**
     * Write a portion of a string to the buffer.
     * 
     * @param str
     *            String to be written from
     * @param off
     *            Offset from which to start reading characters
     * @param len
     *            Number of characters to be written
     */
    @Override
    public void write(String str, int off, int len) {

        if (len < 1) {
            return;
        }

        initialize(len);

        str.getChars(off, off + len, buf, count);
        count += len;

    }

    /**
     * Writes the contents of the buffer to another character stream.
     * 
     * @param out
     *            the output stream to write to
     * @throws IOException
     *             If an I/O error occurs.
     */
    public void writeTo(Writer out) throws IOException {
        out.write(buf, 0, count);
    }

    /**
     * Appends the specified character sequence to this writer.
     * 
     * <p>
     * An invocation of this method of the form <tt>out.append(csq)</tt> behaves
     * in exactly the same way as the invocation
     * 
     * <pre>
     * out.write(csq.toString())
     * </pre>
     * 
     * <p>
     * Depending on the specification of <tt>toString</tt> for the character
     * sequence <tt>csq</tt>, the entire sequence may not be appended. For
     * instance, invoking the <tt>toString</tt> method of a character buffer
     * will return a subsequence whose content depends upon the buffer's
     * position and limit.
     * 
     * @param csq
     *            The character sequence to append. If <tt>csq</tt> is
     *            <tt>null</tt>, then the four characters <tt>"null"</tt> are
     *            appended to this writer.
     * 
     * @return This writer
     * 
     * @since 1.5
     */
    @Override
    public Writer append(CharSequence csq) {
        String s = (csq == null ? "null" : csq.toString());
        write(s, 0, s.length());
        return this;
    }

    /**
     * Appends a subsequence of the specified character sequence to this writer.
     * 
     * <p>
     * An invocation of this method of the form <tt>out.append(csq, start,
     * end)</tt> when <tt>csq</tt> is not <tt>null</tt>, behaves in exactly the
     * same way as the invocation
     * 
     * <pre>
     * out.write(csq.subSequence(start, end).toString())
     * </pre>
     * 
     * @param csq
     *            The character sequence from which a subsequence will be
     *            appended. If <tt>csq</tt> is <tt>null</tt>, then characters
     *            will be appended as if <tt>csq</tt> contained the four
     *            characters <tt>"null"</tt>.
     * 
     * @param start
     *            The index of the first character in the subsequence
     * 
     * @param end
     *            The index of the character following the last character in the
     *            subsequence
     * 
     * @return This writer
     * 
     * @throws IndexOutOfBoundsException
     *             If <tt>start</tt> or <tt>end</tt> are negative,
     *             <tt>start</tt> is greater than <tt>end</tt>, or <tt>end</tt>
     *             is greater than <tt>csq.length()</tt>
     * 
     * @since 1.5
     */
    @Override
    public Writer append(CharSequence csq, int start, int end) {
        String s = (csq == null ? "null" : csq).subSequence(start, end)
                .toString();
        write(s, 0, s.length());
        return this;
    }

    /**
     * Appends the specified character to this writer.
     * 
     * <p>
     * An invocation of this method of the form <tt>out.append(c)</tt> behaves
     * in exactly the same way as the invocation
     * 
     * <pre>
     * out.write(c)
     * </pre>
     * 
     * @param c
     *            The 16-bit character to append
     * 
     * @return This writer
     * 
     * @since 1.5
     */
    @Override
    public Writer append(char c) {
        write(c);
        return this;
    }

    /**
     * Resets the buffer so that you can use it again without throwing away the
     * already allocated buffer.
     */
    public void reset() {
        buf = null;
        count = 0;
    }

    /**
     * Returns a copy of the input data.
     * 
     * @return an array of chars copied from the input data.
     */
    public char[] toCharArray() {
        if (count == 0) {
            return new char[0];
        }

        char newbuf[] = new char[count];
        System.arraycopy(buf, 0, newbuf, 0, count);
        return newbuf;
    }

    /**
     * Returns the current size of the buffer.
     * 
     * @return an int representing the current size of the buffer.
     */
    public int size() {
        return count;
    }

    /**
     * Converts input data to a string.
     * 
     * @return the string.
     */
    @Override
    public String toString() {
        if (count == 0) {
            return "";
        }
        return new String(buf, 0, count);
    }

    /**
     * Flush the stream.
     */
    @Override
    public void flush() {
    }

    /**
     * Close the stream. This method does not release the buffer, since its
     * contents might still be required. Note: Invoking this method in this
     * class will have no effect.
     */
    @Override
    public void close() {
    }
}
