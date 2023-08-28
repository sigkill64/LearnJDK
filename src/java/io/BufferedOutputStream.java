/*
 * Copyright (c) 1994, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package java.io;

/**
 * The class implements a buffered output stream. By setting up such
 * an output stream, an application can write bytes to the underlying
 * output stream without necessarily causing a call to the underlying
 * system for each byte written.
 *
 * @author Arthur van Hoff
 * @since 1.0
 */
/*
 * 带有内部缓存区的字节输出流
 *
 * 写入数据时，会先将数据存入缓冲区。待缓存区满时，才批量将数据写入到输出流。
 */
public class BufferedOutputStream extends FilterOutputStream {
    
    /**
     * The internal buffer where data is stored.
     */
    protected byte[] buf;   // 内部缓冲区
    
    /**
     * The number of valid bytes in the buffer. This value is always
     * in the range {@code 0} through {@code buf.length}; elements
     * {@code buf[0]} through {@code buf[count-1]} contain valid
     * byte data.
     */
    protected int count;    // 记录缓冲区buf中的字节数
    
    
    
    /*▼ 构造器 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Creates a new buffered output stream to write data to the
     * specified underlying output stream.
     *
     * @param out the underlying output stream.
     */
    public BufferedOutputStream(OutputStream out) {
        this(out, 8192);
    }
    
    /**
     * Creates a new buffered output stream to write data to the
     * specified underlying output stream with the specified buffer
     * size.
     *
     * @param out  the underlying output stream.
     * @param size the buffer size.
     *
     * @throws IllegalArgumentException if size &lt;= 0.
     */
    public BufferedOutputStream(OutputStream out, int size) {
        super(out);
        
        if(size<=0) {
            throw new IllegalArgumentException("Buffer size <= 0");
        }
        
        buf = new byte[size];
    }
    
    /*▲ 构造器 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 写 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Writes the specified byte to this buffered output stream.
     *
     * @param b the byte to be written.
     *
     * @throws IOException if an I/O error occurs.
     */
    // 将指定的字节写入到输出流
    @Override
    public synchronized void write(int b) throws IOException {
        // 如果缓冲区已满，会先清空缓冲区
        if(count >= buf.length) {
            flushBuffer();
        }
        
        // 把字符暂存到缓冲区
        buf[count++] = (byte) b;
    }
    
    /**
     * Writes <code>len</code> bytes from the specified byte array
     * starting at offset <code>off</code> to this buffered output stream.
     *
     * <p> Ordinarily this method stores bytes from the given array into this
     * stream's buffer, flushing the buffer to the underlying output stream as
     * needed.  If the requested length is at least as large as this stream's
     * buffer, however, then this method will flush the buffer and write the
     * bytes directly to the underlying output stream.  Thus redundant
     * <code>BufferedOutputStream</code>s will not copy data unnecessarily.
     *
     * @param b   the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     *
     * @throws IOException if an I/O error occurs.
     */
    // 将字节数组b中off处起的len个字节写入到输出流
    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        // 如果待写入数据量大于缓存区【总容量】，则需要先刷新缓冲区
        if(len >= buf.length) {
            /*
             *
             * If the request length exceeds the size of the output buffer,
             * flush the output buffer and then write the data directly.
             * In this way buffered streams will cascade harmlessly.
             */
            flushBuffer();
            
            // 直接向输出流写入，不再经过缓存区
            out.write(b, off, len);
            return;
        }
        
        // 如果缓存区【剩余容量】不足以容纳待写入的数据，则刷新缓冲区
        if(len>buf.length - count) {
            flushBuffer();
        }
        
        // 将待写数据存入缓冲区
        System.arraycopy(b, off, buf, count, len);
        
        count += len;
    }
    
    /*▲ 写 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /*▼ 杂项 ████████████████████████████████████████████████████████████████████████████████┓ */
    
    /**
     * Flushes this buffered output stream. This forces any buffered
     * output bytes to be written out to the underlying output stream.
     *
     * @throws IOException if an I/O error occurs.
     * @see java.io.FilterOutputStream#out
     */
    // 将缓冲区中的字节刷新到输出流
    @Override
    public synchronized void flush() throws IOException {
        flushBuffer();
        out.flush();
    }
    
    /*▲ 杂项 ████████████████████████████████████████████████████████████████████████████████┛ */
    
    
    
    /** Flush the internal buffer */
    // 将缓存区内的数据写入输出流
    private void flushBuffer() throws IOException {
        if(count>0) {
            out.write(buf, 0, count);
            count = 0;
        }
    }
    
}
