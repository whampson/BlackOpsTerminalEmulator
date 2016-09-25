/*
 * The MIT License
 *
 * Copyright 2015-2016 Wes Hampson <thehambone93@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package thehambone.blackopsterminalemulator.util;

/**
 * A {@code QueueException} is an unchecked exception that indicates that an
 * invalid operation was performed on a queue.
 * <p>
 * Created on Nov 29, 2015.
 *
 * @author Wes Hampson
 */
public class QueueException extends RuntimeException
{
    /**
     * Creates a new {@code QueueException}.
     */
    public QueueException()
    {
        super();
    }
    
    /**
     * Creates a new {@code QueueException} with a message.
     * 
     * @param message exception details
     */
    public QueueException(String message)
    {
        super(message);
    }
    
    /**
     * Creates a new {@code QueueException} with the cause of the exception.
     * 
     * @param cause the underlying cause of the exception
     */
    public QueueException(Throwable cause)
    {
        super(cause);
    }
    
    /**
     * Creates a new {@code QueueException} with both a message and the cause of
     * the exception.
     * 
     * @param message exception details
     * @param cause the underlying cause of the exception
     */
    public QueueException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
