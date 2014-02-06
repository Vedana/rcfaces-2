/*
 * $Id: ByteBufferOutputStream.java,v 1.3 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.lang;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author Java team (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
 */
public final class ByteBufferOutputStream extends OutputStream {
    

    /**
     * The buffer where data is stored.
     */
    private byte buf[];

    /**
     * The number of valid bytes in the buffer.
     */
    private int count;

    /**
     * Creates a new byte array output stream, with a buffer capacity of the
     * specified size, in bytes.
     * 
     * @param size
     *            the initial size.
     * @exception IllegalArgumentException
     *                if size is negative.
     */
    public ByteBufferOutputStream(int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Negative initial size: " + size);
        }
        buf = new byte[size];
    }

    /**
     * Writes the specified byte to this byte array output stream.
     * 
     * @param b
     *            the byte to be written.
     */
    @Override
    public void write(int b) {
        int newcount = count + 1;
        if (newcount > buf.length) {
            byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
            System.arraycopy(buf, 0, newbuf, 0, count);
            buf = newbuf;
        }
        buf[count] = (byte) b;
        count = newcount;
    }

    /**
     * Writes <code>len</code> bytes from the specified byte array starting at
     * offset <code>off</code> to this byte array output stream.
     * 
     * @param b
     *            the data.
     * @param off
     *            the start offset in the data.
     * @param len
     *            the number of bytes to write.
     */
    @Override
    public void write(byte b[], int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0)
                || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }

        if (len == 0) {
            return;
        }
        int newcount = count + len;
        if (newcount > buf.length) {
            byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
            System.arraycopy(buf, 0, newbuf, 0, count);
            buf = newbuf;
        }
        System.arraycopy(b, off, buf, count, len);
        count = newcount;
    }

    /**
     * Writes the complete contents of this byte array output stream to the
     * specified output stream argument, as if by calling the output stream's
     * write method using <code>out.write(buf, 0, count)</code>.
     * 
     * @param out
     *            the output stream to which to write the data.
     * @exception IOException
     *                if an I/O error occurs.
     */
    public void writeTo(OutputStream out) throws IOException {
        out.write(buf, 0, count);
    }

    /**
     * Resets the <code>count</code> field of this byte array output stream to
     * zero, so that all currently accumulated output in the ouput stream is
     * discarded. The output stream can be used again, reusing the already
     * allocated buffer space.
     * 
     * @see java.io.ByteBufferInputStream#count
     */
    public void reset() {
        count = 0;
    }

    /**
     * Creates a newly allocated byte array. Its size is the current size of
     * this output stream and the valid contents of the buffer have been copied
     * into it.
     * 
     * @return the current contents of this output stream, as a byte array.
     * @see java.io.ByteArrayOutputStream#size()
     */
    public byte[] toByteArray() {
        byte newbuf[] = new byte[count];
        System.arraycopy(buf, 0, newbuf, 0, count);
        return newbuf;
    }

    /**
     * Returns the current size of the buffer.
     * 
     * @return the value of the <code>count</code> field, which is the number
     *         of valid bytes in this output stream.
     * @see java.io.ByteArrayOutputStream#count
     */
    public int size() {
        return count;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}
