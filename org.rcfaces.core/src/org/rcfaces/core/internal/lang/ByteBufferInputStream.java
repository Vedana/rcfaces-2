/*
 * $Id: ByteBufferInputStream.java,v 1.3 2013/07/03 12:25:06 jbmeslin Exp $
 */
package org.rcfaces.core.internal.lang;

import java.io.InputStream;

/**
 * 
 * @author Java team (latest modification by $Author: jbmeslin $)
 * @version $Revision: 1.3 $ $Date: 2013/07/03 12:25:06 $
 */
public final class ByteBufferInputStream extends InputStream {
    

    /**
     * An array of bytes that was provided by the creator of the stream.
     * Elements <code>buf[0]</code> through <code>buf[count-1]</code> are
     * the only bytes that can ever be read from the stream; element
     * <code>buf[pos]</code> is the next byte to be read.
     */
    private byte buf[];

    /**
     * The index of the next character to read from the input stream buffer.
     * This value should always be nonnegative and not larger than the value of
     * <code>count</code>. The next byte to be read from the input stream
     * buffer will be <code>buf[pos]</code>.
     */
    private int pos;

    /**
     * The index one greater than the last valid character in the input stream
     * buffer. This value should always be nonnegative and not larger than the
     * length of <code>buf</code>. It is one greater than the position of the
     * last byte within <code>buf</code> that can ever be read from the input
     * stream buffer.
     */
    private int count;

    /**
     * Creates a <code>ByteBufferInputStream</code> so that it uses
     * <code>buf</code> as its buffer array. The buffer array is not copied.
     * The initial value of <code>pos</code> is <code>0</code> and the
     * initial value of <code>count</code> is the length of <code>buf</code>.
     * 
     * @param buf
     *            the input buffer.
     */
    public ByteBufferInputStream(byte buf[]) {
        this.buf = buf;
        this.pos = 0;
        this.count = buf.length;
    }

    /**
     * Creates <code>ByteBufferInputStream</code> that uses <code>buf</code>
     * as its buffer array. The initial value of <code>pos</code> is
     * <code>offset</code> and the initial value of <code>count</code> is
     * the minimum of <code>offset+length</code> and <code>buf.length</code>.
     * The buffer array is not copied. The buffer's mark is set to the specified
     * offset.
     * 
     * @param buf
     *            the input buffer.
     * @param offset
     *            the offset in the buffer of the first byte to read.
     * @param length
     *            the maximum number of bytes to read from the buffer.
     */
    public ByteBufferInputStream(byte buf[], int offset, int length) {
        this.buf = buf;
        this.pos = offset;
        this.count = Math.min(offset + length, buf.length);
    }

    /**
     * Reads the next byte of data from this input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the
     * stream has been reached, the value <code>-1</code> is returned.
     * <p>
     * This <code>read</code> method cannot block.
     * 
     * @return the next byte of data, or <code>-1</code> if the end of the
     *         stream has been reached.
     */
    @Override
    public int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    /**
     * Reads up to <code>len</code> bytes of data into an array of bytes from
     * this input stream. If <code>pos</code> equals <code>count</code>,
     * then <code>-1</code> is returned to indicate end of file. Otherwise,
     * the number <code>k</code> of bytes read is equal to the smaller of
     * <code>len</code> and <code>count-pos</code>. If <code>k</code> is
     * positive, then bytes <code>buf[pos]</code> through
     * <code>buf[pos+k-1]</code> are copied into <code>b[off]</code> through
     * <code>b[off+k-1]</code> in the manner performed by
     * <code>System.arraycopy</code>. The value <code>k</code> is added
     * into <code>pos</code> and <code>k</code> is returned.
     * <p>
     * This <code>read</code> method cannot block.
     * 
     * @param b
     *            the buffer into which the data is read.
     * @param off
     *            the start offset of the data.
     * @param len
     *            the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or
     *         <code>-1</code> if there is no more data because the end of the
     *         stream has been reached.
     */
    @Override
    public int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        }

        if ((off < 0) || (off > b.length) || (len < 0)
                || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }

        if (pos >= count) {
            return -1;
        }
        if (pos + len > count) {
            len = count - pos;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    @Override
    public long skip(long n) {
        if (pos + n > count) {
            n = count - pos;
        }
        if (n < 0) {
            return 0;
        }
        pos += n;
        return n;
    }

    @Override
    public int available() {
        return count - pos;
    }

    public void flush() {
    }

    @Override
    public void close() {
    }
}
